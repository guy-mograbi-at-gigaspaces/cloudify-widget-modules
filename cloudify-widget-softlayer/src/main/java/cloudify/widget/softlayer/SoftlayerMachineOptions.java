package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.MachineOptions;
import org.jclouds.compute.domain.OsFamily;

/**
 * User: evgenyf
 * Date: 2/9/14
 */
public class SoftlayerMachineOptions implements MachineOptions {

    private String tag;
    private int machinesCount;
    private OsFamily osFamily;
    private String locationId;
    private String hardwareId;



    // we moved the networkId to the ConnectDetails instead. We pass it to the context;
//    private String networkId;


    public SoftlayerMachineOptions() {

    }

    public SoftlayerMachineOptions setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public String name() {
        return tag + "-" + System.currentTimeMillis();
    }

    public SoftlayerMachineOptions setMachinesCount(int machinesCount) {
        this.machinesCount = machinesCount;
        return this;
    }

    public int machinesCount() {
        return machinesCount;
    }

    public SoftlayerMachineOptions setLocationId(String locationId) {
        this.locationId = locationId;
        return this;
    }

    public String locationId() {
        return locationId;
    }

    public SoftlayerMachineOptions setHardwareId(String hardwareId) {
        this.hardwareId = hardwareId;
        return this;
    }

    public String hardwareId() {
        return hardwareId;
    }

    public SoftlayerMachineOptions setOsFamily(OsFamily osFamily) {
        this.osFamily = osFamily;
        return this;
    }

    public OsFamily osFamily() {
        return osFamily;
    }

    public String getTag() {
        return tag;
    }

    public int getMachinesCount() {
        return machinesCount;
    }

    public OsFamily getOsFamily() {
        return osFamily;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getHardwareId() {
        return hardwareId;
    }
}