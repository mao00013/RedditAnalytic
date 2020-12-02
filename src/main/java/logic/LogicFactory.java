package logic;

//TODO this class is just a skeleton it must be completed
public abstract class LogicFactory {

    public static < T> T getFor( String entityName ) {
        return (T)new AccountLogic();
    }
}
