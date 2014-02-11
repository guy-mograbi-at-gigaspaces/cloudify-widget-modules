package cloudify.widget.ec2;

import cloudify.widget.api.clouds.MachineOptions;
import org.jclouds.compute.domain.OsFamily;

/**
 * User: evgenyf
 * Date: 2/9/14
 */
public class Ec2MachineOptions implements MachineOptions {

    private final String name;
    private int machinesCount;
    private OsFamily osFamily;
    private String locationId;
    private String hardwareId;
    private String imageId;
    private Iterable<String> tags;

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

    public Ec2MachineOptions tags( Iterable<String> tags ){
        this.tags = tags;
        return this;
    }

    public Iterable<String> tags() {
        return tags;
    }

    public Ec2MachineOptions machinesCount( int machinesCount ){
        this.machinesCount = machinesCount;
        return this;
    }

    public int machinesCount() {
        return machinesCount;
    }

    public Ec2MachineOptions locationId( String locationId ){
        this.locationId = locationId;
        return this;
    }

    public String locationId(){
        return locationId;
    }

    public Ec2MachineOptions hardwareId( String hardwareId ){
        this.hardwareId = hardwareId;
        return this;
    }

    public String hardwareId(){
        return hardwareId;
    }

    public Ec2MachineOptions imageId( String imageId ){
        this.imageId = imageId;
        return this;
    }

    public String imageId(){
        return imageId;
    }


    public Ec2MachineOptions osFamily( OsFamily osFamily ){
        this.osFamily = osFamily;
        return this;
    }

    public OsFamily osFamily(){
        return osFamily;
    }
}