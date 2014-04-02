package cloudify.widget.pool.manager.tasks;

import com.google.common.util.concurrent.FutureCallback;

/**
 * a tag interface, only to serve as an abstraction layer on top of guava's future callbacks
 */
public interface TaskCallback<T> extends FutureCallback<T> {
}
