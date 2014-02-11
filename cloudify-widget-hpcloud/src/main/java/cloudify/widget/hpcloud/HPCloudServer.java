package cloudify.widget.hpcloud;


/**
 * HP implementation of CloudServer
 * @author evgenyf
 * Date: 10/7/13
 */
public class HPCloudServer {}
//implements CloudServer{
//
//	private final Server server;
//
//	public HPCloudServer( Server server ){
//		this.server = server;
//	}
//
//	@Override
//	public Multimap<String, CloudAddress> getAddresses() {
//		Multimap<String, Address> addresses = server.getAddresses();
//		Multimap<String, CloudAddress> resultMultimap = ArrayListMultimap.create();
//
//		Set<String> keySet = addresses.keySet();
//		for( String key : keySet ){
//			Collection<Address> addressesCollection = addresses.get( key );
//			List<CloudAddress> newAddresses = new LinkedList<CloudAddress>();
//			for( Address address : addressesCollection ){
//				CloudAddress cloudAddress = createCloudAddress( address );
//				newAddresses.add( cloudAddress );
//			}
//			resultMultimap.putAll( key, newAddresses );
//		}
//
//		return resultMultimap;
//	}
//
//	@Override
//	public String getId() {
//		return server.getId();
//	}
//
//	@Override
//	public Map<String, String> getMetadata() {
//		return server.getMetadata();
//	}
//
//	@Override
//	public String getName() {
//		return server.getName();
//	}
//
//	private static CloudAddress createCloudAddress( Address address ){
//		return new HPCloudAddress( address );
//	}
//
//	@Override
//	public HpCloudServerStatus getStatus() {
//		Status status = server.getStatus();
//		HpCloudServerStatus retValue = null;
//		switch( status ){
//		case ACTIVE:
//			retValue = HpCloudServerStatus.ACTIVE;
//			break;
//		case BUILD:
//			retValue = HpCloudServerStatus.BUILD;
//			break;
//		case DELETED:
//			retValue = HpCloudServerStatus.DELETED;
//			break;
//		case ERROR:
//			retValue = HpCloudServerStatus.ERROR;
//			break;
//		case HARD_REBOOT:
//			retValue = HpCloudServerStatus.HARD_REBOOT;
//			break;
//		case PASSWORD:
//			retValue = HpCloudServerStatus.PASSWORD;
//			break;
//		case PAUSED:
//			retValue = HpCloudServerStatus.PAUSED;
//			break;
//		case REBOOT:
//			retValue = HpCloudServerStatus.REBOOT;
//			break;
//		case REBUILD:
//			retValue = HpCloudServerStatus.REBUILD;
//			break;
//		case RESIZE:
//			retValue = HpCloudServerStatus.RESIZE;
//			break;
//		case REVERT_RESIZE:
//			retValue = HpCloudServerStatus.REVERT_RESIZE;
//			break;
//		case STOPPED:
//			retValue = HpCloudServerStatus.STOPPED;
//			break;
//		case SUSPENDED:
//			retValue = HpCloudServerStatus.SUSPENDED;
//			break;
//		case UNKNOWN:
//			retValue = HpCloudServerStatus.UNKNOWN;
//			break;
//		case VERIFY_RESIZE:
//			retValue = HpCloudServerStatus.VERIFY_RESIZE;
//			break;
//		case UNRECOGNIZED:
//		default:
//			retValue = HpCloudServerStatus.UNRECOGNIZED;
//			break;
//
//		}
//
//		return retValue;
//	}
//
//	@Override
//	public String toString() {
//		return "HPCloudServer [server=" + server + "]";
//	}
//}
