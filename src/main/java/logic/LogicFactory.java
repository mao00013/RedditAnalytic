package logic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO this class is just a skeleton it must be completed
public abstract class LogicFactory {

    private static final String PACKAGE = "logic.";
    private static final String SUFFIX = "Logic";

    public LogicFactory() {
    }

    public static <T> T getFor(String entityName) {
        try {
            Class<?> type = (Class<T>) Class.forName(PACKAGE + entityName + SUFFIX);
            return (T) getFor(type);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LogicFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException(ex);
        }
    }

    public static <T> T getFor(Class<T> type) {
        try {

            Constructor<T> declaredConstructor = type.getDeclaredConstructor();
            return declaredConstructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(LogicFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException(ex);
        }
    }
}
