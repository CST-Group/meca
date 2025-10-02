
package br.unicamp.meca.system1.codelets.rosservice;


import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.support.TimeStamp;
import br.unicamp.meca.mind.MecaMind;
import br.unicamp.meca.system1.codelets.IMotorCodelet;

//import br.unicamp.cst.bindings.ros2java.AddTwoIntsServiceClientSyncRos2;
//import br.unicamp.cst.bindings.ros2java.AddTwoIntsServiceProvider;
import troca_ros.AddTwoIntsResponseMessage;

import org.junit.AfterClass;
import org.junit.BeforeClass;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * ROS2 migration of the ROS1 RosServiceClientTest
 * 
 * @author jrborelli
 */
public class ROS2_RosServiceClientTest {

    private static final Logger LOGGER = Logger.getLogger(ROS2_RosServiceClientTest.class.getName());
    private static MecaMind mecaMind;
    private volatile Memory motorMemory;
    
    private static void SilenceLoggers() {
        Logger.getLogger("pinorobotics.rtpstalk").setLevel(Level.OFF);
        Logger.getLogger("id.jros2client").setLevel(Level.OFF);
    }

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
        SilenceLoggers();
        setup();
        // Start ROS2 service provider
        AddTwoIntsServiceProvider serviceProvider = new AddTwoIntsServiceProvider();
        serviceProvider.start();
        Thread.sleep(500); // give service time to start

        // Create memory object for inputs
        //Memory memory = mecaMind.createMemoryObject("add_two_ints");

        // Instantiate ROS2 synchronous client
        AddTwoIntsServiceClientSyncRos2 clientSync = new AddTwoIntsServiceClientSyncRos2("add_two_ints");
        //clientSync.start();

        // Insert client codelet in mind (for consistency)
        List<IMotorCodelet> motorCodelets = new ArrayList<>();
        motorCodelets.add(clientSync);
        mecaMind.setIMotorCodelets(motorCodelets);
        mecaMind.mountMecaMind();
        mecaMind.start();
        
        Thread.sleep(5000);
		
	motorMemory = clientSync.getInput(clientSync.getId());
		
	Integer expectedSum = 5;
		
	Integer[] numsToSum = new Integer[] {2,3};
	motorMemory.setI(numsToSum);
        System.out.println("Nums to sum were changed to {2,3}");
		
	Thread.sleep(2000);
		
	assertEquals(expectedSum, clientSync.getSum());

        // Cleanup
        //clientSync.stop();
        serviceProvider.stop();
        mecaMind.shutDown();
    }

    @Test
    public void testROS2_RosServiceCallTwice() throws InterruptedException {
        SilenceLoggers();
        setup();
        // Start ROS2 service provider
        AddTwoIntsServiceProvider serviceProvider = new AddTwoIntsServiceProvider();
        serviceProvider.start();
        Thread.sleep(500);

        //Memory memory = mecaMind.createMemoryObject("add_two_ints");

        ROS2_AddTwoIntServiceClient clientSync = new ROS2_AddTwoIntServiceClient("add_two_ints");
        //clientSync.start();

        List<IMotorCodelet> motorCodelets = new ArrayList<>();
        motorCodelets.add(clientSync);
        mecaMind.setIMotorCodelets(motorCodelets);
        mecaMind.mountMecaMind();
        mecaMind.start();
        
        MemoryContainer mc=null;
        motorMemory = clientSync.getInput(clientSync.getId());
        if (motorMemory instanceof MemoryContainer)
                    mc = (MemoryContainer) motorMemory;
        Integer expectedSum = 5;

        // First request
        Integer[] numsToSum = new Integer[]{2, 3};
        
	long tsstartreq = System.currentTimeMillis(); //clientSync.getTSReq();
        long tsstopreq = tsstartreq;
        long tsstartresp = System.currentTimeMillis(); //clientSync.getTSResp();
        long tsstopresp = tsstartresp;
        int id = motorMemory.setI(numsToSum);
        // At this point, motorMemory has 1 internal MemoryObject and id should be 0
        System.out.println("id: "+id);
        System.out.println("\n\nNums to sum were changed to {2,3} at "+TimeStamp.getStringTimeStamp(motorMemory.getTimestamp()));
        System.out.println("Service situation - req:"+TimeStamp.getStringTimeStamp(tsstartreq)+" resp:"+TimeStamp.getStringTimeStamp(tsstartresp));
        while (tsstartreq == tsstopreq || tsstartresp == tsstopresp || tsstopresp <= tsstopreq  ) {
            tsstopresp = clientSync.getTSResp();
            tsstopreq = clientSync.getTSReq();
            System.out.println("startreq: "+TimeStamp.getStringTimeStamp(tsstartreq)+" stopreq: "+TimeStamp.getStringTimeStamp(tsstopreq));
            System.out.println("startresp: "+TimeStamp.getStringTimeStamp(tsstartresp)+" stopresp: "+TimeStamp.getStringTimeStamp(tsstopresp));
            Thread.sleep(100);
        }
        System.out.println("Finished process - req:"+TimeStamp.getStringTimeStamp(tsstopreq)+" resp:"+TimeStamp.getStringTimeStamp(tsstopresp));
		//Thread.sleep(5000);
		
	assertEquals(expectedSum, clientSync.getSum());
		
	expectedSum = 6;
		
	numsToSum = new Integer[] {3,3};
                // This is the tricker part ... instead of calling setI from motorMemory, we should use its MemoryContainer counterpart
        mc.setI(numsToSum,0);
        System.out.println("\n\nNums to sum were changed to {3,3} at "+TimeStamp.getStringTimeStamp(motorMemory.getTimestamp()));
        tsstartreq = clientSync.getTSReq();
        tsstopreq = tsstartreq;
        tsstartresp = clientSync.getTSResp();
        tsstopresp = tsstartresp;
        System.out.println("Service situation - req:"+TimeStamp.getStringTimeStamp(tsstartreq)+" resp:"+TimeStamp.getStringTimeStamp(tsstartresp));
        while (tsstartreq == tsstopreq || tsstartresp == tsstopresp || tsstopresp <= tsstopreq ) {
            tsstopresp = clientSync.getTSResp();
            tsstopreq = clientSync.getTSReq();
            System.out.println("tsstartreq: "+TimeStamp.getStringTimeStamp(tsstartreq)+" tsstopreq: "+TimeStamp.getStringTimeStamp(tsstopreq));
            System.out.println("tsstartresp: "+TimeStamp.getStringTimeStamp(tsstartresp)+" tsstopresp: "+TimeStamp.getStringTimeStamp(tsstopresp));
            System.out.println("motorMemory: "+TimeStamp.getStringTimeStamp(motorMemory.getTimestamp()));
            Thread.sleep(100);
        }
        System.out.println("Finished process - req:"+TimeStamp.getStringTimeStamp(tsstopreq)+" resp"+TimeStamp.getStringTimeStamp(tsstopresp));
        assertEquals(expectedSum, clientSync.getSum());
		
	serviceProvider.stop();
	mecaMind.shutDown();
    }
}