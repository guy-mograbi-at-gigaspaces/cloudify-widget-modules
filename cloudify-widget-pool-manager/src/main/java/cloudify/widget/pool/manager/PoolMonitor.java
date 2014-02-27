package cloudify.widget.pool.manager;

import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.softlayer.SoftlayerCloudServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/21/14
 * Time: 2:54 AM
 */
public class PoolMonitor {
    public String tag;
    public CloudServerApi serverApi;
    public BootstrapMonitor bootstrapMonitor;
    public PoolDao poolDao;

    private static Logger logger = LoggerFactory.getLogger(PoolMonitor.class);

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public PoolStatus getPoolStatus(){
        serverApi.connect();
        Collection<CloudServer> allMachinesWithTag = serverApi.getAllMachinesWithTag(tag);
        logger.info("there are [{}] machines with tag [{}]", allMachinesWithTag.size(), tag);
        return getPoolStatus( allMachinesWithTag );
    }

    public PoolStatus getPoolStatus( Collection<CloudServer> servers ){
        serverApi.connect();
        PoolStatus status = new PoolStatus();


        List<Thread> threads = new LinkedList<Thread>();
        int i = 0;
        for (CloudServer cloudServer : servers) {

            if ( cloudServer.isRunning() ){
                i++;
                GetMachineStatus task = new GetMachineStatus();
                task.setBootstrapMonitor(bootstrapMonitor);
                task.setPublicIp(cloudServer.getServerIp().publicIp);
                task.setStatus(status);
                task.setServerApi(serverApi);

                Thread t = new Thread(task);
                t.start();
                threads.add(t);
            }else{
                logger.info("status is [{}]", ((SoftlayerCloudServer) cloudServer).getStatus());
            }

        }

        logger.info("all in all found [{}] running servers",i);

        for (MachineModel machineModel : poolDao.getMachines()) {
            status.machineModels.put(machineModel.getMachineId(), machineModel);
        }

        for (Thread thread : threads) {
            try{
                thread.join();
            }catch(Exception e){
                logger.error("thread was unable to join",e);
            }
        }

        return status;
    }


    public BootstrapMonitor getBootstrapMonitor() {
        return bootstrapMonitor;
    }

    public void setBootstrapMonitor(BootstrapMonitor bootstrapMonitor) {
        this.bootstrapMonitor = bootstrapMonitor;
    }

    public CloudServerApi getServerApi() {
        return serverApi;
    }

    public void setServerApi(CloudServerApi serverApi) {
        this.serverApi = serverApi;
    }



    public static class GetMachineStatus implements Runnable{
        public static int i = 0;
        BootstrapMonitor bootstrapMonitor;
        String publicIp;
        PoolStatus status;
        CloudServerApi serverApi;
        @Override
        public void run() {
            try{
            i++;
            PoolMachineStatus item = new PoolMachineStatus();


            boolean cloudifyManagementAvailable = bootstrapMonitor.isCloudifyManagementAvailable(publicIp);
            logger.debug("bootstrap is available [{}] on ip [{}]", cloudifyManagementAvailable, publicIp);
            item.managementAvailable = cloudifyManagementAvailable;

            boolean applicationAvailable = bootstrapMonitor.isApplicationAvailable(publicIp);
            logger.debug("application is available [{}] on ip [{}]", applicationAvailable, publicIp);
            item.applicationIsOnline = applicationAvailable;

            if ( item.managementAvailable && !item.applicationIsOnline ){
                logger.info("BLU is down at : " + publicIp);
            }

                if ( item.managementAvailable && item.applicationIsOnline ){
                    logger.info( publicIp + " should be ok ");
                }

            item.ip = publicIp;

//            try{
//                String output = serverApi.runScriptOnMachine("cat /proc/uptime", publicIp, null).getOutput();
//                logger.info("output is [{}]", output);
//                item.uptimeMillis = new Double(Double.parseDouble(output.split(" ")[0])).longValue();
//                logger.info("my uptime is " + item.uptimeMillis);
//            }catch(Exception e){
//                logger.warn("unable to calculate uptime : " + e.getMessage());
//            }

            status.machineStatuses.put(item.getMachineId(), item);
            }catch(Exception e){
                logger.error("there are exceptions ",e);
            }
        }

        public BootstrapMonitor getBootstrapMonitor() {
            return bootstrapMonitor;
        }

        public void setBootstrapMonitor(BootstrapMonitor bootstrapMonitor) {
            this.bootstrapMonitor = bootstrapMonitor;
        }

        public String getPublicIp() {
            return publicIp;
        }

        public void setPublicIp(String publicIp) {
            this.publicIp = publicIp;
        }

        public PoolStatus getStatus() {
            return status;
        }

        public void setStatus(PoolStatus status) {
            this.status = status;
        }

        public CloudServerApi getServerApi() {
            return serverApi;
        }

        public void setServerApi(CloudServerApi serverApi) {
            this.serverApi = serverApi;
        }
    }

    public PoolDao getPoolDao() {
        return poolDao;
    }

    public void setPoolDao(PoolDao poolDao) {
        this.poolDao = poolDao;
    }
}
