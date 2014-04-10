package de.tum.in.i22.uc.thrift.generator.mavenplugin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import de.tum.in.i22.uc.thrift.generator.ThriftSpecificationGenerator;


/**
 * @goal generate
 *
 * @phase compile
 *
 *
 * @author Florian Kelbert
 *
 */
public class ThriftSpecificationGeneratorMavenPlugin  extends AbstractMojo {

	/**
	 * @parameter
	 * @optional
	 */
	private String folder;

	/**
	 * @parameter
	 * @required
	 */
	private Set<String> interfaces;

	/**
	 * @parameter
	 * @required
	 */
	private String[] namespaces;

	/**
	 * @parameter
	 * @optional
	 */
	private String[] includes;

	/**
	 * @parameter
	 * @required
	 */
	private String outfile;

	public void execute() throws MojoExecutionException {

		Path parent = new File(folder).toPath().toAbsolutePath();

		URL baseFolder = null;
		try {
			baseFolder = new URL("file://" + parent + "/");
		} catch (MalformedURLException e) {
			getLog().error("Invalid folder: " + baseFolder);
			throw new MojoExecutionException("Invalid folder: " + baseFolder);
		}

		ThriftSpecificationGenerator generator = new ThriftSpecificationGenerator(namespaces, includes, baseFolder, interfaces, getClassLoader());

		try {
			getLog().info("Generating Thrift specifications ...");
			String str = generator.generate();

			getLog().info("Writing Thrift specifications to file " + outfile + " ...");
			PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(new File(outfile))));
			out.write(str);
			out.close();
			getLog().info("Done writing Thrift specifications.");
		}
		catch (FileNotFoundException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}



	/**
	 * From: http://stackoverflow.com/a/13220011
	 *
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	private ClassLoader getClassLoader() throws MojoExecutionException {
		try {
			List<String> classpathElements = project.getCompileClasspathElements();
			classpathElements.add(project.getBuild().getOutputDirectory());
			classpathElements.add(project.getBuild().getTestOutputDirectory());
			URL urls[] = new URL[classpathElements.size()];

			for (int i = 0; i < classpathElements.size(); ++i) {
				urls[i] = new File(classpathElements.get(i)).toURI().toURL();
			}
			return new URLClassLoader(urls, getClass().getClassLoader());
		} catch (Exception e) {
			throw new MojoExecutionException("Couldn't create a classloader.", e);
		}
	}
}
