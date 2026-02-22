package gameworld;

import engine.assets.ports.AssetType;
import engine.utils.helpers.DoubleVector;
import engine.world.core.AbstractWorldDefinitionProvider;

public final class CatRandomWorldDefinitionProvider extends AbstractWorldDefinitionProvider {

    public CatRandomWorldDefinitionProvider(DoubleVector worldDimension, ProjectAssets assets) {
        super(worldDimension, assets);
    }

    @Override
protected void define() {
    double centerX = this.worldWidth / 2.0;
    double centerY = this.worldHeight / 2.0;

    // 🏠 Background
    this.setBackgroundStatic("room_01");

    // 🐱 Gato jugador
    this.addSpaceshipRandomAsset(1, AssetType.SPACESHIP, 0, 500, 150, 300, 500);

    // 🛏️ CAMA - Sistema Completo: Colliders Horizontales + Colliders Verticales
    double bedHeight = 155.0;      // Alto de la cama
    double bedWidthPx = 1172.0;    // Ancho de la cama
    double bedCenterX = bedWidthPx / 2.0;
    
    // ✅ CAMBIO: Añadir offset para mover las cajas más abajo
    double offsetDown = -23.0;  // ← Ajusta este valor (más grande = más abajo)
    double bedY = this.worldHeight - (bedHeight / 2.0) - offsetDown;
    
    // 2️⃣ COLLIDERS HORIZONTALES (parte de arriba de la cama - donde duerme el gato)
    int numHorizontalSections = 6;
    double sectionWidth = bedWidthPx / numHorizontalSections;
    
    for (int i = 0; i < numHorizontalSections; i++) {
        double sectionX = (sectionWidth / 2.0) + (i * sectionWidth);
        this.addGravityBody("collider_bed", sectionX, bedY, bedHeight, 0, 10000);
    }
    
    // 3️⃣ COLLIDERS VERTICALES (cabecero y pie de cama)
    double sideWallThickness = 80.0;
    int numVerticalSections = 5;
    double verticalSectionHeight = 80.0;
    
    // 🧱 CABECERO (lado izquierdo de la cama)
    double headboardX = sideWallThickness / 2.0;
    
    for (int i = 0; i < numVerticalSections; i++) {
        double offsetY = bedY - (verticalSectionHeight / 2.0) - (i * verticalSectionHeight);
        this.addGravityBody("collider_wall", headboardX, offsetY, sideWallThickness, 0, 10000);
    }
    
    // 🧱 PIE DE CAMA (lado derecho de la cama)
    double footboardX = bedWidthPx - (sideWallThickness / 2.0);
    
    for (int i = 0; i < numVerticalSections; i++) {
        double offsetY = bedY - (verticalSectionHeight / 2.0) - (i * verticalSectionHeight);
        this.addGravityBody("collider_wall", footboardX, offsetY, sideWallThickness, 0, 10000);
    }

    // 🎯 Armas
    this.addWeaponPresetBulletRandomAsset(AssetType.BULLET);
    this.addWeaponPresetMissileLauncherRandomAsset(AssetType.MISSILE);

    //Ratones
    this.addAsteroidPrototypeRandomAsset(
        1,                  // número de ratones a generar
        AssetType.ASTEROID, // tipo (tus ratones)
        100,                // density (puedes dejarlo así)
        0,                  // minAngle
        0,                  // maxAngle (0 para que no rote)
        80,                 // minSize
        80,                 // maxSize (igual al minSize para tamaño fijo)
        900,                // posMinX
        900,                // posMaxX (igual al minX para fijar)
        500,                // posMinY
        500,                // posMaxY (igual al minY)
        0,                  // speedMin
        0,                  // speedMax (sin movimiento inicial)
        0,                  // angularSpeedMin
        0                   // angularSpeedMax (sin rotación)
    );
    }
}