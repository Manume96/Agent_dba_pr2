package agent_dba_pr2.environment;

import agent_dba_pr2.world.Position;

/**
 *
 * @author duckduck
 */
public class Surroundings {
    public Position up;
    public Position down;
    public Position left;
    public Position right;


    // A ver, que no se me olvide que (x,y) != (fila,columna). El cambio lo hace el mundo
    /*public Surroundings(Position pos) {
        this.up = new Position(pos.getX(), pos.getY() + 1);
        this.down = new Position(pos.getX(), pos.getY() - 1);
        this.left = new Position(pos.getX() - 1, pos.getY());
        this.right = new Position(pos.getX() + 1, pos.getY());
    }*/


    public Surroundings(Position pos) {
        this.up = new Position(pos.getX()-1, pos.getY());
        this.down = new Position(pos.getX()+1, pos.getY());
        this.left = new Position(pos.getX(), pos.getY()-1);
        this.right = new Position(pos.getX(), pos.getY()+1);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Surroundings:\n");
        sb.append("  Up:    ").append(up).append("\n");
        sb.append("  Down:  ").append(down).append("\n");
        sb.append("  Left:  ").append(left).append("\n");
        sb.append("  Right: ").append(right).append("\n");
        return sb.toString();
    }
}
