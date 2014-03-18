package de.tum.in.i22.pip.core;

import de.tum.in.i22.pip.core.eventdef.SchemaInitializerEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.AcceptEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.CloneEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.CloseEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.ConnectEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.Dup2EventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.DupEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.ExecveEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.ExitEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.ExitGroupEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.FcntlEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.FtruncateEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.KillEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.LinuxEvents;
import de.tum.in.i22.pip.core.eventdef.Linux.MmapEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.MunmapEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.OpenAtEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.OpenEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.PipeEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.ReadEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.RenameEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.SendfileEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.ShutdownEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.SocketEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.SocketpairEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.SpliceEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.TeeEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.TruncateEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.UnlinkEventHandler;
import de.tum.in.i22.pip.core.eventdef.Linux.WriteEventHandler;
import de.tum.in.i22.pip2pip.Pip2PipTcpImp;

/**
 * This is a dummy class
 *
 * @author Florian Kelbert
 *
 */
class DummyIncludes {
	static boolean dummyInclude() {
		new SchemaInitializerEventHandler();

		new AcceptEventHandler();
		new CloneEventHandler();
		new CloseEventHandler();
		new ConnectEventHandler();
		new Dup2EventHandler();
		new DupEventHandler();
		new ExecveEventHandler();
		new ExitEventHandler();
		new ExitGroupEventHandler();
		new FcntlEventHandler();
		new FtruncateEventHandler();
		new KillEventHandler();
		new LinuxEvents();
		new MmapEventHandler();
		new MunmapEventHandler();
		new OpenAtEventHandler();
		new OpenEventHandler();
		new PipeEventHandler();
		new ReadEventHandler();
		new RenameEventHandler();
		new SendfileEventHandler();
		new ShutdownEventHandler();
		new SocketEventHandler();
		new SocketpairEventHandler();
		new SpliceEventHandler();
		new TeeEventHandler();
		new TruncateEventHandler();
		new UnlinkEventHandler();
		new WriteEventHandler();
		new Pip2PipTcpImp("", 0);

		return true;
	}
}
