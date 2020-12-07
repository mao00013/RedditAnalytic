package logic;

import common.TomcatStartUp;
import dal.EMFactory;
import entity.Post;
import entity.RedditAccount;
import entity.Subreddit;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PostLogicTest {
    private PostLogic postLogic;
    private Post expectedPost;
    private Subreddit expectedSubreddit;
    private SubredditLogic subredditLogic;
    private RedditAccount expectedRedditAccount;
    private RedditAccountLogic redditAccountLogic;

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
        postLogic = LogicFactory.getFor("Post");
        subredditLogic = LogicFactory.getFor("Subreddit");
        redditAccountLogic = LogicFactory.getFor("RedditAccount");
        for(Post post: postLogic.getAll())
            postLogic.delete(post);
        for(Subreddit subreddit: subredditLogic.getAll())
            subredditLogic.delete(subreddit);
        for(RedditAccount redditAccount: redditAccountLogic.getAll())
            redditAccountLogic.delete(redditAccount);

        // create RedditAccount entity
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

        // crate Subreddit entity
        Subreddit subreddit = new Subreddit();
        subreddit.setName("subreddit");
        subreddit.setUrl("http://test.com");
        subreddit.setSubscribers(100);
        em = EMFactory.getEMF().createEntityManager();
        em.getTransaction().begin();
        expectedSubreddit = em.merge(subreddit);
        em.getTransaction().commit();
        em.close();

        // create Post entity
        Post post = new Post();
        post.setTitle("post");
        post.setCreated(new Date(2020, 12, 1));
        post.setPoints(2);
        post.setUniqueId("post");
        post.setCommentCount(2);
        post.setSubredditId(expectedSubreddit);
        post.setRedditAccountId(expectedRedditAccount);
        em = EMFactory.getEMF().createEntityManager();
        em.getTransaction().begin();
        expectedPost = em.merge(post);
        em.getTransaction().commit();
        em.close();
    }

    @AfterEach
    void tearDown() {
        if(expectedPost != null) {
            postLogic.delete(expectedPost);
        }
        if(expectedSubreddit != null) {
            subredditLogic.delete(expectedSubreddit);
        }
        if(expectedRedditAccount != null) {
            redditAccountLogic.delete(expectedRedditAccount);
        }
    }

    @Test
    void testGetColumnNames() {
        List<String> stringList = postLogic.getColumnNames();
        assertEquals( Arrays.asList("ID", "title", "created", "points","subreddit_id", "unique_id", "comment_count", "reddit_account_id"), stringList );
    }

    @Test
    void testGetColumnCodes() {
        List<String> list = postLogic.getColumnCodes();
        assertEquals(Arrays.asList(PostLogic.ID, PostLogic.TITLE, PostLogic.CREATED, PostLogic.POINTS, PostLogic.SUBREDDIT_ID, PostLogic.UNIQUE_ID, PostLogic.COMMENT_COUNT, PostLogic.REDDIT_ACCOUNT_ID), list);
    }

    @Test
    void testExtractDataAsList() {
        List<?> list = postLogic.extractDataAsList( expectedPost);
        assertEquals(expectedPost.getId(), list.get(0));
        assertEquals(expectedPost.getTitle(), list.get(1));
        assertEquals(expectedPost.getCreated(), list.get(2));
        assertEquals(expectedPost.getPoints(), list.get(3));
        assertEquals(expectedPost.getSubredditId().getId(), ((Subreddit)list.get(4)).getId());
        assertEquals(expectedPost.getUniqueID(), list.get(5));
        assertEquals(expectedPost.getCommentCount(), list.get(6));
        assertEquals(expectedPost.getRedditAccountId().getId(), ((RedditAccount) list.get(7)).getId());
    }

    @Test
    void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(PostLogic.ID, new String[] {Integer.toString(expectedPost.getId())});
        sampleMap.put(PostLogic.TITLE, new String[] {expectedPost.getTitle()});
        sampleMap.put(PostLogic.CREATED, new String[] {expectedPost.getCreated().toString()});
        sampleMap.put(PostLogic.POINTS, new String[] {Integer.toString(expectedPost.getPoints())});
        sampleMap.put(postLogic.SUBREDDIT_ID, new String[] {expectedPost.getSubredditId().getId().toString()});
        sampleMap.put(postLogic.UNIQUE_ID, new String[] {expectedPost.getUniqueID()});
        sampleMap.put(PostLogic.COMMENT_COUNT, new String[] {Integer.toString(expectedPost.getCommentCount())});
        sampleMap.put(PostLogic.REDDIT_ACCOUNT_ID, new String[] {expectedPost.getRedditAccountId().getId().toString()});
        Post returnedPost = postLogic.createEntity(sampleMap);
        assertPostEquals(expectedPost, returnedPost);

    }
    /**
     * helper method for testing all comment fields
     *
     * @param expected
     * @param actual
     */
    private void assertPostEquals( Post expected, Post actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getTitle(), actual.getTitle() );
        assertEquals( expected.getCreated().toString(), actual.getCreated().toString() );
        assertEquals( expected.getPoints(), actual.getPoints() );
        assertEquals( expected.getSubredditId().getId(), actual.getSubredditId().getId());
        assertEquals( expected.getCommentCount(), actual.getCommentCount() );
        assertEquals( expected.getUniqueID(), actual.getUniqueID() );
        assertEquals( expected.getRedditAccountId().getId(), actual.getRedditAccountId().getId() );
    }

    @Test
    void testGetAll() {
        List<Post> list = postLogic.getAll();
        int originalSize = list.size();
        assertNotNull(expectedPost);
        postLogic.delete(expectedPost);
        list = postLogic.getAll();
        assertEquals(originalSize -1, list.size());
    }

    @Test
    void testGetWithId() {
        Post returnedPost = postLogic.getWithId(expectedPost.getId());
        returnedPost.setCreated(new Date(returnedPost.getCreated().getTime()));
        assertPostEquals(expectedPost, returnedPost);
    }

    @Test
    void testGetPostWithUniqueId() {
        Post returnedPost = postLogic.getPostWithUniqueId(expectedPost.getUniqueID());
        returnedPost.setCreated(new Date(returnedPost.getCreated().getTime()));
        assertPostEquals(expectedPost, returnedPost);
    }

    @Test
    void testGetPostWithPoints() {
        List<Post> returnedPost = postLogic.getPostWithPoints(expectedPost.getPoints());
        Post post = returnedPost.get(0);
        post.setCreated(new Date(post.getCreated().getTime()));
        if(post.getId().equals(expectedPost.getId())) {
            assertPostEquals(expectedPost, post);
        }
    }

    @Test
    void testGetPostsWithCommentCount() {
        List<Post> returnedPost = postLogic.getPostsWithCommentCount(expectedPost.getCommentCount());
        Post post = returnedPost.get(0);
        post.setCreated(new Date(post.getCreated().getTime()));
        if(post.getId().equals(expectedPost.getId())) {
            assertPostEquals(expectedPost, post);
        }
    }

    @Test
    void testGetPostsWithAuthorID() {
        List<Post> returnedPost = postLogic.getPostsWithAuthorID(expectedPost.getRedditAccountId().getId());
        Post post = returnedPost.get(0);
        post.setCreated(new Date(post.getCreated().getTime()));
        if(post.getId().equals(expectedPost.getId())) {
            assertPostEquals(expectedPost, post);
        }
    }

    @Test
    void testGetPostsWithTitle() {
        List<Post> returnedPost = postLogic.getPostsWithTitle(expectedPost.getTitle());
        Post post = returnedPost.get(0);
        post.setCreated(new Date(post.getCreated().getTime()));
        if(post.getId().equals(expectedPost.getId())) {
            assertPostEquals(expectedPost, post);
        }
    }

    @Test
    void TestGetPostsWithCreated() {
        List<Post> returnedPost = postLogic.getPostsWithCreated(expectedPost.getCreated());
        Post post = returnedPost.get(0);
        post.setCreated(new Date(post.getCreated().getTime()));
        if(post.getId().equals(expectedPost.getId())) {
            assertPostEquals(expectedPost, post);
        }
    }
}
