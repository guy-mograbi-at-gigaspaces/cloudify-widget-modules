package cloudify.widget.pool.manager.settings.dto;

import cloudify.widget.softlayer.SoftlayerConnectDetails;
import cloudify.widget.softlayer.SoftlayerMachineOptions;

/**
* User: eliranm
* Date: 2/27/14
* Time: 3:23 PM
*/
public class SoftlayerProviderSettings extends ProviderSettings {

    public SoftlayerConnectDetails connectDetails;
    public SoftlayerMachineOptions machineOptions;

    public void setConnectDetails(SoftlayerConnectDetails connectDetails) {
        super.connectDetails = connectDetails;
    }

    public void setMachineOptions(SoftlayerMachineOptions machineOptions) {
        super.machineOptions = machineOptions;
    }
}
