package agent_dba_pr2;

import agent_dba_pr2.environment.Environment;
import agent_dba_pr2.proxy.EnvironmentProxy;
import agent_dba_pr2.world.Position;
import agent_dba_pr2.world.World;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import java.io.IOException;
import java.util.Scanner;

public class Agent_dba_pr2 extends Agent {

    private Position initialPos;
    private Position goalPos;

    // Cosas del mundo
    private World world;
    private Environment environment;

    @Override
    protected void setup() {

        // OneShotBehaviour para inicializar el entorno y las posiciones
        this.addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                
                /* Descomentar para que sea interactivo y comentar mapa,initialPos, goalPos que aparecen despues de este bloque
                Scanner scanner = new Scanner(System.in);

                System.out.println("Introduce la ruta del mapa:");
                String mapa = scanner.nextLine();

                System.out.println("Introduce la posición inicial del agente (x y):");
                int xInit = scanner.nextInt();
                int yInit = scanner.nextInt();
                initialPos = new Position(xInit, yInit);

                System.out.println("Introduce la posición objetivo del agente (x y):");
                int xGoal = scanner.nextInt();
                int yGoal = scanner.nextInt();
                goalPos = new Position(xGoal, yGoal);*/
                
                //String mapa = "src/agent_dba_pr2/maps/mapWithVerticalWall.txt";
                //String mapa = "src/agent_dba_pr2/maps/mapWithHorizontalWall.txt";
                //String mapa = "src/agent_dba_pr2/maps/mapWithConvexObstacle.txt";
                //String mapa = "src/agent_dba_pr2/maps/mapWithoutObstacle.txt";
                String mapa = "src/agent_dba_pr2/maps/mapWithDiagonalWall.txt";
                //String mapa = "src/agent_dba_pr2/maps/mapWithConcaveObstacle.txt";
                //String mapa = "src/agent_dba_pr2/maps/mapWithComplexObstacle3.txt";
                //String mapa = "src/agent_dba_pr2/maps/mapWithComplexObstacle4.txt";

                initialPos = new Position(9,9);
                goalPos = new Position(0,9);
                try {
                    world = World.loadFromFile(mapa);
                    // Comprueba si la POSICIÓN INICIAL es válida (dentro de límites Y no un obstáculo)
                    if (!world.isValidPosition(initialPos)) {
                        throw new IllegalArgumentException("La posicion inicial " + initialPos + " no es valida (esta fuera del mapa o es un obstaculo).");
                    }
                    // Comprueba si la POSICIÓN OBJETIVO es válida
                    if (!world.isValidPosition(goalPos)) {
                        throw new IllegalArgumentException("La posicion objetivo " + goalPos + " no es valida (esta fuera del mapa o es un obstaculo).");
                    }
                    environment = new Environment(world, initialPos,goalPos);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                System.out.println("Agente inicializado");

                // Agregamos el comportamiento principal
                /*Se añade aqui para asegurar ejecución secuencial y evitar usar booleanos de instancia para evaluar si se puede o no ejecutar el comportaminto
                cosas de JADE*/
                AgentBehaviour behaviour = new AgentBehaviour(
                        Agent_dba_pr2.this,
                        new EnvironmentProxy(environment)
                );
                addBehaviour(behaviour);
            }
        });

    }
}
