package cloudify.widget.website.dao;

import cloudify.widget.website.dao.mappers.AccountRowMapper;
import cloudify.widget.website.dao.mappers.PoolRowMapper;
import cloudify.widget.website.models.AccountModel;
import cloudify.widget.website.models.PoolConfigurationModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * User: evgenyf
 * Date: 2/27/14
 */
public class AccountDaoImpl implements IAccountDao {

    private static final String TABLE_NAME = "account";
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {


        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long createAccount( AccountModel account ) {

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert( jdbcTemplate ).
                                    withTableName(TABLE_NAME).
                                    usingGeneratedKeyColumns("id");

        Map<String,Object> parametersMap = new HashMap<String,Object>(1);
        parametersMap.put( "uuid", account.getUuid() );

        Number id = jdbcInsert.executeAndReturnKey(parametersMap);

        return ( Long )id;
    }

    @Override
    public boolean deleteAccount( Long id ) {
        String delQuery = "delete from " + TABLE_NAME + " where id = ?";
        int count = jdbcTemplate.update(delQuery, new Object[]{id});
        return count > 0;
    }

    @Override
    public AccountModel readAccountByUuid( String uuid ) {

        String sql = "select * from " + TABLE_NAME + " where uuid = ?";

        AccountModel accountModel  =
                ( AccountModel )jdbcTemplate.queryForObject(sql, new Object[]{uuid}, new AccountRowMapper());
        return accountModel;
    }
}