package cloudify.widget.hpcloud;

import cloudify.widget.api.clouds.ICloudServerStatus;

/**
 * User: eliranm
 * Date: 2/11/14
 * Time: 4:00 PM
 */
public enum HpCloudServerStatus implements ICloudServerStatus {

    // TODO list actual statuses from HP cloud API
    ACTIVE, BUILD, REBUILD, SUSPENDED, PAUSED, RESIZE, VERIFY_RESIZE, REVERT_RESIZE,
    PASSWORD, REBOOT, HARD_REBOOT, DELETED, UNKNOWN, ERROR, STOPPED, UNRECOGNIZED;

    public String value() {
        return name();
    }

    public static HpCloudServerStatus fromValue(String v) {
        try {
            return valueOf(v.replaceAll("\\(.*", ""));
        } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
        }
    }

}
