package cloudify.widget.pool.manager.tasks;

/**
 * User: eliranm
 * Date: 3/10/14
 * Time: 10:07 PM
 */
public class NoopTaskCallback implements TaskCallback {
    @Override
    public void onSuccess(Object result) {
    }
    @Override
    public void onFailure(Throwable t) {
    }
}
