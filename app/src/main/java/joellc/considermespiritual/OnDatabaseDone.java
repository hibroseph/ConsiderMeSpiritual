package joellc.considermespiritual;

/**
 * Created by Joseph on 6/6/2018.
 * This interface helps us know when the database has finished it's task
 */
public interface OnDatabaseDone <T> {
    void onSuccess(T result);
}
