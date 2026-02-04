package engine.model.bodies.impl;

import java.util.List;

import engine.events.domain.ports.BodyToEmitDTO;
import engine.model.bodies.ports.BodyEventProcessor;
import engine.model.bodies.ports.BodyType;
import engine.model.bodies.ports.PlayerDTO;
import engine.model.physics.ports.PhysicsEngine;
import engine.model.physics.ports.PhysicsValuesDTO;
import engine.model.weapons.ports.Weapon;
import engine.model.weapons.ports.WeaponDto;
import engine.utils.spatial.core.SpatialGrid;

public class PlayerBody extends DynamicBody {

    private final List<Weapon> weapons = new java.util.ArrayList<>(4);
    private int currentWeaponIndex = -1;
    private double damage = 0D;
    private double energye = 1D;
    private int temperature = 1;
    private double shield = 1D;
    private int score = 0;
    
    // 🎯 PROTECCIÓN CONTRA EL BUG DE COLISIONES
    private double fixedSize = -1;  // Se inicializa en activate()

    public PlayerBody(BodyEventProcessor bodyEventProcessor,
            SpatialGrid spatialGrid,
            PhysicsEngine physicsEngine,
            double maxLifeInSeconds) {

        super(bodyEventProcessor,
                spatialGrid,
                physicsEngine,
                BodyType.PLAYER,
                maxLifeInSeconds);

        this.setMaxThrustForce(800);
        this.setMaxAngularAcceleration(1000);
        this.setAngularSpeed(0);
    }
    
    // 🎯 INICIALIZAR EL TAMAÑO FIJO CUANDO EL JUGADOR SE ACTIVA
    @Override
    public synchronized void activate() {
        super.activate();
        
        // Ahora sí podemos obtener el tamaño correcto
        PhysicsValuesDTO values = super.getPhysicsValues();
        if (values != null && values.size > 0) {
            this.fixedSize = values.size;
            System.out.println("🐱 Player activated with FIXED size: " + this.fixedSize + " px");
        }
    }
    
    // 🎯 SOBREESCRIBIR getPhysicsValues PARA FORZAR TAMAÑO CONSTANTE
    @Override
    public PhysicsValuesDTO getPhysicsValues() {
        PhysicsValuesDTO values = super.getPhysicsValues();
        
        // Solo aplicar la corrección si ya tenemos el tamaño fijo inicializado
        if (this.fixedSize > 0 && Math.abs(values.size - this.fixedSize) > 1.0) {
            System.out.println("⚠️ BUG DETECTED! Player size was " + values.size + 
                             ", forcing back to " + this.fixedSize);
            
            // Crear nuevos valores físicos con el tamaño correcto
            return new PhysicsValuesDTO(
                values.timeStamp,
                values.posX, values.posY, values.angle,
                this.fixedSize,  // 🎯 TAMAÑO FIJO E INMUTABLE
                values.speedX, values.speedY,
                values.accX, values.accY,
                values.angularSpeed,
                values.angularAcc,
                values.thrust
            );
        }
        
        return values;
    }

    // ========== RESTO DEL CÓDIGO EXISTENTE (NO CAMBIAR) ==========

    public void addWeapon(Weapon weapon) {
        this.weapons.add(weapon);

        if (this.currentWeaponIndex < 0) {
            this.currentWeaponIndex = 0;
        }
    }

    public Weapon getActiveWeapon() {
        if (this.currentWeaponIndex < 0 || this.currentWeaponIndex >= this.weapons.size()) {
            return null;
        }

        return this.weapons.get(this.currentWeaponIndex);
    }

    public int getActiveWeaponIndex() {
        if (this.currentWeaponIndex < 0 || this.currentWeaponIndex >= this.weapons.size()) {
            return -1;
        }

        return this.currentWeaponIndex;
    }

    public WeaponDto getActiveWeaponConfig() {
        Weapon weapon = getActiveWeapon();
        return (weapon != null) ? weapon.getWeaponConfig() : null;
    }

    public double getAmmoStatusPrimary() {
        return getAmmoStatus(0);
    }

    public double getAmmoStatusSecondary() {
        return getAmmoStatus(1);
    }

    public double getAmmoStatusMines() {
        return getAmmoStatus(2);
    }

    public double getAmmoStatusMissiles() {
        return getAmmoStatus(3);
    }

    private double getAmmoStatus(int weaponIndex) {
        if (weaponIndex < 0 || weaponIndex >= this.weapons.size()) {
            return 0D;
        }

        Weapon weapon = this.weapons.get(weaponIndex);
        if (weapon == null) {
            return 0D;
        }

        return weapon.getAmmoStatus();
    }

    public double getDamage() {
        return damage;
    }

    public PlayerDTO getData() {
        PlayerDTO playerData = new PlayerDTO(
                this.getBodyId(),
                "",
                this.damage,
                this.energye,
                this.shield,
                this.temperature,
                this.getActiveWeaponIndex(),
                this.getAmmoStatusPrimary(),
                this.getAmmoStatusSecondary(),
                this.getAmmoStatusMines(),
                this.getAmmoStatusMissiles(),
                this.score);
        return playerData;
    }

    public double getEnergy() {
        return energye;
    }

    public BodyToEmitDTO getProjectileConfig() {
        if (this.currentWeaponIndex < 0 || this.currentWeaponIndex >= this.weapons.size()) {
            return null;
        }

        Weapon weapon = this.weapons.get(this.currentWeaponIndex);
        if (weapon == null) {
            return null;
        }

        return weapon.getProjectileConfig();
    }

    public double getShield() {
        return shield;
    }

    public int getTemperature() {
        return this.temperature;
    }

    public void registerFireRequest() {
        if (this.currentWeaponIndex < 0 || this.currentWeaponIndex >= this.weapons.size()) {
            System.out.println("> No weapon active or no weapons!");
            return;
        }

        Weapon weapon = this.weapons.get(this.currentWeaponIndex);
        if (weapon == null) {
            return;
        }

        weapon.registerRequest();
    }

    public void reverseThrust() {
        this.thurstNow(-this.getMaxThrustForce());
    }

    public void rotateLeftOn() {
        // Rotación desactivada
    }

    public void rotateRightOn() {
        // Rotación desactivada
    }

    public void rotateOff() {
        // Rotación desactivada
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setEnergye(double energye) {
        this.energye = energye;
    }

    public void selectNextWeapon() {
        if (this.weapons.size() <= 0) {
            return;
        }

        this.currentWeaponIndex++;
        this.currentWeaponIndex = this.currentWeaponIndex % this.weapons.size();
    }

    public void selectWeapon(int weaponIndex) {
        if (weaponIndex >= 0 && weaponIndex < this.weapons.size()) {
            this.currentWeaponIndex = weaponIndex;
        }
    }

    public void setShield(double shield) {
        this.shield = shield;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public boolean mustFireNow(PhysicsValuesDTO newPhyValues) {
        if (this.currentWeaponIndex < 0 || this.currentWeaponIndex >= this.weapons.size()) {
            return false;
        }

        Weapon weapon = this.weapons.get(this.currentWeaponIndex);
        if (weapon == null) {
            return false;
        }

        double dtNanos = newPhyValues.timeStamp - this.getPhysicsValues().timeStamp;
        double dtSeconds = ((double) dtNanos) / 1_000_000_0000.0d;

        return weapon.mustFireNow(dtSeconds);
    }
}