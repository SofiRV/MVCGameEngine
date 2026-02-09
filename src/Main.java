import engine.controller.impl.Controller;
import engine.controller.ports.ActionsGenerator;
import engine.model.impl.Model;
import engine.utils.helpers.DoubleVector;
import engine.utils.threading.ThreadPoolManager;
import engine.view.core.View;
import engine.world.ports.WorldDefinition;
import engine.world.ports.WorldDefinitionProvider;
import gameworld.ProjectAssets;

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

        // 🎯 CAMBIO AQUÍ - Usar CatRandomWorldDefinitionProvider
        WorldDefinitionProvider worldProv = new gameworld.CatRandomWorldDefinitionProvider(
                worldDimension, projectAssets);

        Controller controller = new Controller(
                worldDimension, viewDimension, maxBodies,
                new View(), new Model(),
                gameRules);

        controller.activate();

        WorldDefinition worldDef = worldProv.provide();

        new gamelevel.LevelBasic(controller, worldDef);

        // ⚠️ SPAWNER DESACTIVADO (sin ratones por ahora)
        // int maxAsteroidCreationDelay = 200;
        // new gameai.AIBasicSpawner(controller, worldDef, maxAsteroidCreationDelay).activate();
    }
}