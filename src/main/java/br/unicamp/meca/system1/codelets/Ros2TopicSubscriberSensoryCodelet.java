package br.unicamp.meca.system1.codelets;

import br.unicamp.cst.bindings.ros2java.RosTopicSubscriberCodelet;
import id.jrosmessages.Message;

/**
 * A Wrapper of the CST's RosTopicSubscriberCodelet implementing the ISensoryCodelet interface, 
 * in order to be able to be mounted on the MecaMind.
 * 
 * @author andre
 * @author jrborelli - ROS2.
 * 
 * @param <T> The ROS Message Type - Ex: std_msgs.String from ROS standard messages
 */
public abstract class Ros2TopicSubscriberSensoryCodelet<T extends Message> 
        extends RosTopicSubscriberCodelet<T> implements ISensoryCodelet {

    protected String id;

    /**
     * Constructor for the RosTopicSubscriberSensoryCodelet.
     * 
     * @param id the id of this Sensory Codelet, to be used in mounting MECA Mind.
     * @param topic the name of the ROS topic this node will be subscribing to.
     * @param messageType the ROS message class type. Ex: std_msgs.String.class
     */
    public Ros2TopicSubscriberSensoryCodelet(String id, String topic, Class<T> messageType) {
        super(id,topic, messageType);
        this.id = topic;
    }

    /**
     * Returns the id of this RosTopicSubscriberSensoryCodelet.
     * 
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of this RosTopicSubscriberSensoryCodelet.
     * 
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
}