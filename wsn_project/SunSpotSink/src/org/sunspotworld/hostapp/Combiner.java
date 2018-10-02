package org.sunspotworld.hostapp;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.resources.transducers.LEDColor;
import org.sunspotworld.shared.InternalMemory;
import org.sunspotworld.shared.UpdateValues;


public class Combiner implements Runnable{
    
    public Combiner(){}
    
    /* Use Singletton Pattern */
    private static Combiner myInstance = null;
   
    public static Combiner getInstance() {
        synchronized (Combiner.class) {
            if (myInstance == null) {
                myInstance = new Combiner();
            }
        }
        return myInstance;
    }
    private int value1; // value from the 1st stream
    private int value2; // value from the 2nd stream
    
    /* frequency of sampling from spots- it should be the same as the spots
    - it will change accordingly below */
    private final double frequency = 0.1; 
    
    private final ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class); // for showing the Event

    public void run() {
         
        System.out.println("Combiner Thread started");
        leds.setOff(); // at the beginning set off the leds
        long sleepTime = (long) ((1/frequency)*10000); // create the sleeptime 
        System.out.println("Combiner Thread sleeptime: " + sleepTime);
        while(true) 
        { 
                try 
                {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex) 
                {
                    System.out.println("Interrupt in Combiner");
                
                }
                
                System.out.println("Combiner Thread is UP now");
      
                UpdateValues uv = UpdateValues.getInstance();
                /* Block access ( to other thread) in order to read correct data from the internal memory */
                try {
                 uv.BlockMem();
                } catch (InterruptedException ex) {
                 System.out.println("InterruptedException in Combiner BlockMem");
                }
                value1 = InternalMemory.getInstance().getStream1_res();
                value2 = InternalMemory.getInstance().getStream2_res();
                /* unblock the resource */
                try {
                 uv.UnBlockMem();
                } catch (InterruptedException ex) {
                 System.out.println("InterruptedException in Combiner UnBlockMem");
                }
              
                        /* Check for event, only for a positive event */
                
                if(value1 == 1 && value2 == 1)
                {
                    System.out.println("******* EVENT DETECTED *******");
                    leds.setOff();
                    leds.setColor(LEDColor.RED);
                    leds.setOn();
                }
                /* Uncomment this if you want to check for a negative event */
                /* else if(value1 == 2 && value2 == 2)
                {
                    System.out.println("******* EVENT DETECTED *******");
                    leds.setOff();
                    leds.setColor(LEDColor.BLUE);
                    leds.setOn();
                }*/
                else
                {
                    System.out.println("------- NO EVENT DETECTED -------");
                    leds.setOff();
                    leds.setColor(LEDColor.GREEN);
                    leds.setOn();
                }
        
        }
        
    }
    
}
