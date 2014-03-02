package cloudify.widget.pool.manager.settings.dto;

import cloudify.widget.api.clouds.IConnectDetails;
import cloudify.widget.api.clouds.MachineOptions;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * <p>
 * <b>Notice:</b> we must send references of the classes annotated inside @JsonSubTypes to their superclass (this one)
 * or they will be shaded when accessing them from the superclass after deserialization. example:
 * </p>
 * <p>
 * we must do this in HpProviderSettings:
 * </p>
 * <pre>
 * public void setConnectDetails(HpCloudComputeConnectDetails connectDetails) {
 *     super.connectDetails = connectDetails;
 * }
 * </pre>
 *
 * <p>
 * or a null pointer exception will be thrown when calling:
 * </p>
 * <pre>
 * hpProviderSettings.connectDetails
 * </pre>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "name",
        visible = true) // we want the 'name' property to be in the output as well
@JsonSubTypes({
        @JsonSubTypes.Type(value = HpProviderSettings.class, name = "hp"),
        @JsonSubTypes.Type(value = Ec2ProviderSettings.class, name = "ec2"),
        @JsonSubTypes.Type(value = SoftlayerProviderSettings.class, name = "softlayer")})
public class ProviderSettings {

    public static enum ProviderName {
        hp,softlayer,ec2;
    }

    public ProviderName name;
    public IConnectDetails connectDetails;
    public MachineOptions machineOptions;
}
