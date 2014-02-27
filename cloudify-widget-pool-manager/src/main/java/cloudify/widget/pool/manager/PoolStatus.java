package cloudify.widget.pool.manager;

import cloudify.widget.common.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/21/14
 * Time: 3:15 AM
 */
public class PoolStatus {
    @JsonIgnore
    public Map<MachineId, PoolMachineStatus> machineStatuses = new HashMap<MachineId, PoolMachineStatus>();

    @JsonIgnore
    public Map<MachineId, MachineModel> machineModels = new HashMap<MachineId, MachineModel>();

    private static Logger logger = LoggerFactory.getLogger(PoolStatus.class);

    public int total(){
        return machineStatuses.size();
    }

    public int getManagementAvailableCount(){
        return CollectionUtils.countMatches(machineStatuses.values(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return ((PoolMachineStatus) object).managementAvailable;
            }
        });
    }

    public int getApplicationAvailableCount(){
        return CollectionUtils.countMatches(machineStatuses.values(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return ((PoolMachineStatus) object).applicationIsOnline;
            }
        });
    }

    private boolean isInCloud( MachineId machineId ){
        return machineStatuses.containsKey(machineId );
    }

    public int getBusyMachinesCount(){
        return CollectionUtils.countMatches( machineModels.values(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return ((MachineModel)object).busySince > 0;
            }
        });
    }

    private boolean isInDb( MachineId machineId ){
        return machineModels.containsKey(machineId);
    }

    @JsonIgnore
    public Collection<MachineModel> getMachinesModelsWithoutStatus(){

        final Map<MachineId, PoolMachineStatus> finalItems = machineStatuses;

        return CollectionUtils.select( machineModels.values(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return !finalItems.containsKey( ((MachineModel)object).getMachineId());
            }
        });
    }


//    public Collection<MachineModel> getMachineModelWithStatus(){
//
//
//    }

    public int getTotalNodes(){
        return machineStatuses.size();
    }



    public int getTotalModels(){
        return machineModels.size();
    }
    public static class MachineModelWithStatus{
        public MachineModel machineModel;
        public PoolMachineStatus poolMachineStatus;
    }

//    public int validFreeMachines(){
//        CollectionUtils.countMatches( getMachine)
//    }

    public int busyMachines(){
        return CollectionUtils.countMatches( machineModels.values(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return ((MachineModel)object).busySince > 0;
            }
        });
    }

//    public int idleMachines(){
//
//
//
//    }




    @JsonIgnore
    public Collection<PoolMachineStatus> getMachineStatusWithoutModel(){

        final Map<MachineId, MachineModel> finalMachineModels = machineModels;

        return CollectionUtils.select(machineStatuses.values(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return !finalMachineModels.containsKey(((PoolMachineStatus) object).getMachineId());
            }
        });
    }

    public int getMachineStatusWithoutModelCount(){
        return getMachineStatusWithoutModel().size();
    }


    public int getMachineModelWithoutStatusCount(){
        return getMachinesModelsWithoutStatus().size();
    }

    @Override
    public String toString() {
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);

        }catch(Exception e){
            logger.error("invalid JSON mapping",e);
            return "N/A";
        }
    }



}
