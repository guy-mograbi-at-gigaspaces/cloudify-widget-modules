package cloudify.widget.pool.manager.tasks;

import cloudify.widget.pool.manager.dto.NodeModel;

/**
 * User: eliranm
 * Date: 3/9/14
 * Time: 7:01 PM
 */
public interface BootstrapMachineConfig extends TaskConfig {

    String getBootstrapScriptResourcePath();

    NodeModel getNodeModel();
}
