
package br.unicamp.meca.system1.codelets.rostopic;

import br.unicamp.cst.core.entities.Memory;
import id.jrosmessages.std_msgs.StringMessage;
import br.unicamp.meca.system1.codelets.Ros2TopicSubscriberSensoryCodelet;

/**
 * ROS2 version of ChatterTopicSubscriber for MECA
 * 
 * @author jrborelli
 */
public class ROS2_ChatterTopicSubscriber extends Ros2TopicSubscriberSensoryCodelet<StringMessage> {

    public ROS2_ChatterTopicSubscriber(String topic) {
        super("ChatterTopicSubscriber", topic, StringMessage.class);
    }

    @Override
    public void fillMemoryWithReceivedMessage(StringMessage message, Memory sensoryMemory) {
        if (message == null || sensoryMemory == null) {
            if (sensoryMemory != null) sensoryMemory.setI(null);
            return;
        }

        String data = message.data;
        if (data == null) {
            sensoryMemory.setI(null);
        } else {
            System.out.println("I heard: \"" + data + "\"");
            sensoryMemory.setI(data);
        }
    }
}