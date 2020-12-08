package logic;

import common.TomcatStartUp;
import dal.EMFactory;
import entity.Comment;
import entity.Subreddit;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubredditLogicTest {
    private SubredditLogic subredditLogic;
    private Subreddit expectedSubreddit;

    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat( "/RedditAnalytic", "common.ServletListener" );
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }
    @BeforeEach
    void setUp() {
        subredditLogic = LogicFactory.getFor("Subreddit");
        for(Subreddit subreddit: subredditLogic.getAll())
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
    void tearDown() {
        if(expectedSubreddit != null) {
            subredditLogic.delete(expectedSubreddit);
        }
    }

    @Test
    void testGetColumnNames() {
        List<String> stringList = subredditLogic.getColumnNames();
        assertEquals( Arrays.asList("ID", "subscribers", "name", "url"), stringList );
    }

    @Test
    void textGetColumnCodes() {
        List<String> list = subredditLogic.getColumnCodes();
        assertEquals(Arrays.asList(SubredditLogic.ID, SubredditLogic.NAME, SubredditLogic.URL, SubredditLogic.SUBSCRIBERS), list);
    }

    @Test
    void testExtractDataAsList() {
        List<?> list = subredditLogic.extractDataAsList(expectedSubreddit);
        assertEquals(expectedSubreddit.getId(), list.get(0));
        assertEquals(expectedSubreddit.getName(), list.get(1));
        assertEquals(expectedSubreddit.getUrl(), list.get(2));
        assertEquals(expectedSubreddit.getSubscribers(), list.get(3));
    }

    @Test
    void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(SubredditLogic.ID, new String[] {Integer.toString(expectedSubreddit.getId())});
        sampleMap.put(SubredditLogic.NAME, new String[]{expectedSubreddit.getName()});
        sampleMap.put(SubredditLogic.URL, new String[]{expectedSubreddit.getUrl()});
        sampleMap.put(SubredditLogic.SUBSCRIBERS, new String[]{Integer.toString(expectedSubreddit.getSubscribers())});
        Subreddit returnedSubreddit = subredditLogic.createEntity(sampleMap);
        assertSubredditEquals(expectedSubreddit, returnedSubreddit);
    }
    private void assertSubredditEquals(Subreddit expected, Subreddit actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getName(), actual.getName() );
        assertEquals( expected.getUrl(), actual.getUrl());
        assertEquals( expected.getSubscribers(), actual.getSubscribers() );
    }
    @Test
    void testGetAll() {
        List<Subreddit> list = subredditLogic.getAll();
        int originalSize = list.size();
        assertNotNull(expectedSubreddit);
        subredditLogic.delete(expectedSubreddit);
        list = subredditLogic.getAll();
        assertEquals(originalSize -1, list.size());
    }

    @Test
    void testGetWithId() {
        Subreddit returnedSubreddit = subredditLogic.getWithId(expectedSubreddit.getId());
        assertSubredditEquals(expectedSubreddit, returnedSubreddit);
    }

    @Test
    void testGetSubredditWithName() {
        Subreddit returnedSubreddit = subredditLogic.getSubredditWithName(expectedSubreddit.getName());
        assertSubredditEquals(expectedSubreddit, returnedSubreddit);
    }

    @Test
    void testGetSubredditWithUrl() {
        Subreddit returnedSubreddit = subredditLogic.getSubredditWithUrl(expectedSubreddit.getUrl());
        assertSubredditEquals(expectedSubreddit, returnedSubreddit);
    }

    @Test
    void getSubredditsWithSubscribers() {
        List<Subreddit> returnedSubreddit = subredditLogic.getSubredditsWithSubscribers(expectedSubreddit.getSubscribers());
        Subreddit subreddit = returnedSubreddit.get(0);
       if(subreddit.getId().equals(expectedSubreddit.getId())) {
           assertSubredditEquals(expectedSubreddit, subreddit);
       }
    }
}