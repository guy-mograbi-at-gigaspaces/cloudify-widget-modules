package cloudify.widget.ec2;


import cloudify.widget.api.clouds.CloudProvider;
import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * User: evgeny
 * Date: 2/10/14
 * Time: 6:55 PM
 */
public class Ec2CloudUtils {

    private static Logger logger = LoggerFactory.getLogger(Ec2CloudUtils.class);

    private Ec2CloudUtils() {
    }

    public static ComputeServiceContext computeServiceContext(Ec2ConnectDetails connectDetails) {

        String accessId = connectDetails.getAccessId();
        String secretAccessKey = connectDetails.getSecretAccessKey();

        logger.info("creating compute service context");

        Properties overrides = new Properties();
        overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY, "");
        overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY, "");

        String cloudProvider = CloudProvider.AWS_EC2.label;
        logger.info("building new context for provider [{}]", cloudProvider);
        ComputeServiceContext context = ContextBuilder.newBuilder(cloudProvider)
                .overrides(overrides)
                .credentials(accessId, secretAccessKey)
                .buildView(ComputeServiceContext.class);

        return context;
    }

}