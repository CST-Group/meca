/**
 * 
 */
package br.unicamp.meca.system1.codelets.rosservice;

import java.net.URI;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.support.TimeStamp;
import br.unicamp.meca.system1.codelets.RosServiceClientMotorCodelet;
import rosjava_test_msgs.AddTwoIntsRequest;
import rosjava_test_msgs.AddTwoIntsResponse;

/**
 * @author andre
 *
 */
public class AddTwoIntROS1ServiceClient extends RosServiceClientMotorCodelet<AddTwoIntsRequest, AddTwoIntsResponse> {

	private volatile Integer a,b;
        private volatile Integer sum;
        private volatile long tsreq=0, tsresp=0;
	
	public AddTwoIntROS1ServiceClient(String host, URI masterURI) {
		super("AddTwoIntServiceClient", "add_two_ints", rosjava_test_msgs.AddTwoInts._TYPE, host, masterURI);
	}

	@Override
	public boolean formatServiceRequest(Memory motorMemory, AddTwoIntsRequest serviceMessageRequest) {
		if(motorMemory == null || motorMemory.getI() == null) {
			return false;
		}
		
		Integer[] numsToSum = (Integer[]) motorMemory.getI();
                a = numsToSum[0];
                b = numsToSum[1];
		serviceMessageRequest.setA(numsToSum[0]);
		serviceMessageRequest.setB(numsToSum[1]);
                tsreq = System.currentTimeMillis();
                System.out.println("REQUEST a = "+a+" b = "+b+" at "+TimeStamp.getStringTimeStamp(tsreq)+" with data from "+TimeStamp.getStringTimeStamp(motorMemory.getTimestamp()));
                
		return true;
	}

	@Override
	public void processServiceResponse(AddTwoIntsResponse serviceMessageResponse) {
		sum = (int) serviceMessageResponse.getSum();
                tsresp = System.currentTimeMillis();
		System.out.println("RESPONSE Sum = "+sum+" at "+TimeStamp.getStringTimeStamp(tsresp));
	}
	
	/**
	 * @return the sum
	 */
	public synchronized Integer getSum() {
		return sum;
	}
        
        /**
         * 
         * @return timestamp 
         */
        public synchronized long getTSReq() {
            return tsreq;
        }
        
        /**
         * 
         * @return timestamp 
         */
        public synchronized long getTSResp() {
            return tsresp;
        }

}
