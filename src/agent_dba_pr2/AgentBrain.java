package agent_dba_pr2;

import agent_dba_pr2.Action;
import agent_dba_pr2.environment.Surroundings;

public interface AgentBrain {
    Surroundings perceive();
    Action think(Surroundings surroundings);
    void execute(Action action);
    boolean hasFinished();
}
