package cloudify.widget.pool.manager.node_management;

import cloudify.widget.pool.manager.dto.DecisionModel;
import cloudify.widget.pool.manager.dto.DecisionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: eliranm
 * Date: 4/28/14
 * Time: 5:46 PM
 */
public class CreateNodeManager extends NodeManager<CreateNodeManager> {

    private static Logger logger = LoggerFactory.getLogger(CreateNodeManager.class);

    @Override
    public CreateNodeManager decide() {

        logger.info("deciding...");

        Constraints constraints = getConstraints();
        if (constraints.maxNodes - constraints.minNodes > nodesDao.readAllOfPool(constraints.poolSettings.getUuid()).size()) {

        }

        decisionsDao.create(new DecisionModel()
                .setDecisionType(DecisionType.CREATE)
                .setPoolId(constraints.poolSettings.getUuid())
                .setApproved(false)
                .setDetails(new Decision.CreateDecisionDetails()
                        .setInstances(3)
                )
        );

        return this;
    }

    @Override
    public CreateNodeManager execute() {
        logger.info("executing...");
        return this;
    }
}
