package cloudify.widget.pool.manager.node_management;

import cloudify.widget.pool.manager.tasks.TaskName;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.*;

/**
* User: eliranm
* Date: 4/28/14
* Time: 3:24 PM
*/
public class Decision {

    private TaskName _to;

    private DecisionDetails _decisionDetails;

    // TODO how is to() used? use it!

    public Decision to(TaskName to) {
        _to = to;
        return this;
    }

    public Decision details(DecisionDetails decisionDetails) {
        _decisionDetails = decisionDetails;
        return this;
    }

    @Override
    public String toString() {
        return "Decision{" +
                "_to=" + _to +
                ", _decisionDetails=" + _decisionDetails +
                '}';
    }


    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = CreateDecisionDetails.class, name = "createDecisionDetails"),
            @JsonSubTypes.Type(value = DeleteDecisionDetails.class, name = "deleteDecisionDetails"),
            @JsonSubTypes.Type(value = PrepareDecisionDetails.class, name = "prepareDecisionDetails"),
    })
    public static interface DecisionDetails {
    }

    public static class CreateDecisionDetails implements DecisionDetails {

        private int _instances = 0;

        public int getInstances() {
            return _instances;
        }

        public CreateDecisionDetails setInstances(int instances) {
            _instances = instances;
            return this;
        }

        @Override
        public String toString() {
            return "CreateDecisionDetails{" +
                    "_instances=" + _instances +
                    '}';
        }
    }

    public static class DeleteDecisionDetails implements DecisionDetails {

        private Collection<String> _machineIds = new HashSet<String>();

        public Collection<String> getMachineIds() {
            return _machineIds;
        }

        public void setMachineIds(Collection<String> machineIds) {
            _machineIds = machineIds;
        }

        public DeleteDecisionDetails addMachineIds(Set<String> machineIds) {
            _machineIds.addAll(machineIds);
            return this;
        }

        @Override
        public String toString() {
            return "DeleteDecisionDetails{" +
                    "_machineIds=" + _machineIds +
                    '}';
        }
    }

    public static class PrepareDecisionDetails implements DecisionDetails {

        private Collection<String> _machineIds = new HashSet<String>();

        public Collection<String> getMachineIds() {
            return _machineIds;
        }

        public void setMachineIds(Collection<String> machineIds) {
            _machineIds = machineIds;
        }

        public PrepareDecisionDetails addMachineIds(Set<String> machineIds) {
            _machineIds.addAll(machineIds);
            return this;
        }

        @Override
        public String toString() {
            return "PrepareDecisionDetails{" +
                    "_machineIds=" + _machineIds +
                    '}';
        }
    }

}
