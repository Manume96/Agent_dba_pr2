package core.agent;

import core.environment.Environment;
import core.proxy.EnvironmentProxy;
import core.world.Position;
import core.world.World;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import java.io.IOException;
import java.util.Scanner;
import core.environment.EnvironmentWithGUI;
import jade.core.behaviours.SequentialBehaviour;
import core.agent.behaviour.AgentAuthorizationBehaviour;

public class Agent_dba_pr2 extends Agent {
    
    private String secretCode;
    private EnvironmentProxy envProxy;
    
    @Override
    protected void setup() {
        System.out.println("\n=== Agent starting ===");
        
        // 1. Initialiser le monde ET la GUI dès le début
        initializeWorld();
        
        // 2. Créer la séquence de comportements
        SequentialBehaviour sequence = new SequentialBehaviour();
        
        // Phase 1: Authorization
        AgentAuthorizationBehaviour authBehaviour = new AgentAuthorizationBehaviour();
        sequence.addSubBehaviour(authBehaviour);
        
        // Phase 2:
        
        addBehaviour(sequence);
    }
    
    private void initializeWorld() {
        try {
            
            String mapa = "maps/100x100-conObstaculos.txt";
            Position initialPos = new Position(50, 50);
            Position tempGoal = new Position(10, 10); 
            
            World world = World.loadFromFile(mapa);
            EnvironmentWithGUI environment = new EnvironmentWithGUI(world, initialPos, tempGoal);
            this.envProxy = new EnvironmentProxy(environment);
            
            System.out.println("World initialized");
            
        } catch (IOException e) {
            System.err.println("Error loading world: " + e.getMessage());
            doDelete();
        }
    }
    
    @Override
    protected void takeDown() {
        System.out.println("\n=== Agent terminating ===");
    }
}