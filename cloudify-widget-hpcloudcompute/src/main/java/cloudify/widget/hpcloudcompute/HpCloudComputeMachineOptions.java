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

    public HpCloudComputeMachineOptions setHardwareId( String hardwareId ){
        this.hardwareId = hardwareId;
        return this;
    }

    public HpCloudComputeMachineOptions setImageId( String imageId ){
        this.imageId = imageId;
        return this;
    }

    @Override
    public String getMask() {
        return name;
    }

    public String getName() {
        return name;
    }

    public int getMachinesCount() {
        return machinesCount;
    }

    public String getHardwareId() {
        return hardwareId;
    }

    public String getImageId() {
        return imageId;
    }

    public String getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HpCloudComputeMachineOptions that = (HpCloudComputeMachineOptions) o;

        if (machinesCount != that.machinesCount) return false;
        if (hardwareId != null ? !hardwareId.equals(that.hardwareId) : that.hardwareId != null) return false;
        if (imageId != null ? !imageId.equals(that.imageId) : that.imageId != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (tags != null ? !tags.equals(that.tags) : that.tags != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + machinesCount;
        result = 31 * result + (hardwareId != null ? hardwareId.hashCode() : 0);
        result = 31 * result + (imageId != null ? imageId.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        return result;
    }
}