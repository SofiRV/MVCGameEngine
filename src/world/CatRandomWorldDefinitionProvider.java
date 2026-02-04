package world;

import assets.impl.ProjectAssets;
import assets.ports.AssetType;
import engine.utils.helpers.DoubleVector;
import engine.worlddef.core.AbstractWorldDefinitionProvider;

public final class CatRandomWorldDefinitionProvider extends AbstractWorldDefinitionProvider {

    public CatRandomWorldDefinitionProvider(DoubleVector worldDimension, ProjectAssets assets) {
        super(worldDimension, assets);
    }

    @Override
    protected void define() {
        // Background
        this.setBackgroundStatic("room_01");
        
        // Gato en el centro (método más simple)
        this.addSpaceship("cat_02", this.worldWidth / 2.0, this.worldHeight / 2.0, 400,0,100);
        
        // Armas
        this.addWeaponPresetBulletRandomAsset(AssetType.BULLET);
        this.addWeaponPresetMissileLauncherRandomAsset(AssetType.MISSILE);
    }
}