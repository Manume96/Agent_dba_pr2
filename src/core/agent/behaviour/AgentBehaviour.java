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
    private int reportStep;

    private Position targetPosition = null;
    private String currentReindeer = null;

    private String translatedText = null;

    private ACLMessage lastSantaMessage = null;
    private ACLMessage lastRudolphMessage = null;

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
        this.reportStep = 0;

        Logger.info("Comportamiento construido");
    }

    @Override
    public void action() {
        Logger.info("Current phase: " + currentPhase);

        switch (currentPhase) {
            case AUTHORIZATION -> doAuthorizationPhase(); // El agente se presenta ante Santa
            // case TRANSLATION -> doTranslationPhase();// El agente habla con Elfo
            // Traductor
            case SEARCH_GOAL -> doSearch(); // El Agente habla con el Reno y busca objetivos
            case REPORT -> doReportPhase();// El agente Avisa a santa
            case FINISHED -> {
                Logger.info("=== MISSION ACCOMPLISHED ===");
                Logger.info("Agente ha terminado su misión. Cerrando...");
                myAgent.doDelete();
            }
        }
    }

    private void recordLastMessage(ACLMessage msg) {
        if (msg == null)
            return;
        String sender = msg.getSender() != null ? msg.getSender().getLocalName() : null;
        if (AgentName.SANTA.local().equals(sender)) {
            lastSantaMessage = msg;
        } else if (AgentName.RUDOLPH.local().equals(sender)) {
            lastRudolphMessage = msg;
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
                displayMessageWithDelay("Agent", content, "Translator");
                Logger.info("Solicitando traducción para: " + content);
                break;

            case 1: // Recibir la traducción del Traductor
                msg = myAgent.blockingReceive();
                recordLastMessage(msg);
                if (msg != null && ConversationId.TRANSLATION.getId().equals(msg.getConversationId())
                        && msg.getPerformative() == ACLMessage.INFORM) {
                    translatedText = messageProtocol.extractMessageBody(msg);
                    displayMessageWithDelay("Translator", translatedText, "Agent");
                    Logger.info("Traducción recibida: " + translatedText);
                } else {
                    currentPhase = Phase.FINISHED;
                    Logger.warn("Unexpected message while waiting translation: " + msg);
                }
                break;
            case 2: // Enviar PROPOSE a Santa con el texto traducido
                if (translatedText == null) {
                    Logger.error("No translated text available to propose to Santa");
                    currentPhase = Phase.FINISHED;
                    break;
                }
                msg = messageProtocol.createMessage(ACLMessage.PROPOSE, AgentName.SANTA, translatedText,
                        ConversationId.AUTHORIZATION);
                myAgent.send(msg);
                displayMessageWithDelay("Agent", translatedText, "Santa");
                Logger.info("Propuesta enviada a Santa: " + translatedText);
                break;

            case 3: // Esperar respuesta de Santa (ACCEPT/REJECT)
                msg = myAgent.blockingReceive();
                recordLastMessage(msg);
                if (msg != null && ConversationId.AUTHORIZATION.getId().equals(msg.getConversationId())) {
                    if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        displayMessageWithDelay("Santa", lastSantaMessage.getContent(), "Agent");
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
                ACLMessage treq = messageProtocol.createMessage(ACLMessage.REQUEST, AgentName.TRANSLATOR,
                        lastSantaMessage.getContent(), ConversationId.TRANSLATION);
                myAgent.send(treq);
                displayMessageWithDelay("Agent", lastSantaMessage.getContent(), "Translator");
                Logger.info("Forwarded Santa reply to Translator for extraction");
                break;

            case 5: // Esperar la respuesta con el código extraido
                msg = myAgent.blockingReceive();
                recordLastMessage(msg);
                if (msg != null && ConversationId.TRANSLATION.getId().equals(msg.getConversationId())
                        && msg.getPerformative() == ACLMessage.INFORM) {
                    String translated = msg.getContent();
                    displayMessageWithDelay("Translator", translated, "Agent");
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
                        authStep = -1;// Reseteamos para la próxima fase
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
                searchStep = 1;// Pasamos al siguiente paso
                displayMessageWithDelay("Agent", content, "Rudolph");
                Logger.info("Sent PROPOSE to Rudolph with convId=" + secretCode);
                break;

            case 1: // recibir respuesta ACCEPT/REJECT de Rudolph
                msg = myAgent.blockingReceive();
                if (msg != null && secretCode.equals(msg.getConversationId())) {
                    if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        displayMessageWithDelay("Rudolph", "Accepted", "Agent");
                        Logger.info("Rudolph accepted proposal");
                    } else {
                        displayMessageWithDelay("Rudolph", "Rejected", "Agent");
                        Logger.warn("Rudolph rejected proposal. Moving to REPORT phase.");
                        currentPhase = Phase.REPORT;
                        searchStep = 0;
                        return; // No tiene sentido continuar, abortamos aqui
                    }
                    recordLastMessage(msg);
                    if (msg != null && secretCode.equals(msg.getConversationId())
                            && msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        searchStep = 2;
                        Logger.info("Rudolph accepted proposal");
                    } else {
                        currentPhase = Phase.FINISHED;
                        searchStep = 0;
                        Logger.warn("Rudolph rejected proposal or unexpected message: " + msg);
                    }
                }
                break;

            case 2: // Enviar QUERY_REF como respuesta a la última comunicación de Rudolph
                msg = lastRudolphMessage.createReply();
                msg.setPerformative(ACLMessage.QUERY_REF);
                msg.setContent(ContentKeyword.WHERE_IS_REINDEER.getText());
                myAgent.send(msg);
                searchStep = 3;// Pasamos al siguiente paso
                displayMessageWithDelay("Agent", msg.getContent(), "Rudolph");
                Logger.info("Sent QUERY_REF reply to Rudolph (convId=" + secretCode + ")");
                break;

            case 3: // Recibir respuesta de Rudolph
                msg = myAgent.blockingReceive();
                recordLastMessage(msg);

                if (msg == null || !secretCode.equals(msg.getConversationId())) {
                    Logger.warn("Unexpected message while waiting Rudolph position: " + msg);
                    currentPhase = Phase.REPORT;
                    searchStep = 0;
                    break;
                }

                try {
                    Object rudolphResponse = msg.getContentObject(); // puede lanzar UnreadableException
                    if (rudolphResponse instanceof Position position) {
                        targetPosition = position;
                        proxy.setGoalPosition(targetPosition);
                        Logger.info("Received Position from Rudolph: " + targetPosition);
                        searchStep = 4; // seguir con movimiento
                    } else {
                        String body = msg.getContent(); // si ALL_FOUND
                        if (ContentKeyword.ALL_FOUND.getText().equals(body)) {
                            Logger.info("Rudolph reports ALL_FOUND. Switching to REPORT phase.");
                        } else {
                            Logger.warn("Unexpected content from Rudolph: " + body);
                        }
                        currentPhase = Phase.REPORT;
                        searchStep = 0;
                    }
                } catch (Exception e) {
                    Logger.warn("Failed to read Rudolph's content: " + e);
                    currentPhase = Phase.REPORT;
                    searchStep = 0;
                }
                break;

            case 4: // Perceive
                lastPerception = proxy.perceive();
                searchStep = 5;
                break;

            case 5: // Think
                nextAtion = this.think(lastPerception);
                searchStep = 6;
                break;
            case 6: // Execute
                this.execute(nextAtion);
                searchStep = 7;
                break;

            case 7: // Check if goal reached
                if (hasFinished()) {
                    Logger.info("Reached target position of current reindeer: " + targetPosition);
                    searchStep = 8; // Volver a pedir la posición del siguiente reno
                } else {
                    searchStep = 4; // Continuar el ciclo perceive->think->execute
                }
                break;
                
            case 8: // Pedir traduccion
                content = messageProtocol.createMessageBody(ContentKeyword.FOUND_REINDEER.getText());
                msg = messageProtocol.createMessage(ACLMessage.REQUEST, AgentName.TRANSLATOR, content,
                        ConversationId.TRANSLATION);
                myAgent.send(msg);
                searchStep = 9;
                Logger.info("Solicitando traducción para: " + content);
                break;
                
            case 9: // Esperar traduccion
                msg = myAgent.blockingReceive();
                if (msg != null && ConversationId.TRANSLATION.getId().equals(msg.getConversationId())
                        && msg.getPerformative() == ACLMessage.INFORM) {
                    translatedText = messageProtocol.extractMessageBody(msg);
                    Logger.info("Traducción recibida: " + translatedText);
                    searchStep = 10;
                } else {
                    Logger.warn("Unexpected message while waiting translation: " + msg);
                    currentPhase = Phase.REPORT;
                    searchStep = 0;
                }
                break;
                
            case 10: // Enviar mensaje traducido a Santa
                if (translatedText == null) {
                    Logger.error("No translated text available to inform to Santa");
                    currentPhase = Phase.REPORT;
                    searchStep = 0;
                    break;
                }
                msg = messageProtocol.createMessage(ACLMessage.INFORM, AgentName.SANTA, translatedText,
                        ConversationId.REPORT);
                myAgent.send(msg);
                Logger.info("Mensaje enviado a Santa: " + translatedText);
                searchStep = 2; // Buscar al siguiente reno
                break;
                
            default:
                searchStep = 0;
                break;
        }
    }

    private void doReportPhase() {
        ACLMessage msg = null;
        String content = "";

        switch (reportStep) {
            case 0: // Pedir traducción al Traductor
                content = messageProtocol.createMessageBody(ContentKeyword.WHERE_ARE_YOU.getText());
                msg = messageProtocol.createMessage(ACLMessage.REQUEST, AgentName.TRANSLATOR, content,
                        ConversationId.TRANSLATION);
                myAgent.send(msg);
                displayMessageWithDelay("Agent", content, "Translator");
                Logger.info("Requesting translation for: " + content);
                reportStep = 1;
                break;

            case 1: // Respuesta del Traductor
                msg = myAgent.blockingReceive();
                if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
                    translatedText = msg.getContent();
                    displayMessageWithDelay("Translator", translatedText, "Agent");
                    Logger.info("Translation received: " + translatedText);
                    reportStep = 2;
                }
                break;

            case 2: // Contesto al útltimo mensaje de santa
                if (lastSantaMessage != null) {
                    msg = lastSantaMessage.createReply();
                    msg.setPerformative(ACLMessage.QUERY_REF);
                    msg.setContent(translatedText);
                    msg.setConversationId(ConversationId.REPORT.getId());
                    myAgent.send(msg);
                    Logger.info("Replied to Santa with QUERY_REF (using lastSantaMessage).");
                } else {
                    msg = messageProtocol.createMessage(ACLMessage.QUERY_REF, AgentName.SANTA, translatedText,
                            ConversationId.REPORT);
                    myAgent.send(msg);
                    displayMessageWithDelay("Agent", translatedText, "Santa");
                    Logger.warn("lastSantaMessage was null; sent fresh QUERY_REF to Santa.");
                }
                reportStep = 3;
                break;

            case 3: // Respuesta de santa
                msg = myAgent.blockingReceive();
                recordLastMessage(msg);
                if (msg != null && ConversationId.REPORT.getId().equals(msg.getConversationId())
                        && msg.getPerformative() == ACLMessage.INFORM) {
                    proxy.displayMessage("Santa", lastSantaMessage.getContent(), "Agent");
                    displayMessageWithDelay("Santa", lastSantaMessage.getContent(), "Agent");
                    Logger.info("Santa replied: " + lastSantaMessage.getContent());
                    reportStep = 4;
                }
                break;

            case 4: // Pidiendo traducción al traductor
                msg = messageProtocol.createMessage(ACLMessage.REQUEST, AgentName.TRANSLATOR,
                        lastSantaMessage.getContent(),
                        ConversationId.TRANSLATION);
                myAgent.send(msg);
                proxy.displayMessage("Agent", lastSantaMessage.getContent(), "Translator");
                displayMessageWithDelay("Agent", lastSantaMessage.getContent(), "Translator");
                Logger.info("Requesting translation of Santa's reply.");
                reportStep = 5;
                break;
            case 5: // Esperando la respuesta del traductor
                msg = myAgent.blockingReceive();
                if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
                    String val = msg.getContent(); // "Bro Position:(10,10) En Plan"
                    displayMessageWithDelay("Translator", val, "Agent");
                    Logger.info("Phase 3: Translated position message: " + val);

                    try {
                        int start = val.indexOf("(");
                        int end = val.indexOf(")");
                        if (start != -1 && end != -1) {
                            String coords = val.substring(start + 1, end);
                            String[] parts = coords.split(",");
                            int x = Integer.parseInt(parts[0].trim());
                            int y = Integer.parseInt(parts[1].trim());

                            this.targetPosition = new Position(x, y);
                            this.proxy.setGoalPosition(targetPosition);
                            Logger.info("Phase 3: Target set to Santa's position: " + targetPosition);

                            reportStep = 6; // Move to movement loop
                        } else {
                            Logger.error("Phase 3: Could not parse coordinates from: " + val);
                        }
                    } catch (Exception e) {
                        Logger.error("Phase 3: Error parsing position: " + e.getMessage());
                    }
                }
                break;

            case 6:
                lastPerception = proxy.perceive();
                reportStep = 7;
                break;

            case 7:
                nextAtion = this.think(lastPerception);
                reportStep = 8;
                break;

            case 8: // Execute & Check
                this.execute(nextAtion);
                if (hasFinished()) {
                    Logger.info("Arrived at Santa's location!");
                    reportStep = 9;
                } else {
                    reportStep = 6; // Continue loop
                }
                break;

            case 9: // Obtener la traducción
                content = messageProtocol.createMessageBody(ContentKeyword.I_ARRIVED.getText());
                msg = messageProtocol.createMessage(ACLMessage.REQUEST, AgentName.TRANSLATOR, content,
                        ConversationId.TRANSLATION);
                myAgent.send(msg);
                displayMessageWithDelay("Agent", content, "Translator");
                Logger.info("Requesting translation for arrival message.");
                reportStep = 10;
                break;

            case 10: // Esperando la traducción
                msg = myAgent.blockingReceive();
                if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
                    translatedText = msg.getContent();
                    displayMessageWithDelay("Translator", translatedText, "Agent");
                    Logger.info("Translation received: " + translatedText);
                    reportStep = 11;
                }
                break;

            case 11:
                // Reply to Santa's last message when informing of arrival
                if (lastSantaMessage != null) {
                    ACLMessage inform = lastSantaMessage.createReply();
                    inform.setPerformative(ACLMessage.INFORM);
                    inform.setContent(translatedText);
                    inform.setConversationId(ConversationId.REPORT.getId());
                    myAgent.send(inform);
                    Logger.info("Replied to Santa informing arrival.");
                } else {
                    msg = messageProtocol.createMessage(ACLMessage.INFORM, AgentName.SANTA, translatedText,
                            ConversationId.REPORT);
                    myAgent.send(msg);
                    displayMessageWithDelay("Agent", translatedText, "Santa");
                    Logger.warn("lastSantaMessage was null; sent fresh INFORM to Santa.");
                }
                reportStep = 12;
                break;

            case 12:
                msg = myAgent.blockingReceive();
                recordLastMessage(msg);
                if (msg != null) {
                    if (msg.getContent().contains(ContentKeyword.HO_HO_HO.getText())) {
                        displayMessageWithDelay("Santa", msg.getContent(), "Agent");
                        Logger.info("Phase 3: Santa says: " + msg.getContent());
                        currentPhase = Phase.FINISHED;
                    }
                }
                reportStep = 0;
                break;

        }
    }

    @Override
    public boolean done() {
        Logger.info("Checking if agent has finished its mission..." + currentPhase);
        return currentPhase == Phase.FINISHED;
        // return hasFinished();
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

    // Displays message in GUI with a delay for visibility
    private void displayMessageWithDelay(String sender, String message, String recipient) {
        proxy.displayMessage(sender, message, recipient);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Logger.error("Interruption during message display: " + e);
        }
    }
}
