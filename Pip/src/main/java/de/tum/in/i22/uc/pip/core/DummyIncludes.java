package de.tum.in.i22.uc.pip.core;

import de.tum.in.i22.uc.pip.core.eventdef.SchemaInitializerEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.AcceptEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.CloneEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.CloseEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.ConnectEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.Dup2EventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.DupEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.ExecveEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.ExitEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.ExitGroupEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.FcntlEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.FtruncateEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.KillEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.MmapEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.MunmapEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.OpenEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.PipeEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.ReadEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.RenameEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.SendfileEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.ShutdownEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.SocketEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.SocketpairEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.SpliceEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.TeeEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.TruncateEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.UnlinkEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.linux.WriteEventHandler;

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
