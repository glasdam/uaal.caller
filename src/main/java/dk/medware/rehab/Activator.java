package dk.medware.rehab;


import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;

public class Activator implements BundleActivator {
	public static BundleContext osgiContext = null;
	public static ModuleContext context = null;

	public static SCaller scaller = null;
	public static CPublisher cpublisher = null;
	
	public static Thread uaal_thread = null;

	class SimpleThread extends Thread {
	    public SimpleThread(String str) {
	    	super(str);
	    }
	    public void run() {
		  while(true){
				try {
					sleep(10000);
          System.out.println("[REHAB] Fetching!");
		      cpublisher.publishStatusEvent();
		      scaller.callGetExerciseSuggestions();
          System.out.println("[REHAB] Sleeping");
				} catch (InterruptedException e) {
					break;
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("[REHAB] Ended");
	    }
	}
	
	
	public void start(BundleContext bcontext) throws Exception {
        	Activator.osgiContext = bcontext;
        	Activator.context = uAALBundleContainer.THE_CONTAINER
        		.registerModule(new Object[] { bcontext });
        	scaller = new SCaller(context);
        	cpublisher = new CPublisher(context);
        	uaal_thread = new SimpleThread("uaal");
        	uaal_thread.start();
	}

	public void stop(BundleContext arg0) throws Exception {
        	scaller.close();
        	cpublisher.close();
        	uaal_thread.interrupt();
	}

}
