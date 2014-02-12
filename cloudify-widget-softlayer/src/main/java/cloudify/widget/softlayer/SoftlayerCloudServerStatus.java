package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.ICloudServerStatus;

/**
 * User: eliranm
 * Date: 2/11/14
 * Time: 3:19 PM
 */
public enum SoftlayerCloudServerStatus implements ICloudServerStatus {

    // TODO list actual statuses from EC2 cloud API (is stopped a real status?)
    RUNNING, PENDING, STOPPED, UNRECOGNIZED;

    public String value() {
        return name();
    }

    public static SoftlayerCloudServerStatus fromValue(String v) {
        try {
            return valueOf(v.replaceAll("\\(.*", ""));
        } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
        }
    }
}
