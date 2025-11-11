
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

        AgentBehaviour b = new AgentBehaviour();
        
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
        int mejor = Integer.MAX_VALUE;
        Action mejorAccion = null;
        
        if (goal == null) {
            this.action = null;
            return;
        }

        if (up != null && up.getValue() != -1) {
            int h = up.manhattanDistance(goal);
            if (h < mejor) {
                mejor = h;
                mejorAccion = Action.UP;
            }
        }

        if (down != null && down.getValue() != -1) {
            int h = down.manhattanDistance(goal);
            if (h < mejor) {
                mejor = h;
                mejorAccion = Action.DOWN;
            }
        }

        if (left != null && left.getValue() != -1) {
            int h = left.manhattanDistance(goal);
            if (h < mejor) {
                mejor = h;
                mejorAccion = Action.LEFT;
            }
        }

        if (right != null && right.getValue() != -1) {
            int h = right.manhattanDistance(goal);
            if (h < mejor) {
                mejor = h;
                mejorAccion = Action.RIGHT;
            }
        }
        this.action = mejorAccion;
    }
    
    public void execute(){
        proxy.requestMove(this.action);
        this.energy += 1;
    }
}
