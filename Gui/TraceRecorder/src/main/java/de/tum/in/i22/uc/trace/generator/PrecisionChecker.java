package de.tum.in.i22.uc.trace.generator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.linux.FilenameName;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.client.Any2PipClient;
import de.tum.in.i22.uc.pip.eventdef.linux.LinuxEvents;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

class Info {
	public boolean estimationIsCorrect = true; // conservative estimation
	public int numActualTaint;
	public int numEstimatedTaint;
	public double precision;

	public Info(boolean estimationIsCorrect, int numActualTaint, int numEstimatedTaint, double precision) {
		super();
		this.estimationIsCorrect = estimationIsCorrect;
		this.numActualTaint = numActualTaint;
		this.numEstimatedTaint = numEstimatedTaint;
		this.precision = precision;
	}

	@Override
	public String toString() {
		return precision + "," + numActualTaint + "," + numEstimatedTaint + "," + estimationIsCorrect;
	}
	
	public double getPrecision(){
		return precision;
	}
}

public class PrecisionChecker {
	static int PORT = 21004;
	static String HOST = "localhost";
	static String ACTUAL_PROP_FILE = "precision.properties";
	static String OUTPUT_FILE = "output.csv";

	static void printHelp() {
		System.out.println("\nUsage: java [...javacp..]PrecisionChecker [OPTIONS] ");
		System.out.println("\n");
		System.out.println("OPTIONS:\n");
		System.out.println("\t--actualPropFile s");
		System.out
				.println("\t\t Set the input file containing the actual data stored in files. Default = precision.properties (String)\n");

		System.out.println("\t--output s");
		System.out
				.println("\t\t Set the output file for the csv containing the precision results. Defualt = output.csv (String)\n");

		System.out.println("\t--host s");
		System.out
				.println("\t\t Set the URL of the uc infrastructure. The server must implement at least the PIP2PIP interface. Default = localhost (String)\n");

		System.out.println("\t--port i");
		System.out
				.println("\t\t Set the port for the connection to the UC infrastructure. The server must implement at least the PIP2PIP interface. Default = 21003 (Integer)\n");

		System.out.println("\t--help");
		System.out.println("\t\t Prints this help\n");

		System.exit(0);

	}

	static private void parseParameters(String[] args) {
		int i = 0;
		while (i < args.length) {
			switch (args[i]) {
			case "--actualPropFile":
				ACTUAL_PROP_FILE = String.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--output":
				OUTPUT_FILE = String.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--port":
				PORT = Integer.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--host":
				HOST = String.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--help":
				printHelp();
				break;

			default:
				System.out
						.println(args[i]
								+ " does not seem to be a valid option. Type \"java TraceGenerator --help\" for a list of valid options.");
				System.exit(1);
				break;
			}

		}

	}

	public static void main(String[] args) {
		Map<String, TreeSet<String>> actual = new HashMap<String, TreeSet<String>>();
		Map<String, Info> precision = new HashMap<String, Info>();

		Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		parseParameters(args);

		InputStream stream = loader.getResourceAsStream(ACTUAL_PROP_FILE);
		try {
			prop.load(stream);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.err.println("Impossible to continue without proper input file! Aborting..");
			System.exit(1);
		}

		ThriftClientFactory tf = new ThriftClientFactory();
		Location location = new IPLocation(HOST, PORT);
		Any2PipClient pipClient = tf.createAny2PipClient(location);
		try {
			pipClient.connect();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Impossible to connect to usage control infrastructure ["+HOST+","+PORT+"]. Aborting..");
			System.exit(1);
		}

		
		String list = prop.getProperty("list");
		System.out.println("list: " + list);

		for (String file : list.split(",")) {
			if (!file.equals("")) {
				String ds = prop.getProperty(file);
				TreeSet<String> dataList = new TreeSet<String>();
				if (ds != null) {
					for (String d : ds.split(",")) {
						dataList.add(d);
					}
				}
				actual.put(file, dataList);
			}
		}

		System.out.println("------------------------");
		System.out.println("ACTUAL:");
		for (String key : actual.keySet()) {
			System.out.print(key + ": ");
			TreeSet<String> dl = (TreeSet<String>) actual.get(key);
			if (dl != null) {
				for (String d : dl) {
					System.out.print(d);
					if (!d.equals(dl.last()))
						System.out.print(",");
				}
			}
			System.out.println("");
		}
		
		System.out.println("------------------------");
		System.out.println("ESTIMATED:");

		for (String file : actual.keySet()) {
			boolean estimationIsCorrect = true;

			IName filename = FilenameName.create("distr1", LinuxEvents.toRealPath(file));
			Set<IData> ds = pipClient.getDataInContainer(filename);
			System.out.print(filename.getName()+":");
		
			
			if (ds == null)
				ds = new HashSet<IData>();

			
			TreeSet<String> dl = new TreeSet<String>();
			Set<IData> flat = new HashSet<IData>();
			Set<IData> allflat = new HashSet<IData>();
			
			
			for (IData d: ds) {
				flat = pipClient.flattenStructure(d);
				allflat.addAll(flat);
				for (IData fd: flat) {
					dl.add(fd.getId());
				}
			}
			
			ds=allflat;
			
			if (dl != null) {
				for (String s : dl) {
					System.out.print(s);
					if (!s.equals(dl.last()))
						System.out.print(",");
				}
			}
			System.out.println("");

			
			
			Set<String> actualIds = actual.get(file);
			for (String s : actualIds){
				if (!dl.contains(s)) estimationIsCorrect=false;
			}
			
			precision.put(filename.getName(), new Info(estimationIsCorrect, actualIds.size(), ds.size(),
					(((double) (actualIds.size())/(double) (ds.size())))));
		}

		Set<String> sortedNames = new TreeSet<String>();
		sortedNames.addAll(precision.keySet());

		System.out.println("------------------------");
		System.out.println("\n\nPRECISION:");
		PrintWriter writer;
		
		double[] values = new double[precision.size()];
		double sum=0;
		int i=0;
		try {
			writer = new PrintWriter(OUTPUT_FILE);

			for (String s : sortedNames) {
				Info element = (Info) precision.get(s);
				values[i]=element.getPrecision();
				sum+=values[i];
				String output = s + "," + element;
				System.out.println(output);
				writer.println(output);
				i++;
			}

			Arrays.sort(values);
			double median;
			if (values.length % 2 == 0)
			    median = ((double)values[values.length/2] + (double)values[values.length/2 - 1])/2;
			else
			    median = (double) values[values.length/2];
			
			System.out.println("Median,"+median);
			writer.println("Median,"+median);
			
			double average=sum/values.length;
			
			
			System.out.println("Average,"+average);
			writer.println("Average,"+average);
			
			
			writer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pipClient.disconnect();
	}
}
