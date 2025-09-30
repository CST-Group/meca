
package br.unicamp.meca.system1.codelets.rosservice;

import troca_ros.AddTwoIntsRequestMessage;
import troca_ros.AddTwoIntsResponseMessage;
import troca_ros.AddTwoIntsServiceDefinition;
import br.unicamp.meca.system1.codelets.Ros2ServiceClientMotorCodelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;

/**
 *
 * @author jrborelli
 */


public class AddTwoIntsServiceClientSyncRos2 extends Ros2ServiceClientMotorCodelet<AddTwoIntsRequestMessage, AddTwoIntsResponseMessage> {
    
    private volatile Integer a,b;
    private volatile Integer sum;
    private volatile long tsReq = 0, tsResp = 0;

    public AddTwoIntsServiceClientSyncRos2(String serviceName) {
        super("AddTwoIntsServiceClientSyncRos2", serviceName, new AddTwoIntsServiceDefinition());
    }

    /*
    @Override
    public boolean formatServiceRequest(Memory memory, AddTwoIntsRequestMessage requestMessage) {
        Object[] args = (Object[]) memory.getI(); // or however you extract the inputs
        requestMessage.withA((Long) args[0]).withB((Long) args[1]);
        
        return true; // return value if the superclass expects boolean
    }  */

    
    @Override  //exemplo:
    protected boolean formatServiceRequest(Memory memory, AddTwoIntsRequestMessage request) {
        Integer[] inputs = (Integer[]) memory.getI(); // example cast
        if (inputs == null || inputs.length < 2) return false;
        a = inputs[0];
        b = inputs[1];
        request.withA(a);
        request.withB(b);
        tsReq = System.currentTimeMillis();
        return true;
    }
  
    
    @Override
    protected AddTwoIntsRequestMessage createNewRequest() {
        return new AddTwoIntsRequestMessage();
    }
    
    @Override
    public void processServiceResponse(AddTwoIntsResponseMessage response) {
        // For synchronous calls, you may leave it empty
        // or handle logging if needed
    }
    
    
    public AddTwoIntsResponseMessage callService(Integer[] inputs) {
        sum = inputs[0]+inputs[1];
        tsResp = System.currentTimeMillis();
        AddTwoIntsResponseMessage response = new AddTwoIntsResponseMessage(sum);
        processServiceResponse(response);
        return response;
}    
    
    /**
	 * @return the sum
	 */
	public synchronized Integer getSum() {
		return sum;
	}
        
        public synchronized long getTSReq() {
        return tsReq;
    }

    public synchronized long getTSResp() {
        return tsResp;
    }
    
}
