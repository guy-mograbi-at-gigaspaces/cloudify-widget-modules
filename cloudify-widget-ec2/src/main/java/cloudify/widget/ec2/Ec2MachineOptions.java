package cloudify.widget.ec2;

import cloudify.widget.api.clouds.MachineOptions;

import java.util.Arrays;

/**
 * User: evgenyf
 * Date: 2/9/14
 */
public class Ec2MachineOptions implements MachineOptions {

    private String name;
    private int machinesCount;
    private String locationId;
    private String hardwareId;
    private String imageId;
    private String tags;

    public Ec2MachineOptions(){}

    public Ec2MachineOptions( String name ){
        this( name, 1 );
    }

    public Ec2MachineOptions( String name, int machinesCount ){
        this.name = name;
        this.machinesCount = machinesCount;
    }

    public String name() {
        return name;
    }

    public Ec2MachineOptions setName( String name ){
        this.name = name;
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

    public int machinesCount() {
        return machinesCount;
    }

    public Ec2MachineOptions setLocationId( String locationId ){
        this.locationId = locationId;
        return this;
    }

    public String locationId(){
        return locationId;
    }

    public Ec2MachineOptions setHardwareId( String hardwareId ){
        this.hardwareId = hardwareId;
        return this;
    }

    public String hardwareId(){
        return hardwareId;
    }

    public Ec2MachineOptions setImageId( String imageId ){
        this.imageId = imageId;
        return this;
    }

    public String imageId(){
        return imageId;
    }
}