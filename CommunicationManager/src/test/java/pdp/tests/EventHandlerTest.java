package pdp.tests;

import org.junit.Test;

import de.tum.in.i22.cm.in.RequestHandler;

public class EventHandlerTest {
	static RequestHandler eventHandler;
	
	static int k = 0;
	
	static {
		eventHandler = RequestHandler.getInstance();
		Thread t = new Thread(eventHandler);
		t.start();
	}
	
	@Test
	public void testPause() {
//		Thread t = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				System.out.println("Run method");
//				for (int i = 0; i < 10; i++) {
//					System.out.println("Loop");
//					if (Math.random() < 0.5) {
//						System.out.println("pause");
//						eventHandler.pause();
//					} else {
//						System.out.println("Add event");
//						IEvent event = new EventBasic("no " + i, null);
//						IForwarder forwarder = new IForwarder() {
//							@Override
//							public void forwardResponse(IResponse response) {
//								System.out.println(response);
//							}
//						};
//						
//						try {
//							eventHandler.addEvent(event, forwarder);
//						} catch (InterruptedException e) {
//							System.err.print(e);
//						}
//					}
//				}
//			}
//		});
//		
//		t.start();
//		
//		Thread t2 = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				for (int i = 0; i < 10; i++) {
//					try {
//						Thread.sleep(500);
//					} catch(Exception e) {
//						System.err.print(e);
//					}
//					System.out.println("Resume");
//					eventHandler.resume();
//				}
//			}
//		});
//		
//		t2.start();
//		
//		try {
//			System.out.println("Wait for the threads to finish.");
//			t.join();
//			t2.join();
//		} catch (InterruptedException e) {
//			
//		}
	}
	
}
