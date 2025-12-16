package core.proxy;

import core.agent.Action;
import core.environment.Environment;
import core.environment.Surroundings;
import core.world.Position;
import core.logger.Logger;
import core.environment.EnvironmentWithGUI;

public class EnvironmentProxy implements IEnvironmentProxy {

    private final Environment env;
    private Surroundings lastPerception;

    public EnvironmentProxy(Environment env) {
        this.env = env;
    }

    @Override
    public Surroundings perceive() {
        lastPerception = env.perceive();
        Logger.info("Nueva percepción obtenida: " + lastPerception);
        return lastPerception;
    }

    @Override
    public boolean isValidMove(Action action) {
        if (lastPerception == null) {
            Logger.error("No hay percepción previa, no se puede validar movimiento");
            return false;
        }

        Position targetPos = null;
        Position currentPos = env.getAgentPosition();

        switch (action) {
            case UP -> targetPos = lastPerception.up;
            case DOWN -> targetPos = lastPerception.down;
            case LEFT -> targetPos = lastPerception.left;
            case RIGHT -> targetPos = lastPerception.right;
        }

        boolean isValidMove = true;

        // Comprobación de distancia
        int distance = currentPos.manhattanDistance(targetPos);
        if (distance != 1) {
            Logger.warn("Movimiento ilegal: distancia " + distance);
            isValidMove = false;
        }

        Logger.info("Requesting action: " + action + " --> " + targetPos);

        // Validaciones de valor
        switch (targetPos.getValue()) {
            case 0 -> Logger.info("Movimiento válido: " + action);
            case -1 -> {
                Logger.warn("Movimiento inválido OBSTÁCULO: " + action);
                isValidMove = false;
            }
            case -2 -> {
                Logger.warn("Movimiento inválido FUERA DEL RANGO: " + action);
                isValidMove = false;
            }
            default -> {
                Logger.error("Movimiento inválido desconocido: " + action);
                isValidMove = false;
            }
        }

        return isValidMove;
    }

    @Override
    public void requestMove(Action action) {
        if (!isValidMove(action)) {
            Logger.warn("No se puede ejecutar la acción: " + action);
            return; // no hacer nada
        }

        Position targetPos = getDirecton(action);
        env.moveTo(targetPos);
        Logger.info("Agente se movió a " + targetPos);
        debug();
    }

    public Position getDirecton(Action action) {
        return switch (action) {
            case UP -> lastPerception.up;
            case DOWN -> lastPerception.down;
            case LEFT -> lastPerception.left;
            case RIGHT -> lastPerception.right;
        };
    }

    @Override
    public Position getAgentPosition() {
        return env.getAgentPosition();
    }

    public void debug() {
        Logger.info("Mostrando estado del mundo:");
        env.debug();
    }

    @Override
    public Position getGoalPosition() {
        return env.getGoalPosition();
    }

    @Override
    public void setGoalPosition(core.world.Position pos) {
        env.setGoalPosition(pos);
        Logger.info("Goal position updated via proxy to: " + pos);
    }

    @Override
    public int getSpentEnergy() {
        return env.getSpentEnergy();
    }
    
    // Displays message in the GUI 
    public void displayMessage(String sender, String message, String recipient) {
        if (env instanceof EnvironmentWithGUI) {
            EnvironmentWithGUI envGUI = (EnvironmentWithGUI) env;
            envGUI.getGUI().addMessage(sender, message, recipient);
        }
    }
}
