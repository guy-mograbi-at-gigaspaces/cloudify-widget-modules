package cloudify.widget.website.controller;

import cloudify.widget.pool.manager.PoolManagerApi;
import cloudify.widget.pool.manager.dto.NodeModel;
import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.dto.PoolStatus;
import cloudify.widget.pool.manager.tasks.NoopTaskCallback;
import cloudify.widget.website.dao.IAccountDao;
import cloudify.widget.website.dao.IPoolDao;
import cloudify.widget.website.models.AccountModel;
import cloudify.widget.website.models.PoolConfigurationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;


@SuppressWarnings("UnusedDeclaration")
@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private IAccountDao accountDao;

    @Autowired
    private IPoolDao poolDao;

    @Autowired
    private PoolManagerApi poolManagerApi;

    public void setPoolManagerApi(PoolManagerApi poolManagerApi) {
        this.poolManagerApi = poolManagerApi;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public String showIndex(@ModelAttribute("account") AccountModel accountModel) {
        logger.info("account model id is : " + ((accountModel == null) ? "n/a" : accountModel.id));
        //throw new RuntimeException("this is me!!!!");
        return "hello world!";
    }

    @RequestMapping(value = "/admin/account", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<AccountModel> getAccount(@ModelAttribute("account") AccountModel accountModel) {

        ResponseEntity<AccountModel> retValue;
        if (accountModel == null) {
            retValue = new ResponseEntity<AccountModel>(HttpStatus.NOT_FOUND);
        } else {
            retValue = new ResponseEntity<AccountModel>(accountModel, HttpStatus.OK);
        }

        return retValue;
    }

    @RequestMapping(value = "/admin/accounts", method = RequestMethod.GET)
    @ResponseBody
    public List<AccountModel> getAccounts() {
        return accountDao.readAccounts();
    }

    @RequestMapping(value = "/admin/pools", method = RequestMethod.GET)
    @ResponseBody
    public List<PoolConfigurationModel> getPools() {
        return poolDao.readPools();
    }

    @RequestMapping(value = "/admin/accounts", method = RequestMethod.POST)
    @ResponseBody
    public AccountModel createAccount() {
        String accountUuid = UUID.randomUUID().toString();

        AccountModel accountModel = new AccountModel();
        accountModel.setUuid(accountUuid);

        Long accountId = accountDao.createAccount(accountModel);
        accountModel.setId(accountId);

        return accountModel;
    }

    @RequestMapping(value = "/admin/accounts/{accountId}/pools", method = RequestMethod.GET)
    @ResponseBody
    public List<PoolConfigurationModel> getAccountPools(@PathVariable("accountId") Long accountId) {
        return poolDao.readPools(accountId);
    }

    @RequestMapping(value = "/admin/accounts/{accountId}/pools", method = RequestMethod.POST)
    @ResponseBody
    public Long createAccountPool(@PathVariable("accountId") Long accountId, @RequestBody String poolSettingJson) {
        return poolDao.createPool(accountId, poolSettingJson);
    }

    @RequestMapping(value = "/admin/accounts/{accountId}/pools/{poolId}", method = RequestMethod.POST)
    @ResponseBody
    public boolean updateAccountPool(@PathVariable("accountId") Long accountId, @PathVariable("poolId") Long poolId, @RequestBody String newPoolSettingJson) {
        return poolDao.updatePool(poolId, accountId, newPoolSettingJson);
    }

    @RequestMapping(value = "/admin/accounts/{accountId}/pools/{poolId}/delete", method = RequestMethod.POST)
    @ResponseBody
    public boolean deleteAccountPool(@PathVariable("accountId") Long accountId, @PathVariable("poolId") Long poolId) {
        return poolDao.deletePool(poolId, accountId);
    }

    @RequestMapping(value = "/admin/accounts/{accountId}/pools/{poolId}", method = RequestMethod.GET)
    @ResponseBody
    public PoolConfigurationModel getAccountPool(@PathVariable("accountId") Long accountId, @PathVariable("poolId") Long poolId) {
        return poolDao.readPoolByIdAndAccountId(poolId, accountId);
    }

    @RequestMapping(value = "/admin/accounts/{accountId}/pools/{poolId}/status", method = RequestMethod.GET)
    @ResponseBody
    public PoolStatus getAccountPoolStatus(@PathVariable("accountId") Long accountId, @PathVariable("poolId") Long poolId) {
        PoolStatus retValue = null;
        PoolConfigurationModel poolConfiguration = getAccountPool(accountId, poolId);
        if (poolConfiguration != null) {
            PoolSettings poolSettings = poolConfiguration.getPoolSettings();
            if (poolSettings != null) {
                retValue = poolManagerApi.getStatus(poolSettings);
            }
        }

        return retValue;
    }

    @RequestMapping(value = "/admin/pools/status", method = RequestMethod.GET)
    @ResponseBody
    public List<PoolStatus> getAccountPoolsStatus() {
        return null;//"TBD pools statuses";
    }

    @RequestMapping(value = "/admin/accounts/{accountId}/pools/{poolId}/addMachine", method = RequestMethod.POST)
    @ResponseBody
    public String addMachine(@PathVariable("accountId") Long accountId, @PathVariable("poolId") Long poolId) {
        NodeModel nodeModel = new NodeModel();
//            nodeModel.setPoolUuid(  );
//            poolManagerApi.createNode(  );
        return "TBD add machine";
    }

    @RequestMapping(value = "/admin/accounts/{accountId}/pools/{poolId}/nodes/{nodeId}/bootstrap", method = RequestMethod.POST)
    @ResponseBody
    public String nodeBootstrap(@PathVariable("accountId") Long accountId,
                                @PathVariable("poolId") Long poolId, @PathVariable("nodeId") Long nodeId) {
        PoolSettings poolSettings = getAccountPool(accountId, poolId).getPoolSettings();
        poolManagerApi.bootstrapNode( poolSettings, nodeId, new NoopTaskCallback() );
        return "TBD node bootstrap";
    }

    @RequestMapping(value = "/admin/accounts/{accountId}/pools/{poolId}/nodes/{nodeId}/delete", method = RequestMethod.POST)
    @ResponseBody
    public String nodeDelete( @ModelAttribute("poolSettings") PoolSettings poolSettings , @PathVariable("nodeId") Long nodeId) {
        poolManagerApi.deleteNode( poolSettings, nodeId, new NoopTaskCallback());
        return "ok";

    }

    @ModelAttribute("account")
    public AccountModel getUser(HttpServletRequest request) {
        return (AccountModel) request.getAttribute("account");
    }

    @ModelAttribute("poolSettings")
    public PoolSettings getPoolSettings( @PathVariable("accountId") Long accountId,
                                         @PathVariable("poolId") Long poolId ){
        return getAccountPool( accountId, poolId ).getPoolSettings();

    }
}