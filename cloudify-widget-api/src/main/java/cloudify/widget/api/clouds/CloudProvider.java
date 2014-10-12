package cloudify.widget.api.clouds;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 6/11/13
 * Time: 2:17 PM
 */
public enum CloudProvider {
    HP("hpcloud-compute"),
    AWS_EC2("aws-ec2","aws_ec2"),
    SOFTLAYER("softlayer"),
    NA("na");

    public String label;
    public List<String> labels;

    private CloudProvider( String ... labels ) {

        this.labels = Arrays.asList(labels);
        this.label = labels[0];

    }

    public static CloudProvider findByLabel(String cloudProvider) {
        for (CloudProvider provider : values()) {
            if ( provider.labels.contains(cloudProvider)){
                return provider;
            }
        }
        throw new IllegalArgumentException(String.format("cannot find cloudProvider with label [%s]", cloudProvider));
    }
}