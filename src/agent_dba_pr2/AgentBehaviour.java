package agent_dba_pr2;

import agent_dba_pr2.environment.Surroundings;
import agent_dba_pr2.proxy.EnvironmentProxy;
import agent_dba_pr2.world.Position;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import agent_dba_pr2.logger.Logger;
import java.util.HashMap;

public class AgentBehaviour extends Behaviour implements AgentBrain {
    
    private final EnvironmentProxy proxy;
    private final HashMap<Position, Integer> visited;

    public AgentBehaviour(Agent agent, EnvironmentProxy environmentProxy) {
        super(agent);
        this.proxy = environmentProxy;
        this.visited = new HashMap<>();
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
    
    Position current = proxy.getAgentPosition();
    Position goal = proxy.getGoalPosition();
    if (current == null || goal == null) return null;

    Position[] neighbors = {surrs.up, surrs.down, surrs.left, surrs.right};
    Action[] actions = {Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT};

    int mejorDistManhattan = Integer.MAX_VALUE;
    int minVisits = Integer.MAX_VALUE;
    double mejorDistEuclid = Double.MAX_VALUE;  
    Action mejorAccion = null;


    for (int i = 0; i < neighbors.length; i++) {
        Position p = neighbors[i];
        Logger.debug("Evaluando Acción: " + actions[i] + " | Pos: " + p);

        if (p.getValue() == 0) { // -2 = fuera del rango; -1 = obstáculo
            
            
            int visits = visited.getOrDefault(p, 0);
            
            // Heurística: sumar visitas de vecinos de p
            Surroundings vecinosP = new Surroundings(p);
            Position[] vecinos = {vecinosP.up, vecinosP.down, vecinosP.left, vecinosP.right};
            for (Position v : vecinos) {
                if (v != null) {
                    visits += visited.getOrDefault(v, 0);
                }
            }

            int distManhattan = p.manhattanDistance(goal);

            int dx = goal.getX() - p.getX();
            int dy = goal.getY() - p.getY();
            double distEuclid = Math.sqrt(dx * dx + dy * dy);

            Logger.debug(actions[i] +
                " | Valor celda: " + p.getValue() +
                " | Visitas: " + visits +
                " | Distancia Manhattan: " + distManhattan +
                " | dx: " + dx +
                " | dy: " + dy +
                " | Distancia Euclidiana: " + String.format("%.2f", distEuclid));

            // 1. Criterio: menos visitado
            if (visits < minVisits) {
                minVisits = visits;
                mejorDistManhattan = distManhattan;
                mejorDistEuclid = distEuclid;
                mejorAccion = actions[i];
                Logger.debug("Nuevo mejor vecino por menos visitas: " + p +
                             " | Acción: " + actions[i] +
                             " | Min visitas: " + minVisits +
                             " | Mejor distancia Manhattan: " + mejorDistManhattan +
                             " | Mejor distancia Euclidiana: " + String.format("%.2f", mejorDistEuclid));

            } 
            // 2. Criterio: mejor distancia Manhattan
            else if (visits == minVisits && distManhattan < mejorDistManhattan) {
                mejorDistManhattan = distManhattan;
                mejorDistEuclid = distEuclid;
                mejorAccion = actions[i];
                Logger.debug("Nuevo mejor vecino por menor distancia Manhattan: " + p +
                             " | Acción: " + actions[i] +
                             " | Min visitas: " + minVisits +
                             " | Mejor distancia Manhattan: " + mejorDistManhattan +
                             " | Mejor distancia Euclidiana: " + String.format("%.2f", mejorDistEuclid));

            }
            // 3. Desempate: menor distancia Euclidiana (DA PEOR RESULTADOS POR LOS ZIGZAGS)
            else if (visits == minVisits && distManhattan == mejorDistManhattan && distEuclid < mejorDistEuclid) {
                mejorDistEuclid = distEuclid;
                mejorAccion = actions[i];
                Logger.debug("Nuevo mejor vecino por menor distancia Euclidiana: " + p +
                             " | Acción: " + actions[i] +
                             " | Min visitas: " + minVisits +
                             " | Mejor distancia Manhattan: " + mejorDistManhattan +
                             " | Mejor distancia Euclidiana: " + String.format("%.2f", mejorDistEuclid));
            }
            
            
            // 4. Algo de aleatoriedad 
            else if (visits == minVisits && distManhattan == mejorDistManhattan && distEuclid ==  mejorDistEuclid) {
                if (Math.random() < 0.5) {
                    mejorAccion = actions[i];
                    Logger.debug("Desempate aleatorio: " + p + " | Acción: " + actions[i]);
                }

            } 
            


        } else if (p != null) {
            Logger.debug(actions[i] + " bloqueado | Value: " + p.getValue());
        }
    }



    // Actualiza contador de la posición actual
    visited.put(current, visited.getOrDefault(current, 0) + 1);

    Logger.info("Mejor acción elegida: " + mejorAccion + " | Veces visitada: " + minVisits);
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

        /*try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Logger.error("Interrupción durante el sleep"+e);
        }*/
    }

    @Override
    public boolean hasFinished() {
        Position currentPosition = proxy.getAgentPosition();
        Position goalPos = proxy.getGoalPosition();
        Logger.info("Agente posición actual: " + currentPosition + " | Objetivo: " + goalPos + " | Energia gastada: "+proxy.getSpentEnergy());
        return currentPosition.equals(goalPos);
    }

}
