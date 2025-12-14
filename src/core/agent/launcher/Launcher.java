package core.agent.launcher;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import core.agent.Agent_dba;
import core.agent.SantaAgent;
import core.agent.TranslatorAgent;
import core.agent.RudolphAgent;

public class Launcher {

    public static void main(String[] args) {
        // Obtener la instancia del runtime de JADE
        Runtime rt = Runtime.instance();

        // Parámetros del contenedor principal
        String host = "localhost";
        String containerName = "MainContainer";

        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, host);
        profile.setParameter(Profile.CONTAINER_NAME, containerName);
        profile.setParameter(Profile.GUI, "true"); // abrir la interfaz gráfica

        // Crear el contenedor principal
        ContainerController mainContainer = rt.createMainContainer(profile);

        try {
            // Lanzar SantaAgent
            AgentController santa = mainContainer.createNewAgent(
                    "Santa",
                    SantaAgent.class.getCanonicalName(),
                    new Object[]{});
            santa.start();

            // Lanzar TranslatorAgent
            AgentController translator = mainContainer.createNewAgent(
                    "Translator",
                    TranslatorAgent.class.getCanonicalName(),
                    new Object[]{});
            translator.start();

                // Lanzar RudolphAgent
                AgentController rudolph = mainContainer.createNewAgent(
                    "Rudolph",
                    RudolphAgent.class.getCanonicalName(),
                    new Object[]{});
                rudolph.start();

            // Lanzar Agent_dba_pr2
            AgentController agent = mainContainer.createNewAgent("Agent",
                    Agent_dba.class.getCanonicalName(),
                    new Object[]{});
            agent.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
