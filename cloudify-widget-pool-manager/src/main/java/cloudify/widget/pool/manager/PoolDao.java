package cloudify.widget.pool.manager;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/21/14
 * Time: 4:08 AM
 */
public class PoolDao {
    private JdbcTemplate jdbcTemplate;
//    private BasicDataSource bds;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<MachineModel> getMachines() {
        return jdbcTemplate.query("select * from server_node", new MachineModelMapper());
    }

    private static final class MachineModelMapper implements RowMapper<MachineModel> {

        public MachineModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            MachineModel machine = new MachineModel();
            machine.id = rs.getLong("id");
            machine.publicIp = rs.getString("public_ip");
            machine.busySince = rs.getLong("busy_since");
            machine.remote = rs.getBoolean("remote");

//            actor.setLastName(rs.getString("last_name"));
            return machine;
        }
    }
}
