package agent_dba_pr2.world;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import agent_dba_pr2.environment.Surroundings;

/**
 * @author estelle
 * 
 *         Represents the world with a matrix of cells
 *         - Store the map
 *         - Position valid?
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
        return grid[x][y] != OBSTACLE_CELL;
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
            // throw new IllegalArgumentException("Position outside: (" + x + ", " + y +
            // ")");
            return -2; // Tal vez un enum vendria mejor: 0 - valido, -1 - obstaculo, -2 - invalido
        }
        // return grid[y][x];
        return grid[x][y];
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
        return grid[x][y] == OBSTACLE_CELL;
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
    /*
     * public void printWorldWithAgent(Position agentPos) {
     * System.out.println("=== World with Agent ===");
     * for (int y = 0; y < height; y++) {
     * for (int x = 0; x < width; x++) {
     * if (agentPos.getX() == x && agentPos.getY() == y) {
     * System.out.print("A ");
     * } else if (grid[y][x] == OBSTACLE_CELL) {
     * System.out.print("O ");
     * } else {
     * System.out.print(". ");
     * }
     * }
     * System.out.println();
     * }
     * System.out.println("========================");
     * }
     */

    public void printWorldWithAgent(Position agentPos) {
        System.out.println("=== World with Agent ===");
        for (int x = 0; x < height; x++) { // x = fila
            for (int y = 0; y < width; y++) { // y = columna
                if (agentPos.getX() == x && agentPos.getY() == y) {
                    System.out.print("A ");
                } else if (grid[x][y] == OBSTACLE_CELL) {
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
     * Debug : print world + agent + goal
     */
/*    public void printWorldWithAgentAndGoal(Position agentPos, Position goalPos) {
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
*/
public void printWorldWithAgentAndGoal(Position agentPos, Position goalPos) {
    final String RESET = "\u001B[0m";
    final String GREEN = "\u001B[32;1m";
    final String YELLOW = "\u001B[33;1m";
    final String BLUE = "\u001B[36;1m";
    final String RED = "\u001B[31;1m";

    System.out.println("=== World ===");

    // Coordenadas del agente
    int ax = agentPos.getX();
    int ay = agentPos.getY();

    for (int row = 0; row < grid.length; row++) {
        for (int col = 0; col < grid[0].length; col++) {
            boolean isAdjacent =
                    (row == ax - 1 && col == ay) || // arriba
                    (row == ax + 1 && col == ay) || // abajo
                    (row == ax && col == ay - 1) || // izquierda
                    (row == ax && col == ay + 1);   // derecha

            if (agentPos.getX() == row && agentPos.getY() == col) {
                System.out.print(GREEN + "A " + RESET);
            } else if (goalPos.getX() == row && goalPos.getY() == col) {
                System.out.print(YELLOW + "G " + RESET);
            } else if (grid[row][col] == OBSTACLE_CELL) {
                System.out.print(RED + "O " + RESET);
            } else if (isAdjacent) {
                System.out.print(BLUE + ". " + RESET);
            } else {
                System.out.print(". ");
            }
        }
        System.out.println();
    }
    System.out.println("====================");
}

    
    public Surroundings perceive(Position pos) {
        Surroundings s = new Surroundings(pos);
        s.up.setValue(getCell(s.up));
        s.down.setValue(getCell(s.down));
        s.left.setValue(getCell(s.left));
        s.right.setValue(getCell(s.right));
        return s;
    }
}