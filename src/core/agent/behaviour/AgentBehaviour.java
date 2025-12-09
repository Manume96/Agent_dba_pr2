package core.agent.behaviour;

import core.agent.Action;
import core.agent.AgentBrain;
import core.environment.Surroundings;
import core.proxy.EnvironmentProxy;
import core.world.Position;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import core.logger.Logger;
import jade.lang.acl.ACLMessage;
import java.util.HashMap;

public class AgentBehaviour extends Behaviour implements AgentBrain {
    
   private enum Phase {
        AUTHORIZATION,
        TRANSLATION,
        SEARCH_GOAL,
        REPORT,
        FINISHED
    }
    
    private Phase currentPhase;
    private final EnvironmentProxy proxy;
    private final HashMap<Position, Integer> visited;

    public AgentBehaviour(Agent agent, EnvironmentProxy environmentProxy) {
        super(agent);
        this.proxy = environmentProxy;
        this.visited = new HashMap<>();
        this.currentPhase = Phase.AUTHORIZATION;
        Logger.info("Comportamiento construido");
    }
    
    
    @Override
    public void action() {
        switch (currentPhase) {
            case AUTHORIZATION -> doAuthorizationPhase(); //El agente se presenta ante Santa
            case TRANSLATION -> doTranslationPhase();//El agente habla con Elfo Traductor
            case SEARCH_GOAL -> doSearch(); //El Agente habla con el Reno y busca objetivos
            case REPORT -> doReportPhase();//El agente Avisa a santa
            case FINISHED -> {
                Logger.info("Agente ha terminado su misión. Cerrando...");
                myAgent.doDelete();
           }
        }
    }

private void doAuthorizationPhase() {
    Logger.info("[AUTH] Fase de autorización iniciada.");

    // 1. Agente solicita traducción al traductor
    ACLMessage req = MessageProtocol.createMessage(
        ACLMessage.REQUEST,
        MessageProtocol.AGENT_TRANSLATOR,
        "Bro me presento para ayudar en plan",
        MessageProtocol.CONV_AUTHORIZATION
    );

    myAgent.send(req);
    Logger.info("[AUTH] Solicitud de traducción enviada.");

    // 2. Recibir traducción (bloquea hasta recibir algo)
    ACLMessage translated = myAgent.blockingReceive(MessageProtocol.createTemplate(MessageProtocol.CONV_AUTHORIZATION));
    String translatedText = translated.getContent();
    Logger.info("[AUTH] Traducción recibida: " + translatedText);

    // 3. Enviar PROPOSE a Santa con el mensaje traducido
    ACLMessage propose = MessageProtocol.createMessage(
        ACLMessage.PROPOSE,
        MessageProtocol.AGENT_SANTA,
        translatedText,
        MessageProtocol.CONV_AUTHORIZATION
    );
    myAgent.send(propose);
    Logger.info("[AUTH] PROPOSE enviado a Santa.");

    // 4. Esperar respuesta de Santa (ACEPTA o RECHAZA)
    ACLMessage santaReply = myAgent.blockingReceive(MessageProtocol.createTemplate(MessageProtocol.CONV_AUTHORIZATION));

    if (santaReply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
        Logger.info("[AUTH] Aceptado por Santa.");
        String code = MessageProtocol.extractCode(santaReply.getContent());
        Logger.info("[AUTH] Código recibido: " + code);

        currentPhase = Phase.TRANSLATION;

    } else {
        Logger.warn("[AUTH] Santa rechazó la propuesta.");
        currentPhase = Phase.FINISHED;
    }
}

    private void doTranslationPhase() {
        // Proceso de traducción de mensajes de Santa <-> Agent
        // Cuando la traducción esté lista y tengas el código:
        // currentPhase = Phase.SEARCH_GOAL;
        Logger.info("FASE: TRANSLATION");
    }

    private void doReportPhase() {
        // 1. Pedir posición a Santa
        // 2. Moverse hasta allí
        // 3. Enviar mensaje "ya estoy aquí"
        // 4. Esperar "HoHoHo!"
        // 5. Cambiar estado a FINISHED
        Logger.info("FASE: REPORT");
    }
    
    private void doSearch(){
        
        Surroundings surroundings = perceive();
                Action action = think(surroundings);
                execute(action);

                if (hasFinished()) {
                    Logger.info("Objetivo alcanzado, cambiando a fase REPORT");
                    currentPhase = Phase.REPORT;
                }
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
