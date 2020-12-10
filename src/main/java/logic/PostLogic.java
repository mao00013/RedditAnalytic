package logic;

import common.ValidationException;
import dal.PostDAL;
import entity.Post;
import entity.RedditAccount;
import entity.Subreddit;

import java.util.*;
import java.util.function.ObjIntConsumer;

public class PostLogic extends GenericLogic<Post, PostDAL> {

    public static final String UNIQUE_ID = "unique_id";
    public static final String ID = "id";
    public static final String POINTS = "points";
    public static final String COMMENT_COUNT = "comment_points";
    public static final String TITLE = "title";
    public static final String CREATED = "created";
    public static final String SUBREDDIT_ID = "subreddit_id";
    public static final String REDDIT_ACCOUNT_ID = "reddit_account_id";


    public PostLogic() {
        super(new PostDAL());
    }
    /**
     * getColumnNames
     *
     * @return columName list
     */
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "title", "created", "points","subreddit_id", "unique_id", "comment_count", "reddit_account_id");
    }
    /**
     * get Column Codes
     *
     * @return Colum code as list
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, TITLE, CREATED, POINTS, SUBREDDIT_ID, UNIQUE_ID, COMMENT_COUNT, REDDIT_ACCOUNT_ID);
    }
    /**
     * extract data as list
     *
     * @param post entity
     * @return entity value as list
     */
    @Override
    public List<?> extractDataAsList(Post post) {
        return Arrays.asList(post.getId(), post.getTitle(), post.getCreated(), post.getPoints(),  post.getSubredditId(), post.getUniqueID(), post.getCommentCount(), post.getRedditAccountId());
    }
    /**
     * create entity
     *
     * @param parameterMap entity value map
     * @return entity
     */
    @Override
    public Post createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");

        Post entity = new Post();
//        RedditAccount ra = new RedditAccount();
//        Subreddit s = new Subreddit();
        if (parameterMap.containsKey(ID)) {
            try {
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }

        ObjIntConsumer<String> validator = (value, length) -> {
            if (value == null || value.trim().isEmpty() || value.length() > length) {
                String error = "";
                if (value == null || value.trim().isEmpty()) {
                    error = "value cannot be null or empty: " + value;
                }
                if (value.length() > length) {
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException(error);
            }
        };
//        String reddit_account_id = parameterMap.get(REDDIT_ACCOUNT_ID)[0];
//        String subreddit_id = parameterMap.get(SUBREDDIT_ID)[0];
        String uniqueid = parameterMap.get(UNIQUE_ID)[0];
        String points = parameterMap.get(POINTS)[0];
        String comment_count = parameterMap.get(COMMENT_COUNT)[0];
        String title = parameterMap.get(TITLE)[0];
        String created = parameterMap.get(CREATED)[0];

        //validate the data
        validator.accept(uniqueid, 10);
        validator.accept(title, 255);
//        ra.setId(Integer.parseInt(reddit_account_id));
//        s.setId(Integer.parseInt(subreddit_id));


        //set values on entity
//        entity.setRedditAccountId(ra);
//        entity.setSubredditId(s);
        entity.setUniqueId(uniqueid);
        entity.setTitle(title);
        entity.setPoints(Integer.parseInt(points));
        entity.setCommentCount(Integer.parseInt(comment_count));
        entity.setCreated(convertStringToDate(created));
        return entity;
    }
    /**
     * get all entity
     * @return list of all entity
     */
    @Override
    public List<Post> getAll() {
        return get(() -> dal().findAll());
    }
    /**
     *  get entity with id
     * @param id
     * @return entity
     */
    @Override
    public Post getWithId(int id) {
        return get(() -> dal().findById(id));
    }
    /**
     *  get entity with uniqueId
     * @param uniqueId
     * @return entity
     */
    public Post getPostWithUniqueId(String uniqueId) {
        return get(() -> dal().findByUniqueId(uniqueId));
    }
    /**
     *  get entity with points
     * @param points
     * @return entity
     */
    public List<Post> getPostWithPoints(int points) {

        return get(() -> dal().findByPoints(points));
    }
    /**
     *  get entity with commentCount
     * @param commentCount
     * @return entity
     */
    public List<Post> getPostsWithCommentCount(int commentCount) {
        return get(() -> dal().findByCommentCount(commentCount));
    }
    /**
     *  get entity with id
     * @param id
     * @return entity
     */
    public List<Post> getPostsWithAuthorID(int id) {

        return get(() -> dal().findByAuthor(id));
    }
    /**
     *  get entity with title
     * @param title
     * @return entity
     */
    public List<Post> getPostsWithTitle(String title) {

        return get(()-> dal().findByTitle(title));
    }
    /**
     *  get entity with created
     * @param created
     * @return entity
     */
    public List<Post> getPostsWithCreated(Date created) {
        return get(()-> dal().findByCreated(created));
    }
}
