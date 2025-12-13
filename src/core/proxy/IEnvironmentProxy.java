package core.proxy;

import core.agent.Action;
import core.environment.Surroundings;
import core.world.Position;



public interface IEnvironmentProxy {
    Surroundings perceive();
    boolean isValidMove(Action action);
    void requestMove(Action action);
    Position getAgentPosition();
    Position getGoalPosition();
    int getSpentEnergy();
}
