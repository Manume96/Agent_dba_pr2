package core.agent;


import core.proxy.EnvironmentProxy;
import core.world.Position;
import core.world.World;

import jade.core.Agent;
import java.io.IOException;
import core.environment.EnvironmentWithGUI;
import core.agent.behaviour.AgentBehaviour;
import core.logger.Logger;





public class Agent_dba extends Agent {
    

    private String secretCode;
    private EnvironmentProxy envProxy;
    
    @Override
    protected void setup() {
        Logger.info("\n=== Agent starting ===");
        
        initializeWorld();
        AgentBehaviour behaviour = new AgentBehaviour(this, envProxy);
        addBehaviour(behaviour);
    }
    
    private void initializeWorld() {
        try {
            
            String mapa = "maps/100x100-conObstaculos.txt";
            Position initialPos = new Position(99, 99);
            Position tempGoal = new Position(10, 10); 
            
            World world = World.loadFromFile(mapa);
            EnvironmentWithGUI environment = new EnvironmentWithGUI(world, initialPos, tempGoal);
            this.envProxy = new EnvironmentProxy(environment);
            
            Logger.info("World initialized");
            
        } catch (IOException e) {
            System.err.println("Error loading world: " + e.getMessage());
            doDelete();
        }
    }
    
    @Override
    protected void takeDown() {
        Logger.info("\n=== Agent terminating ===");
    }
}