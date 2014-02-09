package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.MachineOptions;
import org.jclouds.compute.domain.OsFamily;

/**
 * User: evgenyf
 * Date: 2/9/14
 */
public class SoftlayerMachineOptions implements MachineOptions {

    private final String name;
    private int machinesCount;
    private OsFamily osFamily;
    private String locationId;
    private String hardwareId;

    public SoftlayerMachineOptions( String name ){
        this( name, 1 );
    }

    public SoftlayerMachineOptions( String name, int machinesCount ){
        this.name = name;
        this.machinesCount = machinesCount;
    }

    public String name() {
        return name;
    }

    public SoftlayerMachineOptions machinesCount( int machinesCount ){
        this.machinesCount = machinesCount;
        return this;
    }

    public int machinesCount() {
        return machinesCount;
    }

    public SoftlayerMachineOptions locationId( String locationId ){
        this.locationId = locationId;
        return this;
    }

    public String locationId(){
        return locationId;
    }

    public SoftlayerMachineOptions hardwareId( String hardwareId ){
        this.hardwareId = hardwareId;
        return this;
    }

    public String hardwareId(){
        return hardwareId;
    }

    public SoftlayerMachineOptions osFamily( OsFamily osFamily ){
        this.osFamily = osFamily;
        return this;
    }

    public OsFamily osFamily(){
        return osFamily;
    }
}