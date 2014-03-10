package cloudify.widget.website.controller;

import cloudify.widget.pool.manager.PoolManagerApi;
import cloudify.widget.pool.manager.dto.*;
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
    public String showIndex( @ModelAttribute("account") AccountModel accountModel ) {
        logger.info("account model id is : " + ( ( accountModel == null ) ? "n/a" : accountModel.id ) );
        //throw new RuntimeException("this is me!!!!");
        return "hello world!";
    }

    @RequestMapping(value="/admin/account", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<AccountModel> getAccount( @ModelAttribute("account") AccountModel accountModel ) {

        ResponseEntity<AccountModel> retValue;
        if( accountModel == null ) {
            retValue = new ResponseEntity<AccountModel>( HttpStatus.NOT_FOUND );
        }
        else{
            retValue = new ResponseEntity<AccountModel>( accountModel, HttpStatus.OK );
        }

        return retValue;
    }

    @RequestMapping(value="/admin/accounts", method=RequestMethod.GET)
    @ResponseBody
    public List<AccountModel> getAccounts(){
        try{
            return accountDao.readAccounts();
        }catch(Exception e){
            logger.error("unable to map pool to JSON", e);
            return null;
//            return new ResponseEntity<String>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

    @RequestMapping(value="/admin/pools", method=RequestMethod.GET)
    @ResponseBody
    public List<PoolConfigurationModel> getPools(){
        try{
            return poolDao.readPools();
        }catch(Exception e){
            logger.error("unable to map pool to JSON", e);
            return null;
//            return new ResponseEntity<String>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

    @RequestMapping(value="/admin/accounts", method=RequestMethod.POST)
    @ResponseBody
    public AccountModel createAccount(){
        try{
            String accountUuid = UUID.randomUUID().toString();

            AccountModel accountModel = new AccountModel();
            accountModel.setUuid( accountUuid );

            Long accountId = accountDao.createAccount(accountModel);
            accountModel.setId( accountId );

            return accountModel;
        }catch(Exception e){
            logger.error("unable to map pool to JSON", e);
            return null;
//            return new ResponseEntity<String>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

    @RequestMapping(value="/admin/accounts/{accountId}/pools", method=RequestMethod.GET)
    @ResponseBody
    public List<PoolConfigurationModel> getAccountPools( @PathVariable("accountId") Long accountId ){
        try{
            return poolDao.readPools( accountId );
        }catch(Exception e){
            logger.error("unable to map pool to JSON", e);
            return null;
        }
    }

    @RequestMapping(value="/admin/accounts/{accountId}/pools", method=RequestMethod.POST)
    @ResponseBody
    public Long createAccountPool( @PathVariable("accountId") Long accountId, @RequestBody String poolSettingJson ){
        try{
            return poolDao.createPool(accountId, poolSettingJson);
        }catch(Exception e){
            return null;
        }
    }

    @RequestMapping(value="/admin/accounts/{accountId}/pools/{poolId}", method=RequestMethod.POST)
    @ResponseBody
    public boolean updateAccountPool( @PathVariable("accountId") Long accountId, @PathVariable("poolId") Long poolId, @RequestBody String newPoolSettingJson ){
        try{
            return poolDao.updatePool( poolId, accountId, newPoolSettingJson );
        }catch(Exception e){
            return false;
        }
    }

    @RequestMapping(value="/admin/accounts/{accountId}/pools/{poolId}/delete", method=RequestMethod.POST)
    @ResponseBody
    public boolean deleteAccountPool( @PathVariable("accountId") Long accountId, @PathVariable("poolId") Long poolId ){
        try{
            return poolDao.deletePool( poolId, accountId );
        }catch(Exception e){
            return false;
        }
    }

    @RequestMapping(value="/admin/accounts/{accountId}/pools/{poolId}", method=RequestMethod.GET)
    @ResponseBody
    public PoolConfigurationModel getAccountPool( @PathVariable("accountId") Long accountId, @PathVariable("poolId") Long poolId ){
        try{
            return poolDao.readPoolByIdAndAccountId(poolId, accountId);
        }catch(Exception e){
            return null;
        }
    }

    @RequestMapping(value="/admin/pools/{poolId}/status", method=RequestMethod.GET)
    @ResponseBody
    public PoolStatus getAccountPoolStatus( @ModelAttribute("account") AccountModel accountModel, @PathVariable("poolId") Long poolId ){
        try{
            PoolStatus retValue = null;
            PoolConfigurationModel poolConfiguration = getAccountPool(accountModel.getId(), poolId);
            if( poolConfiguration != null ){
                PoolSettings poolSettings = poolConfiguration.getPoolSettings();
                if( poolSettings != null ){
                    retValue = poolManagerApi.getStatus( poolSettings );
                }
            }

            return retValue;
        }
        catch(Exception e){
            return null;
        }
    }

    @RequestMapping(value="/admin/pools/status", method=RequestMethod.GET)
    @ResponseBody
    public String getAccountPoolsStatus( @ModelAttribute("account") AccountModel accountModel ){
        try{
            return "TBD pools statuses";
        }catch(Exception e){
            return null;
        }
    }

    @RequestMapping(value="/admin/pools/{poolId}/addMachine", method=RequestMethod.POST)
    @ResponseBody
    public String addMachine( @ModelAttribute("account") AccountModel accountModel, @PathVariable("poolId") Long poolId ){
        try{

            NodeModel nodeModel = new NodeModel();
//            nodeModel.setPoolUuid(  );
//            poolManagerApi.createNode(  );
            return "TBD add machine";
        }catch(Exception e){
            return null;
        }
    }

    @RequestMapping(value="/admin/pools/{poolId}/nodes/{nodeId}/bootstrap", method=RequestMethod.POST)
    @ResponseBody
    public String nodeBootstrap( @ModelAttribute("account") AccountModel accountModel,
                                            @PathVariable("poolId") Long poolId, @PathVariable("nodeId") Long nodeId ){
        try{
            return "TBD node bootstrap";
        }catch(Exception e){
            return null;
        }
    }

    @RequestMapping(value="/admin/pools/{poolId}/nodes/{nodeId}/delete", method=RequestMethod.POST)
    @ResponseBody
    public String nodeDelete( @ModelAttribute("account") AccountModel accountModel,
                                            @PathVariable("poolId") Long poolId, @PathVariable("nodeId") Long nodeId ){
        try{
            poolManagerApi.deleteNode( nodeId );
            return "TBD node delete";
        }catch(Exception e){
            return null;
        }
    }

    @ModelAttribute("account")
    public AccountModel getUser(HttpServletRequest request)
    {
        return (AccountModel) request.getAttribute("account");
    }
}