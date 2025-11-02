package agent_dba_pr2.proxy;

import agent_dba_pr2.Action;
import agent_dba_pr2.environment.Surroundings;
import agent_dba_pr2.world.Position;



public interface IEnvironmentProxy {
    Surroundings perceive();
    boolean isValidMove(Action action);
    void requestMove(Action action);
    Position getAgentPosition();
}
