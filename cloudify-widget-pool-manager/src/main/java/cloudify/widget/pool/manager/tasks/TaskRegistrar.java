package cloudify.widget.pool.manager.tasks;

import cloudify.widget.pool.manager.*;
import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.dto.TaskModel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Handles registration of tasks to the running-tasks data layer.
 * <p/>
 * A decorator is used to
 * perform registration before the decorated task {@code call()} is executed, and un-registration after
 * it is executed.
 */
public class TaskRegistrar {

    /**
     * Decorates tasks with data registration behavior.
     *
     * @param <C> The task config type.
     * @param <R> The expected callable response type.
     */
    public static abstract class Decorator<C extends TaskConfig, R> implements Task<C, R> {

        protected abstract void register();

        protected abstract void unregister();
    }

    /**
     * A decorator which facilitates a database for data registration.
     *
     * @param <C> The task config type.
     * @param <R> The expected callable response type.
     * @see Decorator
     */
    public static class DbDecorator<C extends TaskConfig, R> extends Decorator<C, R> {

        Task<C, R> _decorated;

        private C _taskConfig;

        private final PoolSettings _poolSettings;

        @Autowired
        public TasksDataAccessManager tasksDataAccessManager;
        private TaskModel _taskModel;

        public DbDecorator(Task<C, R> decorated, PoolSettings poolSettings) {
            this._decorated = decorated;
            this._poolSettings = poolSettings;
        }

        @Override
        protected void register() {
            _taskModel = new TaskModel()
                    .setTaskName(_decorated.getTaskName())
                    .setPoolId(_poolSettings.getId());
            if (NodeModelProvider.class.isAssignableFrom(_taskConfig.getClass())) {
                _taskModel.setNodeId(((NodeModelProvider) _taskConfig).getNodeModel().id);
            }
            tasksDataAccessManager.addTask(_taskModel);
        }

        @Override
        protected void unregister() {
            tasksDataAccessManager.removeTask(_taskModel.id);
        }

        @Override
        public R call() throws Exception {
            register();
            R call = _decorated.call();
            unregister();
            return call;
        }

        @Override
        public TaskName getTaskName() {
            return _decorated.getTaskName();
        }

        @Override
        public void setNodesDataAccessManager(NodesDataAccessManager nodesDataAccessManager) {
            _decorated.setNodesDataAccessManager(nodesDataAccessManager);
        }

        @Override
        public void setErrorsDataAccessManager(ErrorsDataAccessManager errorsDataAccessManager) {
            _decorated.setErrorsDataAccessManager(errorsDataAccessManager);
        }

        @Override
        public void setStatusManager(StatusManager statusManager) {
            _decorated.setStatusManager(statusManager);
        }

        @Override
        public void setPoolSettings(PoolSettings poolSettings) {
            _decorated.setPoolSettings(poolSettings);
        }

        @Override
        public void setTaskConfig(C taskConfig) {
            _taskConfig = taskConfig;
            _decorated.setTaskConfig(taskConfig);
        }
    }
}