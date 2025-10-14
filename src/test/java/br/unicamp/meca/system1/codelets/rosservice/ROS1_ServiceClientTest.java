/**
 * 
 */
package br.unicamp.meca.system1.codelets.rosservice;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.ros.RosCore;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.support.TimeStamp;
import br.unicamp.meca.mind.MecaMind;
import br.unicamp.meca.system1.codelets.IMotorCodelet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.LoggerFactory;

/**
 * @author andre
 *
 */
public class ROS1_ServiceClientTest {

	private static RosCore rosCore;
        private volatile Memory motorMemory;
	
	@BeforeAll
    public static void beforeAllTestMethods() {
		rosCore  = RosCore.newPublic("127.0.0.1",11311);
	    rosCore.start();
            try{Thread.sleep(1000);} catch(Exception e){e.printStackTrace();}
    }

	@AfterAll
    public static void afterAllTestMethods() {
        rosCore.shutdown();
    }
    
    public void SilenceLoggers() {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("httpclient")).setLevel(ch.qos.logback.classic.Level.OFF);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.apache")).setLevel(ch.qos.logback.classic.Level.OFF);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.ros")).setLevel(ch.qos.logback.classic.Level.OFF);
        Logger.getLogger("Simulation").setLevel(Level.SEVERE);
    }
    
    @Test
    public void testRosService() throws URISyntaxException, InterruptedException {
    	
		AddTwoIntROS1ServiceProvider addTwoIntService = new AddTwoIntROS1ServiceProvider();
		NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
		NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic("127.0.0.1",new URI("http://127.0.0.1:11311"));
		nodeMainExecutor.execute(addTwoIntService, nodeConfiguration);	
                
		
		Thread.sleep(2000);
		
		MecaMind mecaMind = new MecaMind("RosServiceClient");
		
		List<IMotorCodelet> motorCodelets = new ArrayList<>();
		
		AddTwoIntROS1ServiceClient addTwoIntServiceClient = new AddTwoIntROS1ServiceClient("127.0.0.1",new URI("http://127.0.0.1:11311"));
		motorCodelets.add(addTwoIntServiceClient);
    
		mecaMind.setIMotorCodelets(motorCodelets);
                mecaMind.mountMecaMind();		
		mecaMind.start();
		
		Thread.sleep(5000);
		
		motorMemory = addTwoIntServiceClient.getInput(addTwoIntServiceClient.getId());
		
		Integer expectedSum = 5;
		
		Integer[] numsToSum = new Integer[] {2,3};
		motorMemory.setI(numsToSum);
                System.out.println("Nums to sum were changed to {2,3}");
		
		Thread.sleep(2000);
		
		assertEquals(expectedSum, addTwoIntServiceClient.getSum());
		
		nodeMainExecutor.shutdownNodeMain(addTwoIntService);
		
		mecaMind.shutDown();
    
    }
    
    @Test
    public void testRosServiceCallTwice() throws URISyntaxException, InterruptedException {
    	
		SilenceLoggers();
                AddTwoIntROS1ServiceProvider addTwoIntService = new AddTwoIntROS1ServiceProvider();
		NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
		NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic("127.0.0.1",new URI("http://127.0.0.1:11311"));
		nodeMainExecutor.execute(addTwoIntService, nodeConfiguration);	
		
		Thread.sleep(2000);
		
		MecaMind mecaMind = new MecaMind("RosServiceClient");
		
		List<IMotorCodelet> motorCodelets = new ArrayList<>();
		
		AddTwoIntROS1ServiceClient addTwoIntServiceClient = new AddTwoIntROS1ServiceClient("127.0.0.1",new URI("http://127.0.0.1:11311"));
		motorCodelets.add(addTwoIntServiceClient);
    
		mecaMind.setIMotorCodelets(motorCodelets);
                mecaMind.mountMecaMind();		
		mecaMind.start();
		
		Thread.sleep(5000);
		
                MemoryContainer mc=null;
                motorMemory = addTwoIntServiceClient.getInput(addTwoIntServiceClient.getId());
                // Be careful ... at this point motorMemory is a MemoryContainer, and it is empty
                if (motorMemory instanceof MemoryContainer)
                    mc = (MemoryContainer) motorMemory;
                Integer expectedSum = 5;
		
		Integer[] numsToSum = new Integer[] {2,3};
		int id = motorMemory.setI(numsToSum);
                // At this point, motorMemory has 1 internal MemoryObject and id should be 0
                System.out.println("id: "+id);
                System.out.println("\n\nNums to sum were changed to {2,3} at "+TimeStamp.getStringTimeStamp(motorMemory.getTimestamp()));
		long tsstartreq = addTwoIntServiceClient.getTSReq();
                long tsstopreq = tsstartreq;
                long tsstartresp = addTwoIntServiceClient.getTSResp();
                long tsstopresp = tsstartresp;
                System.out.println("Service situation - req:"+TimeStamp.getStringTimeStamp(tsstartreq)+" resp:"+TimeStamp.getStringTimeStamp(tsstartresp));
                while (tsstartreq == tsstopreq || tsstartresp == tsstopresp ) {
                    tsstopresp = addTwoIntServiceClient.getTSResp();
                    tsstopreq = addTwoIntServiceClient.getTSReq();
                    System.out.println("startreq: "+TimeStamp.getStringTimeStamp(tsstartreq)+" stopreq: "+TimeStamp.getStringTimeStamp(tsstopreq));
                    System.out.println("startresp: "+TimeStamp.getStringTimeStamp(tsstartresp)+" stopreq: "+TimeStamp.getStringTimeStamp(tsstopresp));
                    Thread.sleep(100);
                }
                System.out.println("Finished process - req:"+TimeStamp.getStringTimeStamp(tsstopreq)+" resp:"+TimeStamp.getStringTimeStamp(tsstopresp));
		//Thread.sleep(5000);
		
		assertEquals(expectedSum, addTwoIntServiceClient.getSum());
		
		expectedSum = 6;
		
		numsToSum = new Integer[] {3,3};
                // This is the tricker part ... instead of calling setI from motorMemory, we should use its MemoryContainer counterpart
                mc.setI(numsToSum,0);
                System.out.println("\n\nNums to sum were changed to {3,3} at "+TimeStamp.getStringTimeStamp(motorMemory.getTimestamp()));
                tsstartreq = addTwoIntServiceClient.getTSReq();
                tsstopreq = tsstartreq;
                tsstartresp = addTwoIntServiceClient.getTSResp();
                tsstopresp = tsstartresp;
                System.out.println("Service situation - req:"+TimeStamp.getStringTimeStamp(tsstartreq)+" resp:"+TimeStamp.getStringTimeStamp(tsstartresp));
                while (tsstartreq == tsstopreq || tsstartresp == tsstopresp ) {
                    tsstopresp = addTwoIntServiceClient.getTSResp();
                    tsstopreq = addTwoIntServiceClient.getTSReq();
                    System.out.println("tsstartreq: "+TimeStamp.getStringTimeStamp(tsstartreq)+" tsstopreq: "+TimeStamp.getStringTimeStamp(tsstopreq));
                    System.out.println("tsstartresp: "+TimeStamp.getStringTimeStamp(tsstartresp)+" tsstopreq: "+TimeStamp.getStringTimeStamp(tsstopresp));
                    System.out.println("motorMemory: "+TimeStamp.getStringTimeStamp(motorMemory.getTimestamp()));
                    Thread.sleep(100);
                }
                System.out.println("Finished process - req:"+TimeStamp.getStringTimeStamp(tsstopreq)+" resp"+TimeStamp.getStringTimeStamp(tsstopresp));
                assertEquals(expectedSum, addTwoIntServiceClient.getSum());
		
		nodeMainExecutor.shutdownNodeMain(addTwoIntService);
		
		mecaMind.shutDown();
    
    }
}
