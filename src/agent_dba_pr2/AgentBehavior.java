
package agent_dba_pr2;

import jade.core.behaviours.Behaviour;

/**
 *
 * @author David
 */
public class AgentBehavior extends Behaviour{
    private final Agent_dba_pr2 agente;

    public AgentBehavior() {
        super();
        this.agente = (Agent_dba_pr2) myAgent;
    }
    
    @Override
    public void action(){
        /**********     EN COMENTARIOS PARA QUE NO DE ERROR MIENTRAS **********
         * ********   NO HAYA IMPLEMENTACION DE LAS SIGUIENTES FUNCIONES ******
         
         
        agente.percieve();
        
        agente.think();
        
        agente.execute();
        
        */
    }
    
    @Override
    public boolean done(){
        
        return agente.hasFinished();
    }
}
