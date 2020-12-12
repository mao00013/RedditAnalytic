package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Post;
import entity.RedditAccount;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import static org.junit.jupiter.api.Assertions.*;

class RedditAccountLogicTest {
    private RedditAccountLogic redditAccountLogic;
    private RedditAccount expectedRedditAccount;

    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat( "/RedditAnalytic", "common.ServletListener" );
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }
    @BeforeEach
    final void setUp() {
        redditAccountLogic = LogicFactory.getFor("RedditAccount");
        RedditAccount entity = new RedditAccount();
        entity.setName("test");
        entity.setLinkPoints(1);
        entity.setCommentPoints(1);
        entity.setCreated(Date.from(Instant.now(Clock.systemDefaultZone())));
        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        em.getTransaction().begin();
        expectedRedditAccount = em.merge(entity);
        em.getTransaction().commit();
        em.close();
    }

    @AfterEach
    final void tearDown() {
        if(expectedRedditAccount != null) {
            redditAccountLogic.delete(expectedRedditAccount);
        }
    }


    @Test
    final void testGetColumnNames() {
        List<String> stringList = redditAccountLogic.getColumnNames();
        assertEquals( Arrays.asList("ID", "name", "link_points", "comment_points", "created"), stringList );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = redditAccountLogic.getColumnCodes();
        assertEquals(Arrays.asList(RedditAccountLogic.ID, RedditAccountLogic.NAME, RedditAccountLogic.LINKPOINTS, RedditAccountLogic.COMMENTPOINTS, RedditAccountLogic.CREATED), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = redditAccountLogic.extractDataAsList(expectedRedditAccount);
        assertEquals(expectedRedditAccount.getId(), list.get(0));
        assertEquals(expectedRedditAccount.getName(), list.get(1));
        assertEquals(expectedRedditAccount.getLinkPoints(), list.get(2));
        assertEquals(expectedRedditAccount.getCommentPoints(), list.get(3));
        assertEquals(expectedRedditAccount.getCreated(), list.get(4));
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(RedditAccountLogic.ID, new String[] {Integer.toString(expectedRedditAccount.getId())});
        sampleMap.put(RedditAccountLogic.NAME, new String[] {expectedRedditAccount.getName()});
        sampleMap.put(RedditAccountLogic.LINKPOINTS, new String[] {Integer.toString(expectedRedditAccount.getLinkPoints())});
        sampleMap.put(RedditAccountLogic.CREATED, new String[] {redditAccountLogic.convertDateToString(expectedRedditAccount.getCreated())});

        sampleMap.put(RedditAccountLogic.COMMENTPOINTS, new String[] {Integer.toString(expectedRedditAccount.getCommentPoints())});

        RedditAccount returnedRedditAccount = redditAccountLogic.createEntity(sampleMap);
        assertRedditAccountEquals(expectedRedditAccount, returnedRedditAccount);
    }

    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(RedditAccountLogic.NAME, new String[]{"Hello"});
        sampleMap.put(RedditAccountLogic.LINKPOINTS, new String[]{Integer.toString(2)});
        sampleMap.put(RedditAccountLogic.CREATED, new String[]{redditAccountLogic.convertDateToString(expectedRedditAccount.getCreated())});
        sampleMap.put(RedditAccountLogic.COMMENTPOINTS, new String[]{Integer.toString(2)});

        RedditAccount returnedRedditAccount = redditAccountLogic.createEntity(sampleMap);
        redditAccountLogic.add(returnedRedditAccount);
        returnedRedditAccount = redditAccountLogic.getRedditAccountWithName(returnedRedditAccount.getName());
        returnedRedditAccount.setCreated(new Date((returnedRedditAccount.getCreated().getTime())));
        assertEquals(sampleMap.get(RedditAccountLogic.NAME)[0], returnedRedditAccount.getName());
        redditAccountLogic.delete(returnedRedditAccount);
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(RedditAccountLogic.ID, new String[]{Integer.toString(expectedRedditAccount.getId())});
            map.put(RedditAccountLogic.NAME, new String[]{expectedRedditAccount.getName()});
            map.put(RedditAccountLogic.COMMENTPOINTS, new String[]{Integer.toString(expectedRedditAccount.getCommentPoints())});
            map.put(RedditAccountLogic.CREATED, new String[]{redditAccountLogic.convertDateToString(expectedRedditAccount.getCreated())});
            map.put(RedditAccountLogic.LINKPOINTS, new String[]{Integer.toString(expectedRedditAccount.getLinkPoints())});
        };

        fillMap.accept(sampleMap);
        sampleMap.replace(RedditAccountLogic.ID, null);
        assertThrows(NullPointerException.class, () -> redditAccountLogic.createEntity(sampleMap));
        sampleMap.replace(RedditAccountLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> redditAccountLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(RedditAccountLogic.NAME, null);
        assertThrows(NullPointerException.class, () -> redditAccountLogic.createEntity(sampleMap));
        sampleMap.replace(RedditAccountLogic.NAME, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> redditAccountLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(RedditAccountLogic.CREATED, null);
        assertThrows(NullPointerException.class, () -> redditAccountLogic.createEntity(sampleMap));
        sampleMap.replace(RedditAccountLogic.CREATED, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> redditAccountLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(RedditAccountLogic.LINKPOINTS, null);
        assertThrows(NullPointerException.class, () -> redditAccountLogic.createEntity(sampleMap));
        sampleMap.replace(RedditAccountLogic.LINKPOINTS, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> redditAccountLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(RedditAccountLogic.COMMENTPOINTS, null);
        assertThrows(NullPointerException.class, () -> redditAccountLogic.createEntity(sampleMap));
        sampleMap.replace(RedditAccountLogic.COMMENTPOINTS, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> redditAccountLogic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(RedditAccountLogic.ID, new String[]{Integer.toString(expectedRedditAccount.getId())});
            map.put(RedditAccountLogic.NAME, new String[]{expectedRedditAccount.getName()});
            map.put(RedditAccountLogic.COMMENTPOINTS, new String[]{Integer.toString(expectedRedditAccount.getCommentPoints())});
            map.put(RedditAccountLogic.LINKPOINTS, new String[]{Integer.toString(expectedRedditAccount.getLinkPoints())});
            map.put(RedditAccountLogic.CREATED, new String[]{redditAccountLogic.convertDateToString(expectedRedditAccount.getCreated())});
        };

        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            //from 97 inclusive to 123 exclusive
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        fillMap.accept(sampleMap);
        sampleMap.replace(RedditAccountLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> redditAccountLogic.createEntity(sampleMap));
        sampleMap.replace(RedditAccountLogic.ID, new String[]{"88a"});
        assertThrows(ValidationException.class, () -> redditAccountLogic.createEntity(sampleMap));


        fillMap.accept(sampleMap);
        sampleMap.replace(RedditAccountLogic.NAME, new String[]{""});
        assertThrows(ValidationException.class, () -> redditAccountLogic.createEntity(sampleMap));
        sampleMap.replace(RedditAccountLogic.NAME, new String[]{generateString.apply(101)});
        assertThrows(ValidationException.class, () -> redditAccountLogic.createEntity(sampleMap));
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
        sampleMap.put(RedditAccountLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(RedditAccountLogic.NAME, new String[]{generateString.apply(1)});
        sampleMap.put(RedditAccountLogic.CREATED, new String[]{redditAccountLogic.convertDateToString(expectedRedditAccount.getCreated())});
        sampleMap.put(RedditAccountLogic.LINKPOINTS, new String[]{Integer.toString(1)});
        sampleMap.put(RedditAccountLogic.COMMENTPOINTS, new String[]{Integer.toString(1)});

        RedditAccount returnedRedditAccount = redditAccountLogic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(RedditAccountLogic.ID)[0]), returnedRedditAccount.getId());
        assertEquals(Integer.parseInt(sampleMap.get(RedditAccountLogic.COMMENTPOINTS)[0]), returnedRedditAccount.getCommentPoints());
        assertEquals(Integer.parseInt(sampleMap.get(RedditAccountLogic.LINKPOINTS)[0]), returnedRedditAccount.getLinkPoints());
        assertEquals(sampleMap.get(RedditAccountLogic.NAME)[0], returnedRedditAccount.getName());
        assertEquals(sampleMap.get(RedditAccountLogic.CREATED)[0], redditAccountLogic.convertDateToString(returnedRedditAccount.getCreated()));

        sampleMap = new HashMap<>();
        sampleMap.put(RedditAccountLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(RedditAccountLogic.NAME, new String[]{generateString.apply(100)});
        sampleMap.put(RedditAccountLogic.CREATED, new String[]{redditAccountLogic.convertDateToString(expectedRedditAccount.getCreated())});
        sampleMap.put(RedditAccountLogic.LINKPOINTS, new String[]{Integer.toString(expectedRedditAccount.getLinkPoints())});
        sampleMap.put(RedditAccountLogic.COMMENTPOINTS, new String[]{Integer.toString(expectedRedditAccount.getCommentPoints())});

        returnedRedditAccount = redditAccountLogic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(RedditAccountLogic.ID)[0]), returnedRedditAccount.getId());
        assertEquals((sampleMap.get(RedditAccountLogic.NAME)[0]), returnedRedditAccount.getName());
        assertEquals(Integer.parseInt(sampleMap.get(RedditAccountLogic.COMMENTPOINTS)[0]), returnedRedditAccount.getCommentPoints());
        assertEquals(Integer.parseInt(sampleMap.get(RedditAccountLogic.LINKPOINTS)[0]), returnedRedditAccount.getId());
        assertEquals(Integer.parseInt(sampleMap.get(RedditAccountLogic.LINKPOINTS)[0]), returnedRedditAccount.getId());
        assertEquals(sampleMap.get(RedditAccountLogic.CREATED)[0], redditAccountLogic.convertDateToString(returnedRedditAccount.getCreated()));
    }
    /**
     * helper method for testing all redditaccount fields
     *
     * @param expected
     * @param actual
     */
    private void assertRedditAccountEquals(RedditAccount expected, RedditAccount actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getName(), actual.getName() );
        assertEquals( expected.getLinkPoints(), actual.getLinkPoints() );
        assertEquals( expected.getCommentPoints(), actual.getCommentPoints());
        long timeInMilliSeconds1 = expected.getCreated().getTime();
        long timeInMilliSeconds2 = actual.getCreated().getTime();
        long errorRangeInMilliSeconds = 10000; // 10 seconds
        assertTrue(Math.abs(timeInMilliSeconds1 - timeInMilliSeconds2) < errorRangeInMilliSeconds);
    }
    @Test
    void testGetAll() {
        List<RedditAccount> list = redditAccountLogic.getAll();
        int originalSize = list.size();
        assertNotNull(expectedRedditAccount);
        redditAccountLogic.delete(expectedRedditAccount);
        list = redditAccountLogic.getAll();
        assertEquals(originalSize -1, list.size());
    }

    @Test
    final void testGetWithId() {
        RedditAccount retrunedRedditAccount = redditAccountLogic.getWithId(expectedRedditAccount.getId());
        retrunedRedditAccount.setCreated(new Date(retrunedRedditAccount.getCreated().getTime()));
        assertRedditAccountEquals(expectedRedditAccount, retrunedRedditAccount);
    }

    @Test
    final void testGetRedditAccountWithName() {
        RedditAccount returnedRedditAccount = redditAccountLogic.getRedditAccountWithName(expectedRedditAccount.getName());
        returnedRedditAccount.setCreated(new Date(returnedRedditAccount.getCreated().getTime()));
        assertRedditAccountEquals(expectedRedditAccount, returnedRedditAccount);
    }

    @Test
    final void testGetRedditAccountsWithLinkPoints() {
        List<RedditAccount> returnedRedditAccount = redditAccountLogic.getRedditAccountsWithLinkPoints(expectedRedditAccount.getLinkPoints());
        RedditAccount redditAccount = returnedRedditAccount.get(0);
        redditAccount.setCreated(new Date(redditAccount.getCreated().getTime()));
        if(redditAccount.getId().equals(expectedRedditAccount.getId())) {
            assertRedditAccountEquals(expectedRedditAccount, redditAccount);
        }
    }

    @Test
    final void testGetRedditAccountsWithCommentPoints() {
        List<RedditAccount> returnedRedditAccount = redditAccountLogic.getRedditAccountsWithCommentPoints(expectedRedditAccount.getCommentPoints());
        RedditAccount redditAccount = returnedRedditAccount.get(0);
        redditAccount.setCreated(new Date(redditAccount.getCreated().getTime()));
        if(redditAccount.getId().equals(expectedRedditAccount.getId())){
            assertRedditAccountEquals(expectedRedditAccount, redditAccount);
        }
    }

//    @Test
//    final void testGetRedditAccountsWithCreated() {
//        List<RedditAccount> returnedRedditAccount = redditAccountLogic.getRedditAccountsWithCreated(expectedRedditAccount.getCreated());
//        RedditAccount redditAccount = returnedRedditAccount.get(0);
//        redditAccount.setCreated(new Date(redditAccount.getCreated().getTime()));
//        if(redditAccount.getId().equals(expectedRedditAccount.getId())){
//            assertRedditAccountEquals(expectedRedditAccount, redditAccount);
//        }
//    }
}