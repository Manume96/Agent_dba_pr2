/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agent_dba_pr2.test;
import agent_dba_pr2.world.World;
import agent_dba_pr2.world.Position;
import java.io.IOException;

/**
 *
 * @author estelle
 */

public class TestWorld {
    
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("  TEST WORLD");
        System.out.println("=================================\n");
        
        testLoadFromFile();
        
        testPositionValidation();

        testManhattanDistance();

    }
    
    /**
     * Test 1: load file
     */
    private static void testLoadFromFile() {
        System.out.println("TEST 1: Load file");
        
        try {
            World world = World.loadFromFile("src/agent_dba_pr2/maps/mapWithConcaveObstacle.txt");
            
            System.out.println(" Load with success!");
            System.out.println("  Dimensions: " + world.getWidth() + "x" + world.getHeight());
            
            world.printWorld();
            
        } catch (IOException e) {
            System.err.println(" Error " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Test 2: Validation positions
     */
    private static void testPositionValidation() {
        System.out.println("TEST 2: Validation positions");
        System.out.println("---------------------------------");
        
        try {
            World world = World.loadFromFile("src/agent_dba_pr2/maps/mapWithComplexObstacle3.txt");
            
            Position validPos = new Position(0, 0);
            Position outOfBounds = new Position(-1, 0);
            Position alsoOutOfBounds = new Position(100, 100);
            
            System.out.println("Position (0, 0) valide? " + 
                world.isValidPosition(validPos) + " (expected: true)");
            System.out.println("Position (-1, 0) valide? " + 
                world.isValidPosition(outOfBounds) + " (expected: false)");
            System.out.println("Position (100, 100) valide? " + 
                world.isValidPosition(alsoOutOfBounds) + " (expected: false)");
            
            // Tester isObstacle
            System.out.println("\nTest isObstacle:");
            System.out.println("(4, 5) is obstacle? " + 
                world.isObstacle(outOfBounds));
            
            System.out.println("Success");
            
        } catch (IOException e) {
            System.err.println("Error " + e.getMessage());
        }
        
        System.out.println();
    }
    
   
    /**
     * Test 3: Distance Manhattan
     */
    private static void testManhattanDistance() {
        System.out.println("TEST 3: Distance Manhattan");
        System.out.println("---------------------------------");
        
        Position p1 = new Position(0, 0);
        Position p2 = new Position(3, 4);
        Position p3 = new Position(5, 5);
        
        int dist1 = p1.manhattanDistance(p2);
        int dist2 = p1.manhattanDistance(p3);
        int dist3 = p2.manhattanDistance(p3);
        
        System.out.println("Distance (0,0) -> (3,4): " + dist1 + " (expected: 7)");
        System.out.println("Distance (0,0) -> (5,5): " + dist2 + " (expected: 10)");
        System.out.println("Distance (3,4) -> (5,5): " + dist3 + " (expected: 3)");
        
        if (dist1 == 7 && dist2 == 10 && dist3 == 3) {
            System.out.println("Correct");
        } else {
            System.out.println("Incorrect");
        }
        
        System.out.println();
    }
}