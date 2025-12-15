
package core.agent.communication;

public enum ConversationId {
    AUTHORIZATION("authorization"),
    MISSION("mission"),
    TRANSLATION("translation"),
    SEARCH("search"),
    REPORT("report");

    private final String id;

    ConversationId(String id) { this.id = id; }

    public String getId() { return id; }
}
