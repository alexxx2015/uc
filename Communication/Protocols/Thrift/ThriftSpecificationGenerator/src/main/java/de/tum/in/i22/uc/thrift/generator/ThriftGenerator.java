package de.tum.in.i22.uc.thrift.generator;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

public class ThriftGenerator {

	public static final String[] NAMESPACES = {
		"cpp de.tum.in.i22.uc.thrift.types",
		"csharp de.tum.in.i22.uc.thrift.types",
		"java de.tum.in.i22.uc.thrift.types"
	};

	public static final String[] INCLUDES = {
		"Types.thrift"
	};

	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException {
		File parent = new File(System.getProperty("user.dir"), "../../../CommunicationBase/target/classes/");

		URL baseFolder = new URL("file://" + parent + "/");

		Set<String> interfaces = Sets.newHashSet(
				"de.tum.in.i22.uc.cm.interfaces.IPep2Pdp",
				"de.tum.in.i22.uc.cm.interfaces.IPep2Pip",
				"de.tum.in.i22.uc.cm.interfaces.IPip2Pip",
				"de.tum.in.i22.uc.cm.interfaces.IPmp2Pmp",
				"de.tum.in.i22.uc.cm.interfaces.IPxp2Pdp",
				"de.tum.in.i22.uc.cm.interfaces.IAny2Pip",
				"de.tum.in.i22.uc.cm.interfaces.IPdp2Any",
				"de.tum.in.i22.uc.cm.interfaces.IPip2Pmp",
				"de.tum.in.i22.uc.cm.interfaces.IPmp2Pip",
				"de.tum.in.i22.uc.cm.interfaces.IPdp2Pip",
				"de.tum.in.i22.uc.cm.interfaces.IAny2Pdp",
				"de.tum.in.i22.uc.cm.interfaces.IAny2Pxp",
				"de.tum.in.i22.uc.cm.interfaces.IPep2Any",
				"de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp",
				"de.tum.in.i22.uc.cm.interfaces.IPip2Any",
				"de.tum.in.i22.uc.cm.interfaces.IPdp2Pxp",
				"de.tum.in.i22.uc.cm.interfaces.IPmp2Any",
				"de.tum.in.i22.uc.cm.interfaces.IPxp2Any",
				"de.tum.in.i22.uc.cm.interfaces.IAny2Pmp",
				"de.tum.in.i22.uc.cm.interfaces.IAny2Any");

		System.out.println(generateFrom(baseFolder, interfaces));
	}

	public static String generateFrom(URL baseFolder, Collection<String> interfaces) throws ClassNotFoundException {
		@SuppressWarnings("resource")
		ClassLoader loader = new URLClassLoader(new URL[] { baseFolder }, ClassLoader.getSystemClassLoader());

		List<ThriftService> thriftServices = new LinkedList<>();

		/*
		 * Loop over all provided interfaces ...
		 */
		for (String iface : interfaces) {
			Class<?> cls = loader.loadClass(iface);

			/*
			 * read the interface's ThriftService annotation
			 */
			AThriftService serviceAnnotation = cls.getAnnotation(AThriftService.class);
			if (serviceAnnotation == null) {
				System.err.println("No thrift annotation found for " + iface + ". Skipping.");
				continue;
			}

			ThriftService service = new ThriftService(serviceAnnotation.name(), iface);

			/*
			 * loop over all the interface's methods
			 */
			for (Method method : cls.getMethods()) {

				/*
				 * read the method's ThriftMethod annotation
				 */
				AThriftMethod methodAnnotation = method.getAnnotation(AThriftMethod.class);
				if (methodAnnotation == null) {
					System.err.println("No thrift annotation found for " + method + ". Skipping.");
					continue;
				}

				// add the ThriftMethod to the ThriftService
				service.add(new ThriftMethod(methodAnnotation.signature(), iface, method.toString()));
			}

			thriftServices.add(service);
		}

		String date = new SimpleDateFormat("yyyy/MM/dd, HH:mm:ss").format(Calendar.getInstance().getTime());

		/*
		 * File header
		 */
		StringBuilder sb = new StringBuilder();
		sb.append("/* " + System.lineSeparator()
				+ " * Auto-generated Thrift definitions." + System.lineSeparator()
				+ " * Generated on " + date + System.lineSeparator()
				+ " * from the following interface definitions:" + System.lineSeparator());
		for (ThriftService service : thriftServices) {
			sb.append(" * - " + service.getOriginalIface() + System.lineSeparator());
		}
		sb.append(" */" + System.lineSeparator() + System.lineSeparator());

		/*
		 * Namespaces and includes
		 */
		for (String ns : NAMESPACES) {
			sb.append("namespace " + ns + System.lineSeparator());
		}
		sb.append(System.lineSeparator());

		for (String incl : INCLUDES) {
			sb.append("include \"" + incl + "\"" + System.lineSeparator());
		}
		sb.append(System.lineSeparator());


		/*
		 * The thrift definitions
		 */
		for (ThriftService service : thriftServices) {
			sb.append(service + System.lineSeparator() + System.lineSeparator());
		}

		return sb.toString();
	}
}
