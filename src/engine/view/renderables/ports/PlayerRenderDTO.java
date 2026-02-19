package engine.view.renderables.ports;

public class PlayerRenderDTO {
    public final String entityId;
    public final String playerName;
    public final double health;
    public final int activeWeapon;
    public final double primaryAmmoStatus;
    public final double secondaryAmmoStatus;
    public final double minesStatus;
    public final double missilesStatus;

    public PlayerRenderDTO(
            String entityId,
            String playerName,
            double health,
            int activeWeapon,
            double primaryAmmoStatus,
            double secondaryAmmoStatus,
            double minesStatus,
            double missilesStatus) {

        this.entityId = entityId;
        this.playerName = playerName;
        this.health = health;
        this.activeWeapon = activeWeapon;
        this.primaryAmmoStatus = primaryAmmoStatus;
        this.secondaryAmmoStatus = secondaryAmmoStatus;
        this.minesStatus = minesStatus;
        this.missilesStatus = missilesStatus;
    }

    public Object[] toObjectArray() {
        return new Object[] {
                this.entityId,
                this.playerName,
                this.health,
                this.activeWeapon,
                this.primaryAmmoStatus,
                this.secondaryAmmoStatus,
                this.minesStatus,
                this.missilesStatus
        };
    }
}
