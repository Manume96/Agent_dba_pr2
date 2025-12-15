package core.agent;

import core.environment.Surroundings;

public interface AgentBrain {
    Surroundings perceive();
    Action think(Surroundings surroundings);
    void execute(Action action);
    boolean hasFinished();
}
