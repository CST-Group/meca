/**
 * 
 */

package br.unicamp.meca.system1.codelets;

import br.unicamp.cst.bindings.ros2java.RosTopicOneShotPublisherCodelet;
import br.unicamp.cst.core.entities.Memory;
import id.jrosmessages.Message;

/**
 * A Wrapper of the CST's RosTopicOneShotPublisherCodelet implementing the IMotorCodelet interface, 
 * in order to be able to be mounted on the MecaMind.
 * 
 * @author andre
 * @author jrborelli - ROS2.
 * 
 * @param <T> The ROS Message Type - Ex: std_msgs.String from ROS standard messages
 */
public abstract class Ros2TopicOneShotPublisherMotorCodelet<T extends Message> 
        extends RosTopicOneShotPublisherCodelet<T> implements IMotorCodelet {

    protected String id;

    /**
     * Constructor for the RosTopicOneShotPublisherMotorCodelet.
     * 
     * @param id the id of this Motor Codelet, to be used in mounting MECA Mind.
     * @param topic the name of the ROS topic this node will be publishing to.
     * @param messageType the ROS message class type. Ex: std_msgs.String.class
     */
    public Ros2TopicOneShotPublisherMotorCodelet(String id, String topic, Class<T> messageType) {
        super(id, topic, messageType);
        this.id = id;
    }

    /**
     * Returns the id of this RosTopicOneShotPublisherMotorCodelet.
     * 
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of this RosTopicOneShotPublisherMotorCodelet.
     * 
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
}
