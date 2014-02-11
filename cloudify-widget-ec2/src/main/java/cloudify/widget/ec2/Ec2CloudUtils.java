package cloudify.widget.ec2;


import cloudify.widget.api.clouds.CloudProvider;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: evgeny
 * Date: 2/10/14
 * Time: 6:55 PM
 */
public class Ec2CloudUtils {

    private static Logger logger = LoggerFactory.getLogger(Ec2CloudUtils.class);

    private Ec2CloudUtils() {
    }

    public static ComputeServiceContext computeServiceContext(Ec2CloudCredentials cloudCredentials) {

        String accessId = cloudCredentials.getAccessId();
        String secretAccessKey = cloudCredentials.getSecretAccessKey();

        logger.info("creating compute service context");

        String cloudProvider = CloudProvider.AWS_EC2.label;
        logger.info("building new context for provider [{}]", cloudProvider);
        ComputeServiceContext context = ContextBuilder.newBuilder(cloudProvider)
                .credentials(accessId, secretAccessKey)
                .buildView(ComputeServiceContext.class);

        return context;
    }

}