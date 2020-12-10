package dal;

import entity.Comment;
import entity.RedditAccount;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentDAL extends GenericDAL<Comment> {

    public CommentDAL() {
        super(Comment.class);
    }
    /**
     *  find all entity
     * @return list of entity
     */
    @Override
    public List<Comment> findAll() {
        return findResults("Comment.findAll", null);
    }

    /**
     *  find entity by id
     * @param id
     * @return
     */
    @Override
    public Comment findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return findResult("Comment.findById", map);
    }
    /**
     *  find by uniqueId
     * @param uniqueId
     * @return entity
     */
    public Comment findByUniqueId(String uniqueId) {
        Map<String, Object> map = new HashMap<>();
        map.put("uniqueId", uniqueId);
        return findResult("Comment.findByUniqueId", map);
    }
    /**
     *  find by text
     * @param text
     * @return entity
     */
    public List<Comment> findByText(String text) {
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
        return findResults("Comment.findByText", map);
    }
    /**
     *  find by created
     * @param created
     * @return entity
     */
    public List<Comment> findByCreated(Date created) {
        Map<String, Object> map = new HashMap<>();
        map.put("created", created);
        return findResults("Comment.findByCreated", map);
    }
    /**
     *  find by points
     * @param points
     * @return entity
     */
    public List<Comment> findByPoints(int points) {
        Map<String, Object> map = new HashMap<>();
        map.put("points", points);
        return findResults("Comment.findByPoints", map);
    }
    /**
     *  find by replys
     * @param replys
     * @return entity
     */
    public List<Comment> findByReplys(int replys) {
        Map<String, Object> map = new HashMap<>();
        map.put("replys", replys);
        return findResults("Comment.findByReplys", map);
    }
    /**
     *  find by isReply
     * @param isReply
     * @return entity
     */
    public List<Comment> findByIsReply(boolean isReply) {
        Map<String, Object> map = new HashMap<>();
        map.put("isReply", isReply);
        return findResults("Comment.findByIsReply", map);
    }
}
