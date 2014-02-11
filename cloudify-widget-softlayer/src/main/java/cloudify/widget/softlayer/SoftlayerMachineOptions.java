package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.MachineOptions;
import org.jclouds.compute.domain.OsFamily;

/**
 * User: evgenyf
 * Date: 2/9/14
 */
public class SoftlayerMachineOptions implements MachineOptions {

    private String name;
    private int machinesCount;
    private OsFamily osFamily;
    private String locationId;
    private String hardwareId;

   public SoftlayerMachineOptions(){

   }

    public SoftlayerMachineOptions setName(String name) {
        this.name = name;
        return this;
    }

    public String name() {
        return name;
    }

    public SoftlayerMachineOptions setMachinesCount( int machinesCount ){
        this.machinesCount = machinesCount;
        return this;
    }

    public int machinesCount() {
        return machinesCount;
    }

    public SoftlayerMachineOptions setLocationId( String locationId ){
        this.locationId = locationId;
        return this;
    }

    public String locationId(){
        return locationId;
    }

    public SoftlayerMachineOptions setHardwareId( String hardwareId ){
        this.hardwareId = hardwareId;
        return this;
    }

    public String hardwareId(){
        return hardwareId;
    }

    public SoftlayerMachineOptions setOsFamily( OsFamily osFamily ){
        this.osFamily = osFamily;
        return this;
    }

    public OsFamily osFamily(){
        return osFamily;
    }
}