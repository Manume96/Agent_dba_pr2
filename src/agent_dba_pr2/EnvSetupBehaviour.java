
package agent_dba_pr2;

import jade.core.behaviours.OneShotBehaviour;
import java.util.Scanner;

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
        String mapa = scanner.nextLine();
        
        System.out.println("Introduce la pos X del agente: \n");
        int x = scanner.nextInt();
        System.out.println("Introduce la pos Y del agente: \n");
        int y = scanner.nextInt();
        
        this.pos = new Position(x, y);
        
        this.world = World.loadFromFile(mapa);
        
        this.env = new Environment(world, pos)
        agente.setProxy(new EnvironmentProxy(env));
    }
}