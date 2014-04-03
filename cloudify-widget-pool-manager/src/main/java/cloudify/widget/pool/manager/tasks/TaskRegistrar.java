package cloudify.widget.pool.manager.tasks;

import cloudify.widget.pool.manager.*;
import cloudify.widget.pool.manager.dto.ErrorModel;
import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.dto.TaskModel;
import com.google.common.collect.Maps;

import java.util.HashMap;

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
    public static abstract class TaskDecorator<C extends TaskConfig, R> implements Task<C, R> {

        protected abstract void registerTask();

        protected abstract void unregisterTask();

        public abstract void setTasksDao(ITasksDao tasksDao);
    }

    public static abstract class TaskCallbackDecorator<R> implements TaskCallback<R> {

        protected abstract void registerError(Throwable thrown);

        public abstract void setErrorsDao(ErrorsDao errorsDao);

        public abstract void setPoolSettings(PoolSettings poolSettings);
    }

    /**
     * A decorator which facilitates a DAO for data registration.
     *
     * @param <C> The task config type.
     * @param <R> The expected callable response type.
     * @see cloudify.widget.pool.manager.tasks.TaskRegistrar.TaskDecorator
     */
    public static class TaskDecoratorImpl<C extends TaskConfig, R> extends TaskDecorator<C, R> {

        Task<C, R> _decorated;

        private C _taskConfig;

        private PoolSettings _poolSettings;

        private TaskModel _taskModel;

        private ITasksDao _tasksDao;

        public TaskDecoratorImpl(Task<C, R> decorated) {
            _decorated = decorated;
        }

        @Override
        protected void registerTask() {
            _taskModel = new TaskModel()
                    .setTaskName(_decorated.getTaskName())
                    .setPoolId(_poolSettings.getUuid());
            if (_taskConfig != null && NodeModelProvider.class.isAssignableFrom(_taskConfig.getClass())) {
                _taskModel.setNodeId(((NodeModelProvider) _taskConfig).getNodeModel().id);
            }
            _tasksDao.create(_taskModel);
        }

        @Override
        protected void unregisterTask() {
            _tasksDao.delete(_taskModel.id);
        }

        @Override
        public void setTasksDao(ITasksDao tasksDao) {
            _tasksDao = tasksDao;
        }

        @Override
        public R call() throws Exception {
            registerTask();
            try {
                R call = _decorated.call();
                unregisterTask();

                return call;
            }catch(Exception e) {
                unregisterTask();
                throw e;
            }
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

    public static class TaskCallbackDecoratorImpl<R> extends TaskCallbackDecorator<R> {

        private TaskCallback<R> _decorated;

        private ErrorsDao _errorsDao;

        private PoolSettings _poolSettings;

        public TaskCallbackDecoratorImpl(TaskCallback<R> decorated) {
            _decorated = decorated;
        }

        @Override
        protected void registerError(Throwable thrown) {
            HashMap<String, Object> infoMap = Maps.newHashMap();
            infoMap.put("stackTrace", "i am the stack trace"); // testing, testing
            _errorsDao.create(new ErrorModel()
//                            .setTaskName(/*_decorated.getTaskName()*/) // TODO get the real task name
//                            .setMessage(thrown.getMessage()) // TODO enalrge column data length
                            .setMessage(thrown.getMessage().substring(0, 200))
                            .setInfoFromMap(infoMap)
                            .setPoolId(_poolSettings.getUuid())
            );
        }

        @Override
        public void onSuccess(R result) {
            _decorated.onSuccess(result);
        }

        @Override
        public void onFailure(Throwable t) {
            _decorated.onFailure(t);
            registerError(t);
        }

        @Override
        public void setErrorsDao(ErrorsDao errorsDao) {
            _errorsDao = errorsDao;
        }

        public void setPoolSettings(PoolSettings poolSettings) {
            _poolSettings = poolSettings;
        }
    }
}