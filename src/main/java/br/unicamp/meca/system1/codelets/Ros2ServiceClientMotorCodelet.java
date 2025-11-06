/**
 * 
 */
package br.unicamp.meca.system1.codelets;

import java.net.URI;
import br.unicamp.cst.bindings.ros2java.RosServiceClientCodelet; 
import id.jrosmessages.Message;
import pinorobotics.jrosservices.msgs.ServiceDefinition;

/**
 * A Wrapper of the CST's RosServiceClientCodelet implementing the IMotorCodelet interface, 
 * adapted for ROS 2 (jros2client), to be mounted on the MecaMind.
 * 
 * @author andre
 * @author jrborelli - ROS2
 * 
 * @param <S> Service Message Request - Ex: AddTwoIntsRequest
 * @param <T> Service Message Response - Ex: AddTwoIntsResponse
 */


public abstract class Ros2ServiceClientMotorCodelet<S extends Message, T extends Message>
        extends RosServiceClientCodelet<S, T> implements IMotorCodelet {

    protected String id;

    /**
     * Constructor for the RosServiceClientMotorCodelet.
     * 
     * @param id the id of this Motor Codelet, to be used in mounting MECA Mind.
     * @param serviceName the ROS 2 service name. Ex: "add_two_ints"
     * @param serviceDefinition the service definition object
     */
    
    public Ros2ServiceClientMotorCodelet(String id, String serviceName, 
                                        ServiceDefinition<S, T> serviceDefinition) {
        super(serviceName, serviceDefinition);
        this.id = id;
    }

    /**
     * Returns the id of this RosServiceClientMotorCodelet.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of this RosServiceClientMotorCodelet.
     */
    public void setId(String id) {
        this.id = id;
    }
}