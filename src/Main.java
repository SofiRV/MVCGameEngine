import engine.controller.impl.Controller;
import engine.controller.ports.ActionsGenerator;
import engine.model.impl.Model;
import engine.utils.helpers.DoubleVector;
import engine.utils.threading.ThreadPoolManager;
import engine.view.core.View;
import engine.world.ports.WorldDefinitionProvider;
import gameai.MouseSpawner;
import gameworld.CatRandomWorldDefinitionProvider;
import gameworld.ProjectAssets; // <-- importamos nuestro spawner específico

public class Main {

    public static void main(String[] args) {

        System.setProperty("sun.java2d.uiScale", "1.0");
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "false");

        DoubleVector worldDimension = new DoubleVector(1355, 695);
        DoubleVector viewDimension = new DoubleVector(1355, 695);
        int maxBodies = 1000;

        ProjectAssets projectAssets = new ProjectAssets();

        ThreadPoolManager.configure(maxBodies);
        ThreadPoolManager.prestartAllCoreThreads();

        ActionsGenerator gameRules = new gamerules.DeadInLimitsPlayerImmunity();

        // Usamos nuestro WorldDefinitionProvider
        WorldDefinitionProvider worldProv = new CatRandomWorldDefinitionProvider(
                worldDimension, projectAssets);

        Controller controller = new Controller(
                worldDimension, viewDimension, maxBodies,
                new View(), new Model(),
                gameRules);

        controller.activate();

        // Obtenemos la definición del mundo
        var worldDef = worldProv.provide();

        // Creamos el nivel
        new gamelevel.LevelBasic(controller, worldDef);

        // 🐭 SPAWNER DE RATONES
        int maxCreationDelay = 200; // milisegundos entre ticks
        new MouseSpawner(controller, worldDef, maxCreationDelay).activate();
    }
}