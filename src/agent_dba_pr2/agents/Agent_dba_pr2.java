package agent_dba_pr2.agents;

import agent_dba_pr2.behaviours.AgentBehaviour;
import agent_dba_pr2.environment.Environment;
import agent_dba_pr2.proxy.EnvironmentProxy;
import agent_dba_pr2.world.Position;
import agent_dba_pr2.world.World;

import jade.core.Agent;
import java.io.IOException;
import java.util.Scanner;
import agent_dba_pr2.environment.EnvironmentWithGUI;
import agent_dba_pr2.behaviours.AgentAuthorizationBehaviour;
import jade.core.behaviours.SequentialBehaviour;



public class Agent_dba_pr2 extends Agent {
    
    private String secretCode;
    
    private Position initialPos;
    private Position goalPos;

    private World world;
    private EnvironmentWithGUI environment;
    private EnvironmentProxy envProxy;

    
    @Override
    protected void setup() {
        System.out.println("\nAgent starting...");
        
        initializeWorld();
        
        SequentialBehaviour sequence = new SequentialBehaviour();
        
        // Phase 1 : Authorization
        AgentAuthorizationBehaviour authBehaviour = new AgentAuthorizationBehaviour();
        sequence.addSubBehaviour(authBehaviour);
        
        // Phase 2 : 
        
        
        addBehaviour(sequence);
    }
    
    
    private void initializeWorld() {
        try {
            String mapa = "src/agent_dba_pr2/mapas-pr3/100x100-conObstaculos.txt";
            
            initialPos = new Position(99,99);
            goalPos = new Position(10, 10); 
            
            world = World.loadFromFile(mapa);
            environment = new EnvironmentWithGUI(world, initialPos, goalPos);
            this.envProxy = new EnvironmentProxy(environment);
            
            System.out.println("World initialized");
            
        } catch (IOException e) {
            System.err.println("Error loading world: " + e.getMessage());
            doDelete();
        }
    }
    

    @Override
    protected void takeDown() {
        System.out.println("\n Agent terminating");
    }    
}
