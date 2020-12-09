package dal;

import entity.Comment;
import entity.Subreddit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubredditDAL extends GenericDAL<Subreddit> {

    public SubredditDAL() {
        super(Subreddit.class);
    }

    /**
     *  find all entity
     * @return list of all entity
     */
    @Override
    public List<Subreddit> findAll() {
        return findResults("Subreddit.findAll", null);
    }

    /**
     *  find by id
     * @param id
     * @return entity
     */
    @Override
    public Subreddit findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return findResult("Subreddit.findById", map);
    }

    /**
     *  find by name
     * @param name
     * @return entity
     */
    public Subreddit findByName(String name) {

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        return findResult("Subreddit.findByName", map);
    }

    /**
     *  find by url
     * @param url
     * @return entity
     */
    public Subreddit findByUrl(String url) {
        Map<String, Object> map = new HashMap<>();
        map.put("url", url);
        return findResult("Subreddit.findByUrl", map);
    }

    /**
     *  find by subscribers
     * @param subscribers
     * @return entity
     */
    public List<Subreddit> findBySubscribers(int subscribers) {
        Map<String, Object> map = new HashMap<>();
        map.put("subscribers", subscribers);
        return findResults("Subreddit.findBySubscribers", map);
    }
}
