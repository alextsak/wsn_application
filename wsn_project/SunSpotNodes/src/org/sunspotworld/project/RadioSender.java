package org.sunspotworld.project;
import com.sun.spot.peripheral.NoRouteException;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

public class RadioSender {
   
     /* Use Singletton Pattern */
    private static RadioSender myInstance = null;    
    private RadioSender() {}
    
    public static RadioSender getInstance() {
        synchronized (RadioSender.class) {
            if (myInstance == null) {
                myInstance = new RadioSender();
            }
        }
        return myInstance;
    }
    
    /* Sends data to the sink, given it's address and port */
    public void sendData(double value, String TargetAddr, int port) throws IOException, NoRouteException {
       DatagramConnection dCon = null;
       Datagram dg;
      try {
            // Creates a DatagramConnection
            dCon = (DatagramConnection) Connector.open("radiogram://" + TargetAddr + ":" + port);
            
            // Ask for a datagram with the maximum size allowed
            dg = dCon.newDatagram(dCon.getMaximumLength());

            //Ensures that the next read/write operation will read/write from the start of the datagram
            dg.reset();

            dg.writeDouble(value);  
          
            //Send Datagram
            dCon.send(dg);

            //Close the connection
            dCon.close();

        } catch (IOException ex) {
            System.out.println("Could not open datagram connection");
            dCon.close(); //Close the connection 
        }
    }
    	
    
}
