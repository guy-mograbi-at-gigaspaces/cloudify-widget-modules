package cloudify.widget.pool.manager.tasks;

import cloudify.widget.pool.manager.dto.NodeModel;

/**
 * User: eliranm
 * Date: 3/9/14
 * Time: 6:56 PM
 */
public interface DeleteMachineConfig extends TaskConfig {

    NodeModel getNodeModel();
}
