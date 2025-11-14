
package br.unicamp.meca.system1.codelets.rostopic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.meca.mind.MecaMind;
import br.unicamp.meca.system1.codelets.IMotorCodelet;
import br.unicamp.meca.system1.codelets.ISensoryCodelet;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Fully asynchronous ROS2 Publisher-Subscriber test using CompletableFuture.
 * Reacts immediately when the message arrives.
 * 
 * Author: jrborelli
 */
public class ROS2_RosTopicPublisherSubscriberTest {

    private static final Logger LOGGER = Logger.getLogger(ROS2_RosTopicPublisherSubscriberTest.class.getName());
    private static MecaMind mecaMind;
    
    // optionally silence noisy loggers here if you want:
    private static void SilenceLoggers() {
        Logger.getLogger("pinorobotics.rtpstalk").setLevel(Level.OFF);
        Logger.getLogger("id.jros2client").setLevel(Level.OFF);
    }

    @BeforeAll
    public static void setup() {
        SilenceLoggers();
        LOGGER.info("Setting up MecaMind for ROS2 Publisher/Subscriber test...");
        mecaMind = new MecaMind("ROS2_RosTopicPublisherSubscriber");
        
    }

    @AfterAll
    public static void teardown() {
        LOGGER.info("Tearing down MecaMind...");
        if (mecaMind != null) {
            mecaMind.shutDown();
        }
    }

    @Test
    public void testRos2Topics() throws InterruptedException {

        List<IMotorCodelet> motorCodelets = new ArrayList<>();
        ROS2_ChatterTopicPublisher chatterTopicPublisher = new ROS2_ChatterTopicPublisher("chatter", "chatter");
        motorCodelets.add(chatterTopicPublisher);

        List<ISensoryCodelet> sensoryCodelets = new ArrayList<>();
        ROS2_ChatterTopicSubscriber chatterTopicSubscriber = new ROS2_ChatterTopicSubscriber("chatter","chatter");
        sensoryCodelets.add(chatterTopicSubscriber);
        
        mecaMind.setIMotorCodelets(motorCodelets);
        mecaMind.setISensoryCodelets(sensoryCodelets);
        mecaMind.mountMecaMind();

        mecaMind.start();
        LOGGER.info("MECA Mind started — waiting for topic bridge...");

        // Give time for nodes to start
        Thread.sleep(1000);

        String messageExpected = "Hello World";
        Memory motorMemory = chatterTopicPublisher.getInput(chatterTopicPublisher.getId());
        motorMemory.setI(messageExpected);
                
        final long timeoutMillis = 50000L;
        //final long pollIntervalMs = 50L;
        final long start = System.currentTimeMillis();
        long timeFinished = 0;
        
        Memory sensoryMemory = chatterTopicSubscriber.getOutput(chatterTopicSubscriber.getId());
        String messageActual = null;
        
        while(messageActual==null && (System.currentTimeMillis() - start < timeoutMillis)){
          messageActual = (String) sensoryMemory.getI();  
        }
        
        timeFinished = System.currentTimeMillis() - start;
        
        LOGGER.log(Level.INFO, "Expected = \"{0}\", Actual = \"{1}\"", new Object[]{messageExpected, messageActual});
        assertEquals(messageExpected, messageActual);
        LOGGER.log(Level.INFO, "Message received in {0} seconds", new Object[]{timeFinished});

        mecaMind.shutDown(); // Already in @AfterAll
    }
}
