package core.world;

/**
 *
 * @author estelle
 * 
 * Represents a position in the world (x, y)
 * X = column (0 = left)
 * Y = row (0 = top) */

public class Position {
    private final int x;
    private final int y;
    private int value = 1; 
    
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    

    public void setValue(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    /**
     * Calculates Manhattan distance
     */
    public int manhattanDistance(Position other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }
    
    public Position moveUp() {
        return new Position(x, y - 1);
    }
    
    public Position moveDown() {
        return new Position(x, y + 1);
    }
    
    public Position moveLeft() {
        return new Position(x - 1, y);
    }
    
    public Position moveRight() {
        return new Position(x + 1, y);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return this.x == other.x && this.y == other.y;
    }
    
    /*
    @Override
    public int hashCode() {
        return 31 * x + y;
    }*/
    
    @Override
    public int hashCode() {
        return (x << 16) ^ y;
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")"+value;
    }
}
