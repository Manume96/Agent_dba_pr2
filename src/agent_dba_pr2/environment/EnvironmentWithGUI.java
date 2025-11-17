/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agent_dba_pr2.environment;

import agent_dba_pr2.gui.WorldGUI;
import agent_dba_pr2.logger.Logger;
import agent_dba_pr2.world.Position;
import agent_dba_pr2.world.World;

/**
 * Extended Environment with graphical interface
 */
public class EnvironmentWithGUI extends Environment {
    
    private WorldGUI gui;
    private static final int DELAY_MS = 500; 
    private boolean guiInitialized = false;
    
    public EnvironmentWithGUI(World world, Position initialAgentPos, Position goalPos) {
        super(world, initialAgentPos, goalPos);
        
        this.gui = new WorldGUI(world, initialAgentPos, goalPos);
        this.guiInitialized = true;
        
        // Initialization
        gui.updateEnergy(0);
        Logger.info("GUI initialized");
    }
    
    @Override
    public void moveTo(Position newPosition) {
        super.moveTo(newPosition);
        
        if (guiInitialized) {
            updateGUI();
            
            // Verify if goal reached
            if (newPosition.equals(getGoalPosition())) {
                gui.goalReached();
            }
            
            try {
                Thread.sleep(DELAY_MS);
            } catch (InterruptedException e) {
                Logger.error("Interruption: " + e);
            }
        }
    }
    
    /**
     * Update Gui
     */
    private void updateGUI() {
        if (gui != null && guiInitialized) {
            gui.updateAgentPosition(getAgentPosition());
            gui.updateEnergy(getSpentEnergy());
        }
    }
    
    @Override
    public void debug() {
        super.debug();
        
        if (guiInitialized && gui != null) {
            updateGUI();
        }
    }
}