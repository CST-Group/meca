package troca_ros;

import id.jrosmessages.MessageDescriptor;
import pinorobotics.jrosservices.msgs.ServiceDefinition;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class AddTwoIntsServiceDefinition implements ServiceDefinition<AddTwoIntsRequestMessage, AddTwoIntsResponseMessage> {

    private static final MessageDescriptor<AddTwoIntsRequestMessage> REQUEST_MESSAGE_DESCRIPTOR = new MessageDescriptor<>(AddTwoIntsRequestMessage.class);
    private static final MessageDescriptor<AddTwoIntsResponseMessage> RESPONSE_MESSAGE_DESCRIPTOR = new MessageDescriptor<>(AddTwoIntsResponseMessage.class);

    @Override
    public MessageDescriptor<AddTwoIntsRequestMessage> getServiceRequestMessage() {
        return REQUEST_MESSAGE_DESCRIPTOR;
    }

    @Override
    public MessageDescriptor<AddTwoIntsResponseMessage> getServiceResponseMessage() {
        return RESPONSE_MESSAGE_DESCRIPTOR;
    }
}