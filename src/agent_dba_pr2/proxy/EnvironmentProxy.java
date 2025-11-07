package agent_dba_pr2.proxy;

import agent_dba_pr2.Action;
import agent_dba_pr2.environment.Environment;
import agent_dba_pr2.environment.Surroundings;
import agent_dba_pr2.world.Position;

public class EnvironmentProxy implements IEnvironmentProxy {

    private final Environment env;
    private Surroundings lastPerception;

    public EnvironmentProxy(Environment env) {
        this.env = env;
    }

    @Override
    public Surroundings perceive() {
        lastPerception = env.perceive();
        return lastPerception;
    }

    @Override
    public boolean isValidMove(Action action) {
        if (lastPerception == null) {
            System.out.println("ERROR: no hay percepción previa");
            return false;
        }

        Position targetPos = null;
        Position currentPos = env.getAgentPosition();

        switch (action) {
            case UP:
                targetPos = lastPerception.up;
                break;
            case DOWN:
                targetPos = lastPerception.down;
                break;
            case LEFT:
                targetPos = lastPerception.left;
                break;
            case RIGHT:
                targetPos = lastPerception.right;
                break;
        }

        boolean isValidMove = true;
        // Check distance
        int distance = currentPos.manhattanDistance(targetPos);
        if (distance != 1) {
            System.out.println("Movimiento Ilegal: distancia " + distance);
            isValidMove = false;
        }

        System.out.println("Requesting action: " + action + " --> " + targetPos.toString());
        // Terminar el resto de validaciones
        switch (targetPos.getValue()) {
            case 0: // Libre
                System.out.println("Movimiento válido: " + action);
                break;
            case -1: // Obstaculo
                System.out.println("Movimiento inválido OBSTACULO: " + action);
                isValidMove = false;
                break;
            case -2: // Fuera del rango
                System.out.println("Movimiento inválido FUERA DEL RANGO: " + action);
                isValidMove = false;

                break;
            default: // Caso imposible?
                System.out.println("Movimiento inválido ???? : " + action);
                isValidMove = false;

                break;
        }
        return isValidMove;
    }

    @Override
    public void requestMove(Action action) {
        if (!isValidMove(action)) {
            System.out.println("No se puede ejecutar la acción: " + action);
            return; // no hacer nada
        }

        Position targetPos = getDirecton(action);
        env.moveTo(targetPos);
        debug();
    }

    public Position getDirecton(Action action){
        Position targetPos = null;
            switch (action) {
            case UP:
                targetPos = lastPerception.up;
                break;
            case DOWN:
                targetPos = lastPerception.down;
                break;
            case LEFT:
                targetPos = lastPerception.left;
                break;
            case RIGHT:
                targetPos = lastPerception.right;
                break;
        }
        return targetPos;
    }

    @Override
    public Position getAgentPosition() {
        return env.getAgentPosition();
    }

    public void debug() {
        this.env.debug();
    }
}
