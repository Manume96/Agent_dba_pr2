package core.agent.communication;
import core.agent.communication.MessageProtocol;


public class SantaMessageProtocol extends MessageProtocol{
    
        public SantaMessageProtocol() {
        super("Santa");
    }

    @Override
    public String createMessageBody(String msg) {
        String body = MessageStyle.SANTA.getPrefix() + msg + MessageStyle.SANTA.getSuffix();
        return body;
    };
        
}
