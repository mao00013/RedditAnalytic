package logic;

import common.TomcatStartUp;
import dal.EMFactory;
import entity.*;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CommentLogicTest {
    private CommentLogic commentLogic;
    private Comment expectedComment;
    private RedditAccount expectedRedditAccount;
    private RedditAccountLogic redditAccountLogic;
    private Post expectedPost;
    private PostLogic postLogic;
    private Subreddit expectedSubreddit;
    private SuberdditLogic suberdditLogic;

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

        commentLogic = LogicFactory.getFor( "Comment" );
        postLogic = LogicFactory.getFor("Post");
        suberdditLogic = LogicFactory.getFor("Suberddit");
        redditAccountLogic = LogicFactory.getFor("RedditAccount");

        // clean up
        for(Comment comment : commentLogic.getAll())
            commentLogic.delete(comment);
        for(Post post: postLogic.getAll())
            postLogic.delete(post);
        for(Subreddit subreddit: suberdditLogic.getAll())
            suberdditLogic.delete(subreddit);
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
        post.setUniqueId("post");
        post.setPoints(2);
        post.setCommentCount(2);
        post.setTitle("post_title");
        post.setCreated(new Date(2020, 12, 1));
        post.setSubredditId(expectedSubreddit);
        post.setRedditAccountId(expectedRedditAccount);
        em = EMFactory.getEMF().createEntityManager();
        em.getTransaction().begin();
        expectedPost = em.merge(post);
        em.getTransaction().commit();
        em.close();

        // create Comment entity
        Comment comment = new Comment();
        comment.setCreated(new Date(2020, 12, 2));
        comment.setIsReply(true);
        comment.setPoints(9);
        comment.setPostId(expectedPost);
        comment.setRedditAccountId(new RedditAccount(expectedRedditAccount.getId()));
        comment.setReplys(5);
        comment.setText("abcd");
        comment.setUniqueId("uuid");

        em = EMFactory.getEMF().createEntityManager();
        em.getTransaction().begin();
        expectedComment = em.merge( comment );
        em.getTransaction().commit();
        em.close();
    }

    @AfterEach
    void tearDown() {
        if( expectedComment != null ){
            commentLogic.delete( expectedComment );
        }
        if(expectedPost != null) {
            postLogic.delete(expectedPost);
        }
        if(expectedSubreddit != null) {
            suberdditLogic.delete(expectedSubreddit);
        }
        if(expectedRedditAccount != null) {
            redditAccountLogic.delete(expectedRedditAccount);
        }
    }

    @Test
    void testGetColumnNames() {
        List<String> stringList = commentLogic.getColumnNames();
        assertEquals( Arrays.asList("ID", "text", "created", "points", "replys", "is_reply", "unique_id", "post_id", "reddit_account_id"), stringList );
    }

    @Test
    void testGetColumnCodes() {
        List<String> list = commentLogic.getColumnCodes();
        assertEquals(Arrays.asList(CommentLogic.ID, CommentLogic.TEXT, CommentLogic.CREATED, CommentLogic.POINTS, CommentLogic.REPLYS, CommentLogic.ISREPLY, CommentLogic.UNIQUEID, CommentLogic.POST_ID, CommentLogic.REDDIT_ACCOUNT_ID), list);
    }

    @Test
    void testExtractDataAsList() {
        List<?> list = commentLogic.extractDataAsList( expectedComment );
        assertEquals(expectedComment.getId(), list.get(0));
        assertEquals(expectedComment.getText(), list.get(1));
        assertEquals(expectedComment.getCreated(), list.get(2));
        assertEquals(expectedComment.getPoints(), list.get(3));
        assertEquals(expectedComment.getReplys(), list.get(4));
        assertEquals(expectedComment.getIsReply(), list.get(5));
        assertEquals(expectedComment.getUniqueId(), list.get(6));
        assertEquals(expectedComment.getPostId().getId(), ((Post)list.get(7)).getId());
        assertEquals(expectedComment.getRedditAccountId().getId(), ((RedditAccount) list.get(8)).getId());
    }

    @Test
    void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(CommentLogic.ID, new String[] {Integer.toString(expectedComment.getId())});
        sampleMap.put(CommentLogic.TEXT, new String[] {expectedComment.getText()});
        sampleMap.put(CommentLogic.CREATED, new String[] {expectedComment.getCreated().toString()});
        sampleMap.put(CommentLogic.POINTS, new String[] {Integer.toString(expectedComment.getPoints())});
        sampleMap.put(CommentLogic.REPLYS, new String[] {Integer.toString(expectedComment.getReplys())});
        sampleMap.put(CommentLogic.UNIQUEID, new String[] {expectedComment.getUniqueId()});
        sampleMap.put(CommentLogic.POST_ID, new String[] {expectedComment.getPostId().getId().toString()});
        sampleMap.put(CommentLogic.REDDIT_ACCOUNT_ID, new String[] {expectedComment.getRedditAccountId().getId().toString()});
        sampleMap.put(CommentLogic.ISREPLY, new String[] {expectedComment.getIsReply()?"1":"0"});

        Comment returnedComment = commentLogic.createEntity(sampleMap);
        assertCommentEquals(expectedComment, returnedComment);
    }
    /**
     * helper method for testing all comment fields
     *
     * @param expected
     * @param actual
     */
    private void assertCommentEquals( Comment expected, Comment actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getText(), actual.getText() );

        assertEquals( expected.getCreated().toString(), actual.getCreated().toString() );
        assertEquals( expected.getPoints(), actual.getPoints() );
        assertEquals( expected.getReplys(), actual.getReplys() );
        assertEquals( expected.getUniqueId(), actual.getUniqueId() );
        assertEquals( expected.getPostId().getId(), actual.getPostId().getId() );
        assertEquals( expected.getRedditAccountId().getId(), actual.getRedditAccountId().getId() );
    }

    @Test
    void testGetAll() {
        //get all the comments from the DB
        List<Comment> list = commentLogic.getAll();
        //store the size of list, this way we know how many comments exits in DB
        int originalSize = list.size();
        //make sure comment was created successfully
        assertNotNull(expectedComment);
        commentLogic.delete(expectedComment);
        //get all comments again
        list = commentLogic.getAll();
        //the new size of comments must be one less
        assertEquals(originalSize -1, list.size());
    }

    @Test
    void testGetWithId() {
        //using the id of test comment get another comment from logic
        Comment returnedComment = commentLogic.getWithId( expectedComment.getId() );
        //the two comment (testComment and returnedComment) must be the same
        returnedComment.setCreated(new Date(returnedComment.getCreated().getTime()));
        assertCommentEquals( expectedComment, returnedComment );
    }

    @Test
    void testGetCommentWithUniqueId() {
        Comment returnedComment = commentLogic.getCommentWithUniqueId(expectedComment.getUniqueId());
        returnedComment.setCreated(new Date(returnedComment.getCreated().getTime()));
        assertCommentEquals(expectedComment, returnedComment);
    }

    @Test
    void getCommentsWithText() {
        List<Comment> returnedComments = commentLogic.getCommentsWithText(expectedComment.getText());
        Comment comment = returnedComments.get(0);
        comment.setCreated(new Date(comment.getCreated().getTime()));
        if(comment.getId().equals(expectedComment.getId())){
            assertCommentEquals(expectedComment, comment);
        }
    }


    @Test
    void testGetCommentsWithCreated() {
        List<Comment> returnedComments = commentLogic.getCommentsWithCreated(expectedComment.getCreated());
        Comment comment = returnedComments.get(0);
        comment.setCreated(new Date(comment.getCreated().getTime()));
        if(comment.getId().equals(expectedComment.getId())){
            assertCommentEquals(expectedComment, comment);
        }
    }

    @Test
    void getCommentsWithPoints() {
        List<Comment> returnedComments = commentLogic.getCommentsWithPoints(expectedComment.getPoints());
        Comment comment = returnedComments.get(0);
        comment.setCreated(new Date(comment.getCreated().getTime()));
        if(comment.getId().equals(expectedComment.getId())){
            assertCommentEquals(expectedComment, comment);
        }
    }

    @Test
    void getCommentsWithReplys() {
        List<Comment> returnedComments = commentLogic.getCommentsWithReplys(expectedComment.getReplys());
        Comment comment = returnedComments.get(0);
        comment.setCreated(new Date(comment.getCreated().getTime()));
        if(comment.getId().equals(expectedComment.getId())){
            assertCommentEquals(expectedComment, comment);
        }
    }

    @Test
    void getCommentsWithIsReply() {
        List<Comment> returnedComments = commentLogic.getCommentsWithIsReply(expectedComment.getIsReply());
        Comment comment = returnedComments.get(0);
        comment.setCreated(new Date(comment.getCreated().getTime()));
        if(comment.getId().equals(expectedComment.getId())){
            assertCommentEquals(expectedComment, comment);
        }
    }
}