package org.sunspotworld.hostapp;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import org.sunspotworld.shared.InternalMemory;
import org.sunspotworld.shared.UpdateValues;


public class RadioReceiver implements Runnable {
    
    private final int PORT = 40; // the port where the communication will take place 
   
    /* Addresses of the 2 spots */
    private final String REMOTE_SPOT1_ADDRESS = "c0a8.0102.0000.1010";
    private final String REMOTE_SPOT2_ADDRESS = "c0a8.0102.0000.1011";
    
    
    /******************************* CUMSUM ********************/
    private double P1=0,N1=0,P2=0,N2=0;
    private final double up_thresh = 40; // upper threshold - user can change this
    private final double low_thresh = 40; // lower threshold - user can change this
    private final double up_k = 1; // above tolerance - user can change this
    private final double low_k = 1; // below tolerance - user can change this
    private final double target_val = 1; // target value - user can change this
    /**********************************************************/
   

    public RadioReceiver(){}

    /* Use Singletton Pattern */
    private static RadioReceiver myInstance = null;

    public static RadioReceiver getInstance() {
        synchronized (RadioReceiver.class) {
            if (myInstance == null) {
                myInstance = new RadioReceiver();
            }
        }
        return myInstance;
    }
   
    public void run() {
        
        DatagramConnection dCon = null;
        Datagram dg = null;
        System.out.println("Receiving Thread");
        try {
                //Open Radiogram Connection on port 40
                dCon = (DatagramConnection) Connector.open("radiogram://:"+PORT);
                // ask a datagram with the maximum size allowed
                dg = dCon.newDatagram(dCon.getMaximumLength());
            } catch (IOException e) {
                System.out.println("Could not open radiogram receiver connection");
                return;
            }
        
        /* Start receiving data from the nodes*/
        while (true) 
        {
                
            try 
            {
                //Ensures that the next read/write operation will read/write from the start of the datagram
                dg.reset();

                //Receive a Datagram
                dCon.receive(dg);
      
                double tempC = dg.readDouble();
         
                System.out.println("Received Temperature: " + tempC + " from: " + dg.getAddress().toLowerCase());
                
                if(dg.getAddress().toLowerCase().equals(REMOTE_SPOT1_ADDRESS)) // if the datagram is from the 1st stream...
                {
                    CumSum(tempC,REMOTE_SPOT1_ADDRESS, P1, N1 );    
                }
                else if(dg.getAddress().toLowerCase().equals(REMOTE_SPOT2_ADDRESS)) // if the datagram is from the 2nd stream...
                {
                    CumSum(tempC,REMOTE_SPOT2_ADDRESS, P2, N2 ); 
                }
                else
                {
                    System.out.println("Could not match the address");
                }
            
            }   
            catch (IOException e) {
                System.out.println("Nothing received");
                
            }
        }
        
    }
    
                            /* CUMSUM ALGORITHM */
    public void CumSum(double value, String stream, double P, double N)
    {
        int s_up,s_down; // up and down signal detection
        double pMax, nMin; // max and min values
        
        double p = value - (target_val + up_k)+P;
        if(p>0)
        {
            pMax = p; 
        }
        else
        {
            pMax = 0;
        }
        double n = value - (target_val - low_k)+N;
        if(n<0)
        {
            nMin = n;
        }
        else
        {
            nMin = 0; 
        }
            
            /**** Check Upper Threshold ****/
        if(pMax > up_thresh)
        {
            s_up = 1; //positive event (only this case is checked from  the Combiner)       
            AddToMemory(stream, s_up); // add new value to the memory 
            /* Reset P,N from every stream */
            if(stream.equals(REMOTE_SPOT1_ADDRESS))
            {
               P1=0;
               N1=0;  
            }
            if(stream.equals(REMOTE_SPOT2_ADDRESS))
            {
               P2=0;
               N2=0;   
            }
           
        }
          /*****  Check Lower Threshold  *****/
        else if(nMin < -low_thresh)
        {
            s_down = 2; // negative event
            AddToMemory(stream, s_down); // add new value to the memory 
            /* Reset P,N from every stream */
            if(stream.equals(REMOTE_SPOT1_ADDRESS))
            {
               P1=0;
               N1=0;  
            }
            if(stream.equals(REMOTE_SPOT2_ADDRESS))
            {
               P2=0;
               N2=0;   
            }
        }
        else
        {
            // No event found so put 0 to memory and set P,N with the current max and min 
           AddToMemory(stream, 0);
           if(stream.equals(REMOTE_SPOT1_ADDRESS))
            {
               P1=pMax;
               N1=nMin;  
            }
            if(stream.equals(REMOTE_SPOT2_ADDRESS))
            {
               P2=pMax;
               N2=nMin;   
            }
        }   
    }
    
    public void AddToMemory(String stream, int event)
    {
         /* For the 1st stream... */
         if(stream.equals(REMOTE_SPOT1_ADDRESS))
         {
             UpdateValues uv = UpdateValues.getInstance();
             /* Block the memory in order to write the new value */
             try {
                 uv.BlockMem();
             } catch (InterruptedException ex) {
                 System.out.println("BlockMem RadioReceiver stream1");
             }
             InternalMemory.getInstance().setStream1_res(event);
             /* Unblock the memory  */
             try {
                 uv.UnBlockMem();
             } catch (InterruptedException ex) {
                 System.out.println("UnBlockMem RadioReceiver stream1");
             }
        }
        /* For the 2nd stream... */ 
        if(stream.equals(REMOTE_SPOT2_ADDRESS))
        {
               
            UpdateValues uv = UpdateValues.getInstance();
            /* Block the memory in order to write the new value */
            try {
                 uv.BlockMem();
             } catch (InterruptedException ex) {
                 System.out.println("BlockMem RadioReceiver stream2");
             }
             InternalMemory.getInstance().setStream2_res(event);
             /* Unblock the memory  */
             try {
                 uv.UnBlockMem();
             } catch (InterruptedException ex) {
                 System.out.println("UnBlockMem RadioReceiver stream2");
             }
        } 
    }

}
