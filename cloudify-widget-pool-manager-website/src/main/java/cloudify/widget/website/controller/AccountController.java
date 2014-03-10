package cloudify.widget.website.controller;

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
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private IAccountDao accountDao;

    @Autowired
    private IPoolDao poolDao;

    @RequestMapping(value="/account/pools", method=RequestMethod.GET)
    @ResponseBody
    public List<PoolConfigurationModel> getPools( @ModelAttribute("account") AccountModel accountModel){
        try{
            return poolDao.readPools( accountModel );
        }catch(Exception e){
            logger.error("unable to retrieve pools", e);
            return null;
//            return new ResponseEntity<String>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

    @RequestMapping(value="/account/pools/{poolId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<PoolConfigurationModel> getPoolConfiguration( @ModelAttribute("account") AccountModel accountModel, @PathVariable("poolId") Long poolId ) {

        PoolConfigurationModel poolConfigurationModel = poolDao.readPoolByIdAndAccountId( poolId, accountModel.getId());
        ResponseEntity<PoolConfigurationModel> retValue;

        if( poolConfigurationModel == null ) {
            retValue = new ResponseEntity<PoolConfigurationModel>( HttpStatus.NOT_FOUND );
        }
        else{
            retValue = new ResponseEntity<PoolConfigurationModel>( poolConfigurationModel, HttpStatus.OK );
        }

        return retValue;
    }

    @RequestMapping(value="/account/pools", method=RequestMethod.POST)
    @ResponseBody
    public Long createPool(@ModelAttribute("account") AccountModel accountModel, @RequestBody String poolSettingJson){
        try{
            return poolDao.createPool( accountModel.getId(), poolSettingJson );
        }catch(Exception e){
            logger.error("unable to createPool", e);
            return null;
//            return new ResponseEntity<String>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

    @RequestMapping(value="/account/pools/{poolId}", method=RequestMethod.POST)
    @ResponseBody
    public boolean updatePoolConfiguration( @ModelAttribute("account") AccountModel accountModel,
                                            @PathVariable("poolId") Long poolId, @RequestBody String poolSettingJson ) {

        return poolDao.updatePool( poolId, accountModel.getId(), poolSettingJson );
    }

    @ModelAttribute("account")
    public AccountModel getUser(HttpServletRequest request)
    {
        return (AccountModel) request.getAttribute("account");
    }

}