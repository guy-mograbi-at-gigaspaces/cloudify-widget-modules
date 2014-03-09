package cloudify.widget.pool.manager.tasks;

import cloudify.widget.pool.manager.PoolManager;
import cloudify.widget.pool.manager.TaskErrorsManager;
import cloudify.widget.pool.manager.dto.PoolSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: eliranm
 * Date: 3/5/14
 * Time: 6:00 PM
 */
public class BootstrapMachinePoolTask implements PoolTask {

    private static Logger logger = LoggerFactory.getLogger(CreateMachinePoolTask.class);


    @Override
    public void run() {
        //        NodeModel updatedNodeModel = poolManager.getNode(taskData.getNodeModel().id);
//        logger.info("machine deleted, updating node model in the database [{}]", updatedNodeModel);
    }


    @Override
    public TaskName getTaskName() {
        return null;
    }

    @Override
    public void setPoolManager(PoolManager poolManager) {
    }

    @Override
    public void setTaskErrorsManager(TaskErrorsManager taskErrorsManager) {
    }

    @Override
    public void setPoolSettings(PoolSettings poolSettings) {
    }

    @Override
    public void setTaskData(TaskData taskData) {
    }
}
