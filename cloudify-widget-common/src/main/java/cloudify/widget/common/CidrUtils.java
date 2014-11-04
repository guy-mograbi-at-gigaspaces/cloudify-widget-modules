package cloudify.widget.common;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 10/28/14
 * Time: 6:10 PM
 */
public class CidrUtils {


    /**
     * A class that enables to get an IP range from CIDR specification. It supports
     * both IPv4 and IPv6.
     */
    private final String cidr;

    private InetAddress inetAddress;
    private InetAddress startAddress;
    private InetAddress endAddress;
    private final int prefixLength;


    public CidrUtils(String cidr) throws UnknownHostException {

        // guy - add support to single ip
        if ( !cidr.contains("/")){
            this.cidr = cidr + "/32"; // single ip representation in
        }else{
            this.cidr = cidr;
        }

        /* split CIDR to address and prefix part */
        int index = this.cidr.indexOf("/");
        String addressPart = this.cidr.substring(0, index);
        String networkPart = this.cidr.substring(index + 1);

        inetAddress = InetAddress.getByName(addressPart);
        prefixLength = Integer.parseInt(networkPart);

        calculate();
    }


    private void calculate() throws UnknownHostException {

        ByteBuffer maskBuffer;
        int targetSize;
        if (inetAddress.getAddress().length == 4) {
            maskBuffer =
                    ByteBuffer
                            .allocate(4)
                            .putInt(-1);
            targetSize = 4;
        } else {
            maskBuffer = ByteBuffer.allocate(16)
                    .putLong(-1L)
                    .putLong(-1L);
            targetSize = 16;
        }

        BigInteger mask = (new BigInteger(1, maskBuffer.array())).not().shiftRight(prefixLength);

        ByteBuffer buffer = ByteBuffer.wrap(inetAddress.getAddress());
        BigInteger ipVal = new BigInteger(1, buffer.array());

        BigInteger startIp = ipVal.and(mask);
        BigInteger endIp = startIp.add(mask.not());

        byte[] startIpArr = toBytes(startIp.toByteArray(), targetSize);
        byte[] endIpArr = toBytes(endIp.toByteArray(), targetSize);

        this.startAddress = InetAddress.getByAddress(startIpArr);
        this.endAddress = InetAddress.getByAddress(endIpArr);

    }

    private byte[] toBytes(byte[] array, int targetSize) {
        int counter = 0;
        List<Byte> newArr = new ArrayList<Byte>();
        while (counter < targetSize && (array.length - 1 - counter >= 0)) {
            newArr.add(0, array[array.length - 1 - counter]);
            counter++;
        }

        int size = newArr.size();
        for (int i = 0; i < (targetSize - size); i++) {

            newArr.add(0, (byte) 0);
        }

        byte[] ret = new byte[newArr.size()];
        for (int i = 0; i < newArr.size(); i++) {
            ret[i] = newArr.get(i);
        }
        return ret;
    }

    public String getNetworkAddress() {

        return this.startAddress.getHostAddress();
    }

    public InetAddress getStartAddress() {
        return startAddress;
    }

    public InetAddress getEndAddress() {
        return endAddress;
    }

    public String getBroadcastAddress() {
        return this.endAddress.getHostAddress();
    }

    /**
     *
     * checks if range of ipAddress is contained inside my range
     *
     * @param ipAddress - cidr representation or single ip
     * @return
     * @throws UnknownHostException
     */
    public boolean isInRange(String ipAddress) throws UnknownHostException {

        CidrUtils target = new CidrUtils(ipAddress);

        BigInteger start = new BigInteger(1, this.startAddress.getAddress());
        BigInteger end = new BigInteger(1, this.endAddress.getAddress());

        BigInteger targetStart = new BigInteger(1, target.getStartAddress().getAddress());
        BigInteger targetEnd = new BigInteger(1, target.getEndAddress().getAddress());

        int st = start.compareTo(targetStart);
        int te = targetEnd.compareTo(end);

        return (st == -1 || st == 0) && (te == -1 || te == 0);
    }

}
