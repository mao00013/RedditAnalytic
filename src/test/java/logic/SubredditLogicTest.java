package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Comment;
import entity.Subreddit;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import static org.junit.jupiter.api.Assertions.*;

class SubredditLogicTest {
    private SubredditLogic subredditLogic;
    private Subreddit expectedSubreddit;

    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat("/RedditAnalytic", "common.ServletListener");
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }

    @BeforeEach
    final void setUp() {
        subredditLogic = LogicFactory.getFor("Subreddit");
        for (Subreddit subreddit : subredditLogic.getAll())
            subredditLogic.delete(subreddit);
        EntityManager em = EMFactory.getEMF().createEntityManager();
        Subreddit subreddit = new Subreddit();
        subreddit.setUrl("http://test.com");
        subreddit.setName("subreddit");
        subreddit.setSubscribers(100);
        em = EMFactory.getEMF().createEntityManager();
        em.getTransaction().begin();
        expectedSubreddit = em.merge(subreddit);
        em.getTransaction().commit();
        em.close();
    }

    @AfterEach
    final void tearDown() {
        if (expectedSubreddit != null) {
            subredditLogic.delete(expectedSubreddit);
        }
    }

    @Test
    final void testGetColumnNames() {
        List<String> stringList = subredditLogic.getColumnNames();
        assertEquals(Arrays.asList("ID", "subscribers", "name", "url"), stringList);
    }

    @Test
    final void textGetColumnCodes() {
        List<String> list = subredditLogic.getColumnCodes();
        assertEquals(Arrays.asList(SubredditLogic.ID, SubredditLogic.NAME, SubredditLogic.URL, SubredditLogic.SUBSCRIBERS), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = subredditLogic.extractDataAsList(expectedSubreddit);
        assertEquals(expectedSubreddit.getId(), list.get(0));
        assertEquals(expectedSubreddit.getName(), list.get(1));
        assertEquals(expectedSubreddit.getUrl(), list.get(2));
        assertEquals(expectedSubreddit.getSubscribers(), list.get(3));
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(SubredditLogic.ID, new String[]{Integer.toString(expectedSubreddit.getId())});
        sampleMap.put(SubredditLogic.NAME, new String[]{expectedSubreddit.getName()});
        sampleMap.put(SubredditLogic.URL, new String[]{expectedSubreddit.getUrl()});
        sampleMap.put(SubredditLogic.SUBSCRIBERS, new String[]{Integer.toString(expectedSubreddit.getSubscribers())});
        Subreddit returnedSubreddit = subredditLogic.createEntity(sampleMap);
        assertSubredditEquals(expectedSubreddit, returnedSubreddit);
    }


    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(SubredditLogic.NAME, new String[]{"Test Create Entity"});
        sampleMap.put(SubredditLogic.URL, new String[]{"https:google.com"});
        sampleMap.put(SubredditLogic.SUBSCRIBERS, new String[]{Integer.toString(1)});

        Subreddit returnedSubreddit = subredditLogic.createEntity(sampleMap);
        subredditLogic.add(returnedSubreddit);
        returnedSubreddit = subredditLogic.getSubredditWithName(returnedSubreddit.getName());
        assertEquals(sampleMap.get(SubredditLogic.NAME)[0], returnedSubreddit.getName());
        subredditLogic.delete(returnedSubreddit);
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(SubredditLogic.ID, new String[]{Integer.toString(expectedSubreddit.getId())});
            map.put(SubredditLogic.NAME, new String[]{expectedSubreddit.getName()});
            map.put(SubredditLogic.URL, new String[]{expectedSubreddit.getUrl()});
            map.put(SubredditLogic.SUBSCRIBERS, new String[]{Integer.toString(expectedSubreddit.getSubscribers())});
        };

        fillMap.accept(sampleMap);
        sampleMap.replace(SubredditLogic.ID, null);
        assertThrows(NullPointerException.class, () -> subredditLogic.createEntity(sampleMap));
        sampleMap.replace(SubredditLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> subredditLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(SubredditLogic.NAME, null);
        assertThrows(NullPointerException.class, () -> subredditLogic.createEntity(sampleMap));
        sampleMap.replace(SubredditLogic.NAME, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> subredditLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(SubredditLogic.SUBSCRIBERS, null);
        assertThrows(NullPointerException.class, () -> subredditLogic.createEntity(sampleMap));
        sampleMap.replace(SubredditLogic.SUBSCRIBERS,new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> subredditLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(SubredditLogic.URL, null);
        assertThrows(NullPointerException.class, () -> subredditLogic.createEntity(sampleMap));
        sampleMap.replace(SubredditLogic.URL, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> subredditLogic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(SubredditLogic.ID, new String[]{Integer.toString(expectedSubreddit.getId())});
            map.put(SubredditLogic.NAME, new String[]{expectedSubreddit.getName()});
            map.put(SubredditLogic.URL, new String[]{expectedSubreddit.getUrl()});
            map.put(SubredditLogic.SUBSCRIBERS, new String[]{Integer.toString(expectedSubreddit.getSubscribers())});
        };

        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            //from 97 inclusive to 123 exclusive
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        fillMap.accept(sampleMap);
        sampleMap.replace(SubredditLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> subredditLogic.createEntity(sampleMap));
        sampleMap.replace(SubredditLogic.ID, new String[]{"88a"});
        assertThrows(ValidationException.class, () -> subredditLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(SubredditLogic.NAME, new String[]{""});
        assertThrows(ValidationException.class, () -> subredditLogic.createEntity(sampleMap));
        sampleMap.replace(SubredditLogic.NAME, new String[]{generateString.apply(101)});
        assertThrows(ValidationException.class, () -> subredditLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(SubredditLogic.URL, new String[]{""});
        assertThrows(ValidationException.class, () -> subredditLogic.createEntity(sampleMap));
        sampleMap.replace(SubredditLogic.URL, new String[]{generateString.apply(201)});
        assertThrows(ValidationException.class, () -> subredditLogic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityEdgeValues() {
        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(PostLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(SubredditLogic.NAME, new String[]{generateString.apply(1)});
        sampleMap.put(SubredditLogic.URL, new String[]{generateString.apply(1)});
        sampleMap.put(SubredditLogic.SUBSCRIBERS, new String[]{Integer.toString(1)});

        Subreddit returnedSubreddit = subredditLogic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(SubredditLogic.ID) [0]), returnedSubreddit.getId());
        assertEquals(sampleMap.get(SubredditLogic.NAME)[0], returnedSubreddit.getName());
        assertEquals(sampleMap.get(SubredditLogic.URL)[0], returnedSubreddit.getUrl());
        assertEquals(Integer.parseInt(sampleMap.get(SubredditLogic.SUBSCRIBERS)[0]), returnedSubreddit.getSubscribers());

        sampleMap = new HashMap<>();
        sampleMap.put(SubredditLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(SubredditLogic.NAME, new String[]{generateString.apply(100)});
        sampleMap.put(SubredditLogic.URL, new String[]{generateString.apply(200)});
        sampleMap.put(SubredditLogic.SUBSCRIBERS, new String[]{Integer.toString(1)});

        returnedSubreddit = subredditLogic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(SubredditLogic.ID)[0]), returnedSubreddit.getId());
        assertEquals(sampleMap.get(SubredditLogic.NAME)[0], returnedSubreddit.getName());
        assertEquals(sampleMap.get(SubredditLogic.URL) [0], returnedSubreddit.getUrl());
        assertEquals(Integer.parseInt(sampleMap.get(SubredditLogic.SUBSCRIBERS)[0]), returnedSubreddit.getSubscribers());
    }

    private void assertSubredditEquals(Subreddit expected, Subreddit actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getName(), actual.getName() );
        assertEquals( expected.getUrl(), actual.getUrl());
        assertEquals( expected.getSubscribers(), actual.getSubscribers() );
    }
    @Test
    final void testGetAll() {
        List<Subreddit> list = subredditLogic.getAll();
        int originalSize = list.size();
        assertNotNull(expectedSubreddit);
        subredditLogic.delete(expectedSubreddit);
        list = subredditLogic.getAll();
        assertEquals(originalSize -1, list.size());
    }

    @Test
    final void testGetWithId() {
        Subreddit returnedSubreddit = subredditLogic.getWithId(expectedSubreddit.getId());
        assertSubredditEquals(expectedSubreddit, returnedSubreddit);
    }

    @Test
    final void testGetSubredditWithName() {
        Subreddit returnedSubreddit = subredditLogic.getSubredditWithName(expectedSubreddit.getName());
        assertSubredditEquals(expectedSubreddit, returnedSubreddit);
    }

    @Test
    final void testGetSubredditWithUrl() {
        Subreddit returnedSubreddit = subredditLogic.getSubredditWithUrl(expectedSubreddit.getUrl());
        assertSubredditEquals(expectedSubreddit, returnedSubreddit);
    }

    @Test
    final void getSubredditsWithSubscribers() {
        List<Subreddit> returnedSubreddit = subredditLogic.getSubredditsWithSubscribers(expectedSubreddit.getSubscribers());
        Subreddit subreddit = returnedSubreddit.get(0);
       if(subreddit.getId().equals(expectedSubreddit.getId())) {
           assertSubredditEquals(expectedSubreddit, subreddit);
       }
    }
}