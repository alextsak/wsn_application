/*
* This class is used for synchronization, in order to update correctly the two 
* values for the Combiner and the Event-Detector(RadioReceiver) threads
*/
package org.sunspotworld.shared;

public class UpdateValues {
    
    boolean blocked = false;
    private static UpdateValues myInstance = null;

    private UpdateValues() {}
    
    public static UpdateValues getInstance() {
        synchronized (UpdateValues.class) {
            if (myInstance == null) {
                myInstance = new UpdateValues();
            }
        }
        return myInstance;
    }
    
   public synchronized void BlockMem() throws InterruptedException {
       while(blocked){
           try{
               wait();
           }catch(InterruptedException e){
               e.printStackTrace();
           }
           blocked = true;
       }
   }
    
   public synchronized void UnBlockMem() throws InterruptedException{
       blocked = false;
       notify();
   } 
    
}
