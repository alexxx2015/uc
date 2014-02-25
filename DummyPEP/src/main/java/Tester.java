import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class Tester {
	public static void main(String[] args) throws SocketException {
		System.out.println(getInetAddresses(Inet4Address.class));
		System.out.println(getInetAddresses(Inet6Address.class));
		System.out.println(getInetAddresses(InetAddress.class));
	}


	/**
	 * Returns this machines IP addresses, IPv4, IPv6, or both -- depending on the
	 * specified parameter.
	 *
	 * @param clazz Specifies which IP addresses to return: Inet4Address.class (IPv4), Inet6Address.class (IPv6), or InetAddress.class (both)
	 * @return the set of IP addresses, possibly empty. Never null.
	 */
	public static <T extends InetAddress> Set<T> getInetAddresses(Class<T> clazz) {
		Set<T> result = new HashSet<T>();

		Enumeration<NetworkInterface> interfaces = null;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			return result;
		}

		if (interfaces != null) {
			while (interfaces.hasMoreElements()) {
				NetworkInterface current = interfaces.nextElement();
				try {
					if (!current.isUp() || current.isLoopback()
							|| current.isVirtual()) {
						continue;
					}
				} catch (SocketException e) {
				}

				Enumeration<InetAddress> addresses = current.getInetAddresses();

				if (addresses != null) {
					while (addresses.hasMoreElements()) {
						InetAddress current_addr = addresses.nextElement();
						if (current_addr.isLoopbackAddress()) {
							continue;
						}

						try {
							clazz.cast(current_addr);
							result.add((T) current_addr);
						}
						catch (ClassCastException e) {
						}
					}
				}
			}
		}

		return result;
	}
}
