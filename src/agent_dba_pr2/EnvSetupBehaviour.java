
package agent_dba_pr2;

import jade.core.behaviours.OneShotBehaviour;
import java.util.Scanner;

/**
 *
 * @author Usuario
 */
public class EnvSetupBehaviour extends OneShotBehaviour{
    private Scanner scanner;
    
    
    
    @Override
    public void action(){
        System.out.println("Introduce la ruta del mapa: \n");
        scanner = new Scanner(System.in);
        String mapa = scanner.nextLine();
    }
}
