package org.sunspotworld.hostapp;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.util.IEEEAddress;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;



public class SunSpotHostApplication extends MIDlet {
    
    private ISwitch sw1,sw2;
    private RadioReceiver rRec = null;
    private Combiner comb = null;
    private Thread t1=null;
    private Thread t2=null;
   
    private static SunSpotHostApplication myInstance = null;

    /* Use Singletton Pattern */
    private SunSpotHostApplication() {}
    
    public static SunSpotHostApplication getInstance() {
        synchronized (SunSpotHostApplication.class) {
            if (myInstance == null) {
                myInstance = new SunSpotHostApplication();
            }
        }
        return myInstance;
    }
   
    protected void startApp() throws MIDletStateChangeException {
        
        long sinkAddr;
        sinkAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress(); // get SINK address
        System.out.println("Sink address = " + IEEEAddress.toDottedHex(sinkAddr));
   
        /* Creating the two threads... */
        rRec = RadioReceiver.getInstance();
        comb = Combiner.getInstance();
        t1 = new Thread(rRec);
        t2 = new Thread(comb);
        t1.start(); // start the Event-Detector Thread
        t2.start(); // start the Combiner thread 
        
    }

    

   
    protected void pauseApp() {
        
    }

    
    protected void destroyApp(boolean bln) throws MIDletStateChangeException {
        
        
       notifyDestroyed();
    }

   

   

}
