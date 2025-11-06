package troca_ros;

import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadata;
import id.jrosmessages.RosInterfaceType;
import id.xfunction.XJson;
import java.util.Objects;

/**
 * Definition for example_interfaces/AddTwoInts_Request
 *
 * @author lambdaprime intid@protonmail.com
 */
@MessageMetadata(
        name = AddTwoIntsRequestMessage.NAME,
        fields = {"a", "b"},
        interfaceType = RosInterfaceType.SERVICE)
public class AddTwoIntsRequestMessage implements Message {

    static final String NAME = "troca_ros/AddTwoIntsServiceRequest";
    public int a;
    public int b;

    public AddTwoIntsRequestMessage() {}

    public AddTwoIntsRequestMessage(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public AddTwoIntsRequestMessage withA(int a) {
       this.a = a;
       return this;
   }
   
   public AddTwoIntsRequestMessage withB(int b) {
       this.b = b;
       return this;
   }
    
    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    @Override
    public boolean equals(Object obj) {
        var other = (AddTwoIntsRequestMessage) obj;
        return Objects.equals(a, other.b) && Objects.equals(a, other.b);
    }

    @Override
    public String toString() {
        return XJson.asString("a", a,"b", b);
    }
}