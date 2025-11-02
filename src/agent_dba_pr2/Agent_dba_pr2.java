
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
    private int energy;
    
    private boolean setup;
    
    private EnvironmentProxy env;
    
    
    @Override
    protected void setup(){
        AgentBehavior b = new AgentBehavior();
        
        this.addBehaviour(b);
        
        this env = new EnvironmentProxy()
        
        this.setup = false;
    }
    
    public boolean hasFinished(){
        return (pos_x == goal_x && pos_y == goal_y);
    }
    
    public void perceive(){
        if (!setup){
            this.pos_x = 0;
            this.pos_y = 0;
            this.goal_x = 0;
            this.goal_y = 0;
            this.energy = 0;
            
            this.setup = true;
        }
        
        this.up = proxy.up;
        this.down = proxy.down;
        this.left = proxy.left;
        this.right = proxy.right;
    }
    
    public void think(){
        
    }
    
    public void execute(){
        
    }
}
