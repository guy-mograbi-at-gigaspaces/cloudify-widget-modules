package cloudify.widget.pool.manager.tasks;

import cloudify.widget.pool.manager.*;
import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.dto.TaskModel;

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

        public abstract void setTasksDao(ITasksDao tasksDao);
    }

    /**
     * A decorator which facilitates a DAO for data registration.
     *
     * @param <C> The task config type.
     * @param <R> The expected callable response type.
     * @see Decorator
     */
    public static class DecoratorImpl<C extends TaskConfig, R> extends Decorator<C, R> {

        Task<C, R> _decorated;

        private C _taskConfig;

        private PoolSettings _poolSettings;

        private TaskModel _taskModel;

        private ITasksDao _tasksDao;

        public DecoratorImpl(Task<C, R> decorated) {
            this._decorated = decorated;
        }

        @Override
        protected void register() {
            _taskModel = new TaskModel()
                    .setTaskName(_decorated.getTaskName())
                    .setPoolId(_poolSettings.getUuid());
            if (_taskConfig != null && NodeModelProvider.class.isAssignableFrom(_taskConfig.getClass())) {
                _taskModel.setNodeId(((NodeModelProvider) _taskConfig).getNodeModel().id);
            }
            _tasksDao.create(_taskModel);
        }

        @Override
        protected void unregister() {
            _tasksDao.delete(_taskModel.id);
        }

        @Override
        public void setTasksDao(ITasksDao tasksDao) {
            this._tasksDao = tasksDao;
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
        public void setPoolSettings(PoolSettings poolSettings) {
            _poolSettings = poolSettings;
            _decorated.setPoolSettings(poolSettings);
        }

        @Override
        public void setTaskConfig(C taskConfig) {
            _taskConfig = taskConfig;
            _decorated.setTaskConfig(taskConfig);
        }
    }
}