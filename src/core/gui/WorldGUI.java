package core.gui;

import core.world.Position;
import core.world.World;
import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Graphical User Interface to visualize the world and agent movement
 * - Obstacles: Gray
 * - Visited cells: Yellow
 * - Goal: Blue
 * - Initial position: Green
 * - Agent: Orange circle
 */

public class WorldGUI extends JFrame {
    
    private final WorldPanel worldPanel;
    private final JLabel energyLabel;
    private int currentEnergy = 0;
    
    public WorldGUI(World world, Position initialPos, Position goalPos) {
        super("GUI Agent Navigation");
        
        this.worldPanel = new WorldPanel(world, initialPos, goalPos);
        this.energyLabel = new JLabel("Energy: 0");
        
        setupUI();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxWidth = (int)(screenSize.width * 0.95);
        int maxHeight = (int)(screenSize.height * 0.95);
        
        if (getWidth() > maxWidth || getHeight() > maxHeight) {
            setSize(Math.min(getWidth(), maxWidth), Math.min(getHeight(), maxHeight));
        }
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Main panel with a scroll pane
        JScrollPane scrollPane = new JScrollPane(worldPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Legend panel on the right side
        JPanel legendPanel = createLegendPanel();
        add(legendPanel, BorderLayout.EAST);
    }
    
    private JPanel createLegendPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(150, 0));
        
        panel.add(createLegendItem(new Color(255, 140, 0), "Agent"));
        panel.add(createLegendItem(Color.GRAY, "Obstacle"));
        panel.add(createLegendItem(Color.YELLOW, "Visited"));
        panel.add(createLegendItem(Color.BLUE, "Goal"));
        panel.add(createLegendItem(Color.GREEN, "Start"));
        panel.add(energyLabel);
        
        return panel;
    }
    
    private JPanel createLegendItem(Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        item.add(colorBox);
        item.add(new JLabel(text));
        return item;
    }
    
    /**
     * Updates the agent's position on the GUI
     */
    public void updateAgentPosition(Position newPos) {
        worldPanel.updateAgentPosition(newPos);
        worldPanel.scrollToAgent();
        repaint();
    }
    
    /**
     * Updates the energy consumed display
     */
    public void updateEnergy(int energy) {
        energyLabel.setText("Energy consumed: " + energy);
        this.currentEnergy = energy;
    }
    
    /**
     * Marks the goal as reached and shows a dialog
     */
    public void goalReached() {
        JOptionPane.showMessageDialog(this, 
            "The agent has reached the goal!\nTotal energy: " + currentEnergy,
            "Goal Reached", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Inner panel that draws the world grid
     */
    private static class WorldPanel extends JPanel {
        private static final int MARGIN = 20;
        private static final int MAX_WINDOW_SIZE = 800;
        
        private final World world;
        private final Position initialPos;
        private final Position goalPos;
        private Position currentAgentPos;
        private final Set<Position> visitedPositions;
        private final int cellSize;
        
        public WorldPanel(World world, Position initialPos, Position goalPos) {
            this.world = world;
            this.initialPos = initialPos;
            this.goalPos = goalPos;
            this.currentAgentPos = initialPos;
            this.visitedPositions = new HashSet<>();
            
            visitedPositions.add(initialPos);
            
            // Calculate cell size based on world dimensions
            this.cellSize = calculateCellSize(world.getWidth(), world.getHeight());
            
            // Calculate panel size based on world dimensions
            int width = world.getHeight() * cellSize + 2 * MARGIN;
            int height = world.getWidth() * cellSize + 2 * MARGIN;
            setPreferredSize(new Dimension(width, height));
            setBackground(Color.WHITE);
        }
        
        /**
         * Calculates optimal cell size based on map dimensions 10 or 50
         */
        private int calculateCellSize(int rows, int cols) {
            int maxDimension = Math.max(rows, cols);
            
            if (maxDimension <= 10) {
                return 40;
            } else if (maxDimension <= 20) {
                return 30; 
            } else if (maxDimension <= 50) {
                return 15; 
            } else {
                return 10; 
            }
        }
        
        public void updateAgentPosition(Position newPos) {
            this.currentAgentPos = newPos;
            visitedPositions.add(newPos);
        }
        
        /**
         * Scrolls the view
         */
        public void scrollToAgent() {
            int x = MARGIN + currentAgentPos.getY() * cellSize;
            int y = MARGIN + currentAgentPos.getX() * cellSize;
            
            Rectangle agentRect = new Rectangle(
                x - cellSize, 
                y - cellSize, 
                cellSize * 3, 
                cellSize * 3
            );
            
            scrollRectToVisible(agentRect);
        }
        
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw grid and cells
            for (int row = 0; row < world.getWidth(); row++) {
                for (int col = 0; col < world.getHeight(); col++) {
                    Position pos = new Position(row, col);
                    int x = MARGIN + col * cellSize;
                    int y = MARGIN + row * cellSize;
                    
                    // Determine cell color based on its state
                    if (world.isObstacle(pos)) {
                        // Obstacle in gray
                        g2d.setColor(Color.GRAY);
                        g2d.fillRect(x, y, cellSize, cellSize);
                    } else if (visitedPositions.contains(pos) && !pos.equals(currentAgentPos)) {
                        // Visited cells in yellow
                        g2d.setColor(new Color(255, 255, 150));
                        g2d.fillRect(x, y, cellSize, cellSize);
                    } else if (pos.equals(goalPos)) {
                        // Goal in blue
                        g2d.setColor(new Color(100, 150, 255));
                        g2d.fillRect(x, y, cellSize, cellSize);
                    } else if (pos.equals(initialPos)) {
                        // Initial position in green
                        g2d.setColor(new Color(150, 255, 150));
                        g2d.fillRect(x, y, cellSize, cellSize);
                    } else {
                        // Empty cell in white
                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(x, y, cellSize, cellSize);
                    }
                    
                    // Draw cell border
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.drawRect(x, y, cellSize, cellSize);
                }
            }
            
            // Draw special markers
            drawGoalMarker(g2d);
            drawInitialMarker(g2d);
            
            // Draw agent last (on top of everything)
            drawAgent(g2d);
        }
        
        private void drawAgent(Graphics2D g2d) {
            int x = MARGIN + currentAgentPos.getY() * cellSize;
            int y = MARGIN + currentAgentPos.getX() * cellSize;
            
            int centerX = x + cellSize / 2;
            int centerY = y + cellSize / 2;
            int radius = cellSize / 3;
            
            // Draw orange circle
            g2d.setColor(new Color(255, 140, 0)); 
            g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }
        
        
        /**
         * Draw a letter in the cell
         */
        private void drawMarker(Graphics2D g2d, Position pos, String text, Color color) {
            int x = MARGIN + pos.getY() * cellSize;
            int y = MARGIN + pos.getX() * cellSize;
            
            g2d.setColor(color);
            int fontSize = Math.max(10, cellSize - 8);
            g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
            g2d.drawString(text, x + cellSize/2 - fontSize/3, y + cellSize/2 + fontSize/3);
        }
        
        private void drawGoalMarker(Graphics2D g2d) {
            drawMarker(g2d, goalPos, "G", Color.BLUE);
        }
        
        private void drawInitialMarker(Graphics2D g2d) {
            if (!currentAgentPos.equals(initialPos)) {
                drawMarker(g2d, initialPos, "S", Color.GREEN.darker());
            }
        }
    }
}