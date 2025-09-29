
package br.unicamp.meca.system1.codelets.rosservice;

import br.unicamp.cst.bindings.ros2java.RosServiceClientSync;
import troca_ros.AddTwoIntsRequestMessage;
import troca_ros.AddTwoIntsResponseMessage;
import troca_ros.AddTwoIntsServiceDefinition;
import br.unicamp.meca.system1.codelets.Ros2ServiceClientMotorCodelet;
import br.unicamp.cst.core.entities.Memory;

/**
 *
 * @author jrborelli
 */


public class AddTwoIntsServiceClientSyncRos2 extends Ros2ServiceClientMotorCodelet<AddTwoIntsRequestMessage, AddTwoIntsResponseMessage> {

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
        request.withA(inputs[0]);
        request.withB(inputs[1]);
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
    
    
    public AddTwoIntsResponseMessage callService(Long[] inputs) {
        AddTwoIntsRequestMessage request = createNewRequest();

        Memory memory = new Memory("temp");
        memory.setI(inputs);
        formatServiceRequest(memory, request);

        AddTwoIntsResponseMessage response = super.callService(request);
        processServiceResponse(response);

    return response;
}    
    
}
