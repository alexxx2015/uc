package de.tum.in.i22.uc.trace.generator;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;

/*****
 * Bug: if one zip file is extracted from within another zip file, the list of
 * its content is wrong.
 * 
 * 
 * @author moka
 *
 */

public class TraceGenerator {

	// not final because configurable via commandline parameters

	private static final boolean DEBUG = false;

	private static int traceLength = 20;
	private static long SEED = 1;
	private static int MAXRETRY = 50;

	private static double pZip = 5;
	private static double pUnzip = 5;
	private static double pCopy = 5;
	private static double pDelete = 5;

	private static double pMerge = 5;
	private static double pNew = 0.5;
	private static int numLinesToMerge = 10;

	private static int INITFILES = 10;
	private static int MAXFILES = 100;
	private static int MAXMERGE = 4;

	private static String ZIPEXTENSION = ".zip";
	private static String NORMALEXTENSION = ".txt";

	private static String FILEPREFIX = "file";

	private static class FileObject {
		private String filename;
		private List<FileObject> content;
		private boolean isZip = true;

		public FileObject(String filename, List<FileObject> content, boolean isZip) {
			this.filename = filename;
			this.content = content;
			this.isZip = isZip;
		}

		public FileObject(FileObject src) {
			this.filename = src.filename;
			this.content = new LinkedList<FileObject>(src.content);
			this.isZip = src.isZip;
		}

		public boolean isZip() {
			return isZip;
		}

		public String getFilename() {
			return filename;
		}

		public List<FileObject> getContent() {
			return content;
		}

		public void setContent(List<FileObject> content) {
			this.content = content;
		}

		public int getSizeOfContent() {
			if (content == null)
				return 0;
			return content.size();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((filename == null) ? 0 : filename.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FileObject other = (FileObject) obj;
			if (filename == null) {
				if (other.filename != null)
					return false;
			} else if (!filename.equals(other.filename))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return filename;
		}

	}

	private static final String ZIPCMD = "zip";
	private static final String UNZIPCMD = "unzip -o";
	private static final String COPYCMD = "cp";
	private static final String DELETECMD = "rm";
	private static final String MERGECMD = "python pymerge.py";

	private static double sumOfP;

	// mapping commands - probabilities execution
	public static final Map<String, Double> COMMANDS = new HashMap<>(5);

	private static Set<FileObject> existingNormalFiles = new HashSet<FileObject>();
	private static Set<FileObject> existingZipFiles = new HashSet<FileObject>();

	private static Set<String> listOfFinalFiles = new HashSet<String>();

	// max number of files to merge into an archive
	private static LinkedList<String> trace = new LinkedList<String>();

	private static Random rand = new Random();

	static private void init() {
		// initialize randomization
		rand = new Random(SEED);

		trace = new LinkedList<String>();
		existingNormalFiles = new HashSet<FileObject>();
		existingZipFiles = new HashSet<FileObject>();
		listOfFinalFiles = new HashSet<String>();

		COMMANDS.put(ZIPCMD, pZip);
		COMMANDS.put(UNZIPCMD, pUnzip);
		COMMANDS.put(COPYCMD, pCopy);
		COMMANDS.put(DELETECMD, pDelete);
		COMMANDS.put(MERGECMD, pMerge);

		sumOfP = pZip + pCopy + pDelete + pUnzip + pMerge;

		// initialize list of used files
		for (int x = 0; x < INITFILES; x++) {
			FileObject file = getFreshNormalFile();
			existingNormalFiles.add(file);
			listOfFinalFiles.add(file.getFilename());
		}

	}

	static private FileObject getFreshNormalFile() {
		return getFreshFile(true);
	}

	static private FileObject getFreshZipFile() {
		return getFreshFile(false);
	}

	static private FileObject getExistingNormalFile() {
		return getExistingFile(true);
	}

	static private FileObject getExistingZipFile() {
		if ((existingZipFiles == null) || (existingZipFiles.size() == 0))
			return null;
		return getExistingFile(false);
	}

	static private FileObject getExistingRandomFile() {
		int sn = existingNormalFiles.size();
		int sz = existingZipFiles.size();

		double r = rand.nextDouble() * (sn + sz);

		boolean normal = (r <= sn);

		return getExistingFile(normal);
	}

	static private List<FileObject> getListOfExistingRandomFiles(int length) {
		List<FileObject> result = new LinkedList<FileObject>();
		List<FileObject> shuffledNormal = new LinkedList<FileObject>(existingNormalFiles);
		List<FileObject> shuffledZip = new LinkedList<FileObject>(existingZipFiles);

		int half = rand.nextInt(length) + 1;

		Collections.shuffle(shuffledNormal, rand);
		Collections.shuffle(shuffledZip, rand);

		result.addAll(shuffledNormal.subList(0, Math.min(half, shuffledNormal.size())));
		result.addAll(shuffledZip.subList(0, Math.min(length - half, shuffledZip.size())));

		Collections.shuffle(result, rand);

		Assert.assertTrue((result.size() > 0));

		return result;
	}

	static private List<FileObject> getListOfExistingNormalFiles(int length) {
		List<FileObject> shuffledContent = new LinkedList<FileObject>(existingNormalFiles);

		Collections.shuffle(shuffledContent, rand);

		return shuffledContent.subList(0, Math.min(length, shuffledContent.size()));
	}

	static private List<FileObject> getListOfRandomFilesFromZip(FileObject zip, int numOfFilesToSplit) {
		if (zip == null) {
			// throw an exception
			Assert.assertTrue("Cannot extract files from a null zip object, can I?", false);
			return null;
		}
		// we extract at most the number of files that are in the content, no
		// more
		int num = zip.getSizeOfContent();
		if (num < numOfFilesToSplit)
			num = numOfFilesToSplit;

		List<FileObject> shuffledContent = zip.getContent();

		Collections.shuffle(shuffledContent, rand);

		return shuffledContent.subList(0, Math.min(shuffledContent.size(), num));
	}

	static private FileObject getFreshFile(boolean normal) {
		String extension;
		if (normal) {
			extension = NORMALEXTENSION;

		} else {
			extension = ZIPEXTENSION;
		}

		for (int x = 0; x < MAXFILES; x++) {
			String file = String.format("%s%03d%s", FILEPREFIX, x, extension);

			Set<FileObject> set;

			if (normal) {
				set = existingNormalFiles;
			} else
				set = existingZipFiles;

			boolean fileIsNew = true;

			// I know, this part can be optimized
			for (FileObject f : set) {
				if (file.equals(f.getFilename())) {
					fileIsNew = false;
					break;
				}
			}

			if (fileIsNew) {
				return new FileObject(file, null, !normal);
			}

		}

		// otherwise throw an exception
		Assert.assertTrue("Too many " + (normal ? "normal" : "zip") + " files (>" + MAXFILES
				+ "), impossible to create a new one", false);
		// never reached
		return null;
	}

	static private FileObject getExistingFile(boolean normal) {
		int size = 0;
		if (normal) {
			size = existingNormalFiles.size();
		} else {
			size = existingZipFiles.size();
		}

		if (size == 0) { // if the set is empty
			return null;
		}

		int randomIndex = rand.nextInt(size);
		int i = 0;

		if (normal) {
			for (FileObject s : existingNormalFiles) {
				if (i == randomIndex)
					return s;
				i = i + 1;
			}
		} else {
			for (FileObject z : existingZipFiles) {
				if (i == randomIndex)
					return z;
				i = i + 1;
			}
		}

		// should be never reached
		Assert.assertTrue("Didn't manage to find a random element in the " + (normal ? "normal" : "zip")
				+ " set. Funny, isn't it?", false);
		return null;
	}

	static private String listAsString(List<FileObject> pars) {
		String result = "";
		if (pars == null)
			return "";
		for (FileObject p : pars) {
			result = result + p + " ";
		}
		return result.trim(); // remove last space
	}

	static private Entry<String, Double> getNextCommand() {
		double r = rand.nextDouble() * sumOfP;

		double count = 0.0;

		for (Entry<String, Double> e : COMMANDS.entrySet()) {
			count += e.getValue();
			if (r <= count)
				return e;
		}

		// at least one object should match, so we should never arrive here
		// throw an exception
		Assert.assertTrue("Didn't find a command", false);
		// never reached
		return null;

	}

	static private void generate(int length) {
		int retry = 0;
		int l=0;
		while (l < length) {

			if (existingZipFiles.size() + existingNormalFiles.size() == 0) {
				retry++;
				if (retry > MAXRETRY)
					throw new RuntimeException(
							"Enough! With this settings it was not possible to generate a good trace within "
									+ MAXRETRY + " attempts!");
				SEED = SEED + rand.nextInt();
				l = 0;
				System.err.println("Attempts number " + retry
						+ ": Every file has been deleted. Let's try again with another trace (new seed=" + SEED + ")");
				init();
			}

			// pick a random value for the command
			Entry<String, Double> e = getNextCommand();
			String traceCommand = "";
			switch (e.getKey()) {

			case UNZIPCMD:
				if (existingZipFiles.size() > 0) {
					FileObject z = getExistingZipFile();
					int numFilesToSplit = (rand.nextInt(z.getSizeOfContent())) + 1;
					List<FileObject> sourcePars = getListOfRandomFilesFromZip(z, numFilesToSplit);
					for (FileObject f : sourcePars)
						listOfFinalFiles.add(f.filename);
					traceCommand = UNZIPCMD + " " + z + " " + listAsString(sourcePars);
				} else {
					l--;
				}
				break;

			case ZIPCMD:
				if (existingZipFiles.size() + existingNormalFiles.size() > 0) {
					FileObject dst = getFreshZipFile();
					int numSrc = rand.nextInt(MAXMERGE) + 1;
					List<FileObject> srcPars = getListOfExistingRandomFiles(numSrc);

					dst.setContent(srcPars);
					existingZipFiles.add(dst);
					listOfFinalFiles.add(dst.filename);

					trace.add(DELETECMD+" "+dst); //ZIP ONLY APPENDS --> need to manually delete the file.
					l++;
					traceCommand = ZIPCMD + " " + dst + " " + listAsString(srcPars);
				} else {
					l--;
				}
				break;

			case COPYCMD:
				if (existingZipFiles.size() + existingNormalFiles.size() > 0) {
					FileObject src, dst;

					src = getExistingRandomFile();

					if (src.isZip) {
						dst = getFreshZipFile();
						dst.setContent(new LinkedList<FileObject>(src.getContent()));
						existingZipFiles.add(dst);
					} else {
						dst = getFreshNormalFile();
						existingNormalFiles.add(dst);
					}

					listOfFinalFiles.add(dst.getFilename());

					traceCommand = COPYCMD + " " + src + " " + dst;
				} else {
					l--;
				}
				break;

			case DELETECMD:
				if (existingNormalFiles.size() + existingZipFiles.size() > 0) {
					FileObject del = getExistingRandomFile();

					if (!del.isZip()) {
						existingNormalFiles.remove(del);
					} else {
						existingZipFiles.remove(del);
					}

					listOfFinalFiles.remove(del.getFilename());
					traceCommand = DELETECMD + " " + del;
				} else {
					l--;
				}

				break;

			case MERGECMD:
				if (existingZipFiles.size() + existingNormalFiles.size() > 0) {
					FileObject dst;
					if (rand.nextDouble() <= pNew) {
						dst = getFreshNormalFile();
					} else {
						dst = getExistingNormalFile();
					}

					int numSrc = rand.nextInt(MAXMERGE) + 1;
					List<FileObject> srcPars = getListOfExistingNormalFiles(numSrc);

					listOfFinalFiles.add(dst.getFilename());

					traceCommand = MERGECMD + " " + dst + " " + numLinesToMerge + " " + listAsString(srcPars);
				} else {
					l--;
				}
				break;

			}

			if (!traceCommand.equals("")) {
				trace.add(traceCommand);
			}

			if (DEBUG) {
				System.out.println(l + ": " + traceCommand);
				if (l % 5 == 0) {
					System.out.print("Normal: {");
					for (FileObject s : existingNormalFiles)
						System.out.print(s + ",");
					System.out.println("}");

					System.out.print("Zip: {");
					for (FileObject z : existingZipFiles) {
						System.out.print(z.getFilename() + "[" + z.getSizeOfContent() + "]");

						// Print also content
						/*
						 * System.out.print("("); for (String s:
						 * z.getContent()){ System.out.print(s+","); }
						 * System.out.print(")");
						 */

						System.out.print(",");
					}
					System.out.println("}");
					System.out.println("");
				}
			}
		l++;
		}

	}

	static private void parseParameters(String[] args) {
		int i = 0;
		while (i < args.length) {
			switch (args[i]) {
			case "--seed":
				SEED = Integer.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--pZip":
				pZip = Double.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--pUnzip":
				pUnzip = Double.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--pDelete":
				pDelete = Double.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--pCopy":
				pCopy = Double.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--pMerge":
				pMerge = Double.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--pNew":
				pNew = Double.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--numLinesToMerge":
				numLinesToMerge = Integer.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--traceLength":
				traceLength = Integer.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--maxMerge":
				MAXMERGE = Integer.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--zipExtension":
				ZIPEXTENSION = args[i + 1];
				i++;
				i++;
				break;

			case "--normalExtension":
				NORMALEXTENSION = args[i + 1];
				i++;
				i++;
				break;

			case "--filePrefix":
				FILEPREFIX = args[i + 1];
				i++;
				i++;
				break;

			case "--initFiles":
				INITFILES = Integer.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--maxFiles":
				MAXFILES = Integer.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--maxRetry":
				MAXRETRY = Integer.valueOf(args[i + 1]);
				i++;
				i++;
				break;

			case "--help":
				System.out.println("\nUsage: java TraceGenerator [OPTIONS] ");
				System.out.println("\n");
				System.out.println("OPTIONS:\n");
				System.out.println("\t--seed n");
				System.out.println("\t\t Set the seed to n (Integer)\n");

				System.out.println("\t--pZip w");
				System.out.println("\t--pUnzip w");
				System.out.println("\t--pDelete w");
				System.out.println("\t--pCopy w");
				System.out.println("\t--pMerge w");
				System.out
						.println("\t\t Set the likelihood of a certain event. The next event on the trace is of type T with probability w/(sum of all ws) (double)\n");

				System.out.println("\t--pNew p");
				System.out
						.println("\t\t Set the probability that a merge event will append to an existing file vs creating a fresh file. p must be within 0 and 1. (double)\n");

				System.out.println("\t--numLinesToMerge p");
				System.out
						.println("\t\t Set the number of lines to be randomly picked from any of the source files during a merge event. (integer)\n");

				System.out.println("\t--traceLength n");
				System.out.println("\t\t Set the length of the generated trace to n (Integer)\n");

				System.out.println("\t--maxMerge n");
				System.out.println("\t\t Set the maximum number of sources for a merge event to n (Integer)\n");

				System.out.println("\t--zipExtension s");
				System.out
						.println("\t\t Set the extension of archive files resulting after a merge event to s (String)\n");

				System.out.println("\t--normalExtension s");
				System.out.println("\t\t Set the extension of non-archive files to s (String)\n");

				System.out.println("\t--filePrefix f");
				System.out.println("\t\t Set the prefix for file names to f (String)\n");

				System.out.println("\t--initFiles n");
				System.out.println("\t\t Set the number of initial files to n (Integer)\n");

				System.out.println("\t--maxFiles n");
				System.out.println("\t\t Set the maximum number of files allowed in the system to n (Integer)\n");

				System.out.println("\t--maxRetry n");
				System.out
						.println("\t\t If at a certain point, the trace results in the situation in which every file has been deleted, the program generates a ne random seed and restarts. This option sets the maximum number of restarts allowed before giving up to n (Integer)\n");

				System.out.println("\t--help");
				System.out.println("\t\t Prints this help\n");

				System.exit(0);
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
		parseParameters(args);
		init();
		generate(traceLength);

		// System.out.println("---------------");

		System.out.println("CONFIG:seed:" + SEED);
		System.out.println("CONFIG:tracelength:" + traceLength);
		System.out.println("CONFIG:----------");
		System.out.println("CONFIG:pZip:" + pZip);
		System.out.println("CONFIG:pUnzip:" + pUnzip);
		System.out.println("CONFIG:pCopy:" + pCopy);
		System.out.println("CONFIG:pDelete:" + pDelete);
		System.out.println("CONFIG:pMerge:" + pMerge);
		System.out.println("CONFIG:----------");
		System.out.println("CONFIG:maxretry:" + MAXRETRY);
		System.out.println("CONFIG:initfiles:" + INITFILES);
		System.out.println("CONFIG:maxfiles:" + MAXFILES);
		System.out.println("CONFIG:maxmerge:" + MAXMERGE);
		System.out.println("CONFIG:----------");
		System.out.println("CONFIG:fileprefix:" + FILEPREFIX);
		System.out.println("CONFIG:zipextension:" + ZIPEXTENSION);
		System.out.println("CONFIG:normalextension:" + NORMALEXTENSION);
		System.out.println("CONFIG:----------");
		System.out.println("INFO:filesNummber:" + listOfFinalFiles.size());
		for (String s : listOfFinalFiles) {
			System.out.println("F:" + s);
		}
		// System.out.println("---------------");

		int i = 0;
		for (String s : trace) {
			if (DEBUG)
				System.out.print("Trace[" + i++ + "]:");
			System.out.println(s);
		}

	}
}
