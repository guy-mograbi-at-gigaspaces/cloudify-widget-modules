package cloudify.widget.pool.manager.dto;

import cloudify.widget.pool.manager.tasks.TaskName;

/**
 * User: eliranm
 * Date: 3/9/14
 * Time: 2:15 PM
 */
public class TaskErrorModel {

    public static final int INITIAL_ID = -1;

    public long id = INITIAL_ID;
    public TaskName taskName;
    public String poolId;
    public String message;
    public String info;

    public TaskErrorModel setId(long id) {
        this.id = id;
        return this;
    }

    public TaskErrorModel setTaskName(TaskName taskName) {
        this.taskName = taskName;
        return this;
    }

    public TaskErrorModel setPoolId(String poolId) {
        this.poolId = poolId;
        return this;
    }

    public TaskErrorModel setMessage(String message) {
        this.message = message;
        return this;
    }

    public TaskErrorModel setInfo(String info) {
        this.info = info;
        return this;
    }

    @Override
    public String toString() {
        return "TaskErrorModel{" +
                "id=" + id +
                ", taskName=" + taskName +
                ", poolId='" + poolId + '\'' +
                ", message='" + message + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
