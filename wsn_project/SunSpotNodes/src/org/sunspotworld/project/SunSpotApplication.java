package org.sunspotworld.project;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ITemperatureInput;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;



public class SunSpotApplication extends MIDlet {

   
    private final int PORT = 40; 
    private final double freqOFSampling = 0.1; // frequency of sampling
    private final String REMOTE_SINK_ADDRESS = "c0a8.0102.0000.100f"; // address of the sink
    private final ITemperatureInput tempSensor = (ITemperatureInput) Resources.lookup(ITemperatureInput.class); // for getting the temperature
    
    
    protected void startApp() throws MIDletStateChangeException {
       
        long spotAddr;
        spotAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
        long timeOfSampling = (long) ((1/freqOFSampling)*1000);
        
        
                    /****** Start Sampling ******/
        while (true) 
        { 
            double tempC=0;      
            try {
                tempC = tempSensor.getCelsius(); // Temperature in Celcius.    
            } catch (IOException ex) {
                System.out.println("Temp problem");
            }
            System.out.println("Temperature in SPOT: " + IEEEAddress.toDottedHex(spotAddr) + " is " + tempC);
            try {
                
                RadioSender.getInstance().sendData(tempC, REMOTE_SINK_ADDRESS, PORT); 
            } catch (IOException ex) {
                System.out.println("Problem in SendData");
            }
            Utils.sleep(timeOfSampling);      // Sleep for this period
        }
        
    }
    
    protected void pauseApp() {
        
    }
    
   
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
     notifyDestroyed();
    }
}
