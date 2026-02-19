package engine.model.bodies.ports;

public class PlayerDTO {
   public final String entityId;

    public final String playerName;
    public final double health;
    
    public final int activeWeapon;
    public final double prymaryAmmoStatus;
    public final double secondaryAmmoStatus;
    public final double minesStatus;
    public final double missilesStatus;
    public final int score;

    public PlayerDTO(
            String entityId,

            String playerName,
            double health,
            int activeWeapon,
            double prymaryAmmoStatus,
            double secondaryAmmoStatus,
            double minesStatus,
            double missilesStatus,
            int score) {

        this.entityId = entityId;
        this.playerName = playerName;
        this.health = health;
        this.activeWeapon = activeWeapon;
        this.prymaryAmmoStatus = prymaryAmmoStatus;
        this.secondaryAmmoStatus = secondaryAmmoStatus;
        this.minesStatus = minesStatus;
        this.missilesStatus = missilesStatus;
        this.score = score;
    }
}
