package cloudify.widget.pool.manager.tasks;

import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.pool.manager.MachineId;
import cloudify.widget.pool.manager.MachineModel;
import cloudify.widget.pool.manager.PoolDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/21/14
 * Time: 8:02 AM
 */
public class CleanPool implements Runnable{
    public String tag;
    public CloudServerApi cloudServerApi;

    public PoolDao poolDao;

    private static Logger logger = LoggerFactory.getLogger(CleanPool.class);

    @Override
    public void run() {
        final CloudServerApi finalCloudServerApi = cloudServerApi;


        final HashSet<MachineId> busyMachines = new HashSet<MachineId>();
        List<MachineModel> machines = poolDao.getMachines();
        for (MachineModel machine : machines) {
            if(machine.busySince > 0 ){
                busyMachines.add(machine.getMachineId());
            }
        }


        List<Thread> threads = new LinkedList<Thread>();
        cloudServerApi.connect( );
        Collection<CloudServer> ibmp = cloudServerApi.getAllMachinesWithTag(tag);
        for (CloudServer cloudServer : ibmp) {
            final CloudServer finalCloudServer = cloudServer;
            logger.info(cloudServer.getName());
            Thread t = new Thread( new Runnable() {
                @Override
                public void run() {
                    logger.info(finalCloudServer.getName() + " :: " + finalCloudServer.getId());
                    if ( !busyMachines.contains(new MachineId(finalCloudServer.getServerIp().publicIp))){
//                        cloudServerApi.delete(finalCloudServer.getId());
                    }else{
                        logger.info("not deleting.. machine is busy");
                    }

                }
            });
            t.start();
            threads.add(t);

        }

        int total = threads.size();
        int done = 0;
        logger.info("taking down [{}] machines", total);

        for (Thread thread : threads) {
            try {
                thread.join();
                done++;
                logger.info("took down {}/{} machines", done, total);
            } catch (InterruptedException e) {
                logger.error("error while joining thread",e);
            }
        }



    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public CloudServerApi getCloudServerApi() {
        return cloudServerApi;
    }

    public PoolDao getPoolDao() {
        return poolDao;
    }

    public void setPoolDao(PoolDao poolDao) {
        this.poolDao = poolDao;
    }

    public void setCloudServerApi(CloudServerApi cloudServerApi) {
        this.cloudServerApi = cloudServerApi;
    }
}
