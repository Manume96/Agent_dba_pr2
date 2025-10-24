
package agent_dba_pr2;

import jade.core.Agent;

/**
 *
 * @author duckduck
 */
public class Agent_dba_pr2 extends Agent{

    //Sensores
    private int pos_x, pos_y;
    private int goal_x, goal_y;
    private int up, down, left, right;
    
    @Override
    protected void setup(){
        AgentBehavior b = new AgentBehavior();
        
        this.addBehaviour(b);
    }
    
    public boolean hasFinished(){
        return (pos_x == goal_x && pos_y == goal_y);
    }
    
}
