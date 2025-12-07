package core.environment;

import core.logger.Logger;
import core.world.Position;
import core.world.World;

/**
 *
 * @author duckduck
 */
public class Environment {
    
    private final World world;
    
    private Position agentCurrPos;
    private Position goalPos;
    private int spentEnergy;
    
    //OK
    public Environment(World world, Position initialAgentPos, Position goalPos) {
        this.world = world;
        this.agentCurrPos = initialAgentPos;
        this.goalPos = goalPos;
        this.spentEnergy = 0;
        Logger.info("Estado inicial del entorno: Agente en " + agentCurrPos 
                + ", Objetivo en " + goalPos + ", Energía gastada: " + spentEnergy);
        
        debug();
    }

    //OK
    public Surroundings perceive() {
        Surroundings surs = world.perceive(this.agentCurrPos);
        return surs;
    }

    //OK
    public Position getAgentPosition() {
        return this.agentCurrPos;
    }
    
    public Position getGoalPosition() {
        return this.goalPos;
    }

    //OK
    public void updateAgentPosition(Position newPos) {
        this.agentCurrPos = newPos;
    }

    public void moveTo(Position newPosition) {
        Logger.info("Agente se ha movido a " + newPosition);
        this.agentCurrPos = newPosition;
        this.spentEnergy += 1;
    }

    public void debug() {
        Logger.info("Agente posición actual: " + agentCurrPos + " | Objetivo: " + goalPos + " | Energía gastada: " + spentEnergy);
        world.printWorldWithAgentAndGoal(agentCurrPos, goalPos);
    }

    public int getSpentEnergy() {
        return spentEnergy;
    }
}
