package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.dto.TaskModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: eliranm
 * Date: 3/9/14
 * Time: 2:09 PM
 */
public class TasksDataAccessManager {

    @Autowired
    private ITasksDao tasksDao;


    public List<TaskModel> listTasks(PoolSettings poolSettings) {
        return tasksDao.getAllTasksForPool(poolSettings.getUuid());
    }

    public TaskModel getTask(long taskId) {
        return tasksDao.read(taskId);
    }

    public boolean addTask(TaskModel taskModel) {
        return tasksDao.create(taskModel);
    }

    public int removeTask(long taskId) {
        return tasksDao.delete(taskId);
    }

    public int updateTask(TaskModel taskModel) {
        return tasksDao.update(taskModel);
    }


    public void setTasksDao(TasksDao tasksDao) {
        this.tasksDao = tasksDao;
    }
}
