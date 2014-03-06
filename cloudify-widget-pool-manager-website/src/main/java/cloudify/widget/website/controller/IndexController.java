package cloudify.widget.website.controller;

import cloudify.widget.website.dao.IAccountDao;
import cloudify.widget.website.dao.IPoolDao;
import cloudify.widget.website.models.AccountModel;
import cloudify.widget.website.models.PoolConfigurationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@SuppressWarnings("UnusedDeclaration")
@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private IAccountDao accountDao;

    @Autowired
    private IPoolDao poolDao;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public String showIndex(  @ModelAttribute("account") AccountModel accountModel ) {
        logger.info("account model id is : " + accountModel.id );
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

    @RequestMapping(value="/pools", method=RequestMethod.GET)
    public @ResponseBody List<PoolConfigurationModel> getPools(@ModelAttribute("account") AccountModel accountModel){
        try{
            return poolDao.readPools( accountModel );
        }catch(Exception e){
            logger.error("unable to map pool to JSON", e);
            return null;
//            return new ResponseEntity<String>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

    @RequestMapping(value="/pool/{poolId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<PoolConfigurationModel> getPoolConfiguration( @ModelAttribute("account") AccountModel accountModel, @PathVariable("poolId") Long poolId ) {

        PoolConfigurationModel poolConfigurationModel = poolDao.readPoolByAccountId( poolId, accountModel);
        ResponseEntity<PoolConfigurationModel> retValue;

        if( poolConfigurationModel == null ) {
            retValue = new ResponseEntity<PoolConfigurationModel>( HttpStatus.NOT_FOUND );
        }
        else{
            retValue = new ResponseEntity<PoolConfigurationModel>( poolConfigurationModel, HttpStatus.OK );
        }

        return retValue;
    }

    @RequestMapping(value="/admin/accounts", method=RequestMethod.GET)
    public @ResponseBody List<AccountModel> getAccounts(){
        try{
            return accountDao.readAccounts();
        }catch(Exception e){
            logger.error("unable to map pool to JSON", e);
            return null;
//            return new ResponseEntity<String>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

    @RequestMapping(value="/admin/pools", method=RequestMethod.GET)
    public @ResponseBody List<PoolConfigurationModel> getPools(){
        try{
            return poolDao.readPools();
        }catch(Exception e){
            logger.error("unable to map pool to JSON", e);
            return null;
//            return new ResponseEntity<String>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

    @ModelAttribute("account")
    public AccountModel getUser(HttpServletRequest request)
    {
        return (AccountModel) request.getAttribute("account");
    }
}