package de.tum.in.i22.uc.thrift.generator;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ThriftSpecificationGenerator {

	private final String[] _namespaces;

	private final String[] _includes;

	private final URL _folder;

	private final Set<String> _interfaces;

	private final ClassLoader _classLoader;


	public ThriftSpecificationGenerator(String[] namespaces, String[] includes, URL folder, Set<String> interfaces, ClassLoader classLoader) {
		_namespaces = namespaces;
		_includes = includes;
		_folder = folder;
		_interfaces = interfaces;
		_classLoader = classLoader;
	}

	public ThriftSpecificationGenerator(String[] namespaces, String[] includes, URL folder, Set<String> interfaces) {
		this(namespaces, includes, folder, interfaces, ClassLoader.getSystemClassLoader());
	}


	public String generate() {
		@SuppressWarnings("resource")
		ClassLoader loader = new URLClassLoader(new URL[] { _folder }, _classLoader);

		List<ThriftService> thriftServices = new LinkedList<>();

		/*
		 * Loop over all provided interfaces ...
		 */
		for (String iface : _interfaces) {
			Class<?> cls = null;
			try {
				cls = loader.loadClass(iface);
			} catch (ClassNotFoundException e) {
				System.err.println(e);
			}

			if (cls == null) {
				continue;
			}

			System.out.println(cls.getAnnotations().length);

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
		sb.append("/* " + System.lineSeparator() + " * Auto-generated Thrift definitions." + System.lineSeparator()
				+ " * Generated on " + date + System.lineSeparator() + " * from the following interface definitions:"
				+ System.lineSeparator());
		for (ThriftService service : thriftServices) {
			sb.append(" * - " + service.getOriginalIface() + System.lineSeparator());
		}
		sb.append(" */" + System.lineSeparator() + System.lineSeparator());

		/*
		 * Namespaces and includes
		 */
		for (String ns : _namespaces) {
			sb.append("namespace " + ns + System.lineSeparator());
		}
		sb.append(System.lineSeparator());

		for (String incl : _includes) {
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















	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException {
		String[] namespaces = { "cpp de.tum.in.i22.uc.thrift.types",
				"csharp de.tum.in.i22.uc.thrift.types", "java de.tum.in.i22.uc.thrift.types" };

		String[] includes = { "Types.thrift" };

		File parent = new File(System.getProperty("user.dir"), "../../../../CommunicationBase/target/classes/");

		URL baseFolder = new URL("file://" + parent + "/");

		Set<String> interfaces = new HashSet<>();
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IPep2Pdp");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IPep2Pip");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IPip2Pip");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IPmp2Pmp");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IPxp2Pdp");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IAny2Pip");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IPdp2Any");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IPip2Pmp");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IPmp2Pip");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IPdp2Pip");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IAny2Pdp");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IAny2Pxp");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IPep2Any");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IPip2Any");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IPdp2Pxp");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IPmp2Any");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IPxp2Any");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IAny2Pmp");
		interfaces.add("de.tum.in.i22.uc.cm.interfaces.IAny2Any");

		ThriftSpecificationGenerator generator = new ThriftSpecificationGenerator(namespaces, includes, baseFolder, interfaces);

		System.out.println(generator.generate());
	}


}
