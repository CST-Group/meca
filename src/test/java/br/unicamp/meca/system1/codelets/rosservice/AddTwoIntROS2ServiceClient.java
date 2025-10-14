
package br.unicamp.meca.system1.codelets.rosservice;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.meca.system1.codelets.Ros2ServiceClientMotorCodelet;
import br.unicamp.cst.support.TimeStamp;
import troca_ros.AddTwoIntsRequestMessage;
import troca_ros.AddTwoIntsResponseMessage;
import troca_ros.AddTwoIntsServiceDefinition;

/**
 * ROS2 version of AddTwoIntServiceClient for MECA
 * 
 * @author jrborelli
 * 
 */
public class AddTwoIntROS2ServiceClient extends Ros2ServiceClientMotorCodelet<AddTwoIntsRequestMessage, AddTwoIntsResponseMessage> {

    private volatile Integer a, b;
    private volatile Integer sum;
    private volatile long tsReq = 0, tsResp = 0;
    public boolean processing = false;

    public AddTwoIntROS2ServiceClient(String serviceName) {
        super(serviceName, serviceName, new AddTwoIntsServiceDefinition());
    }

    @Override
    protected AddTwoIntsRequestMessage createNewRequest() {
        return new AddTwoIntsRequestMessage();
    }

    @Override
    protected boolean formatServiceRequest(Memory motorMemory, AddTwoIntsRequestMessage request) {
        processing = true;
        //System.out.println("Request");
        if (motorMemory == null || motorMemory.getI() == null) {
            if (motorMemory == null) System.out.println("motorMemory is null");
            else {
                if (motorMemory.getI() == null) System.out.println("motorMemory.getI() is null");
            }
            return false;
        }
        //System.out.println("Arrived here");
        Integer[] numsToSum = (Integer[]) motorMemory.getI();
        a = numsToSum[0];
        b = numsToSum[1];
        request.withA(a).withB(b);
        tsReq = System.currentTimeMillis();

        System.out.println("REQUEST a=" + a + " b=" + b +
                " at " + TimeStamp.getStringTimeStamp(tsReq) +
                " with data from " + TimeStamp.getStringTimeStamp(motorMemory.getTimestamp()));

        return true;
    }

    @Override
    protected void processServiceResponse(AddTwoIntsResponseMessage response) {
        //System.out.println("Response");
        if (response != null) {
            sum = response.sum;
            tsResp = System.currentTimeMillis();
            System.out.println("RESPONSE Sum=" + sum + " at " + TimeStamp.getStringTimeStamp(tsResp));
        }
        processing = false;
    }

    public synchronized Integer getSum() {
        return sum;
    }

    public synchronized long getTSReq() {
        return tsReq;
    }

    public synchronized long getTSResp() {
        return tsResp;
    }
    
    @Override
    public void stop() {
        while(processing) {
            try{Thread.sleep(100);} catch(Exception e){}
        }
        super.stop();
    }
}