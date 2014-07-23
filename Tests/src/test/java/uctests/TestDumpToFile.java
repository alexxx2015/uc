package uctests;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Test;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;

public class TestDumpToFile extends GenericTest {
	@Test
	public void TestDumpEvent() {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		LinkedList<IEvent> ll = new LinkedList<IEvent>();

        
        
		Map<String, String> map = new HashMap<String,String>();
		map.put("pn1", "pv1");
		ll.add(mf.createActualEvent("test1a", map));
		ll.add(mf.createActualEvent("test1b", map));
		map.put("pn2", "pv2");
		ll.add(mf.createActualEvent("test2a", map));
		ll.add(mf.createActualEvent("test2b", map));
		map.put("pn3", "pv3");
		ll.add(mf.createActualEvent("test3", map));
		
		
		
		
		 try{
			 
				FileOutputStream fout = new FileOutputStream("c:\\tmp\\address.ser");
				ObjectOutputStream oos = new ObjectOutputStream(fout);   
				oos.writeObject(ll);
				oos.close();
				System.out.println("Done");
		 
			   }catch(Exception ex){
				   ex.printStackTrace();
			   }
		 

		 
		 	LinkedList<IEvent> ll2= new LinkedList<>();
		   try{
			   
			   FileInputStream fin = new FileInputStream("c:\\tmp\\address.ser");
			   ObjectInputStream ois = new ObjectInputStream(fin);
			   ll2 = (LinkedList<IEvent>) ois.readObject();
			   ois.close();
	 
		   }catch(Exception ex){
			   ex.printStackTrace();
		   }
		   
		   System.out.println("event before: " + ll);
		   System.out.println("event after: " + ll2);
	}
}
