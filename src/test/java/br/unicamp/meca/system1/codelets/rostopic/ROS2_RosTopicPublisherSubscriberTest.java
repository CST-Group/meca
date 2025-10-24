
package br.unicamp.meca.system1.codelets.rostopic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.meca.mind.MecaMind;
import br.unicamp.meca.system1.codelets.IMotorCodelet;
import br.unicamp.meca.system1.codelets.ISensoryCodelet;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ROS2 version of the MECA ROS1 Publisher-Subscriber test
 * 
 * @author jrborelli
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
        ROS2_ChatterTopicPublisher chatterTopicPublisher = new ROS2_ChatterTopicPublisher("chatter");
        motorCodelets.add(chatterTopicPublisher);

        List<ISensoryCodelet> sensoryCodelets = new ArrayList<>();
        ROS2_ChatterTopicSubscriber chatterTopicSubscriber = new ROS2_ChatterTopicSubscriber("chatter");
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


/*

// Add these imports:
import java.time.Duration;
import java.time.Instant;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@Test
    public void testChatterTopicIntegration() throws Exception {
        // --- Create specialized codelets ---
        ROS2_ChatterTopicPublisher publisherCodelet = new ROS2_ChatterTopicPublisher("chatter");
        ROS2_ChatterTopicSubscriber subscriberCodelet = new ROS2_ChatterTopicSubscriber("chatter");
        LOGGER.log(Level.INFO,"#############" + subscriberCodelet.getId());
        // --- Create Memory objects named exactly as the ROS topic ---
        MemoryObject topicMemoryForPublisher = new MemoryObject();
        topicMemoryForPublisher.setName("chatter"); // IMPORTANT: name == topic

        MemoryObject topicMemoryForSubscriber = new MemoryObject();
        topicMemoryForSubscriber.setName("chatter"); // IMPORTANT: name == topic
        
        MemoryObject topicMemoryForPublisherNNN = new MemoryObject();           //NOVO
        topicMemoryForPublisherNNN.setName("NNN"); // IMPORTANT: name == topic  //NOVO
        
        //MemoryObject topicMemoryForSubscriberNNN = new MemoryObject();          //NOVO
        //topicMemoryForSubscriberNNN.setName("NNN"); // IMPORTANT: name == topic  //NOVO

        // --- Attach the topic-named memories to the codelets BEFORE mounting ---
        // This ensures that RosTopicSubscriberCodelet.accessMemoryObjects()
        // (which looks up outputs by topic name) will find the memory immediately.
        publisherCodelet.addInput(topicMemoryForPublisher);
        publisherCodelet.addInput(topicMemoryForPublisherNNN);
        subscriberCodelet.addOutput(topicMemoryForSubscriber);
        //subscriberCodelet.addOutput(topicMemoryForSubscriberNNN);//NOVO

        // --- Register codelets in the MECA mind lists ---
        List<IMotorCodelet> motorCodelets = new ArrayList<>();
        motorCodelets.add(publisherCodelet);

        List<ISensoryCodelet> sensoryCodelets = new ArrayList<>();
        sensoryCodelets.add(subscriberCodelet);

        mecaMind.setIMotorCodelets(motorCodelets);
        mecaMind.setISensoryCodelets(sensoryCodelets);

        // --- Mount and start MECA Mind ---
        // mountMecaMind will also create its own memories (by codelet ID),
        // but we already provided a topic-named memory that the codelets expect.
        mecaMind.mountMecaMind();
        mecaMind.start();

        LOGGER.info("MECA Mind started — waiting for topic bridge...");

        // --- Send the test message by setting the publisher's memory ---
        String messageExpected = "Hello World";
        String messageExpectedNNN = "NNN";
        // set on the memory object we attached to the publisher earlier
        topicMemoryForPublisher.setI(messageExpected);
        topicMemoryForPublisherNNN.setI(messageExpectedNNN);

        // --- Wait for the subscriber to update the subscriber memory ---
        // We poll short intervals, respect total timeout (5s).
        final long timeoutMillis = 50000L;
        final long pollIntervalMs = 50L;
        final long start = System.currentTimeMillis();
        String messageActual = null;

        while (System.currentTimeMillis() - start < timeoutMillis) {
            Object val = topicMemoryForSubscriber.getI();
            if (val instanceof String) {
                messageActual = (String) val;
                // we got a string — break
                break;
            }
            TimeUnit.MILLISECONDS.sleep(pollIntervalMs);
        }

        // --- Validate ---
        LOGGER.log(Level.INFO, "Expected = \"{0}\", Actual = \"{1}\"", new Object[]{messageExpected, messageActual});
        assertEquals(messageExpected, messageActual);

        // --- Shutdown ---
        mecaMind.shutDown();
    }
*/