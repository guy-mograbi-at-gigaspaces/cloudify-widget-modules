package cloudify.widget.website.dao;

import cloudify.widget.website.models.AccountModel;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/27/14
 * Time: 10:39 AM
 */
public interface IAccountDao {

    public Long createAccount( AccountModel account );

    public boolean deleteAccount( Long id );

}
