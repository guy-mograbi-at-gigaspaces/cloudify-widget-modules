package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.NodeModel;
import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.dto.PoolStatusCount;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: eliranm
 * Date: 3/2/14
 * Time: 1:49 PM
 */
public class NodesDataAccessManager {

    @Autowired
    private NodesDao nodesDao;


    public List<NodeModel> listNodes(PoolSettings poolSettings) {
        return nodesDao.readAllOfPool(poolSettings.getUuid());
    }

    public List<PoolStatusCount> getPoolStatusCounts() {
        return nodesDao.getPoolStatusCounts();
    }

    public List<PoolStatusCount> getPoolStatusCountsOfPool(String poolId) {
        return nodesDao.getPoolStatusCountsOfPool(poolId);
    }

    public NodeModel getNode(long nodeId) {
        return nodesDao.read(nodeId);
    }

    public boolean addNode(NodeModel nodeModel) {
        return nodesDao.create(nodeModel);
    }

    public int removeNode(long nodeId) {
        return nodesDao.delete(nodeId);
    }

    public int updateNode(NodeModel nodeModel) {
        return nodesDao.update(nodeModel);
    }


    public void setNodesDao(NodesDao nodesDao) {
        this.nodesDao = nodesDao;
    }

}
