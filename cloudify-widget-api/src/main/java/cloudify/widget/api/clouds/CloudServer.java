package cloudify.widget.api.clouds;

import java.util.Map;

import com.google.common.collect.Multimap;

/**
 * @author evnmgenyf
 *         Date: 10/7/13
 */
public interface CloudServer {

    /**
     * @return the ip addresses assigned to the server
     */
//    @Deprecated
//    public Multimap<String, CloudAddress> getAddresses();

    /**
     * @return id
     */
    public String getId();

//    @Deprecated
//    public Map<String, String> getMetadata();

    public String getName();

    public CloudServerStatus getStatus();

    public ServerIp getServerIp();

}