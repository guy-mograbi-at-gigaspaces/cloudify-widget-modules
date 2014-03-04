package cloudify.widget.website.controller;

import cloudify.widget.website.dao.IAccountDao;
import cloudify.widget.website.dao.IPoolDao;
import cloudify.widget.website.models.AccountModel;
import cloudify.widget.website.models.PoolConfigurationModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;


@SuppressWarnings("UnusedDeclaration")
@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private IAccountDao accountDao;

    @Autowired
    private IPoolDao poolDao;

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public String showIndex() {
        return "hello world!";
    }

    @RequestMapping(value="/account/{uuid}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getAccount( @PathVariable("uuid") String uuid ) {

        AccountModel accountModel = accountDao.readAccountByUuid( uuid );
        ResponseEntity<String> retValue;

        if( accountModel == null ) {
            retValue = new ResponseEntity<String>( HttpStatus.NOT_FOUND );
        }
        else{
            try {
                String objectStr = mapper.writeValueAsString(accountModel);
                retValue = new ResponseEntity<String>( objectStr, HttpStatus.OK );
            }
            catch( IOException e ) {
                if( logger.isErrorEnabled() ){
                    logger.error( "Unable to parse Account with uuid [" + uuid + "] to Json", e );
                }
                retValue = new ResponseEntity<String>( HttpStatus.NOT_FOUND );
            }
        }

        return retValue;
    }

    @RequestMapping(value="/pool/{accountId}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getPoolConfiguration( @PathVariable("accountId") Long accountId ) {

        PoolConfigurationModel poolConfigurationModel = poolDao.readPoolByAccountId( accountId );
        ResponseEntity<String> retValue;

        if( poolConfigurationModel == null ) {
            retValue = new ResponseEntity<String>( HttpStatus.NOT_FOUND );
        }
        else{
            try {
                String objectStr = mapper.writeValueAsString(poolConfigurationModel);
                retValue = new ResponseEntity<String>( objectStr, HttpStatus.OK );
            }
            catch( IOException e ) {
                if( logger.isErrorEnabled() ){
                    logger.error( "Unable to parse Pool Configuration with account id [" + accountId + "] to Json", e );
                }
                retValue = new ResponseEntity<String>( HttpStatus.NOT_FOUND );
            }
        }

        return retValue;
    }
}