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

    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "unique_id", "points", "comment_points", "title", "created", "subreddit_id", "reddit_account_id");
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, UNIQUE_ID, POINTS, COMMENT_COUNT, TITLE, CREATED, SUBREDDIT_ID, REDDIT_ACCOUNT_ID);
    }

    @Override
    public List<?> extractDataAsList(Post post) {
        return Arrays.asList(post.getId(), post.getUniqueID(), post.getPoints(), post.getCommentCount(), post.getTitle(), post.getCreated(), post.getSubredditId(), post.getRedditAccountId());
    }

    @Override
    public Post createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");

        Post entity = new Post();
        RedditAccount ra = new RedditAccount();
        Subreddit s = new Subreddit();
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
        String reddit_account_id = parameterMap.get(REDDIT_ACCOUNT_ID)[0];
        String subreddit_id = parameterMap.get(SUBREDDIT_ID)[0];
        String uniqueid = parameterMap.get(UNIQUE_ID)[0];
        String points = parameterMap.get(POINTS)[0];
        String comment_count = parameterMap.get(COMMENT_COUNT)[0];
        String title = parameterMap.get(TITLE)[0];
        String created = parameterMap.get(CREATED)[0];

        //validate the data
        validator.accept(uniqueid, 10);
        validator.accept(title, 255);
        ra.setId(Integer.parseInt(reddit_account_id));
        s.setId(Integer.parseInt(subreddit_id));


        //set values on entity
        entity.setRedditAccountId(ra);
        entity.setSubredditId(s);
        entity.setUniqueId(uniqueid);
        entity.setTitle(title);
        entity.setPoints(Integer.parseInt(points));
        entity.setCommentCount(Integer.parseInt(comment_count));
        entity.setCreated(new Date(created));
        return entity;
    }

    @Override
    public List<Post> getAll() {
        return get(() -> dal().findAll());
    }

    @Override
    public Post getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    public Post getPostWithUniqueId(String uniqueId) {
        return get(() -> getPostWithUniqueId(uniqueId));
    }

    public List<Post> getPostWithPoints(int points) {
        return get(() -> getPostWithPoints(points));
    }

    public List<Post> getPostsWithCommentCount(int commentCount) {
        return get(() -> getPostsWithCommentCount(commentCount));
    }

    public List<Post> getPostsWithAuthorID(int id) {

        return get(() -> getPostsWithAuthorID(id));
    }

    public List<Post> getPostsWithTitle(String title) {

        return get(()->getPostsWithTitle(title));
    }

    public List<Post> getPostsWithCreated(Date created) {
        return get(()->getPostsWithCreated(created));
    }
}
