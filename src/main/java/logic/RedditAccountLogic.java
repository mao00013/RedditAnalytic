package logic;

import common.ValidationException;
import dal.RedditAccountDAL;
import entity.Post;
import entity.RedditAccount;
import entity.Subreddit;

import java.util.*;
import java.util.function.ObjIntConsumer;

public class RedditAccountLogic extends GenericLogic<RedditAccount, RedditAccountDAL> {
    public static final String NAME = "name";
    public static final String LINKPOINTS = "link_points";
    public static final String COMMENTPOINTS = "comment_points";
    public static final String CREATED = "created";
    public static final String ID = "id";

    RedditAccountLogic() {
        super(new RedditAccountDAL());
    }

    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "name", "link_points", "comment_points", "created");
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, NAME, LINKPOINTS, COMMENTPOINTS, CREATED);
    }

    @Override
    public List<?> extractDataAsList(RedditAccount redditAccount) {
        return Arrays.asList(redditAccount.getId(), redditAccount.getName(), redditAccount.getLinkPoints(), redditAccount.getCommentPoints(), redditAccount.getCreated());
    }

    @Override
    public RedditAccount createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");

        RedditAccount entity = new RedditAccount();
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
        String link_points = parameterMap.get(LINKPOINTS)[0];
        String comment_points = parameterMap.get(COMMENTPOINTS)[0];
        String created = parameterMap.get(CREATED)[0];

        //validate the data
        validator.accept(name, 100);


        //set values on entity
        entity.setName(name);
        entity.setLinkPoints(Integer.parseInt(link_points));
        entity.setCommentPoints(Integer.parseInt(comment_points));
        entity.setCreated(new Date(created));
        return entity;

    }

    @Override
    public List<RedditAccount> getAll() {
        return get(() -> dal().findAll());
    }

    @Override
    public RedditAccount getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    public RedditAccount getRedditAccountWithName(String name) {
        return get(() -> getRedditAccountWithName(name));
    }

    public List<RedditAccount> getRedditAccountsWithLinkPoints(int linkPoints) {
        return get(() -> getRedditAccountsWithLinkPoints(linkPoints));
    }

    public List<RedditAccount> getRedditAccountsWithCommentPoints(int commentPoints) {
        return get(() -> getRedditAccountsWithCommentPoints(commentPoints));
    }

    public List<RedditAccount> getRedditAccountsWithCreated(Date created) {
        return get(() -> getRedditAccountsWithCreated(created));
    }


}
