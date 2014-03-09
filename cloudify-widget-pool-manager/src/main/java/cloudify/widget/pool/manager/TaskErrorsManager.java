package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.dto.TaskErrorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: eliranm
 * Date: 3/9/14
 * Time: 2:09 PM
 */
public class TaskErrorsManager {

    private static Logger logger = LoggerFactory.getLogger(TaskErrorsManager.class);

    @Autowired
    private TaskErrorsDao taskErrorsDao;


    public List<TaskErrorModel> listTaskErrors(PoolSettings poolSettings) {
        return taskErrorsDao.readAllOfPool(poolSettings.getId());
    }

    public TaskErrorModel getTaskError(long taskErrorId) {
        return taskErrorsDao.read(taskErrorId);
    }

    public boolean addTaskError(TaskErrorModel taskErrorModel) {
        return taskErrorsDao.create(taskErrorModel);
    }

    public int removeTaskError(long taskErrorId) {
        return taskErrorsDao.delete(taskErrorId);
    }

    public int updateTaskError(TaskErrorModel taskErrorModel) {
        return taskErrorsDao.update(taskErrorModel);
    }


    public void setTaskErrorsDao(TaskErrorsDao taskErrorsDao) {
        this.taskErrorsDao = taskErrorsDao;
    }
}
