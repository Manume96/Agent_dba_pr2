
package agent_dba_pr2;


import agent_dba_pr2.environment.Surroundings;
import agent_dba_pr2.proxy.EnvironmentProxy;
import agent_dba_pr2.world.Position;

import jade.core.Agent;

/**
 *
 * @author duckduck
 */
public class Agent_dba_pr2 extends Agent{

    //Sensores
    private Position pos;
    private Position goal;
    private Position up, down, left, right;
    private int energy;
    
    private Action action;
    
    private boolean setup;
    
    private EnvironmentProxy proxy;
    
    
    @Override
    protected void setup(){

        AgentBehavior b = new AgentBehavior();
        
        this.addBehaviour(b);

        EnvSetupBehaviour b2 = new EnvSetupBehaviour();
        
        this.addBehaviour(b2);

        this.setup = false;
    }

    public void setProxy(EnvironmentProxy proxy) {
        this.proxy = proxy;
    }
    
    public boolean hasFinished(){
        return (pos == goal);
    }
    
    public void perceive(){
        
        Surroundings surr = proxy.perceive();
        
        if (!setup){
            this.pos = proxy.getAgentPosition();
            //this.goal = proxy.goal();
            this.energy = 0;
            
            this.setup = true;
        }
        
        
        this.up = surr.up;
        this.down = surr.down;
        this.left = surr.left;
        this.right = surr.right;
    }
    
    public void think(){
        
        
    }
    
    public void execute(){
        proxy.requestMove(this.action);
        this.energy += 1;
    }
}
