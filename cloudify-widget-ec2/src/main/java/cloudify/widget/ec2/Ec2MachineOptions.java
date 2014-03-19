package cloudify.widget.ec2;

import cloudify.widget.api.clouds.MachineOptions;

import java.util.Arrays;

/**
 * User: evgenyf
 * Date: 2/9/14
 */
public class Ec2MachineOptions implements MachineOptions {

    private String mask;
    private int machinesCount;
    private String locationId;
    private String hardwareId;
    private String imageId;
    private String tags;

    public Ec2MachineOptions(){}

    public Ec2MachineOptions( String mask){
        this(mask, 1 );
    }

    public Ec2MachineOptions( String mask, int machinesCount ){
        this.mask = mask;
        this.machinesCount = machinesCount;
    }

    public String getMask() {
        return mask;
    }

    public Ec2MachineOptions setMask(String mask){
        this.mask = mask;
        return this;
    }

    public Ec2MachineOptions setTags( String tags ){
        this.tags = tags;
        return this;
    }

    public Iterable<String> tags() {
        String[] split = tags.split(",");
        return Arrays.asList( split );
    }

    public Ec2MachineOptions setMachinesCount( int machinesCount ){
        this.machinesCount = machinesCount;
        return this;
    }

    public int getMachinesCount() {
        return machinesCount;
    }

    public Ec2MachineOptions setLocationId( String locationId ){
        this.locationId = locationId;
        return this;
    }

    public String getLocationId(){
        return locationId;
    }

    public Ec2MachineOptions setHardwareId( String hardwareId ){
        this.hardwareId = hardwareId;
        return this;
    }

    public String getHardwareId(){
        return hardwareId;
    }

    public Ec2MachineOptions setImageId( String imageId ){
        this.imageId = imageId;
        return this;
    }

    public String getImageId(){
        return imageId;
    }

}