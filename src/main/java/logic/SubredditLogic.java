package logic;

import common.ValidationException;
import dal.SubredditDAL;
import entity.Account;
import entity.Post;
import entity.Subreddit;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

public class SubredditLogic extends GenericLogic<Subreddit, SubredditDAL> {

    public static final String SUBSCRIBERS = "subscribers";
    public static final String NAME = "name";
    public static final String URL = "url";
    public static final String ID = "id";

    public SubredditLogic() {
        super(new SubredditDAL());
    }

    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "subscribers", "name", "url");
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, NAME, URL, SUBSCRIBERS);
    }

    @Override
    public List<?> extractDataAsList(Subreddit subreddit) {
        return Arrays.asList(subreddit.getId(), subreddit.getName(),  subreddit.getUrl(), subreddit.getSubscribers());
    }

    @Override
    public Subreddit createEntity(Map<String, String[]> parameterMap) {

        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        Subreddit entity = new Subreddit();
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

        String name = parameterMap.get(NAME)[0];
        String url = parameterMap.get(URL)[0];
        String subscribers = parameterMap.get(SUBSCRIBERS)[0];

        //validate the data
        validator.accept(name, 100);
        validator.accept(url, 200);

        //set values on entity
        entity.setName(name);
        entity.setUrl(url);
        entity.setSubscribers(Integer.parseInt(subscribers));
        return entity;


    }

    @Override
    public List<Subreddit> getAll() {
        return get(() -> dal().findAll());
    }

    @Override
    public Subreddit getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    public Subreddit getSubredditWithName(String name) {
        return get(() -> dal().fidByName(name));
    }

    public Subreddit getSubredditWithUrl(String url) {
        return get(() ->  dal().findByUrl(url));
    }

    public List<Subreddit> getSubredditsWithSubscribers(int subscribers) {

        return get(() ->  dal().findBySubscribers(subscribers));
    }

}
