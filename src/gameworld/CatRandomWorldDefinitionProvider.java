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
    this.addSpaceship("cat_02", 700, 300, 400, 0, 500);

    // 🛏️ Cama PEGADA al borde inferior izquierdo
int bedSize = 1170;
int marginLeft = bedSize / 2;      // 200 (para centrar horizontalmente desde el borde)

double bedX = marginLeft;                      // 200 (pegada a la izquierda)
double bedY = this.worldHeight - 79; // 695 - 200 = 495

this.addGravityBody("bed_01", bedX, bedY, bedSize, 0, 10000);

    // 🎯 Armas
    this.addWeaponPresetBulletRandomAsset(AssetType.BULLET);
    this.addWeaponPresetMissileLauncherRandomAsset(AssetType.MISSILE);
}
}