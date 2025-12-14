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
import jade.lang.acl.MessageTemplate;
import core.agent.communication.AgentMessageProtocol;
import core.agent.communication.AgentName;
import core.agent.communication.ConversationId;
import core.agent.communication.MessageStyle;
import core.agent.communication.ContentKeyword;
import core.agent.communication.MessageProtocol;
import jade.core.AID;
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
    private String secretCode = null;

    // Authorization sub-steps
    private int authStep;
    // Search sub-steps for communication with Rudolph
    private int searchStep;
    private Position targetPosition = null;
    private String currentReindeer = null;

    private String translatedText = null;
    private ACLMessage santaReply = null;

    private MessageProtocol messageProtocol;

    private Surroundings lastPerception;
    private Action nextAtion;
    private boolean isGoalReached = false;

    public AgentBehaviour(Agent agent, EnvironmentProxy environmentProxy) {
        super(agent);
        this.proxy = environmentProxy;
        this.visited = new HashMap<>();
        this.currentPhase = Phase.AUTHORIZATION;
        this.messageProtocol = new AgentMessageProtocol();

        this.authStep = 0;
        this.searchStep = 0;
        Logger.info("Comportamiento construido");
    }

    private void doSearch() {
        if (secretCode == null) {
            Logger.error("No secretCode available. Returning to AUTHORIZATION.");
            currentPhase = Phase.AUTHORIZATION;
            return;
        }

        ACLMessage msg = null;
        String content = "";
        switch (searchStep) {
            case 0: // enviar PROPOSE con el codigo secreto a Rudolph
                content = messageProtocol.createMessageBody(ContentKeyword.WANT_TO_HELP.getText());
                msg = messageProtocol.createMessage(ACLMessage.PROPOSE, AgentName.RUDOLPH, content,
                        ConversationId.MISSION);
                msg.setConversationId(secretCode); // Esto es un parche para comprobar el código secreto
                myAgent.send(msg);
                Logger.info("Sent PROPOSE to Rudolph with convId=" + secretCode);
                searchStep=1;// Pasamos al siguiente paso
                break;

            case 1: // recibir respuesta ACCEPT/REJECT de Rudolph
                msg = myAgent.blockingReceive();
                if (msg != null && secretCode.equals(msg.getConversationId())) {
                    if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        Logger.info("Rudolph accepted proposal");
                    } else {
                        Logger.warn("Rudolph rejected proposal. Moving to REPORT phase.");
                        currentPhase = Phase.REPORT;
                        searchStep = 0;
                        return; // No tiene sentido continuar, abortamos aqui
                    }
                } else {
                    Logger.warn("Unexpected message while waiting Rudolph accept/reject: " + msg);
                }
                searchStep=2;// Pasamos al siguiente paso
                break;

            case 2: // Realizar QUERY_REF sobre la posición del siguiente reno
                msg = messageProtocol.createMessage(ACLMessage.QUERY_REF, AgentName.RUDOLPH,
                        ContentKeyword.WHERE_IS_REINDEER.getText(),
                        ConversationId.MISSION);
                msg.setConversationId(secretCode);// Esto es un parche para comprobar el código secreto
                myAgent.send(msg);
                Logger.info("Sent QUERY_REF to Rudolph (convId=" + secretCode + ")");
                searchStep=3;// Pasamos al siguiente paso
                break;

            case 3: // Recibir respuesta de Rudolph. Si posición -> Paso 4, si ALL_FOUND -> Paso 5
                msg = myAgent.blockingReceive();
                if (msg != null && secretCode.equals(msg.getConversationId())) {
                    try {
                        Object obj = msg.getContentObject();
                        if (obj instanceof Position) {
                            targetPosition = (Position) obj;
                            this.proxy.setGoalPosition(targetPosition);
                            Logger.info("Received Position object from Rudolph: " + targetPosition);
                            Logger.info("Starting movement loop towards " + targetPosition + " (placeholder)");
                            searchStep=4;// Pasamos al siguiente paso
                        } else {
                            Logger.warn("Received non-Position object from Rudolph: "
                                    + (obj == null ? "null" : obj.getClass()));
                            currentPhase = Phase.REPORT;
                            searchStep = 0;
                            return; // No tiene sentido continuar, abortamos aqui
                        }
                    } catch (Exception e) {
                        // If cannot read object, check for ALL_FOUND string in content
                        Logger.debug("getContentObject() failed: " + e);
                        String body = msg.getContent();
                        if (body != null && ContentKeyword.ALL_FOUND.getText().equals(body)) {
                            Logger.info("Rudolph reports ALL_FOUND. Switching to REPORT phase.");
                            currentPhase = Phase.REPORT;
                            searchStep = 0;
                            return;
                        }
                        Logger.warn("Unexpected message while waiting Rudolph position: " + msg);
                        currentPhase = Phase.REPORT;
                        searchStep = 0;
                        return;
                    }
                    // searchStep ya se asignó explícitamente arriba
                } else {
                    Logger.warn("Unexpected message while waiting Rudolph position: " + msg);
                    currentPhase = Phase.REPORT;
                    searchStep = 0;
                    return;
                }
                break;
            case 4: // Perceive
                lastPerception = proxy.perceive();
                searchStep = 5;
                break;

            case 5: // THink
                nextAtion = this.think(lastPerception);
                searchStep = 6;
                break;
            case 6: //Execute
                this.execute(nextAtion);
                searchStep = 7;
                break;
            
            case 7: // Check if goal reached
                if (hasFinished()) {
                    Logger.info("Reached target position of current reindeer: " + targetPosition);
                    searchStep = 2; // Volver a pedir la posición del siguiente reno
                } else {
                    searchStep = 4; // Continuar el ciclo perceive->think->execute
                }
                break;
            default:
                searchStep = 0;
                break;
        }
    }

    @Override
    public void action() {
        switch (currentPhase) {
            case AUTHORIZATION -> doAuthorizationPhase(); // El agente se presenta ante Santa
            // case TRANSLATION -> doTranslationPhase();// El agente habla con Elfo
            // Traductor
            case SEARCH_GOAL -> doSearch(); // El Agente habla con el Reno y busca objetivos
            // case REPORT -> doReportPhase();// El agente Avisa a santa
            case FINISHED -> {
                Logger.info("Agente ha terminado su misión. Cerrando...");
                myAgent.doDelete();
            }
        }
    }

    private void doAuthorizationPhase() {

        ACLMessage msg = null;
        String content = "";
        switch (authStep) {
            case 0: // Pedir traducción al Traductor

                content = messageProtocol.createMessageBody(ContentKeyword.WANT_TO_HELP.getText());
                msg = messageProtocol.createMessage(ACLMessage.REQUEST, AgentName.TRANSLATOR, content,
                        ConversationId.TRANSLATION);
                myAgent.send(msg);
                Logger.info("Solicitando traducción para: " + content);
                break;

            case 1: // Recibir la traducción del Traductor
                msg = myAgent.blockingReceive();
                if (msg != null && ConversationId.TRANSLATION.getId().equals(msg.getConversationId())
                        && msg.getPerformative() == ACLMessage.INFORM) {
                    translatedText = messageProtocol.extractMessageBody(msg);
                    Logger.info("Traducción recibida: " + translatedText);
                } else {
                    Logger.warn("Unexpected message while waiting translation: " + msg);
                }
                break;

            case 2: // Enviar PROPOSE a Santa con el texto traducido
                if (translatedText == null) {
                    Logger.error("No translated text available to propose to Santa");
                    currentPhase = Phase.FINISHED;
                    break;
                }
                content = messageProtocol.createMessageBody(translatedText);
                msg = messageProtocol.createMessage(ACLMessage.PROPOSE, AgentName.SANTA, content,
                        ConversationId.AUTHORIZATION);
                myAgent.send(msg);
                Logger.info("Propuesta enviada a Santa: " + translatedText);
                break;

            case 3: // Esperar respuesta de Santa (ACCEPT/REJECT)
                msg = myAgent.blockingReceive();
                if (msg != null && ConversationId.AUTHORIZATION.getId().equals(msg.getConversationId())) {
                    if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        santaReply = msg;
                        Logger.info("Santa accepted. Will request translation in next step.");
                    } else if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                        Logger.warn("Santa rejected the proposal. Finishing.");
                        currentPhase = Phase.FINISHED;
                        authStep = 0;
                    } else {
                        Logger.warn("Unexpected performative from Santa: " + msg.getPerformative());
                    }
                } else {
                    Logger.warn("Unexpected message while waiting Santa reply: " + msg);
                }
                break;

            case 4: // Enviar petición de traducción al Translator con la respuesta de Santa
                if (santaReply == null) {
                    Logger.error("No Santa reply to forward to Translator");
                    currentPhase = Phase.FINISHED;
                    break;
                }
                ACLMessage treq = messageProtocol.createMessage(ACLMessage.REQUEST, AgentName.TRANSLATOR,
                        santaReply.getContent(), ConversationId.TRANSLATION);
                myAgent.send(treq);
                Logger.info("Forwarded Santa reply to Translator for extraction");
                break;

            case 5: // Esperar la respuesta con el código extraido
                msg = myAgent.blockingReceive();
                if (msg != null && ConversationId.TRANSLATION.getId().equals(msg.getConversationId())
                        && msg.getPerformative() == ACLMessage.INFORM) {
                    String translated = msg.getContent();
                    Logger.info("Received translation from Translator: " + translated);
                    if (core.agent.communication.Translator.isStyle(translated, MessageStyle.GENZ)) {
                        String prefix = MessageStyle.GENZ.getPrefix();
                        String suffix = MessageStyle.GENZ.getSuffix();
                        String coreText = translated;
                        if (coreText.startsWith(prefix))
                            coreText = coreText.substring(prefix.length());
                        if (coreText.endsWith(suffix))
                            coreText = coreText.substring(0, coreText.length() - suffix.length());
                        secretCode = coreText.trim();
                        Logger.info("Extracted secret code: " + secretCode);
                        currentPhase = Phase.SEARCH_GOAL;
                        authStep = 0;
                    } else {
                        Logger.warn("Translator returned unexpected style when extracting code: " + translated);
                    }
                } else {
                    Logger.warn("Unexpected message while waiting translator for code extraction: " + msg);
                }
                break;

            default:
                authStep = 0;
                break;
        }
        authStep += 1;
    }

    @Override
    public boolean done() {
        return false;
        //return hasFinished();
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
        if (current == null || goal == null)
            return null;

        Position[] neighbors = { surrs.up, surrs.down, surrs.left, surrs.right };
        Action[] actions = { Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT };

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
                Position[] vecinos = { vecinosP.up, vecinosP.down, vecinosP.left, vecinosP.right };
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
                else if (visits == minVisits && distManhattan == mejorDistManhattan && distEuclid == mejorDistEuclid) {
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

        /*
         * try {
         * Thread.sleep(1500);
         * } catch (InterruptedException e) {
         * Logger.error("Interrupción durante el sleep"+e);
         * }
         */
    }

    @Override
    public boolean hasFinished() {
        Position currentPosition = proxy.getAgentPosition();
        Position goalPos = proxy.getGoalPosition();
        // Logger.info("Agente posición actual: " + currentPosition + " | Objetivo: " +
        // goalPos + " | Energia gastada: "
        // + proxy.getSpentEnergy());
        return currentPosition.equals(goalPos);
    }

}
