package uctests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.Test;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;

public class DistributionTest extends GenericTest {

	private static final String PEP_PARAMETER_LINUX = "Linux";

	@Test
	public void first() {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/testPmp.xml"));
			pmp.deployPolicyRawXMLPmp(new String(encoded));
		} catch (IOException e) {
			e.printStackTrace();
			assert false;
		}

		pip.initialRepresentation(new NameBasic ("FILE_A./tmp/foo"),pip.getDataInContainer(new NameBasic("initialContainer")));

		pdp.notifyEventSync(createLinuxOpenEvent("A", 123, "/tmp/foo", 3, true));
		pdp.notifyEventSync(createLinuxReadEvent("A", 123, 3, true));
		pdp.notifyEventSync(createLinuxSocketEvent("A", 123, "AF_INET", "SOCK_STREAM", 4, true));
		pdp.notifyEventSync(createLinuxConnectEvent("A", 123, "192.168.201.1", 4567, "127.0.0.1", 9999, 4, true));
		pdp.notifyEventSync(createLinuxWriteEvent("A", 123, 4, true));
	}



	public static IEvent createLinuxSocketEvent(final String host, final int pid,
			final String domain, final String type, final int fd, boolean isActual) {
		return new EventBasic("Socket",
				new HashMap<String, String>(){
			private static final long serialVersionUID = 1L;
			{
				put(EventBasic.PEP_PARAMETER_KEY, PEP_PARAMETER_LINUX);
				put("host", host);
				put("pid", pid+"");
				put("domain", domain);
				put("type", type);
				put("fd", fd+"");
			}},
			isActual);
	}

	public static IEvent createLinuxAcceptEvent(final String host, final int pid, final String localIP,
			final int localPort, final String remoteIP, final int remotePort,
			final int oldfd, final int newfd, boolean isActual) {
		return new EventBasic("Accept",
				new HashMap<String, String>(){
			private static final long serialVersionUID = 1L;
			{
				put(EventBasic.PEP_PARAMETER_KEY, PEP_PARAMETER_LINUX);
				put("host", host);
				put("pid", pid+"");
				put("localIP", localIP);
				put("localPort", localPort+"");
				put("remoteIP", remoteIP);
				put("remotePort", remotePort+"");
				put("oldfd", oldfd+"");
				put("newfd", newfd+"");
			}},
			isActual);
	}

	public static IEvent createLinuxConnectEvent(final String host, final int pid, final String localIP,
			final int localPort, final String remoteIP, final int remotePort,
			final int fd, boolean isActual) {
		return new EventBasic("Connect",
				new HashMap<String, String>(){
			private static final long serialVersionUID = 1L;
			{
				put(EventBasic.PEP_PARAMETER_KEY, PEP_PARAMETER_LINUX);
				put("host", host);
				put("pid", pid+"");
				put("localIP", localIP);
				put("localPort", localPort+"");
				put("remoteIP", remoteIP);
				put("remotePort", remotePort+"");
				put("fd", fd+"");
			}},
			isActual);
	}

	public static IEvent createLinuxWriteEvent(final String host, final int pid, final int fd, boolean isActual) {
		return new EventBasic("Write",
				new HashMap<String, String>(){
			private static final long serialVersionUID = 1L;
			{
				put(EventBasic.PEP_PARAMETER_KEY, PEP_PARAMETER_LINUX);
				put("host", host);
				put("pid", pid+"");
				put("fd", fd+"");
			}},
			isActual);
	}

	public static IEvent createLinuxReadEvent(final String host, final int pid, final int fd, boolean isActual) {
		return new EventBasic("Read",
				new HashMap<String, String>(){
			private static final long serialVersionUID = 1L;
			{
				put(EventBasic.PEP_PARAMETER_KEY, PEP_PARAMETER_LINUX);
				put("host", host);
				put("pid", pid+"");
				put("fd", fd+"");
			}},
			isActual);
	}

	public static IEvent createLinuxExecveEvent(final String host, final int pid, final String filename, boolean isActual) {
		return new EventBasic("Execve",
				new HashMap<String, String>(){
			private static final long serialVersionUID = 1L;
			{
				put(EventBasic.PEP_PARAMETER_KEY, PEP_PARAMETER_LINUX);
				put("host", host);
				put("pid", pid+"");
				put("filename", filename);
			}},
			isActual);
	}

	public static IEvent createLinuxOpenEvent(final String host, final int pid,
			final String filename, final int fd, boolean isActual) {
		return new EventBasic("Open",
				new HashMap<String, String>(){
			private static final long serialVersionUID = 1L;
			{
				put(EventBasic.PEP_PARAMETER_KEY, "Linux");
				put("host", host);
				put("pid", pid+"");
				put("fd", fd+"");
				put("filename", filename);
				put("trunc", "false");
			}},
			isActual);
	}


}
