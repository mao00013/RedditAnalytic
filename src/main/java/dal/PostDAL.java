package dal;

import entity.Account;
import entity.Post;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostDAL extends GenericDAL<Post> {


    public PostDAL() {
        super(Post.class);
    }
    /**
     *  find all entity
     * @return list of entity
     */
    @Override
    public List<Post> findAll() {
        return findResults("Post.findAll", null);
    }
    /**
     *  find by id
     * @param id
     * @return entity
     */
    @Override
    public Post findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return findResult("Post.findById", map);
    }
    /**
     *  find by uniqueId
     * @param uniqueId
     * @return entity
     */
    public Post findByUniqueId(String uniqueId) {
        Map<String, Object> map = new HashMap<>();
        map.put("uniqueId", uniqueId);
        return findResult("Post.findByUniqueId", map);
    }
    /**
     *  find by points
     * @param points
     * @return entity
     */
    public List<Post> findByPoints(int points) {
        Map<String, Object> map = new HashMap<>();
        map.put("points", points);
        return findResults("Post.findByPoints", map);
    }
    /**
     *  find by commentCount
     * @param commentCount
     * @return entity
     */
    public List<Post> findByCommentCount(int commentCount) {
        Map<String, Object> map = new HashMap<>();
        map.put("commentCount", commentCount);
        return findResults("Post.findByCommentCount", map);
    }
    /**
     *  find by title
     * @param title
     * @return entity
     */
    public List<Post> findByTitle(String title) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        return findResults("Post.findByTitle", map);
    }
    /**
     *  find by created
     * @param created
     * @return entity
     */
    public List<Post> findByCreated(Date created) {
        Map<String, Object> map = new HashMap<>();
        map.put("created", created);
        return findResults("Post.findByCreated", map);
    }
    /**
     *  find by id
     * @param id
     * @return entity
     */
    public List<Post> findByAuthor(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return findResults("Post.findByAuthor", map);
    }

}
