package agent_dba_pr2;

import agent_dba_pr2.environment.Surroundings;
import agent_dba_pr2.proxy.EnvironmentProxy;
import agent_dba_pr2.world.Position;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import agent_dba_pr2.logger.Logger;

public class AgentBehaviour extends Behaviour implements AgentBrain {
    
    private final EnvironmentProxy proxy;

    public AgentBehaviour(Agent agent, EnvironmentProxy environmentProxy) {
        super(agent);
        this.proxy = environmentProxy;
        Logger.info("Comportamiento construido");
    }

    @Override
    public void action() {
        Surroundings surroundings = perceive();
        Action action = think(surroundings);
        execute(action);
    }

    @Override
    public boolean done() {
        return hasFinished();
    }

    @Override
    public Surroundings perceive() {
        Logger.info("Agente percibe");
        return proxy.perceive();
    }

    @Override
    public Action think(Surroundings surrs) {
        Logger.info("Agente piensa");
        Position currentPos = proxy.getAgentPosition();
        Position goalPos = proxy.getGoalPosition();
        if (goalPos == null || currentPos == null) return null;

        Position[] neighbors = {surrs.up, surrs.down, surrs.left, surrs.right};
        Action[] actions = {Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT};

        int mejorDist = Integer.MAX_VALUE;
        Action mejorAccion = null;

        for (int i = 0; i < neighbors.length; i++) {
            Position p = neighbors[i];
            if (p != null && p.getValue() != -1) {
                int dist = p.manhattanDistance(goalPos);
                if (dist < mejorDist) {
                    mejorDist = dist;
                    mejorAccion = actions[i];
                }
            }
        }

        Logger.info("Mejor acción elegida: " + mejorAccion);
        return mejorAccion;
    }

    @Override
    public void execute(Action action) {
        if (action != null) {
            Logger.info("Agente ejecuta una acción: " + action);
            proxy.requestMove(action);
        } else {
            Logger.warn("El agente no sabe qué hacer. Esperando...");
        }

        try {
            Thread.sleep(3000); // pausa de 3 segundos para debug
        } catch (InterruptedException e) {
            Logger.error("Interrupción durante el sleep"+e);
        }
    }

    @Override
    public boolean hasFinished() {
        Position currentPosition = proxy.getAgentPosition();
        Position goalPos = proxy.getGoalPosition();
        Logger.info("Agente posición actual: " + currentPosition + " | Objetivo: " + goalPos);
        return currentPosition.equals(goalPos);
    }

}
