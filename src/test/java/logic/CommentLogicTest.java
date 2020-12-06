package logic;

import common.TomcatStartUp;
import dal.EMFactory;
import entity.*;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
        entity.setCreated(new Date());
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
        post.setCreated(new Date());
        post.setSubredditId(expectedSubreddit);
        post.setRedditAccountId(expectedRedditAccount);
        em = EMFactory.getEMF().createEntityManager();
        em.getTransaction().begin();
        expectedPost = em.merge(post);
        em.getTransaction().commit();
        em.close();

        // create Comment entity
        Comment comment = new Comment();
        comment.setCreated(new Date());
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
    void getColumnCodes() {
    }

    @Test
    void extractDataAsList() {
    }

    @Test
    void createEntity() {
    }

    @Test
    void getAll() {
    }

    @Test
    void getWithId() {
    }

    @Test
    void getCommentWithUniqueId() {
    }

    @Test
    void getCommentsWithText() {
    }

    @Test
    void getCommentsWithCreated() {
    }

    @Test
    void getCommentsWithPoints() {
    }

    @Test
    void getCommentsWithReplys() {
    }

    @Test
    void getCommentsWithIsReply() {
    }
}