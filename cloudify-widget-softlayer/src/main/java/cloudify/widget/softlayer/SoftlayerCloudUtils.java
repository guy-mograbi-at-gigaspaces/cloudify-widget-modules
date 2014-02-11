package cloudify.widget.softlayer;


import cloudify.widget.api.clouds.CloudProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.softlayer.compute.VirtualGuestToReducedNodeMetaDataLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * User: eliranm
 * Date: 2/5/14
 * Time: 3:41 PM
 */
public class SoftlayerCloudUtils {

    private static Logger logger = LoggerFactory.getLogger(SoftlayerCloudUtils.class);

    private SoftlayerCloudUtils() {
    }

    public static ComputeServiceContext computeServiceContext( String user , String key, boolean api) {

        logger.info("creating compute service context");
        Set<Module> modules = new HashSet<Module>();

        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(org.jclouds.softlayer.compute.functions.VirtualGuestToNodeMetadata.class).to(VirtualGuestToReducedNodeMetaDataLocal.class);
            }
        });

        ComputeServiceContext context;
        Properties overrides = new Properties();
        overrides.put("jclouds.timeouts.AccountClient.getActivePackages", String.valueOf(10 * 60 * 1000));
        if (api) {
            overrides.put("jclouds.keystone.credential-type", "apiAccessKeyCredentials");
        }

        String cloudProvider = CloudProvider.SOFTLAYER.label;
        logger.info("building new context for provider [{}]", cloudProvider);
        context = ContextBuilder.newBuilder(cloudProvider)
                .credentials(user, key)
                .overrides(overrides)
                .modules(modules)
                .buildView(ComputeServiceContext.class);

        return context;
    }

}
//{
//
//	public static final String SOFTLAYER_PROPERTIES_FILE_NAME = CloudProvider.SOFTLAYER.label + "-cloud.properties";
//
//
//	public static File createSoftlayerCloudFolder( String userId, String secretKey, Conf conf ) throws IOException {
//
//		String cloudifyBuildFolder = conf.server.environment.cloudifyHome;
//		File cloudifyEscFolder = new File(cloudifyBuildFolder, conf.server.cloudBootstrap.cloudifyEscDirRelativePath);
//		File origCloudFolder = getOriginSoftlayerCloudFolder( conf );
//		File destFolder = new File(cloudifyEscFolder, CloudProvider.SOFTLAYER.label + CloudifyFactory.getTempSuffix());
//		FileUtils.copyDirectory(origCloudFolder, destFolder);
//
//        try {
//            File propertiesFile = new File(destFolder, SOFTLAYER_PROPERTIES_FILE_NAME );
//
//            // GUY - Important - Note - Even though this is the "properties" files, it is not used for "properties" per say
//            // we are actually writing a groovy file that defines variables.
//            Collection<String> newLines = new LinkedList<String>();
//            newLines.add("");
//            newLines.add("user="+ StringUtils.wrapWithQuotes(userId));
//            newLines.add("apiKey="+ StringUtils.wrapWithQuotes(secretKey));
//            FileUtils.writeLines( propertiesFile, newLines, true );
//
//            return destFolder;
//        } catch (Exception e) {
//            throw new RuntimeException( String.format("error while writing cloud properties"), e );
//        }
//	}
//
//	public static File getOriginSoftlayerCloudFolder( Conf conf ){
//		CloudBootstrapConfiguration cloudConf = conf.server.cloudBootstrap;
//		String cloudifyBuildFolder = conf.server.environment.cloudifyHome;
//		File cloudifyEscFolder = new File(cloudifyBuildFolder, cloudConf.cloudifyEscDirRelativePath);
//
//		//copy the content of hp configuration files to a new folder
//        return new File( cloudifyEscFolder, CloudProvider.SOFTLAYER.label );
//	}
//
//	/*
//	public static String getSoftlayerManagementMachinePrefix( Conf conf ){
//
//		String retValue = null;
//    	File originSoftlayerCloudFolder = CloudifyFactory.getOriginSoftlayerCloudFolder(conf);
//    	File propertiesFile =
//    		new File( originSoftlayerCloudFolder, CloudifyFactory.SOFTLAYER_PROPERTIES_FILE_NAME );
//    	if( propertiesFile.exists() ){
//    		Properties softlayerCloudProperties = new Properties();
//    		try {
//    			FileInputStream in = new FileInputStream( propertiesFile );
//				softlayerCloudProperties.load( in );
//			}
//    		catch( IOException e ) {
//    			logger.error( e.toString(), e );
//			}
//    		String managerMachinePrefix =
//    				softlayerCloudProperties.getProperty( CloudifyFactory.SOFTLAYER_MANAGER_MACHINE_PREFIX );
//
//    		managerMachinePrefix = StringUtils.removeStart(managerMachinePrefix, "\"");
//    		managerMachinePrefix = StringUtils.removeEnd(managerMachinePrefix, "\"");
//   			retValue = managerMachinePrefix;
//    	}
//    	if( utils.StringUtils.isEmptyOrSpaces( retValue ) ){
//    		retValue = conf.server.cloudBootstrap.existingManagementMachinePrefix;
//    	}
//
//    	return retValue;
//	}*/
//
//	public static void createCloudifySecurityGroup( ComputeServiceContext context, CloudBootstrapConfiguration cloudConf ) {
//		//not implemented
//	}
//
//
//}
