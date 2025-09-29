
package br.unicamp.meca.system1.codelets.rosservice;

import static org.junit.Assert.assertEquals;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.support.TimeStamp;
import br.unicamp.meca.mind.MecaMind;
import br.unicamp.meca.system1.codelets.IMotorCodelet;

//import br.unicamp.cst.bindings.ros2java.AddTwoIntsServiceClientSyncRos2;
//import br.unicamp.cst.bindings.ros2java.AddTwoIntsServiceProvider;
import troca_ros.AddTwoIntsRequestMessage;
import troca_ros.AddTwoIntsResponseMessage;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ROS2 migration of the ROS1 RosServiceClientTest
 * 
 * @author jrborelli
 */
public class ROS2_RosServiceClientTest {

    private static final Logger LOGGER = Logger.getLogger(ROS2_RosServiceClientTest.class.getName());
    private static MecaMind mecaMind;

    @BeforeClass
    public static void setup() {
        mecaMind = new MecaMind("ROS2_RosServiceClientTest");
        Logger.getLogger("id.jros2client").setLevel(Level.OFF);
    }

    @AfterClass
    public static void cleanup() {
        if (mecaMind != null) mecaMind.shutDown();
    }

    @Test
    public void testROS2_RosServiceCallOnce() throws InterruptedException {
        // Start ROS2 service provider
        AddTwoIntsServiceProvider serviceProvider = new AddTwoIntsServiceProvider();
        serviceProvider.start();
        Thread.sleep(500); // give service time to start

        // Create memory object for inputs
        Memory memory = mecaMind.createMemoryObject("add_two_ints");

        // Instantiate ROS2 synchronous client
        AddTwoIntsServiceClientSyncRos2 clientSync = new AddTwoIntsServiceClientSyncRos2("add_two_ints");
        clientSync.start();

        // Insert client codelet in mind (for consistency)
        List<IMotorCodelet> motorCodelets = new ArrayList<>();
        motorCodelets.add(clientSync);
        mecaMind.setIMotorCodelets(motorCodelets);
        mecaMind.mountMecaMind();
        mecaMind.start();

        // Send first request
        Long[] inputs = new Long[]{2L, 3L};
        memory.setI(inputs);

        AddTwoIntsResponseMessage response = clientSync.callService(inputs);
        Long sum = response.sum;

        LOGGER.info("Sum received: " + sum);
        assertEquals(Long.valueOf(5L), sum);

        // Cleanup
        clientSync.stop();
        serviceProvider.stop();
        mecaMind.shutDown();
    }

    @Test
    public void testROS2_RosServiceCallTwice() throws InterruptedException {
        // Start ROS2 service provider
        AddTwoIntsServiceProvider serviceProvider = new AddTwoIntsServiceProvider();
        serviceProvider.start();
        Thread.sleep(500);

        Memory memory = mecaMind.createMemoryObject("add_two_ints");

        AddTwoIntsServiceClientSyncRos2 clientSync = new AddTwoIntsServiceClientSyncRos2("add_two_ints");
        clientSync.start();

        List<IMotorCodelet> motorCodelets = new ArrayList<>();
        motorCodelets.add(clientSync);
        mecaMind.setIMotorCodelets(motorCodelets);
        mecaMind.mountMecaMind();
        mecaMind.start();

        // First request
        Long[] inputs1 = new Long[]{2L, 3L};
        memory.setI(inputs1);
        AddTwoIntsResponseMessage response1 = clientSync.callService(inputs1);
        assertEquals(Long.valueOf(5L), response1.sum);

        // Second request
        Long[] inputs2 = new Long[]{3L, 3L};
        memory.setI(inputs2);
        AddTwoIntsResponseMessage response2 = clientSync.callService(inputs2);
        assertEquals(Long.valueOf(6L), response2.sum);

        // Cleanup
        clientSync.stop();
        serviceProvider.stop();
        mecaMind.shutDown();
    }
}