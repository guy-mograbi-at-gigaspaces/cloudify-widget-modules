package cloudify.widget.website;

import cloudify.widget.pool.manager.PoolManager;
import cloudify.widget.pool.manager.dto.ManagerSettings;
import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.dto.ProviderSettings;
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

    public void setPoolManager(PoolManager poolManager) {
        this.poolManager = poolManager;
    }

    @Autowired
    private PoolManager poolManager;



    @Test
    public void testPool() {

        PoolConfigurationModel poolModel1 = createPoolConfigurationModel( (long)1 );
        PoolConfigurationModel poolModel2 = createPoolConfigurationModel( (long)2  );

        Long poolId1 = poolDao.createPool(poolModel1);
        logger.info( "Pool 1 created, poolSettings [{}], account Id [{}], id [{}]",
                        "authKey1", poolModel1.getAccountId(), poolId1 );

        Long poolId2 = poolDao.createPool( poolModel2 );
        logger.info( "Pool 2 created, account Id [{}], id [{}]", poolModel2.getAccountId(), poolId2 );

        PoolConfigurationModel poolConfigurationModel = poolDao.readPool(poolId1);
        logger.info( "Pool with id [{}] was [{}]", poolId1, poolConfigurationModel != null ? "found" : "not found" );
        Assert.assertNotNull( "retrieved PoolConfiguration id [" + poolId1 + "] should not be null", poolConfigurationModel );
        Assert.assertEquals( "Retrieved account Id is not as expected", poolModel1.getAccountId(), poolConfigurationModel.getAccountId() );
        Assert.assertEquals( "Retrieved pool configuration authKey is not as expected", poolModel1.getPoolSettings().getAuthKey(), poolConfigurationModel.getPoolSettings().getAuthKey() );
        Assert.assertEquals( "Retrieved pool configuration id is not as expected", poolModel1.getPoolSettings().getId(), poolConfigurationModel.getPoolSettings().getId() );
        Assert.assertEquals( "Retrieved pool configuration max nodes is not as expected", poolModel1.getPoolSettings().getMaxNodes(), poolConfigurationModel.getPoolSettings().getMaxNodes() );
        Assert.assertEquals( "Retrieved pool configuration min nodes is not as expected", poolModel1.getPoolSettings().getMinNodes(), poolConfigurationModel.getPoolSettings().getMinNodes() );
        Assert.assertNotNull("Retrieved pool configuration cannot be null", poolConfigurationModel.getPoolSettings());

        poolConfigurationModel = poolDao.readPool(poolId2);
        logger.info( "Pool with id [{}] was ", poolId2, poolConfigurationModel != null ? "found" : "not found" );
        Assert.assertNotNull( "retrieved PoolConfiguration id [" + poolId2 + "] should not be null", poolConfigurationModel );
        Assert.assertEquals( "Retrieved account Id is not as expected", poolModel2.getAccountId(), poolConfigurationModel.getAccountId() );
        Assert.assertEquals( "Retrieved pool configuration authKey is not as expected", poolModel2.getPoolSettings().getAuthKey(), poolConfigurationModel.getPoolSettings().getAuthKey() );
        Assert.assertEquals( "Retrieved pool configuration id is not as expected", poolModel2.getPoolSettings().getId(), poolConfigurationModel.getPoolSettings().getId() );
        Assert.assertEquals( "Retrieved pool configuration max nodes is not as expected", poolModel2.getPoolSettings().getMaxNodes(), poolConfigurationModel.getPoolSettings().getMaxNodes() );
        Assert.assertEquals("Retrieved pool configuration min nodes is not as expected", poolModel2.getPoolSettings().getMinNodes(), poolConfigurationModel.getPoolSettings().getMinNodes());
        Assert.assertNotNull("Retrieved pool configuration cannot be null", poolConfigurationModel.getPoolSettings());


        ProviderSettings updatedProviderSettings = new ProviderSettings();
        updatedProviderSettings.setName( ProviderSettings.ProviderName.hp );

         PoolConfigurationModel updatedPoolModel1 = createPoolConfigurationModel( (long)1 );
        updatedPoolModel1.setId( poolId1 );
        poolDao.updatePool( updatedPoolModel1 );

        PoolConfigurationModel updatedPoolConfigurationModel = poolDao.readPool(poolId1);
        logger.info( "Updated Pool with id [{}] was ", poolId1, updatedPoolConfigurationModel != null ? "found" : "not found" );
        Assert.assertEquals("Retrieved account Id is not as expected", updatedPoolModel1.getAccountId(), updatedPoolConfigurationModel.getAccountId());
        Assert.assertEquals( "Retrieved pool configuration authKey is not as expected", updatedPoolModel1.getPoolSettings().getAuthKey(), updatedPoolConfigurationModel.getPoolSettings().getAuthKey() );
        Assert.assertEquals( "Retrieved pool configuration id is not as expected", updatedPoolModel1.getPoolSettings().getId(), updatedPoolConfigurationModel.getPoolSettings().getId() );
        Assert.assertEquals("Retrieved pool configuration max nodes is not as expected", updatedPoolModel1.getPoolSettings().getMaxNodes(), updatedPoolConfigurationModel.getPoolSettings().getMaxNodes());
        Assert.assertEquals( "Retrieved pool configuration min nodes is not as expected", updatedPoolModel1.getPoolSettings().getMinNodes(), updatedPoolConfigurationModel.getPoolSettings().getMinNodes() );
        Assert.assertNotNull("Retrieved pool configuration cannot be null", poolConfigurationModel.getPoolSettings());



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

        AccountModel readAccountModel1 = accountDao.readAccountByUuid(accountModel1.getUuid());
        Assert.assertEquals("uuid of read account is not as expected ", accountModel1.getUuid(), readAccountModel1.getUuid() );
        Assert.assertEquals("id of read account is not as expected ", accountId1, readAccountModel1.getId() );

        AccountModel readAccountModel2 = accountDao.readAccountByUuid(accountModel2.getUuid());
        Assert.assertEquals("uuid of read account is not as expected ", accountModel2.getUuid(), readAccountModel2.getUuid() );
        Assert.assertEquals("id of read account is not as expected ", accountId2, readAccountModel2.getId() );

        boolean account1Deleted = accountDao.deleteAccount( accountId1 );
        Assert.assertTrue("Account with id [" + accountId1 + "] was not deleted", account1Deleted);

        boolean account2Deleted = accountDao.deleteAccount( accountId2 );
        Assert.assertTrue("Account with id [" + accountId2 + "] was not deleted", account2Deleted);

    }

    private PoolConfigurationModel createPoolConfigurationModel( long accountId ){

        PoolConfigurationModel poolModel = new PoolConfigurationModel();
        poolModel.setAccountId( accountId );

        ManagerSettings managerSettings = poolManager.getSettings();

        PoolSettings poolSettings = managerSettings.getPools().get( 0 );
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