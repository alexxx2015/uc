package de.tum.in.i22.uc.pip.core;

import de.tum.in.i22.uc.pip.eventdef.SchemaInitializerEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.AcceptEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.CloneEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.CloseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.ConnectEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.Dup2EventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.DupEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.ExecveEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.ExitEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.ExitGroupEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.FcntlEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.FtruncateEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.KillEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.MmapEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.MunmapEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.OpenEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.PipeEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.ReadEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.RenameEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.SendfileEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.ShutdownEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.SocketEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.SocketpairEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.SpliceEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.TeeEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.TruncateEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.UnlinkEventHandler;
import de.tum.in.i22.uc.pip.eventdef.linux.WriteEventHandler;

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
		new MmapEventHandler();
		new MunmapEventHandler();
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

		return true;
	}
}
