package cloudify.widget.ec2;

import java.util.List;

/**
 * Created by kinneretzin on 10/30/14.
 */
public class WidgetSecurityGroupData {

    private java.util.List<String> ips;
    private List<Integer>ports;
    private String name;

    public WidgetSecurityGroupData(String name, List<String> ips, List<Integer> ports) {
        this.name = name;
        this.ips = ips;
        this.ports = ports;
    }

    public List<String> getIps() {
        return ips;
    }

    public List<Integer> getPorts() {
        return ports;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }

    public void setPorts(List<Integer> ports) {
        this.ports = ports;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
