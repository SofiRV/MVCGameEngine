package gameai;

import java.util.ArrayList;

import engine.controller.ports.WorldManager;
import engine.generators.AbstractIAGenerator;
import engine.world.ports.DefItem;
import engine.world.ports.DefItemDTO;
import engine.world.ports.DefItemPrototypeDTO;
import engine.world.ports.WorldDefinition;

public class MouseSpawner extends AbstractIAGenerator {

    private final ArrayList<DefItem> mousePrototypes;
    private final ArrayList<DefItemDTO> activeMice = new ArrayList<>();

    private final double spawnX = 900;
    private final double spawnY = 500;

    private final int spawnDelayTicks = 20; // ticks entre ratones
    private int ticksSinceLastSpawn = 0;

    public MouseSpawner(
            WorldManager worldEvolver,
            WorldDefinition worldDefinition,
            int maxCreationDelay) {

        super(worldEvolver, worldDefinition, maxCreationDelay);
        this.mousePrototypes = worldDefinition.asteroids;
    }

    @Override
    protected String getThreadName() {
        return "MouseSpawner";
    }

    @Override
    protected void onTick() {
        ticksSinceLastSpawn++;

        // Limpiar ratones que ya no existen en el juego
        activeMice.removeIf(mouse -> mouse == null);

        // Generar ratón si pasó suficiente tiempo
        if (ticksSinceLastSpawn >= spawnDelayTicks && !mousePrototypes.isEmpty()) {

            DefItemPrototypeDTO proto = (DefItemPrototypeDTO) mousePrototypes.get(0);

            // Creamos un ratón nuevo
            DefItemDTO newMouse = new DefItemDTO(
                proto.assetId,
                80,    // tamaño fijo
                0,     // ángulo fijo
                spawnX,
                spawnY,
                100,   // density fija
                -100,  // velocidad X hacia la izquierda
                0,     // velocidad Y
                0,     // angularSpeed
                0      // thrust
            );

            this.addDynamicIntoTheGame(newMouse);
            activeMice.add(newMouse);

            ticksSinceLastSpawn = 0;
        }
    }
}