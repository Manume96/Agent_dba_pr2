
package agent_dba_pr2;

import agent_dba_pr2.environment.Environment;
import agent_dba_pr2.proxy.EnvironmentProxy;
import agent_dba_pr2.world.Position;
import agent_dba_pr2.world.World;
import jade.core.behaviours.OneShotBehaviour;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Usuario
 */
public class EnvSetupBehaviour extends OneShotBehaviour{
    private Scanner scanner;
    private Agent_dba_pr2 agente;
    private String mapa;
    private Position pos;
    private World world;
    private Environment env;
    
    
    public EnvSetupBehaviour(){
        this.agente = (Agent_dba_pr2) myAgent;
    }
    
    @Override
    public void action(){
        System.out.println("Introduce la ruta del mapa: \n");
        scanner = new Scanner(System.in);
        this.mapa = scanner.nextLine();
        
        System.out.println("Introduce la pos X del agente: \n");
        int x = scanner.nextInt();
        System.out.println("Introduce la pos Y del agente: \n");
        int y = scanner.nextInt();
        
        this.pos = new Position(x, y);
        
        try {
            this.world = World.loadFromFile(mapa);
        } catch (IOException ex) {
            Logger.getLogger(EnvSetupBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.env = new Environment(world, pos);
        agente.setProxy(new EnvironmentProxy(env));
    }
}