package cloudify.widget.pool.manager.node_management;

import cloudify.widget.pool.manager.PoolManagerApi;
import cloudify.widget.pool.manager.TaskExecutor;
import cloudify.widget.pool.manager.dto.DecisionModel;
import cloudify.widget.pool.manager.dto.DecisionType;
import cloudify.widget.pool.manager.dto.NodeModel;
import cloudify.widget.pool.manager.tasks.CreateMachine;
import cloudify.widget.pool.manager.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * User: eliranm
 * Date: 4/28/14
 * Time: 5:46 PM
 */
public class CreateNodeManager extends NodeManager<CreateNodeManager> {

    private static Logger logger = LoggerFactory.getLogger(CreateNodeManager.class);

    private DecisionModel decision;

    @Autowired
    private PoolManagerApi poolManagerApi;

    @Override
    public CreateNodeManager decide() {
        logger.info("deciding...");

        Constraints constraints = getConstraints();
        List<NodeModel> nodeModels = nodesDao.readAllOfPool(constraints.poolSettings.getUuid());

        // we create more machines only if existing machines are less than the minimum
        if (constraints.minNodes <= nodeModels.size()) {
            return this;
        }

        int numInstancesSum = 0;

        // TODO refactor - extract to actionNeeded() -> boolean or something
        // check if there are decisions in the queue, and if executing them will satisfy the constraints
        List<DecisionModel> decisionModels = decisionsDao.readAllOfPoolWithDecisionType(
                constraints.poolSettings.getUuid(), DecisionType.CREATE);
        if (decisionModels != null && !decisionModels.isEmpty()) {
            // figure out how many machines we're intending to create
            for (DecisionModel decisionModel : decisionModels) {
                numInstancesSum += ((CreateDecisionDetails) decisionModel.details).getNumInstances();
            }
            if (numInstancesSum + nodeModels.size() >= constraints.minNodes) {
                // no action needed, the queue will satisfy the constraints in the following iteration(s)
                return this;
            }
        }


        CreateDecisionDetails decisionDetails = new CreateDecisionDetails()
                .setNumInstances(constraints.minNodes - nodeModels.size() - numInstancesSum);

        decision = new DecisionModel()
                .setDecisionType(DecisionType.CREATE)
                .setPoolId(constraints.poolSettings.getUuid())
                .setApproved(Mode.AUTO_APPROVAL == mode)
                .setDetails(decisionDetails);

        decisionsDao.create(decision);

        return this;
    }

    @Override
    public CreateNodeManager execute() {
        logger.info("executing...");

        if (decision.approved) {
            // TODO prevent casting - used generics in model
            int numInstances = ((CreateDecisionDetails) decision.details).getNumInstances();
            for (int i = 0; i < numInstances; i++) {
                poolManagerApi.createNode(getConstraints().poolSettings, null);
            }
        }

        return this;
    }

}
