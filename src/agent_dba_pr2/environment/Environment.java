package agent_dba_pr2.environment;
  
import agent_dba_pr2.world.Position;
import agent_dba_pr2.world.World;


/**
 *
 * @author duckduck
 */
public class Environment {
    
    private final World world;
    
    private Position agentCurrPos;
    
    
    //OK
    public Environment(World world,Position initialAgentPos){
        this.world = world;
         this.agentCurrPos = initialAgentPos;
    };

    
    //OK
    public Surroundings perceive(){
        Surroundings surs = world.perceive(this.agentCurrPos);
        return surs;
    }

    //OK
    public Position getAgentPosition(){
        return this.agentCurrPos;
    }
    
    //OK
    public void updateAgentPosition(Position newPos){
        this.agentCurrPos = newPos;
    }







    public void moveTo(Position newPosition){
        //Aqui verificar la decisión
        System.out.println("Agente se ha movido");
        this.agentCurrPos = newPosition;
    };

    public void debug(){
        world.printWorldWithAgent(this.agentCurrPos);
    }
    
}
