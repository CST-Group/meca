
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
public class ROS2_AddTwoIntServiceClient extends Ros2ServiceClientMotorCodelet<AddTwoIntsRequestMessage, AddTwoIntsResponseMessage> {

    private volatile Long a, b;
    private volatile Long sum;
    private volatile long tsReq = 0, tsResp = 0;

    public ROS2_AddTwoIntServiceClient(String serviceName) {
        super("ROS2_AddTwoIntServiceClient", serviceName, new AddTwoIntsServiceDefinition());
    }

    @Override
    protected AddTwoIntsRequestMessage createNewRequest() {
        return new AddTwoIntsRequestMessage();
    }

    @Override
    protected boolean formatServiceRequest(Memory motorMemory, AddTwoIntsRequestMessage request) {
        if (motorMemory == null || motorMemory.getI() == null) {
            return false;
        }

        Long[] numsToSum = (Long[]) motorMemory.getI();
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
        if (response != null) {
            sum = response.sum;
            tsResp = System.currentTimeMillis();
            System.out.println("RESPONSE Sum=" + sum + " at " + TimeStamp.getStringTimeStamp(tsResp));
        }
    }

    public synchronized Long getSum() {
        return sum;
    }

    public synchronized long getTSReq() {
        return tsReq;
    }

    public synchronized long getTSResp() {
        return tsResp;
    }
}