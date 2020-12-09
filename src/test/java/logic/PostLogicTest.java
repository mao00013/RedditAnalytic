package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Post;
import entity.RedditAccount;
import entity.Subreddit;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import static org.junit.jupiter.api.Assertions.*;

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
    final void setUp() {
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
    final void tearDown() {
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
    final void testGetColumnNames() {
        List<String> stringList = postLogic.getColumnNames();
        assertEquals( Arrays.asList("ID", "title", "created", "points","subreddit_id", "unique_id", "comment_count", "reddit_account_id"), stringList );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = postLogic.getColumnCodes();
        assertEquals(Arrays.asList(PostLogic.ID, PostLogic.TITLE, PostLogic.CREATED, PostLogic.POINTS, PostLogic.SUBREDDIT_ID, PostLogic.UNIQUE_ID, PostLogic.COMMENT_COUNT, PostLogic.REDDIT_ACCOUNT_ID), list);
    }

    @Test
    final void testExtractDataAsList() {
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
    final void testCreateEntity() {
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

    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(PostLogic.TITLE, new String[]{"Test Create Entity"});
        sampleMap.put(PostLogic.CREATED, new String[]{expectedPost.getCreated().toString()});
        sampleMap.put(PostLogic.POINTS, new String[]{Integer.toString(1)});
        sampleMap.put(PostLogic.SUBREDDIT_ID, new String[]{expectedPost.getSubredditId().getId().toString()});
        sampleMap.put(PostLogic.UNIQUE_ID, new String[]{"222"});
        sampleMap.put(PostLogic.COMMENT_COUNT, new String[]{Integer.toString(2)});
        sampleMap.put(PostLogic.REDDIT_ACCOUNT_ID, new String[]{expectedPost.getRedditAccountId().getId().toString()});

        Post returnedPost = postLogic.createEntity(sampleMap);
        postLogic.add(returnedPost);
        returnedPost = postLogic.getPostWithUniqueId(returnedPost.getUniqueID());
        returnedPost.setCreated(new Date(returnedPost.getCreated().getTime()));
        assertEquals(sampleMap.get(PostLogic.TITLE)[0], returnedPost.getTitle());
        postLogic.delete(returnedPost);
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(PostLogic.ID, new String[] {Integer.toString(expectedPost.getId())});
            map.put(PostLogic.TITLE, new String[] {expectedPost.getTitle()});
            map.put(PostLogic.CREATED, new String[] {expectedPost.getCreated().toString()});
            map.put(PostLogic.POINTS, new String[] {Integer.toString(expectedPost.getPoints())});
            map.put(postLogic.SUBREDDIT_ID, new String[] {expectedPost.getSubredditId().getId().toString()});
            map.put(postLogic.UNIQUE_ID, new String[] {expectedPost.getUniqueID()});
            map.put(PostLogic.COMMENT_COUNT, new String[] {Integer.toString(expectedPost.getCommentCount())});
            map.put(PostLogic.REDDIT_ACCOUNT_ID, new String[] {expectedPost.getRedditAccountId().getId().toString()});
        };

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.ID, null);
        assertThrows(NullPointerException.class, () -> postLogic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.ID, new String[] {});
        assertThrows(IndexOutOfBoundsException.class, () -> postLogic.createEntity(sampleMap));
        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.TITLE, null);
        assertThrows(NullPointerException.class, () -> postLogic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.TITLE, new String[] {});
        assertThrows(IndexOutOfBoundsException.class, () -> postLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.CREATED, null);
        assertThrows(NullPointerException.class, () -> postLogic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.CREATED, new String[] {});
        assertThrows(IndexOutOfBoundsException.class, () -> postLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.POINTS, null);
        assertThrows(NullPointerException.class, () -> postLogic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.POINTS, new String[] {});
        assertThrows(IndexOutOfBoundsException.class, () -> postLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.SUBREDDIT_ID, null);
        assertThrows(NullPointerException.class, () -> postLogic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.SUBREDDIT_ID, new String[] {});
        assertThrows(IndexOutOfBoundsException.class, () -> postLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.UNIQUE_ID, null);
        assertThrows(NullPointerException.class, () -> postLogic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.UNIQUE_ID, new String[] {});
        assertThrows(IndexOutOfBoundsException.class, () -> postLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.COMMENT_COUNT, null);
        assertThrows(NullPointerException.class, () -> postLogic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.COMMENT_COUNT, new String[] {});
        assertThrows(IndexOutOfBoundsException.class, () -> postLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.REDDIT_ACCOUNT_ID, null);
        assertThrows(NullPointerException.class, () -> postLogic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.REDDIT_ACCOUNT_ID, new String[] {});
        assertThrows(IndexOutOfBoundsException.class, () -> postLogic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(PostLogic.ID, new String[]{Integer.toString(expectedPost.getId())});
            map.put(PostLogic.TITLE, new String[]{expectedPost.getTitle()});
            map.put(PostLogic.CREATED, new String[]{expectedPost.getCreated().toString()});
            map.put(PostLogic.POINTS, new String[]{Integer.toString(expectedPost.getPoints())});
            map.put(PostLogic.SUBREDDIT_ID, new String[]{expectedPost.getSubredditId().getId().toString()});
            map.put(PostLogic.UNIQUE_ID, new String[]{expectedPost.getUniqueID()});
            map.put(PostLogic.COMMENT_COUNT, new String[]{Integer.toString(expectedPost.getCommentCount())});
            map.put(PostLogic.REDDIT_ACCOUNT_ID, new String[]{expectedPost.getRedditAccountId().getId().toString()});
        };

        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            //from 97 inclusive to 123 exclusive
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> postLogic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.ID, new String[]{"88a"});
        assertThrows(ValidationException.class, () -> postLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.TITLE, new String[]{""});
        assertThrows(ValidationException.class, () -> postLogic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.TITLE, new String[]{generateString.apply(256)});
        assertThrows(ValidationException.class, () -> postLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(PostLogic.UNIQUE_ID, new String[]{""});
        assertThrows(ValidationException.class, () -> postLogic.createEntity(sampleMap));
        sampleMap.replace(PostLogic.UNIQUE_ID, new String[]{generateString.apply(11)});
        assertThrows(ValidationException.class, () -> postLogic.createEntity(sampleMap));
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
        sampleMap.put(PostLogic.TITLE, new String[]{generateString.apply(1)});
        sampleMap.put(PostLogic.CREATED, new String[]{expectedPost.getCreated().toString()});
        sampleMap.put(PostLogic.REDDIT_ACCOUNT_ID, new String[]{expectedPost.getRedditAccountId().getId().toString()});
        sampleMap.put(PostLogic.COMMENT_COUNT, new String[]{Integer.toString(1)});
        sampleMap.put(PostLogic.UNIQUE_ID, new String[]{generateString.apply(1)});
        sampleMap.put(PostLogic.SUBREDDIT_ID, new String[]{expectedPost.getSubredditId().getId().toString()});
        sampleMap.put(PostLogic.POINTS, new String[]{Integer.toString(1)});

        Post returnedPost = postLogic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(PostLogic.ID)[0]), returnedPost.getId());
        assertEquals(sampleMap.get(PostLogic.TITLE)[0], returnedPost.getTitle());
        assertEquals(sampleMap.get(PostLogic.CREATED)[0], returnedPost.getCreated().toString());
        assertEquals(sampleMap.get(PostLogic.REDDIT_ACCOUNT_ID)[0], returnedPost.getRedditAccountId().getId().toString());
        assertEquals(Integer.parseInt(sampleMap.get(PostLogic.COMMENT_COUNT)[0]), returnedPost.getCommentCount());
        assertEquals(Integer.parseInt(sampleMap.get(PostLogic.POINTS)[0]), returnedPost.getPoints());
        assertEquals(sampleMap.get(PostLogic.UNIQUE_ID)[0], returnedPost.getUniqueID());
        assertEquals(sampleMap.get(PostLogic.SUBREDDIT_ID)[0], returnedPost.getSubredditId().getId().toString());

        sampleMap = new HashMap<>();
        sampleMap.put(PostLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(PostLogic.TITLE, new String[]{generateString.apply(255)});
        sampleMap.put(PostLogic.UNIQUE_ID, new String[]{generateString.apply(10)});
        sampleMap.put(PostLogic.CREATED, new String[]{expectedPost.getCreated().toString()});
        sampleMap.put(PostLogic.POINTS, new String[]{Integer.toString(1)});
        sampleMap.put(PostLogic.COMMENT_COUNT, new String[]{Integer.toString(1)});
        sampleMap.put(PostLogic.REDDIT_ACCOUNT_ID, new String[]{expectedPost.getRedditAccountId().getId().toString()});
        sampleMap.put(PostLogic.SUBREDDIT_ID, new String[]{expectedPost.getSubredditId().getId().toString()});

        returnedPost = postLogic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(PostLogic.ID) [0]), returnedPost.getId());
        assertEquals(sampleMap.get(PostLogic.TITLE) [0], returnedPost.getTitle());
        assertEquals(sampleMap.get(PostLogic.CREATED) [0], returnedPost.getCreated().toString());
        assertEquals(sampleMap.get(PostLogic.UNIQUE_ID )[0], returnedPost.getUniqueID());
        assertEquals(Integer.parseInt(sampleMap.get(PostLogic.COMMENT_COUNT) [0]), returnedPost.getCommentCount());
        assertEquals(Integer.parseInt(sampleMap.get(PostLogic.POINTS) [0]), returnedPost.getPoints());
        assertEquals(sampleMap.get(PostLogic.REDDIT_ACCOUNT_ID) [0], returnedPost.getRedditAccountId().getId().toString());
        assertEquals(sampleMap.get(PostLogic.SUBREDDIT_ID) [0], returnedPost.getSubredditId().getId().toString());
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
    final void testGetAll() {
        List<Post> list = postLogic.getAll();
        int originalSize = list.size();
        assertNotNull(expectedPost);
        postLogic.delete(expectedPost);
        list = postLogic.getAll();
        assertEquals(originalSize -1, list.size());
    }

    @Test
    final void testGetWithId() {
        Post returnedPost = postLogic.getWithId(expectedPost.getId());
        returnedPost.setCreated(new Date(returnedPost.getCreated().getTime()));
        assertPostEquals(expectedPost, returnedPost);
    }

    @Test
    final void testGetPostWithUniqueId() {
        Post returnedPost = postLogic.getPostWithUniqueId(expectedPost.getUniqueID());
        returnedPost.setCreated(new Date(returnedPost.getCreated().getTime()));
        assertPostEquals(expectedPost, returnedPost);
    }

    @Test
    final void testGetPostWithPoints() {
        List<Post> returnedPost = postLogic.getPostWithPoints(expectedPost.getPoints());
        Post post = returnedPost.get(0);
        post.setCreated(new Date(post.getCreated().getTime()));
        if(post.getId().equals(expectedPost.getId())) {
            assertPostEquals(expectedPost, post);
        }
    }

    @Test
    final void testGetPostsWithCommentCount() {
        List<Post> returnedPost = postLogic.getPostsWithCommentCount(expectedPost.getCommentCount());
        Post post = returnedPost.get(0);
        post.setCreated(new Date(post.getCreated().getTime()));
        if(post.getId().equals(expectedPost.getId())) {
            assertPostEquals(expectedPost, post);
        }
    }

    @Test
    final void testGetPostsWithAuthorID() {
        List<Post> returnedPost = postLogic.getPostsWithAuthorID(expectedPost.getRedditAccountId().getId());
        Post post = returnedPost.get(0);
        post.setCreated(new Date(post.getCreated().getTime()));
        if(post.getId().equals(expectedPost.getId())) {
            assertPostEquals(expectedPost, post);
        }
    }

    @Test
    final void testGetPostsWithTitle() {
        List<Post> returnedPost = postLogic.getPostsWithTitle(expectedPost.getTitle());
        Post post = returnedPost.get(0);
        post.setCreated(new Date(post.getCreated().getTime()));
        if(post.getId().equals(expectedPost.getId())) {
            assertPostEquals(expectedPost, post);
        }
    }

    @Test
    final void testGetPostsWithCreated() {
        List<Post> returnedPost = postLogic.getPostsWithCreated(expectedPost.getCreated());
        Post post = returnedPost.get(0);
        post.setCreated(new Date(post.getCreated().getTime()));
        if(post.getId().equals(expectedPost.getId())) {
            assertPostEquals(expectedPost, post);
        }
    }
}
