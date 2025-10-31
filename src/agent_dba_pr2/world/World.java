/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agent_dba_pr2.world;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author estelle
 * 
 * Represents the world with a matrix of cells
 * - Store the map 
 * - Position valid?
 * 
 */


public class World {
    private int[][] grid;
    private int width;  
    private int height;
    
    public static final int FREE_CELL = 0;
    public static final int OBSTACLE_CELL = -1;
    
    
    private World(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new int[height][width];
    }
    
    /**
     * Load the world from the text file
     * 
     * @param filename
     * @return world
     */
    public static World loadFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        
        try {
            // Dimensions
            int height = Integer.parseInt(reader.readLine().trim());
            int width = Integer.parseInt(reader.readLine().trim());
            
            World world = new World(width, height);
            
            // Read the matrix line by line
            for (int y = 0; y < height; y++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IOException("Incomplete: line " + y + " missing");
                }
                
                String[] values = line.split("\t");
                if (values.length != width) {
                    throw new IOException("Error width " + values.length);
                }
                
                for (int x = 0; x < width; x++) {
                    world.grid[y][x] = Integer.parseInt(values[x].trim());
                }
            }
            
            System.out.println("World loaded: " + width + "x" + height);
            return world;
            
        } finally {
            reader.close();
        }
    }
    
    
    public boolean isValidPosition(Position pos) {
        return isValidPosition(pos.getX(), pos.getY());
    }
    
    public boolean isValidPosition(int x, int y) {
        // limits 
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
        // obstacle
        return grid[y][x] != OBSTACLE_CELL;
    }
    
    /**
     * Vérifie si une position est dans les limites du monde
     * (mais peut être un obstacle)
     */
    public boolean isInBounds(Position pos) {
        return isInBounds(pos.getX(), pos.getY());
    }
    
    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    /**
     * retunr cell's value
     */
    public int getCell(Position pos) {
        return getCell(pos.getX(), pos.getY());
    }
    
    public int getCell(int x, int y) {
        if (!isInBounds(x, y)) {
            throw new IllegalArgumentException("Position outside: (" + x + ", " + y + ")");
        }
        return grid[y][x];
    }
    
    /**
     * Verify if is an obstacle
     */
    public boolean isObstacle(Position pos) {
        return isObstacle(pos.getX(), pos.getY());
    }
    
    public boolean isObstacle(int x, int y) {
        if (!isInBounds(x, y)) {
            return true; 
        }
        return grid[y][x] == OBSTACLE_CELL;
    }
    
    /**
     * Getters pour les dimensions
     */
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    /**
     * print the world
     */
    public void printWorld() {
        System.out.println("=== World " + width + "x" + height + " ===");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] == OBSTACLE_CELL) {
                    System.out.print("O ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println("==================");
    }
    
    /**
     * DEBUG : print the world withe the agent
     */
    public void printWorldWithAgent(Position agentPos) {
        System.out.println("=== World with Agent ===");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (agentPos.getX() == x && agentPos.getY() == y) {
                    System.out.print("A ");
                } else if (grid[y][x] == OBSTACLE_CELL) {
                    System.out.print("O ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println("========================");
    }
    
    /**
     * Debut : print world + agent + goal
     */
    public void printWorldWithAgentAndGoal(Position agentPos, Position goalPos) {
        System.out.println("=== World completed ===");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (agentPos.getX() == x && agentPos.getY() == y) {
                    System.out.print("A ");
                } else if (goalPos.getX() == x && goalPos.getY() == y) {
                    System.out.print("G ");
                } else if (grid[y][x] == OBSTACLE_CELL) {
                    System.out.print("O ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println("====================");
    }
}