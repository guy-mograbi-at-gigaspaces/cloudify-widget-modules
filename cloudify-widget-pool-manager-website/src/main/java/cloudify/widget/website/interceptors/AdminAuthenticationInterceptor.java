package cloudify.widget.website.interceptors;

import cloudify.widget.common.StringUtils;
import cloudify.widget.website.config.AppConfig;
import cloudify.widget.website.dao.IAccountDao;
import cloudify.widget.website.dao.IPoolDao;
import cloudify.widget.website.models.AccountModel;
import cloudify.widget.website.models.PoolConfigurationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 3/5/14
 * Time: 5:03 PM
 */

public class AdminAuthenticationInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = LoggerFactory.getLogger(AdminAuthenticationInterceptor.class);

    @Autowired
    private IAccountDao accountDao;

    @Autowired
    private AppConfig conf;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        logger.info("in Admin interceptor");
        String adminUuid = request.getHeader("AdminUuid");

        if( StringUtils.isEmpty( adminUuid ) ){
            response.sendError(401, "{'message' : 'admin uuid missing on request header'}");
            return false;
        }
        else{
             boolean adminUidsIdentical = StringUtils.equals( conf.getAdminUuid(), adminUuid );
            if( !adminUidsIdentical ){
                response.sendError(401, "{'message' : 'admin uuid authentication failed'}");
                return false;
            }
        }

        String accountUuid = request.getHeader("AccountUuid");
        if( !StringUtils.isEmpty( accountUuid ) ) {
            AccountModel accountModel = accountDao.readAccountByUuid(accountUuid);
            if( accountModel == null ) {
                response.sendError(401, "{'message' : 'Account with uuid [" + accountUuid + "] not found'}");
                return false;
            }

            request.setAttribute("account", accountModel);
        }

        return super.preHandle(request, response, handler);
    }

    public IAccountDao getAccountDao() {
        return accountDao;
    }

    public void setAccountDao(IAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public AppConfig getConf() {
        return conf;
    }

    public void setConf(AppConfig conf) {
        this.conf = conf;
    }
}