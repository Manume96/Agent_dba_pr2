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

        Position start = new Position(3, 3);
        Environment env = new Environment(world, start);
        EnvironmentProxy envProxy = new EnvironmentProxy(env);

        System.out.println("=== TEST 1: Percepción ===");
        testPerceive(envProxy);

        System.out.println("=== TEST 2: Valid Move ===");
        testValidMove(envProxy);

        System.out.println("=== TEST 3: Move ===");
        testMove(envProxy);
    }

    private static void testPerceive(EnvironmentProxy envProx) {
        envProx.debug();

        Surroundings s = envProx.perceive();
        System.out.println(s.toString());

    }

    private static void testValidMove(EnvironmentProxy envProx) {
        envProx.isValidMove(Action.UP);
        envProx.isValidMove(Action.DOWN);
        envProx.isValidMove(Action.LEFT);
        envProx.isValidMove(Action.RIGHT);

    }

    private static void testMove(EnvironmentProxy envProx) {
        envProx.requestMove(Action.RIGHT);
        envProx.requestMove(Action.UP);

  
    }

}
