package core.agent.communication;

import jade.lang.acl.ACLMessage;
public abstract class MessageProtocol  {
        private String agentName;
        public MessageProtocol(String agentName) {
             this.agentName = agentName;
         }
   
        
    public ACLMessage createMessage(int performative, AgentName receiver, 
                                           String content, ConversationId conversationId) {
        ACLMessage msg = new ACLMessage(performative);
        msg.addReceiver(receiver.toAID());
        msg.setContent(content);
        msg.setConversationId(conversationId.getId());
        msg.setLanguage("English");
        msg.setOntology("christmas");
        return msg;
    }
    
    public abstract String createMessageBody(String msg);
    

    public String extractMessageBody(ACLMessage msg) {
        return msg.getContent();
    }
}
