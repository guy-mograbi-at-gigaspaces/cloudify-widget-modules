package cloudify.widget.ec2;

import cloudify.widget.api.clouds.ISshDetails;

/**
 * User: evgeny
 * Date: 2/10/14
 * Time: 6:55 PM
 */
public class Ec2SshDetails implements ISshDetails {

    private final int port;
    private final String user;
    private final String privateKey;
    private final String publicIp;

    public Ec2SshDetails( int port, String user, String privateKey, String publicIp ){
        this.port = port;
        this.user = user;
        this.privateKey = privateKey;
        this.publicIp = publicIp;
    }

    public int getPort(){
        return port;
    }

    public String getUser(){
        return user;
    }

    public String getPrivateKey(){
        return privateKey;
    }

    public String getPublicIp(){
        return publicIp;
    }
}