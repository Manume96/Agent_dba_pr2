/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.agent.launcher;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.Boot;

public class Launcher {

    public static void main(String[] args) {
        // On ignore complètement les arguments que NetBeans pourrait donner (dont -conf leap.properties)
        // et on définit NOUS-MÊMES les arguments pour JADE.

        String[] jadeArgs = new String[] {
                "-gui",
                "Santa:agent_dba_pr2.agents.SantaAgent;" +
                "Translator:agent_dba_pr2.agents.TranslatorAgent;" +
                "Agent:agent_dba_pr2.agents.Agent_dba_pr2"
        };

        
        Boot.main(jadeArgs);
    }
}

