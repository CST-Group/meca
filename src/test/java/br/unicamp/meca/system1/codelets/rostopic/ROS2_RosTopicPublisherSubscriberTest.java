
package br.unicamp.meca.system1.codelets.rostopic;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.meca.mind.MecaMind;
import br.unicamp.meca.system1.codelets.IMotorCodelet;
import br.unicamp.meca.system1.codelets.ISensoryCodelet;

import java.util.ArrayList;
import java.util.List;

/**
 * ROS2 version of the MECA ROS1 Publisher-Subscriber test
 * 
 * @author jrborelli
 */
public class ROS2_RosTopicPublisherSubscriberTest {

    private static MecaMind mecaMind;

    @BeforeClass
    public static void beforeAllTestMethods() {
        mecaMind = new MecaMind("ROS2_RosTopicPublisherSubscriber");
    }

    @AfterClass
    public static void afterAllTestMethods() {
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

        // Give time for nodes to start
        Thread.sleep(1000);

        String messageExpected = "Hello World";
        Memory motorMemory = chatterTopicPublisher.getInput(chatterTopicPublisher.getId());
        motorMemory.setI(messageExpected);

        // Wait for the message to propagate
        Thread.sleep(1000);

        Memory sensoryMemory = chatterTopicSubscriber.getOutput(chatterTopicSubscriber.getId());
        String messageActual = (String) sensoryMemory.getI();

        assertEquals(messageExpected, messageActual);

        mecaMind.shutDown();
    }
}