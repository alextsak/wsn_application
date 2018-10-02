/*
 * This class stands for the internal memory of the two positions.
 * One position for every stream 
 */
package org.sunspotworld.shared;


public class InternalMemory {
    
    private int stream1_res; // store result for the 1st stream
    private int stream2_res; // store result for the 2nd stream
    private static InternalMemory myInstance = null;
    
    private InternalMemory() {
        // Does nothing
    }
    /* Use Singletton Pattern */
    public static InternalMemory getInstance() {
        synchronized (InternalMemory.class) {
            if (myInstance == null) {
                myInstance = new InternalMemory();
            }
        }
        return myInstance;
    }
    

    public int getStream1_res() {
        return stream1_res;
    }

    public void setStream1_res(int stream1_res) {
        this.stream1_res = stream1_res;
    }

    public int getStream2_res() {
        return stream2_res;
    }

    public void setStream2_res(int stream2_res) {
        this.stream2_res = stream2_res;
    }
    
    
    
}
