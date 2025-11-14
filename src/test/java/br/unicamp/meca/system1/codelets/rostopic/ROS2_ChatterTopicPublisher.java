package br.unicamp.meca.system1.codelets.rostopic;

import br.unicamp.cst.core.entities.Memory;
import id.jrosmessages.std_msgs.StringMessage;
import br.unicamp.meca.system1.codelets.Ros2TopicPublisherMotorCodelet;

/**
 *
 * @author jrborelli
 */

public class ROS2_ChatterTopicPublisher extends Ros2TopicPublisherMotorCodelet<StringMessage> {

    public ROS2_ChatterTopicPublisher(String name, String topic) {
        super(name, topic, StringMessage.class);
    }

    @Override
    protected StringMessage createNewMessage() {
        return new StringMessage();
    }

    @Override
    protected void fillMessageToBePublished(Memory motorMemory, StringMessage message) {
        if (motorMemory == null || message == null) return;

        Object data = motorMemory.getI();
        if (data instanceof String) {
            message.withData((String) data);
        } else {
            message.withData("");
        }
    }
}