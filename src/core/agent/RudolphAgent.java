package core.agent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import core.agent.communication.ReindeerName;
import core.agent.communication.ContentKeyword;
import core.logger.Logger;
import core.world.Position;

import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;

public class RudolphAgent extends Agent {

	private final Queue<ReindeerName> queue = new LinkedList<>();
	private final Map<ReindeerName, Position> positions = new HashMap<>();
 	private final String expectedCode = "CODE-20160528";

	@Override
	protected void setup() {
		Logger.info("Rudolph starting...");

		// Inicializar mapa de renos a posiciones
		positions.put(ReindeerName.DASHER, new Position(20, 20));
		positions.put(ReindeerName.DANCER, new Position(30, 40));
		positions.put(ReindeerName.VIXEN, new Position(15, 60));
		positions.put(ReindeerName.PRANCER, new Position(80, 10));
		positions.put(ReindeerName.CUPID, new Position(12, 75));
		positions.put(ReindeerName.COMET, new Position(5, 5));
		positions.put(ReindeerName.BLITZEN, new Position(60, 60));
		positions.put(ReindeerName.DONNER, new Position(70, 25));

		// Llenar la cola
		queue.add(ReindeerName.DASHER);
		queue.add(ReindeerName.DANCER);
		queue.add(ReindeerName.VIXEN);
		queue.add(ReindeerName.PRANCER);
		queue.add(ReindeerName.CUPID);
		queue.add(ReindeerName.COMET);
		queue.add(ReindeerName.BLITZEN);
		queue.add(ReindeerName.DONNER);

		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				ACLMessage msg = blockingReceive();
				if (msg != null) {
					handleMessage(msg);
				} else {
					block();
				}
			}
		});
	}

	private void handleMessage(ACLMessage msg) {
		ACLMessage reply = msg.createReply();
		int perf = msg.getPerformative();
		String conv = msg.getConversationId();

		// Validar código exacto
		boolean validCode = conv != null && conv.equals(expectedCode);

		switch (perf) {
			case ACLMessage.PROPOSE -> {
				if (validCode) {
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					reply.setContent("Accepted");
					send(reply);
					Logger.info("Rudolph: accepted proposal (conv=" + conv + ")");
				} else {
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
					reply.setContent("Bad code");
					send(reply);
					Logger.warn("Rudolph: rejected proposal (conv=" + conv + ")");
				}
			}

			case ACLMessage.QUERY_REF -> {
				if (!validCode) {
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("Invalid code");
					send(reply);
					Logger.warn("Rudolph: refused request due invalid code (conv=" + conv + ")");
					break;
				}

				if (!queue.isEmpty()) {
					ReindeerName rn = queue.poll();
					Position p = positions.get(rn);
					// Always send Position as a serialized object
					reply.setPerformative(ACLMessage.INFORM);
					try {
						reply.setContentObject(p);
						send(reply);
						Logger.info("Rudolph: sent Position object for " + rn + " -> " + p);
					} catch (Exception e) {
						// Do NOT fallback to string; report error and refuse
						Logger.error("Rudolph: failed to serialize Position for " + rn + " | error=" + e);
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("Serialization error");
						send(reply);
					}
				} else {
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(ContentKeyword.ALL_FOUND.getText());
					send(reply);
					Logger.info("Rudolph: all found message sent");
				}
			}

			default -> {
				Logger.warn("Rudolph: performative not handled: " + perf);
			}
		}
	}

	@Override
	protected void takeDown() {
		Logger.info("Rudolph terminating.");
	}
}
