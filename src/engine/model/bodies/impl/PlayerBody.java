package engine.model.bodies.impl;

import java.util.List;

import engine.events.domain.ports.BodyToEmitDTO;
import engine.model.bodies.ports.BodyEventProcessor;
import engine.model.bodies.ports.BodyType;
import engine.model.bodies.ports.PlayerDTO;
import engine.model.emitter.impl.BasicEmitter;
import engine.model.emitter.ports.EmitterConfigDto;
import engine.model.physics.ports.PhysicsEngine;
import engine.model.physics.ports.PhysicsValuesDTO;
import engine.utils.spatial.core.SpatialGrid;

public class PlayerBody extends DynamicBody {

    // region Fields
    private final List<String> weaponIds = new java.util.ArrayList<>(4);
    private int currentWeaponIndex = -1; // -1 = sin arma
    
    // 🩷 Sistema de vida (0.0 = muerto, 1.0 = vida completa)
    private static final double MAX_HEALTH = 100.0;
    private double health = MAX_HEALTH;
    
    // 🛡️ Invulnerabilidad temporal
    private boolean isInvulnerable = false;
    private long lastDamageTime = 0;
    private static final long INVULNERABILITY_DURATION_NANOS = 2_000_000_000L; // 2 segundos
    
    // 👁️ Parpadeo visual durante invulnerabilidad
    private boolean isVisible = true;
    private static final long BLINK_INTERVAL_NANOS = 100_000_000L; // 0.1 segundos
    
    // 🏆 Puntuación
    private int score = 0;
    // endregion

    public PlayerBody(BodyEventProcessor bodyEventProcessor,
            SpatialGrid spatialGrid,
            PhysicsEngine physicsEngine,
            double maxLifeInSeconds,
            String emitterId) {

        super(bodyEventProcessor,
                spatialGrid,
                physicsEngine,
                BodyType.PLAYER,
                maxLifeInSeconds,
                emitterId);

        this.setMaxThrustForce(800);
        this.setMaxAngularAcceleration(1000);
        this.setAngularSpeed(0);  // ✅ Sin rotación automática
    }

    // region Weapons
    public void addWeapon(String emitterId) {
        this.weaponIds.add(emitterId);

        if (this.currentWeaponIndex < 0) {
            // Signaling existence of weapon in the spaceship
            this.currentWeaponIndex = 0;
        }
    }

    public BasicEmitter getActiveWeapon() {
        if (this.currentWeaponIndex < 0 || this.currentWeaponIndex >= this.weaponIds.size()) {
            return null;
        }

        return this.getEmitter(this.weaponIds.get(this.currentWeaponIndex));
    }

    public int getActiveWeaponIndex() {
        if (this.currentWeaponIndex < 0 || this.currentWeaponIndex >= this.weaponIds.size()) {
            return -1;
        }

        return this.currentWeaponIndex;
    }

    public EmitterConfigDto getActiveWeaponConfig() {
        BasicEmitter emitter = getActiveWeapon();
        return (emitter != null) ? emitter.getConfig() : null;
    }

    private int getAmmoStatus(int weaponIndex) {
        if (weaponIndex < 0 || weaponIndex >= this.weaponIds.size()) {
            return 0;
        }

        BasicEmitter emitter = this.getEmitter(this.weaponIds.get(weaponIndex));
        if (emitter == null) {
            return 0;
        }

        return emitter.getBodiesRemaining();
    }

    public int getAmmoStatusPrimary() {
        return getAmmoStatus(0);
    }

    public int getAmmoStatusSecondary() {
        return getAmmoStatus(1);
    }

    public int getAmmoStatusMines() {
        return getAmmoStatus(2);
    }

    public int getAmmoStatusMissiles() {
        return getAmmoStatus(3);
    }

    public BodyToEmitDTO getProjectileConfig() {
        if (this.currentWeaponIndex < 0 || this.currentWeaponIndex >= this.weaponIds.size()) {
            return null;
        }

        BasicEmitter emitter = this.getEmitter(this.weaponIds.get(this.currentWeaponIndex));
        if (emitter == null) {
            return null;
        }

        return emitter.getBodyToEmitConfig();
    }

    public void registerFireRequest() {
        if (this.currentWeaponIndex < 0 || this.currentWeaponIndex >= this.weaponIds.size()) {
            System.out.println("> No weapon active or no weapons!");
            return;
        }

        BasicEmitter emitter = this.getEmitter(this.weaponIds.get(this.currentWeaponIndex));
        if (emitter == null) {
            return;
        }

        emitter.registerRequest();
    }

    public boolean mustFireNow(PhysicsValuesDTO newPhyValues) {
        if (this.currentWeaponIndex < 0 || this.currentWeaponIndex >= this.weaponIds.size()) {
            return false;
        }

        BasicEmitter emitter = this.getEmitter(this.weaponIds.get(this.currentWeaponIndex));
        if (emitter == null) {
            return false;
        }

        double dtNanos = newPhyValues.timeStamp - this.getPhysicsValues().timeStamp;
        double dtSeconds = ((double) dtNanos) / 1_000_000_000.0d;

        return emitter.mustEmitNow(dtSeconds);
    }

    public void selectNextWeapon() {
        if (this.weaponIds.size() <= 0) {
            return;
        }

        this.currentWeaponIndex++;
        this.currentWeaponIndex = this.currentWeaponIndex % this.weaponIds.size();
    }

    public void selectWeapon(int weaponIndex) {
        if (weaponIndex >= 0 && weaponIndex < this.weaponIds.size()) {
            this.currentWeaponIndex = weaponIndex;
        }
    }
    // endregion

    // region Health System
    /**
     * Apply damage to the player.
     * @param amount Amount of damage (in HP, not percentage)
     */
    public void takeDamage(double amount) {
        // Si está invulnerable, ignorar daño
        if (this.isInvulnerable) {
            return;
        }
        
        // Aplicar daño
        this.health -= amount;
        
        if (this.health < 0) {
            this.health = 0;
        }
        
        // Activar invulnerabilidad
        this.isInvulnerable = true;
        this.lastDamageTime = System.nanoTime();
        
        System.out.println("🐱💥 Player took " + amount + " damage! Health: " + 
                         String.format("%.1f", this.health) + "/" + MAX_HEALTH);
        
        // Verificar muerte
        if (this.isDead()) {
            System.out.println("💀 Player died!");
        }
    }

    /**
     * Heal the player.
     * @param amount Amount to heal (in HP, not percentage)
     */
    public void heal(double amount) {
        this.health += amount;
        
        if (this.health > MAX_HEALTH) {
            this.health = MAX_HEALTH;
        }
        
        System.out.println("🐱💚 Player healed " + amount + " HP! Health: " + 
                         String.format("%.1f", this.health) + "/" + MAX_HEALTH);
    }

    /**
     * Check if player is dead.
     */
    public boolean isDead() {
        return this.health <= 0;
    }

    /**
     * Get health as a percentage (0.0 to 1.0) for UI display.
     */
    public double getHealthPercentage() {
        return this.health / MAX_HEALTH;
    }

    /**
     * Update invulnerability state (called every frame).
     */
    public void updateInvulnerability() {
        if (!this.isInvulnerable) {
            this.isVisible = true;
            return;
        }
        
        long now = System.nanoTime();
        long elapsed = now - this.lastDamageTime;
        
        // Terminar invulnerabilidad después de 2 segundos
        if (elapsed >= INVULNERABILITY_DURATION_NANOS) {
            this.isInvulnerable = false;
            this.isVisible = true;
            return;
        }
        
        // Parpadeo visual durante invulnerabilidad
        long blinkCycle = elapsed / BLINK_INTERVAL_NANOS;
        this.isVisible = (blinkCycle % 2 == 0);
    }

    /**
     * Check if player should be rendered (for blinking effect).
     */
    public boolean isVisible() {
        return this.isVisible;
    }
    // endregion

    // region Data Transfer
    public PlayerDTO getData() {
        PlayerDTO playerData = new PlayerDTO(
                this.getBodyId(),
                "",  // playerName (vacío por ahora)
                this.getHealthPercentage(),  // ✅ health como porcentaje
                this.getActiveWeaponIndex(),
                this.getAmmoStatusPrimary(),
                this.getAmmoStatusSecondary(),
                this.getAmmoStatusMines(),
                this.getAmmoStatusMissiles(),
                this.score);
        return playerData;
    }
    // endregion

    // region Movement
    public void reverseThrust() {
        this.thurstNow(-this.getMaxThrustForce());
    }

    // 🎯 ROTACIÓN DESACTIVADA PARA JUEGO DE GATOS
    public void rotateLeftOn() {
        // Vacío - El gato no rota
    }

    public void rotateRightOn() {
        // Vacío - El gato no rota
    }

    public void rotateOff() {
        // Vacío - El gato no rota
    }

    /**
     * Make the cat jump (only works when on ground)
     */
    public void jump(double jumpSpeed) {
        PhysicsEngine engine = this.getPhysicsEngine();
        
        // Check if the engine supports jumping (CatPhysicsEngine)
        if (engine instanceof engine.model.physics.implementations.CatPhysicsEngine) {
            engine.model.physics.implementations.CatPhysicsEngine catEngine = 
                (engine.model.physics.implementations.CatPhysicsEngine) engine;
            catEngine.jump(jumpSpeed);
        }
    }
    // endregion
}