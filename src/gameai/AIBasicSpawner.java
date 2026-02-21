/*package gameai;

import java.util.ArrayList;
import java.util.Random;
import engine.controller.ports.WorldManager;
import engine.generators.AbstractIAGenerator;
import engine.world.ports.DefItem;
import engine.world.ports.DefItemDTO;
import engine.world.ports.WorldDefinition;

public class AIBasicSpawner extends AbstractIAGenerator {

    private final ArrayList<DefItem> asteroidDefs;
    private final Random rnd = new Random();

    // CONTROL PARA RATONES FIJOS
    private boolean fixedMouseSpawned = false;

    public AIBasicSpawner(
            WorldManager worldEvolver, WorldDefinition worldDefinition,
            int maxCreationDelay) {

        super(worldEvolver, worldDefinition, maxCreationDelay);
        this.asteroidDefs = this.worldDefinition.asteroids;
    }

    @Override
    protected String getThreadName() {
        return "AIBasicSpawner";
    }

    @Override
    protected void onActivate() {
        // Puedes inicializar recursos si quieres
    }

    @Override
protected void onTick() {
    if (!fixedMouseSpawned && !asteroidDefs.isEmpty()) {
        DefItem defItem = asteroidDefs.get(0); // mouse_01

        DefItemDTO dto = new DefItemDTO(
            defItem.getAssetId(),
            defItem.getSize(),
            defItem.getAngle(),
            900,
            500,
            defItem.getDensity(),
            -100, // velocidad hacia la izquierda
            0,
            0,
            0
        );

        this.addDynamicIntoTheGame(dto);
        fixedMouseSpawned = true; // ahora no se genera de nuevo
    }
}
}*/