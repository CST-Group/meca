package troca_ros;

import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadata;
import id.jrosmessages.RosInterfaceType;
import id.xfunction.XJson;
import java.util.Objects;

/**
 * Definition for example_interfaces/AddTwoInts_Response
 *
 * @author lambdaprime intid@protonmail.com
 */
@MessageMetadata(name = AddTwoIntsResponseMessage.NAME, interfaceType = RosInterfaceType.SERVICE)
public class AddTwoIntsResponseMessage implements Message {

    static final String NAME = "troca_ros/AddTwoIntsServiceResponse";

    public int sum;

    public AddTwoIntsResponseMessage() {}

    public AddTwoIntsResponseMessage(int sum) {
        this.sum = sum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sum);
    }

    @Override
    public boolean equals(Object obj) {
        var other = (AddTwoIntsResponseMessage) obj;
        return sum == other.sum;
    }

    @Override
    public String toString() {
        return XJson.asString("sum", sum);
    }
}