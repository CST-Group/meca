
package br.unicamp.meca.system1.codelets.rosservice;


import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.support.TimeStamp;
import br.unicamp.meca.mind.MecaMind;
import br.unicamp.meca.system1.codelets.IMotorCodelet;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
//import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * ROS2 version of the MECA ROS1 RosServiceClientTest
 * 
 * @author jrborelli
 */


public class ROS2_ServiceClientTest {

    private static final Logger LOGGER = Logger.getLogger(ROS2_ServiceClientTest.class.getName());
    private static MecaMind mecaMind;
    private volatile Memory motorMemory;
    private static AddTwoIntsROS2ServiceProvider serviceProvider;
    
    private static void SilenceLoggers() {
        Logger.getLogger("pinorobotics.rtpstalk").setLevel(Level.OFF);
        Logger.getLogger("id.jros2client").setLevel(Level.OFF);
    }

    @BeforeAll
    public static void setup() {
        SilenceLoggers();
        mecaMind = new MecaMind("ROS2_RosServiceClientTest");
        serviceProvider = new AddTwoIntsROS2ServiceProvider();
        serviceProvider.start();
    }

    @AfterAll
    public static void cleanup() {
        if (mecaMind != null) mecaMind.shutDown();
        serviceProvider.stop();
    }

    @Test
    public void testROS2_RosServiceCallOnce() throws InterruptedException {
        System.out.println("\nStarting the RosServiceCallOnce test");
        Thread.sleep(5000); //Necessary, so that the mind can shutdown.
        
        AddTwoIntROS2ServiceClient clientSync = new AddTwoIntROS2ServiceClient("AddTwoInts", "add_two_ints");
        //clientSync.setName("AddTwoInts");
        System.out.println("The default name of the codelet is "+clientSync.getName());

        List<IMotorCodelet> motorCodelets = new ArrayList<>();
        motorCodelets.add(clientSync);
        mecaMind.setIMotorCodelets(motorCodelets);
        mecaMind.mountMecaMind();
        List<Memory> lm = clientSync.getInputs();
        System.out.println("The number of inputs is "+lm.size());
        for (Memory m :lm) {
            System.out.println("The name of memory is "+m.getName());
        }
        mecaMind.start();
        
		
	motorMemory = clientSync.getInput(clientSync.getId());
		
	Integer expectedSum = 5;
		
	Integer[] numsToSum = new Integer[] {2,3};
	motorMemory.setI(numsToSum);
        
        System.out.println("Nums to sum were changed to {2,3}");
		
        long tsstartreq = System.currentTimeMillis(); //clientSync.getTSReq();
        long tsstopreq = tsstartreq;
        long tsstartresp = System.currentTimeMillis(); //clientSync.getTSResp();
        long tsstopresp = tsstartresp;
        //int id = motorMemory.setI(numsToSum);
        // At this point, motorMemory has 1 internal MemoryObject and id should be 0
        //System.out.println("id: "+id);  
        System.out.println("\n\nNums to sum were changed to {2,3} at "+TimeStamp.getStringTimeStamp(motorMemory.getTimestamp()));
        System.out.println("Service situation - req:"+TimeStamp.getStringTimeStamp(tsstartreq)+" resp:"+TimeStamp.getStringTimeStamp(tsstartresp));
        
        while (clientSync.getSum()== null || tsstartreq == tsstopreq || tsstartresp == tsstopresp || tsstopresp <= tsstopreq  ) {
            tsstopresp = clientSync.getTSResp();
            tsstopreq = clientSync.getTSReq();
            clientSync.getSum();
        }
        
        mecaMind.shutDown();
        
        System.out.println("Finished process - req:"+TimeStamp.getStringTimeStamp(tsstopreq)+" resp:"+TimeStamp.getStringTimeStamp(tsstopresp));
	System.out.println("I took "+TimeStamp.getStringTimeStamp(tsstopresp-tsstartreq,"mm:ss.SSS")+ " s to attend the service request !!");
		
	assertEquals(expectedSum, clientSync.getSum());

        System.out.println("The test was finished!");
    }
    
    public String cvr(long t) {
        return TimeStamp.getStringTimeStamp(t,"hh:mm:ss.SSS");
    }

    @Test
    public void testROS2_RosServiceCallTwice() throws InterruptedException {
        System.out.println("\nStarting the RosServiceCallTwice test");

        AddTwoIntROS2ServiceClient clientSync = new AddTwoIntROS2ServiceClient("AddTwoInts","add_two_ints");

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
        
        while (clientSync.getSum()== null || tsstartreq == tsstopreq || tsstartresp == tsstopresp || tsstopresp <= tsstopreq  ) {
            tsstopresp = clientSync.getTSResp();
            tsstopreq = clientSync.getTSReq();
            clientSync.getSum();
            System.out.print(".");
        }
        
        System.out.println("\nFinished process - req:"+TimeStamp.getStringTimeStamp(tsstopreq)+" resp:"+TimeStamp.getStringTimeStamp(tsstopresp));

	System.out.println("I took "+TimeStamp.getStringTimeStamp(tsstopresp-tsstartreq,"mm:ss.SSS")+ " s to attend the service request !!");
		
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
        
        while (clientSync.getSum()== null || tsstartreq == tsstopreq || tsstartresp == tsstopresp || tsstopresp <= tsstopreq ) {
            tsstopresp = clientSync.getTSResp();
            tsstopreq = clientSync.getTSReq();
            clientSync.getSum();
            System.out.print(".");
        }
        
        motorMemory.setI(null);
        motorMemory = null;
        mc.setI(null,0);
        mc = null;
        mecaMind.shutDown();
        
        System.out.println("\nFinished process - req:"+TimeStamp.getStringTimeStamp(tsstopreq)+" resp:"+TimeStamp.getStringTimeStamp(tsstopresp));
        System.out.println("I took "+TimeStamp.getStringTimeStamp(tsstopresp-tsstartreq,"mm:ss.SSS")+ " s to attend the service request !!");
	
        assertEquals(expectedSum, clientSync.getSum());
        
        System.out.println("The test was finished!");
    }
}

/*
@Test
    public void testROS2_RosServiceCallOnce() throws InterruptedException {
        System.out.println("\nStarting the RosServiceCallOnce test");
        //SilenceLoggers();
        //setup();
        // Start ROS2 service provider
//        AddTwoIntsROS2ServiceProvider serviceProvider = new AddTwoIntsROS2ServiceProvider();
//        serviceProvider.start();
//        Thread.sleep(500); // give service time to start

        // Create memory object for inputs
        //Memory memory = mecaMind.createMemoryObject("add_two_ints");

        // Instantiate ROS2 synchronous client
        //AddTwoIntsServiceClientSyncRos2 clientSync = new AddTwoIntsServiceClientSyncRos2("add_two_ints");
        AddTwoIntROS2ServiceClient clientSync = new AddTwoIntROS2ServiceClient("add_two_ints");
        //clientSync.start();

        // Insert client codelet in mind (for consistency)
        List<IMotorCodelet> motorCodelets = new ArrayList<>();
        motorCodelets.add(clientSync);
        mecaMind.setIMotorCodelets(motorCodelets);
        mecaMind.mountMecaMind();
        mecaMind.start();
        
        //Thread.sleep(5000);
		
	motorMemory = clientSync.getInput(clientSync.getId());
		
	Integer expectedSum = 5;
		
	Integer[] numsToSum = new Integer[] {2,3};
	motorMemory.setI(numsToSum);
        System.out.println("Nums to sum were changed to {2,3}");
		
	//Thread.sleep(2000);
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
            //System.out.println("startreq: "+TimeStamp.getStringTimeStamp(tsstartreq)+" stopreq: "+TimeStamp.getStringTimeStamp(tsstopreq));
            //System.out.println("startresp: "+TimeStamp.getStringTimeStamp(tsstartresp)+" stopresp: "+TimeStamp.getStringTimeStamp(tsstopresp));
            Thread.sleep(100);
        }
        System.out.println("Finished process - req:"+TimeStamp.getStringTimeStamp(tsstopreq)+" resp:"+TimeStamp.getStringTimeStamp(tsstopresp));
	System.out.println("I took "+TimeStamp.getStringTimeStamp(tsstopresp-tsstartreq,"mm:ss.SSS")+ " s to attend the service request !!");
		
	assertEquals(expectedSum, clientSync.getSum());

        // Cleanup
        //clientSync.stop();
        mecaMind.shutDown();
        //Thread.sleep(5000);
        //clientSync.stop();
        //serviceProvider.stop();
        //while (serviceProvider.running) Thread.sleep(100);
        System.out.println("The test was finished!");
    }
    
    public String cvr(long t) {
        return TimeStamp.getStringTimeStamp(t,"hh:mm:ss.SSS");
    }

    @Test
    public void testROS2_RosServiceCallTwice() throws InterruptedException {
        System.out.println("\nStarting the RosServiceCallTwice test");
        SilenceLoggers();
        //setup();
        // Start ROS2 service provider
//        AddTwoIntsROS2ServiceProvider serviceProvider = new AddTwoIntsROS2ServiceProvider();
//        serviceProvider.start();
//        Thread.sleep(500);

        //Memory memory = mecaMind.createMemoryObject("add_two_ints");

        AddTwoIntROS2ServiceClient clientSync = new AddTwoIntROS2ServiceClient("add_two_ints");
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
            System.out.print(".");
            //System.out.println("req: "+cvr(tsstartreq)+" "+cvr(tsstopreq));
            //System.out.println("resp: "+cvr(tsstartresp)+" "+cvr(tsstopresp));
            Thread.sleep(100);
        }
        System.out.println("\nFinished process - req:"+TimeStamp.getStringTimeStamp(tsstopreq)+" resp:"+TimeStamp.getStringTimeStamp(tsstopresp));
		//Thread.sleep(5000);
	System.out.println("I took "+TimeStamp.getStringTimeStamp(tsstopresp-tsstartreq,"mm:ss.SSS")+ " s to attend the service request !!");
		
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
            //System.out.println("tsstartreq: "+TimeStamp.getStringTimeStamp(tsstartreq)+" tsstopreq: "+TimeStamp.getStringTimeStamp(tsstopreq));
            //System.out.println("tsstartresp: "+TimeStamp.getStringTimeStamp(tsstartresp)+" tsstopresp: "+TimeStamp.getStringTimeStamp(tsstopresp));
            //System.out.println("motorMemory: "+TimeStamp.getStringTimeStamp(motorMemory.getTimestamp()));
            System.out.print(".");
            Thread.sleep(100);
        }
        System.out.println("\nFinished process - req:"+TimeStamp.getStringTimeStamp(tsstopreq)+" resp:"+TimeStamp.getStringTimeStamp(tsstopresp));
        System.out.println("I took "+TimeStamp.getStringTimeStamp(tsstopresp-tsstartreq,"mm:ss.SSS")+ " s to attend the service request !!");
	
        assertEquals(expectedSum, clientSync.getSum());
	mecaMind.shutDown();
        //clientSync.stop();
        //Thread.sleep(5000);
	//serviceProvider.stop();
        //while (serviceProvider.running) Thread.sleep(100);
	//Thread.sleep(5000);
        System.out.println("The test was finished!");
    }
*/