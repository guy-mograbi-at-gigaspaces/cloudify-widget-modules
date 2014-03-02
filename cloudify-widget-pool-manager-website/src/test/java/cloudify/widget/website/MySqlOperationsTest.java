package cloudify.widget.website;

import cloudify.widget.pool.manager.PoolSettings;
import cloudify.widget.website.dao.IAccountDao;
import cloudify.widget.website.dao.IPoolDao;
import cloudify.widget.website.models.AccountModel;
import cloudify.widget.website.models.PoolConfigurationModel;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context.xml"})
public class MySqlOperationsTest {

    private static Logger logger = LoggerFactory.getLogger(MySqlOperationsTest.class);

    @Autowired
    private IAccountDao accountDao;

    @Autowired
    private IPoolDao poolDao;

    @Test
    public void testPool() {

        PoolConfigurationModel poolModel1 = createPoolConfigurationModel( (long)1, "test1" );
        PoolConfigurationModel poolModel2 = createPoolConfigurationModel( (long)2, "test2" );

        Long poolId1 = poolDao.createPool( poolModel1 );
        logger.info( "Pool 1 created, poolSettings [{}], account Id [{}], id [{}]",
                        poolModel1.getPoolSettings().name, poolModel1.getAccountId(), poolId1 );

        Long poolId2 = poolDao.createPool( poolModel2 );
        logger.info( "Pool 2 created, poolSettings [{}], account Id [{}], id [{}]",
                poolModel2.getPoolSettings().name, poolModel2.getAccountId(), poolId2 );

        PoolConfigurationModel poolConfigurationModel = poolDao.readPool(poolId1);
        logger.info( "Pool with id [{}] was [{}]", poolId1, poolConfigurationModel != null ? "found" : "not found" );
        Assert.assertNotNull( "retrieved PoolConfiguration id [" + poolId1 + "] should not be null", poolConfigurationModel );

        poolConfigurationModel = poolDao.readPool(poolId2);
        Assert.assertNotNull( "retrieved PoolConfiguration id [" + poolId2 + "] should not be null", poolConfigurationModel );
        logger.info( "Pool with id [{}] was ", poolId2, poolConfigurationModel != null ? "found" : "not found" );

        boolean pool1Deleted = poolDao.deletePool(poolId1);
        logger.info( "Pool with id [{}] was ", poolId1, pool1Deleted ? "deleted" : "not deleted" );
        Assert.assertTrue("PoolConfiguration with id [" + poolId1 + "] was not deleted", pool1Deleted);

        boolean pool2Deleted = poolDao.deletePool(poolId2);
        Assert.assertTrue("PoolConfiguration with id [" + poolId2 + "] was not deleted", pool2Deleted);
    }

    @Test
    public void testAccount() {

        AccountModel accountModel1 = createAccountModel();
        AccountModel accountModel2 = createAccountModel();

        Long accountId1 = accountDao.createAccount(accountModel1);
        logger.info( "Account 1 created, uuid [{}], id [{}]", accountModel1.getUuid(), accountId1 );

        Long accountId2 = accountDao.createAccount(accountModel2);
        logger.info( "Account 2 created, uuid [{}], id [{}]", accountModel2.getUuid(), accountId2 );

        boolean account1Deleted = accountDao.deleteAccount( accountId1 );
        Assert.assertTrue("Account with id [" + accountId1 + "] was not deleted", account1Deleted);

        boolean account2Deleted = accountDao.deleteAccount( accountId2 );
        Assert.assertTrue("Account with id [" + accountId2 + "] was not deleted", account2Deleted);

    }

    private static PoolConfigurationModel createPoolConfigurationModel( long accountId, String poolSettingsStr ){
        PoolConfigurationModel poolModel = new PoolConfigurationModel();
        poolModel.setAccountId( accountId );
        PoolSettings poolSettings = new PoolSettings();
        poolSettings.name = poolSettingsStr;
        poolModel.setPoolSettings( poolSettings );

        return poolModel;
    }

    private static AccountModel createAccountModel(){
        String accountUuid = createUuid();
        AccountModel accountModel = new AccountModel();
        accountModel.setUuid( accountUuid );

        return accountModel;
    }

    private static String createUuid(){
        UUID accountUuid = UUID.randomUUID();
        return accountUuid.toString();
    }
}