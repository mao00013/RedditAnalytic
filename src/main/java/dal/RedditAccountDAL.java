package dal;

import entity.RedditAccount;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedditAccountDAL extends GenericDAL<RedditAccount> {

    public RedditAccountDAL() {
        super(RedditAccount.class);
    }
    /**
     *  find all entity
     * @return list of entity
     */
    @Override
    public List<RedditAccount> findAll() {
        return findResults("RedditAccount.findAll", null);
    }
    /**
     *  find by id
     * @param id
     * @return entity
     */
    @Override
    public RedditAccount findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return findResult("RedditAccount.findById", map);
    }
    /**
     *  find by name
     * @param name
     * @return entity
     */
    public RedditAccount findByName(String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        return findResult("RedditAccount.findByName", map);
    }
    /**
     *  find by linkPoints
     * @param linkPoints
     * @return entity
     */
    public List<RedditAccount> findByLinkPoints(int linkPoints) {
        Map<String, Object> map = new HashMap<>();
        map.put("linkPoints", linkPoints);
        return findResults("RedditAccount.findByLinkPoints", map);
    }
    /**
     *  find by commentPoints
     * @param commentPoints
     * @return entity
     */
    public List<RedditAccount> findByCommentPoints(int commentPoints) {
        Map<String, Object> map = new HashMap<>();
        map.put("commentPoints", commentPoints);
        return findResults("RedditAccount.findByCommentPoints", map);
    }

    /**
     *  find by created
     * @param created
     * @return entity
     */
    public List<RedditAccount> findByCreated(Date created) {
        Map<String, Object> map = new HashMap<>();
        map.put("created", created);
        return findResults("RedditAccount.findByCreated", map);
    }
}
