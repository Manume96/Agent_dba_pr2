package agent_dba_pr2.test;

import java.io.IOException;

import agent_dba_pr2.Action;
import agent_dba_pr2.environment.Environment;
import agent_dba_pr2.environment.Surroundings;
import agent_dba_pr2.proxy.EnvironmentProxy;
import agent_dba_pr2.world.Position;
import agent_dba_pr2.world.World;

public class TestEnvironment {

    public static void main(String[] args) throws IOException {

        String path = "src/agent_dba_pr2/maps/mapWithVerticalWall.txt";
        World world = World.loadFromFile(path);

        // Definir posición inicial y objetivo
        Position start = new Position(3, 3);
        Position goal = new Position(5, 5);

        // Crear entorno y proxy
        Environment env = new Environment(world, start, goal);
        EnvironmentProxy envProxy = new EnvironmentProxy(env);

        System.out.println("=== TEST 1: Percepción ===");
        testPerceive(envProxy);

        System.out.println("\n=== TEST 2: Valid Move ===");
        testValidMove(envProxy);

        System.out.println("\n=== TEST 3: Move ===");
        testMove(envProxy);
    }

    private static void testPerceive(EnvironmentProxy envProxy) {
        envProxy.debug();
        Surroundings s = envProxy.perceive();
        System.out.println("Percepción obtenida: " + s);
    }

    private static void testValidMove(EnvironmentProxy envProxy) {
        System.out.println("Verificando movimientos válidos:");
        for (Action a : Action.values()) {
            boolean valid = envProxy.isValidMove(a);
            System.out.println("Acción " + a + " válida? " + valid);
        }
    }

    private static void testMove(EnvironmentProxy envProxy) {
        System.out.println("Moviendo agente...");
        envProxy.requestMove(Action.RIGHT);
        envProxy.requestMove(Action.UP);

        Position currentPos = envProxy.getAgentPosition();
        System.out.println("Posición final del agente: " + currentPos);
    }
}
