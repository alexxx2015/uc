package de.tum.in.i22.uc.thrift.generator.mavenplugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;

import de.tum.in.i22.uc.thrift.generator.ThriftSpecificationGenerator;


/**
 * @goal generate
 *
 * @phase compile
 *
 *
 * @author florian
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

	/**
	 */
	@Override
	public void execute() throws MojoExecutionException {

		Path parent = new File(folder).toPath().toAbsolutePath();

		URL baseFolder = null;
		try {
			baseFolder = new URL("file://" + parent + "/");
		} catch (MalformedURLException e) {
			getLog().error("Invalid folder: " + baseFolder);
			return;
		}

		ThriftSpecificationGenerator generator = new ThriftSpecificationGenerator(namespaces, includes, baseFolder, interfaces, getClassLoader());

		System.out.println(generator.generate());
	}



	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	private ClassLoader getClassLoader() throws MojoExecutionException
	{
	  try
	  {
	    List<String> classpathElements = project.getCompileClasspathElements();
	    classpathElements.add(project.getBuild().getOutputDirectory() );
	    classpathElements.add(project.getBuild().getTestOutputDirectory() );
	    URL urls[] = new URL[classpathElements.size()];

	    for ( int i = 0; i < classpathElements.size(); ++i )
	    {
	      urls[i] = new File( classpathElements.get( i ) ).toURI().toURL();
	    }
	    return new URLClassLoader(urls, getClass().getClassLoader() );
	  }
	  catch (Exception e)
	  {
	    throw new MojoExecutionException("Couldn't create a classloader.", e);
	  }
	}
}
