package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.*;
import cloudify.widget.pool.manager.node_management.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: eliranm
 * Date: 4/25/14
 * Time: 10:01 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:pool-manager-test-context.xml"})
public class TestNodeManagement {

    private static Logger logger = LoggerFactory.getLogger(TestNodeManagement.class);

    @Autowired
    private SettingsDataAccessManager settingsDataAccessManager;

    @Autowired
    private NodeManagementRunner nodeManagementRunner;

    @Autowired
    private CreateNodeManager createNodeManager;

    @Autowired
    private DecisionsDao decisionsDao;

    private ManagerSettings managerSettings;

    @Before
    public void setup() {
        managerSettings = settingsDataAccessManager.read();
        Assert.assertNotNull("manager settings should not be null", managerSettings);
    }

    @Test
    public void testDecisionMaker() throws IOException {

        createNodeManager.mode(NodeManager.Mode.MANUAL_APPROVAL);

        PoolSettings poolSettings = managerSettings.getPools().getByProviderName(ProviderSettings.ProviderName.hp);
        logger.info("- pool settings [{}]", poolSettings.getProvider().getName());

        for (int i = 0; i < 3; i++) {

            createNodeManager
                    .having(new Constraints(poolSettings))
                    .decide()
                    .execute();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void testDecisionsDao() {

        PoolSettings poolSettings = managerSettings.getPools().getByProviderName(ProviderSettings.ProviderName.hp);

        // create

        decisionsDao.create(new DecisionModel()
                        .setDecisionType(DecisionType.CREATE)
                        .setPoolId(poolSettings.getUuid())
                        .setApproved(true)
                        .setDetails(new Decision.CreateDecisionDetails()
                                        .setInstances(3)
                        )
        );

        Set<String> machineIds = new HashSet<String>();
        machineIds.addAll(Arrays.asList("a", "b", "c"));
        decisionsDao.create(new DecisionModel()
                        .setDecisionType(DecisionType.DELETE)
                        .setPoolId(poolSettings.getUuid())
                        .setApproved(false)
                        .setDetails(new Decision.DeleteDecisionDetails()
                                        .addMachineIds(machineIds)
                        )
        );

        DecisionModel prepareDecisionModel = new DecisionModel()
                .setDecisionType(DecisionType.PREPARE)
                .setPoolId(poolSettings.getUuid())
                .setApproved(false)
                .setDetails(new Decision.PrepareDecisionDetails()
                                .addMachineIds(machineIds)
                );
        decisionsDao.create(prepareDecisionModel);

        // read

        DecisionModel readPrepareDecisionModel = decisionsDao.read(prepareDecisionModel.id);

        logger.info(String.valueOf(readPrepareDecisionModel));


    }


    public void setSettingsDataAccessManager(SettingsDataAccessManager settingsDataAccessManager) {
        this.settingsDataAccessManager = settingsDataAccessManager;
    }

}
