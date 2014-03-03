package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.NodeModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * User: eliranm
 * Date: 3/2/14
 * Time: 7:09 PM
 */
public class PoolDao {

    public static final String POOL_ID = "pool_id";
    public static final String NODE_STATUS = "node_status";
    public static final String MACHINE_ID = "machine_id";
    public static final String CLOUDIFY_VERSION = "cloudify_version";

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void create(final NodeModel nodeModel) {
        jdbcTemplate.update(
                "insert into nodes (" + POOL_ID + "," + NODE_STATUS + "," + MACHINE_ID + "," + CLOUDIFY_VERSION + ") values (?, ?, ?, ?)",
                nodeModel.poolId, nodeModel.nodeStatus.name(), nodeModel.machineId, nodeModel.cloudifyVersion
        );
    }

    public List<NodeModel> read() {
        return jdbcTemplate.query("select * from nodes", new RowMapper<NodeModel>() {
            @Override
            public NodeModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                NodeModel nodeModel = new NodeModel();
                nodeModel.id = rs.getLong("id");
                nodeModel.poolId = rs.getString(POOL_ID);
                nodeModel.nodeStatus = NodeModel.NodeStatus.valueOf(rs.getString(NODE_STATUS));
                nodeModel.machineId = rs.getString(MACHINE_ID);
                nodeModel.cloudifyVersion = rs.getString(CLOUDIFY_VERSION);
                return nodeModel;
            }
        });
    }

    public void update(NodeModel nodeModel) {
        jdbcTemplate.update("update nodes set " + POOL_ID + " = ?," + NODE_STATUS + " = ?," + MACHINE_ID + " = ?," + CLOUDIFY_VERSION + " = ?  where id = ?",
                nodeModel.poolId, nodeModel.nodeStatus.name(), nodeModel.machineId, nodeModel.cloudifyVersion, nodeModel.id);
    }

    public void delete(String id) {
        jdbcTemplate.update("delete from nodes where id = ?", id);
    }

}
