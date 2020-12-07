package logic;

import common.TomcatStartUp;
import dal.EMFactory;
import entity.Post;
import entity.RedditAccount;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import java.util.*;

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
    void setUp() {
        redditAccountLogic = LogicFactory.getFor("RedditAccount");

        for(RedditAccount redditAccount: redditAccountLogic.getAll())
            redditAccountLogic.delete(redditAccount);
        RedditAccount entity = new RedditAccount();
        entity.setName("test");
        entity.setLinkPoints(1);
        entity.setCommentPoints(1);
        entity.setCreated(new Date(2020, 12, 3));
        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        em.getTransaction().begin();
        expectedRedditAccount = em.merge(entity);
        em.getTransaction().commit();
        em.close();
    }

    @AfterEach
    void tearDown() {
        if(expectedRedditAccount != null) {
            redditAccountLogic.delete(expectedRedditAccount);
        }
    }


    @Test
    void testGetColumnNames() {
        List<String> stringList = redditAccountLogic.getColumnNames();
        assertEquals( Arrays.asList("ID", "name", "link_points", "comment_points", "created"), stringList );
    }

    @Test
    void testGetColumnCodes() {
        List<String> list = redditAccountLogic.getColumnCodes();
        assertEquals(Arrays.asList(RedditAccountLogic.ID, RedditAccountLogic.NAME, RedditAccountLogic.LINKPOINTS, RedditAccountLogic.COMMENTPOINTS, RedditAccountLogic.CREATED), list);
    }

    @Test
    void testExtractDataAsList() {
        List<?> list = redditAccountLogic.extractDataAsList(expectedRedditAccount);
        assertEquals(expectedRedditAccount.getId(), list.get(0));
        assertEquals(expectedRedditAccount.getName(), list.get(1));
        assertEquals(expectedRedditAccount.getLinkPoints(), list.get(2));
        assertEquals(expectedRedditAccount.getCommentPoints(), list.get(3));
        assertEquals(expectedRedditAccount.getCreated(), list.get(4));
    }

    @Test
    void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(RedditAccountLogic.ID, new String[] {Integer.toString(expectedRedditAccount.getId())});
        sampleMap.put(RedditAccountLogic.NAME, new String[] {expectedRedditAccount.getName()});
        sampleMap.put(RedditAccountLogic.LINKPOINTS, new String[] {Integer.toString(expectedRedditAccount.getLinkPoints())});
        sampleMap.put(RedditAccountLogic.CREATED, new String[] {expectedRedditAccount.getCreated().toString()
        });
        sampleMap.put(RedditAccountLogic.COMMENTPOINTS, new String[] {Integer.toString(expectedRedditAccount.getCommentPoints())});

        RedditAccount returnedRedditAccount = redditAccountLogic.createEntity(sampleMap);
        assertRedditAccountEquals(expectedRedditAccount, returnedRedditAccount);
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
        assertEquals( expected.getCreated().toString(), actual.getCreated().toString() );
        assertEquals( expected.getLinkPoints(), actual.getLinkPoints() );
        assertEquals( expected.getCommentPoints(), actual.getCommentPoints());
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
    void testGetWithId() {
        RedditAccount retrunedRedditAccount = redditAccountLogic.getWithId(expectedRedditAccount.getId());
        retrunedRedditAccount.setCreated(new Date(retrunedRedditAccount.getCreated().getTime()));
        assertRedditAccountEquals(expectedRedditAccount, retrunedRedditAccount);
    }

    @Test
    void testGetRedditAccountWithName() {
        RedditAccount returnedRedditAccount = redditAccountLogic.getRedditAccountWithName(expectedRedditAccount.getName());
        returnedRedditAccount.setCreated(new Date(returnedRedditAccount.getCreated().getTime()));
        assertRedditAccountEquals(expectedRedditAccount, returnedRedditAccount);
    }

    @Test
    void testGetRedditAccountsWithLinkPoints() {
        List<RedditAccount> returnedRedditAccount = redditAccountLogic.getRedditAccountsWithLinkPoints(expectedRedditAccount.getLinkPoints());
        RedditAccount redditAccount = returnedRedditAccount.get(0);
        redditAccount.setCreated(new Date(redditAccount.getCreated().getTime()));
        if(redditAccount.getId().equals(expectedRedditAccount.getId())) {
            assertRedditAccountEquals(expectedRedditAccount, redditAccount);
        }
    }

    @Test
    void testGetRedditAccountsWithCommentPoints() {
        List<RedditAccount> returnedRedditAccount = redditAccountLogic.getRedditAccountsWithCommentPoints(expectedRedditAccount.getCommentPoints());
        RedditAccount redditAccount = returnedRedditAccount.get(0);
        redditAccount.setCreated(new Date(redditAccount.getCreated().getTime()));
        if(redditAccount.getId().equals(expectedRedditAccount.getId())){
            assertRedditAccountEquals(expectedRedditAccount, redditAccount);
        }
    }

    @Test
    void testGetRedditAccountsWithCreated() {
        List<RedditAccount> returnedRedditAccount = redditAccountLogic.getRedditAccountsWithCreated(expectedRedditAccount.getCreated());
        RedditAccount redditAccount = returnedRedditAccount.get(0);
        redditAccount.setCreated(new Date(redditAccount.getCreated().getTime()));
        if(redditAccount.getId().equals(expectedRedditAccount.getId())){
            assertRedditAccountEquals(expectedRedditAccount, redditAccount);
        }
    }
}