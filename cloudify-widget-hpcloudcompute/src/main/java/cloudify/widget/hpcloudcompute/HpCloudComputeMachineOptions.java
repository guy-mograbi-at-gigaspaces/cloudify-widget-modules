package cloudify.widget.hpcloudcompute;

import cloudify.widget.api.clouds.MachineOptions;

import java.util.Arrays;

/**
 * User: evgenyf
 * Date: 2/9/14
 */
public class HpCloudComputeMachineOptions implements MachineOptions {

    private String name;
    private int machinesCount;
    private String zone;
    private String hardwareId;
    private String imageId;
    private String tags;

    public HpCloudComputeMachineOptions(){}

    public HpCloudComputeMachineOptions(String name){
        this( name, 1 );
    }

    public HpCloudComputeMachineOptions(String name, int machinesCount){
        this.name = name;
        this.machinesCount = machinesCount;
    }

    public String name() {
        return name;
    }

    public HpCloudComputeMachineOptions setName( String name ){
        this.name = name;
        return this;
    }

    public HpCloudComputeMachineOptions setTags( String tags ){
        this.tags = tags;
        return this;
    }

    public Iterable<String> tags() {
        String[] split = tags.split(",");
        return Arrays.asList( split );
    }

    public HpCloudComputeMachineOptions setMachinesCount( int machinesCount ){
        this.machinesCount = machinesCount;
        return this;
    }

    public int machinesCount() {
        return machinesCount;
    }

    public HpCloudComputeMachineOptions setZone( String zone ){
        this.zone = zone;
        return this;
    }

    public String zone(){
        return zone;
    }

    public HpCloudComputeMachineOptions setHardwareId( String hardwareId ){
        this.hardwareId = hardwareId;
        return this;
    }

    public String hardwareId(){
        return hardwareId;
    }

    public HpCloudComputeMachineOptions setImageId( String imageId ){
        this.imageId = imageId;
        return this;
    }

    public String imageId(){
        return imageId;
    }
}