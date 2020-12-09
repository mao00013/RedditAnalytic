package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.*;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import static org.junit.jupiter.api.Assertions.*;

class CommentLogicTest {
    private CommentLogic commentLogic;
    private Comment expectedComment;
    private RedditAccount expectedRedditAccount;
    private RedditAccountLogic redditAccountLogic;
    private Post expectedPost;
    private PostLogic postLogic;
    private Subreddit expectedSubreddit;
    private SubredditLogic subredditLogic;

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

        commentLogic = LogicFactory.getFor( "Comment" );
        postLogic = LogicFactory.getFor("Post");
        subredditLogic = LogicFactory.getFor("Subreddit");
        redditAccountLogic = LogicFactory.getFor("RedditAccount");

        // clean up
        for(Comment comment : commentLogic.getAll())
            commentLogic.delete(comment);
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
    final void tearDown() {
        if( expectedComment != null ){
            commentLogic.delete( expectedComment );
        }
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
        List<String> stringList = commentLogic.getColumnNames();
        assertEquals( Arrays.asList("ID", "text", "created", "points", "replys", "is_reply", "unique_id", "post_id", "reddit_account_id"), stringList );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = commentLogic.getColumnCodes();
        assertEquals(Arrays.asList(CommentLogic.ID, CommentLogic.TEXT, CommentLogic.CREATED, CommentLogic.POINTS, CommentLogic.REPLYS, CommentLogic.ISREPLY, CommentLogic.UNIQUEID, CommentLogic.POST_ID, CommentLogic.REDDIT_ACCOUNT_ID), list);
    }

    @Test
    final void testExtractDataAsList() {
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
    final void testCreateEntity() {
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

    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(CommentLogic.TEXT, new String[]{"Test Create Entity"});
        sampleMap.put(CommentLogic.POINTS, new String[]{Integer.toString(1)});
        sampleMap.put(CommentLogic.ISREPLY, new String[]{"1"});
        sampleMap.put(CommentLogic.REPLYS, new String[]{Integer.toString(1)});
        sampleMap.put(CommentLogic.UNIQUEID, new String[]{"uid"});
        sampleMap.put(CommentLogic.REDDIT_ACCOUNT_ID, new String[] {expectedComment.getRedditAccountId().getId().toString()});
        sampleMap.put(CommentLogic.POST_ID, new String[] {expectedComment.getPostId().getId().toString()});
        sampleMap.put(CommentLogic.CREATED, new String[] {expectedComment.getCreated().toString()});
        Comment returnedCommet = commentLogic.createEntity(sampleMap);
        commentLogic.add(returnedCommet);
        returnedCommet = commentLogic.getCommentWithUniqueId(returnedCommet.getUniqueId());
        returnedCommet.setCreated(new Date(returnedCommet.getCreated().getTime()));
        assertEquals(sampleMap.get(CommentLogic.UNIQUEID)[0], returnedCommet.getUniqueId());

        commentLogic.delete(returnedCommet);
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map ) -> {
            map.clear();
            map.put( CommentLogic.ID, new String[]{ Integer.toString( expectedComment.getId() ) } );
            map.put( CommentLogic.TEXT, new String[]{expectedComment.getText() } );
            map.put(CommentLogic.POINTS, new String[]{Integer.toString(expectedComment.getPoints())} );
            map.put(CommentLogic.UNIQUEID, new String[]{expectedComment.getUniqueId()});
            map.put(CommentLogic.ISREPLY, new String[]{expectedComment.getIsReply()? "1" : "0"});
            map.put(CommentLogic.CREATED, new String[]{expectedComment.getCreated().toString()});
            map.put(CommentLogic.REPLYS, new String[]{Integer.toString(expectedComment.getReplys())});
            map.put(CommentLogic.POST_ID, new String[]{expectedComment.getPostId().getId().toString()});
            map.put(CommentLogic.REDDIT_ACCOUNT_ID, new String[]{expectedComment.getRedditAccountId().getId().toString()});
        };

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.ID, null);
        assertThrows(NullPointerException.class, () -> commentLogic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> commentLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.TEXT, null);
        assertThrows(NullPointerException.class, () -> commentLogic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.TEXT, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> commentLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.CREATED, null);
        assertThrows(NullPointerException.class, () -> commentLogic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.CREATED, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> commentLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.POST_ID, null);
        assertThrows(NullPointerException.class, () -> commentLogic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.POST_ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> commentLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.POINTS, null);
        assertThrows(NullPointerException.class, () -> commentLogic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.POINTS, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> commentLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.REPLYS, null);
        assertThrows(NullPointerException.class, () -> commentLogic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.REPLYS, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> commentLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.UNIQUEID, null);
        assertThrows(NullPointerException.class, () -> commentLogic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.UNIQUEID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> commentLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.REDDIT_ACCOUNT_ID, null);
        assertThrows(NullPointerException.class, () -> commentLogic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.REDDIT_ACCOUNT_ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> commentLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.ISREPLY, null);
        assertThrows(NullPointerException.class, () -> commentLogic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.ISREPLY, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> commentLogic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map ) -> {
            map.clear();
            map.put( CommentLogic.ID, new String[]{ Integer.toString( expectedComment.getId() ) } );
            map.put( CommentLogic.TEXT, new String[]{expectedComment.getText() } );
            map.put(CommentLogic.POINTS, new String[]{Integer.toString(expectedComment.getPoints())} );
            map.put(CommentLogic.UNIQUEID, new String[]{expectedComment.getUniqueId()});
            map.put(CommentLogic.ISREPLY, new String[]{expectedComment.getIsReply()? "1" : "0"});
            map.put(CommentLogic.CREATED, new String[]{expectedComment.getCreated().toString()});
            map.put(CommentLogic.REPLYS, new String[]{Integer.toString(expectedComment.getReplys())});
            map.put(CommentLogic.POST_ID, new String[]{expectedComment.getPostId().getId().toString()});
            map.put(CommentLogic.REDDIT_ACCOUNT_ID, new String[]{expectedComment.getRedditAccountId().getId().toString()});
        };

        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            //from 97 inclusive to 123 exclusive
            return new Random().ints( 'a', 'z' + 1 ).limit( length )
                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                    .toString();
        };

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.ID, new String[] {""});
        assertThrows(ValidationException.class, () -> commentLogic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.ID, new String[]{"88a"});
        assertThrows(ValidationException.class, () -> commentLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.TEXT, new String[]{""});
        assertThrows(ValidationException.class, () -> commentLogic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.TEXT, new String[]{generateString.apply(10001)});
        assertThrows(ValidationException.class, () -> commentLogic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.UNIQUEID, new String[]{""});
        assertThrows(ValidationException.class, () -> commentLogic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.UNIQUEID, new String[]{generateString.apply(11)});
        assertThrows(ValidationException.class, () -> commentLogic.createEntity(sampleMap));
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
        sampleMap.put(CommentLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(CommentLogic.TEXT, new String[]{generateString.apply(1)});
        sampleMap.put(CommentLogic.ISREPLY, new String[]{"1"});
        sampleMap.put(CommentLogic.POINTS, new String[]{Integer.toString(1)});
        sampleMap.put(CommentLogic.UNIQUEID, new String[]{generateString.apply(1)});
        sampleMap.put(CommentLogic.REPLYS, new String[]{Integer.toString(1)});
        sampleMap.put(CommentLogic.POST_ID, new String[]{expectedComment.getPostId().getId().toString()});
        sampleMap.put(CommentLogic.REDDIT_ACCOUNT_ID, new String[]{expectedComment.getRedditAccountId().getId().toString()});
        sampleMap.put(CommentLogic.CREATED, new String[]{(expectedComment.getCreated().toString())});
        //idealy every test should be in its own method
        Comment returnedComment = commentLogic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(CommentLogic.ID)[0]), returnedComment.getId());
        assertEquals(sampleMap.get(CommentLogic.TEXT)[0], returnedComment.getText());
        assertEquals(sampleMap.get(CommentLogic.CREATED)[0], returnedComment.getCreated().toString());
        assertEquals(sampleMap.get(CommentLogic.POST_ID)[0], returnedComment.getPostId().getId().toString());
        assertEquals(Integer.parseInt(sampleMap.get(CommentLogic.POINTS)[0]), returnedComment.getPoints());
        assertEquals(Integer.parseInt(sampleMap.get(CommentLogic.REPLYS)[0]), returnedComment.getReplys());
        assertEquals(sampleMap.get(CommentLogic.ISREPLY)[0], returnedComment.getIsReply()? "0":"1");
        assertEquals(sampleMap.get(CommentLogic.UNIQUEID)[0], returnedComment.getUniqueId());
        assertEquals(sampleMap.get(CommentLogic.REDDIT_ACCOUNT_ID)[0], returnedComment.getRedditAccountId().getId().toString());

        sampleMap = new HashMap<>();
        sampleMap.put(CommentLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(CommentLogic.TEXT, new String[]{generateString.apply(10000)});
        sampleMap.put(CommentLogic.UNIQUEID, new String[]{generateString.apply(10)});
        sampleMap.put(CommentLogic.ISREPLY, new String[]{"1"});
        sampleMap.put(CommentLogic.REPLYS, new String[]{Integer.toString(1)});
        sampleMap.put(CommentLogic.POST_ID, new String[]{expectedComment.getPostId().getId().toString()});
        sampleMap.put(CommentLogic.REDDIT_ACCOUNT_ID, new String[]{expectedComment.getRedditAccountId().getId().toString()});
        sampleMap.put(CommentLogic.CREATED, new String[]{(expectedComment.getCreated().toString())});
        sampleMap.put(CommentLogic.POINTS, new String[]{Integer.toString(1)});

        returnedComment = commentLogic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(CommentLogic.ID)[0]), returnedComment.getId());
        assertEquals(sampleMap.get(CommentLogic.TEXT)[0], returnedComment.getText());
        assertEquals(sampleMap.get(CommentLogic.CREATED)[0], returnedComment.getCreated().toString());
        assertEquals(sampleMap.get(CommentLogic.POST_ID)[0], returnedComment.getPostId().getId().toString());
        assertEquals(Integer.parseInt(sampleMap.get(CommentLogic.POINTS)[0]), returnedComment.getPoints());
        assertEquals(Integer.parseInt(sampleMap.get(CommentLogic.REPLYS)[0]), returnedComment.getReplys());
        assertEquals(sampleMap.get(CommentLogic.ISREPLY)[0], returnedComment.getIsReply()? "0":"1");
        assertEquals(sampleMap.get(CommentLogic.UNIQUEID)[0], returnedComment.getUniqueId());
        assertEquals(sampleMap.get(CommentLogic.REDDIT_ACCOUNT_ID)[0], returnedComment.getRedditAccountId().getId().toString());
    }


    /**
     * helper method for testing all account fields
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
    final void testGetAll() {
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
    final void testGetWithId() {
        Comment returnedComment = commentLogic.getWithId( expectedComment.getId() );
        returnedComment.setCreated(new Date(returnedComment.getCreated().getTime()));
        assertCommentEquals( expectedComment, returnedComment );
    }

    @Test
    final void testGetCommentWithUniqueId() {
        Comment returnedComment = commentLogic.getCommentWithUniqueId(expectedComment.getUniqueId());
        returnedComment.setCreated(new Date(returnedComment.getCreated().getTime()));
        assertCommentEquals(expectedComment, returnedComment);
    }

    @Test
    final void testGetCommentsWithText() {
        List<Comment> returnedComments = commentLogic.getCommentsWithText(expectedComment.getText());
        Comment comment = returnedComments.get(0);
        comment.setCreated(new Date(comment.getCreated().getTime()));
        if(comment.getId().equals(expectedComment.getId())){
            assertCommentEquals(expectedComment, comment);
        }
    }


    @Test
    final void testGetCommentsWithCreated() {
        List<Comment> returnedComments = commentLogic.getCommentsWithCreated(expectedComment.getCreated());
        Comment comment = returnedComments.get(0);
        comment.setCreated(new Date(comment.getCreated().getTime()));
        if(comment.getId().equals(expectedComment.getId())){
            assertCommentEquals(expectedComment, comment);
        }
    }

    @Test
    final void testGetCommentsWithPoints() {
        List<Comment> returnedComments = commentLogic.getCommentsWithPoints(expectedComment.getPoints());
        Comment comment = returnedComments.get(0);
        comment.setCreated(new Date(comment.getCreated().getTime()));
        if(comment.getId().equals(expectedComment.getId())){
            assertCommentEquals(expectedComment, comment);
        }
    }

    @Test
    final void testGetCommentsWithReplys() {
        List<Comment> returnedComments = commentLogic.getCommentsWithReplys(expectedComment.getReplys());
        Comment comment = returnedComments.get(0);
        comment.setCreated(new Date(comment.getCreated().getTime()));
        if(comment.getId().equals(expectedComment.getId())){
            assertCommentEquals(expectedComment, comment);
        }
    }

    @Test
    final void testGetCommentsWithIsReply() {
        List<Comment> returnedComments = commentLogic.getCommentsWithIsReply(expectedComment.getIsReply());
        Comment comment = returnedComments.get(0);
        comment.setCreated(new Date(comment.getCreated().getTime()));
        if(comment.getId().equals(expectedComment.getId())){
            assertCommentEquals(expectedComment, comment);
        }
    }
}