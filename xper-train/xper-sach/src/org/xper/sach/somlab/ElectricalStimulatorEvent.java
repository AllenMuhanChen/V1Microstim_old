package org.xper.sach.somlab;

import org.xper.sach.somlab.ElectricalStimulator.CMD;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPort;
import jssc.SerialPortException;

public  class ElectricalStimulatorEvent 
	implements SerialPortEventListener {
	// July 16 2015
	
	SerialPort portReference;
	ElectricalStimulator stimRef;
	String hexStr;
	 
	public ElectricalStimulatorEvent(SerialPort openedPort, ElectricalStimulator stimRef){
		portReference = openedPort;
		this.stimRef = stimRef;
	}

    public  void serialEvent(SerialPortEvent serialPortEvent){
    	
    	if(serialPortEvent.isRXCHAR()){
    		
    		int n = serialPortEvent.getEventValue();
    		
    		if(n> 1){ 														//Check bytes count in the input buffer
         			
    			// let the main thread read this
        		if(stimRef.CommandType == CMD.PINGCMD){
        			//System.out.println("Event:: PINGCMD");
        			return;
        		}
        		
                //Read data, if  bytes available 
                try {
               
                	if(stimRef.CommandType == CMD.BATCMD){
                		stimRef.readBuff = portReference.readBytes();   
                		//System.out.println("Event:: BATCMD");
                		stimRef.processIncomingBytes(stimRef.readBuff, stimRef.CommandType.BATCMD);
                	}
                	else if(stimRef.CommandType == CMD.PUTCMD || stimRef.CommandType == CMD.SINGLETRIGGERCMD){
                		
                		hexStr = portReference.readHexString(n); 
                		
	                	if(hexStr.compareTo("00 F0 40 40") != 0){
	                		System.out.println("Received :" + hexStr);
	                	}
	                	 //System.out.println(String.format("%X",  stimRef.readBuff));
                	}
                	
                	
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
 		
    	}
// JK 25 July 2016 
//    	this doesn't do anything useful    	
    	//
//        else if(serialPortEvent.isCTS()){//If CTS line has changed state
//            if(serialPortEvent.getEventValue() == 1){//If line is ON
//                System.out.println("CTS - ON");
//            }
//            else {
//                System.out.println("CTS - OFF");
//            }
//        }
//        else if(serialPortEvent.isDSR()){///If DSR line has changed state
//            if(serialPortEvent.getEventValue() == 1){//If line is ON
//                System.out.println("DSR - ON");
//            }
//            else {
//                System.out.println("DSR - OFF");
//            }
//        }

    }

}


