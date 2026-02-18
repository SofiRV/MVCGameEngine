package engine.model.physics.implementations;

import static java.lang.System.nanoTime;

import engine.model.physics.core.AbstractPhysicsEngine;
import engine.model.physics.ports.PhysicsValuesDTO;

/**
 * Physics engine optimized for 2D platformer games.
 * 
 * Features:
 * - Direct horizontal movement (no inertia)
 * - Instant stop when thrust = 0
 * - Gravity with automatic ground detection
 * - Jump support (only when on ground)
 * - No rotation
 * - Preserves horizontal movement during jumps and landings
 */
public class CatPhysicsEngine extends AbstractPhysicsEngine {

    // ========== CONFIGURATION CONSTANTS ==========
    private static final double MAX_HORIZONTAL_SPEED = 300.0;  // pixels per second
    private static final double GRAVITY = 980.0;  // pixels per second² (Earth-like gravity)
    private static final double MAX_FALL_SPEED = 800.0;  // Terminal velocity
    
    // ========== GROUND DETECTION STATE ==========
    private boolean isOnGround = true;  // Start on ground
    private double lastPosY = -1.0;  // Track last Y position
    private int framesStuckY = 0;  // Count frames where Y doesn't change
    private int airborneFrames = 0;  // Frames since jump
    private static final int FRAMES_TO_CONFIRM_GROUND = 3;  // Frames stuck to confirm ground
    private static final int MIN_AIRBORNE_FRAMES = 20;  // Minimum frames in air after jump
    private static final double POSITION_EPSILON = 0.1;  // Position change threshold

    // region Constructors
    public CatPhysicsEngine(PhysicsValuesDTO phyVals) {
        super(phyVals);
        this.lastPosY = phyVals.posY;
    }
    // endregion

    // *** PUBLIC ***

    @Override
    public void angularAccelerationInc(double angularAcc) {
        // No rotation in platformer mode - do nothing
    }

    @Override
    public PhysicsValuesDTO calcNewPhysicsValues() {
        PhysicsValuesDTO phyVals = this.getPhysicsValues();
        long now = nanoTime();
        long elapsedNanos = now - phyVals.timeStamp;
        double dt = ((double) elapsedNanos) / 1_000_000_000.0d; // Nanos to seconds

        // Protection against anomalous values
        if (dt <= 0.0) {
            dt = 0.001;
        } else if (dt > 0.5) {
            dt = 0.5;
        }

        return integratePlatformerPhysics(phyVals, dt);
    }

    @Override
    public boolean isThrusting() {
        PhysicsValuesDTO phyValues = this.getPhysicsValues();
        return phyValues.thrust != 0.0d;
    }

    // region Rebounds
    @Override
    public void reboundInEast(PhysicsValuesDTO phyValues,
            double worldDim_x, double worldDim_y) {
        
        // Stop horizontal movement and snap to boundary
        PhysicsValuesDTO reboundPhyVals = new PhysicsValuesDTO(
                phyValues.timeStamp,
                0.0001d, phyValues.posY, phyValues.angle,
                phyValues.size,
                0.0, phyValues.speedY,  // Stop horizontal movement
                0.0, phyValues.accY,
                0.0, 0.0,
                phyValues.thrust);  // Preserve thrust

        this.setPhysicsValues(reboundPhyVals);
    }

    @Override
    public void reboundInWest(PhysicsValuesDTO phyValues,
            double worldDim_x, double worldDim_y) {
        
        // Stop horizontal movement and snap to boundary
        PhysicsValuesDTO reboundPhyVals = new PhysicsValuesDTO(
                phyValues.timeStamp,
                worldDim_x - 0.0001, phyValues.posY, phyValues.angle,
                phyValues.size,
                0.0, phyValues.speedY,  // Stop horizontal movement
                0.0, phyValues.accY,
                0.0, 0.0,
                phyValues.thrust);  // Preserve thrust

        this.setPhysicsValues(reboundPhyVals);
    }

    @Override
    public void reboundInNorth(PhysicsValuesDTO phyValues, 
            double worldDim_x, double worldDim_y) {
        
        // Hit ceiling - stop vertical movement
        PhysicsValuesDTO reboundPhyVals = new PhysicsValuesDTO(
                phyValues.timeStamp,
                phyValues.posX, 0.0001, phyValues.angle,
                phyValues.size,
                phyValues.speedX, 0.0,  // Stop vertical movement
                phyValues.accX, 0.0,
                0.0, 0.0,
                phyValues.thrust);  // Preserve thrust

        this.setPhysicsValues(reboundPhyVals);
        this.isOnGround = false;
        this.framesStuckY = 0;
    }

    @Override
    public void reboundInSouth(PhysicsValuesDTO phyValues, 
            double worldDim_x, double worldDim_y) {
        
        // Land on ground (world boundary)
        PhysicsValuesDTO reboundPhyVals = new PhysicsValuesDTO(
                phyValues.timeStamp,
                phyValues.posX, worldDim_y - 0.0001, phyValues.angle,
                phyValues.size,
                phyValues.speedX, 0.0,  // Stop vertical movement (landed)
                phyValues.accX, 0.0,
                0.0, 0.0,
                phyValues.thrust);  // ✅ PRESERVE THRUST

        this.setPhysicsValues(reboundPhyVals);
        
        // Landing detected
        if (airborneFrames >= MIN_AIRBORNE_FRAMES) {
            this.isOnGround = true;
            this.framesStuckY = FRAMES_TO_CONFIRM_GROUND;
            this.airborneFrames = 0;
            System.out.println("🐱✅ LANDED (rebound) at Y=" + String.format("%.1f", phyValues.posY) +
                             " thrust=" + phyValues.thrust +
                             " speedX=" + phyValues.speedX);
        }
    }
    // endregion

    @Override
    public void setAngularSpeed(double angularSpeed) {
        // No rotation in platformer mode - do nothing
    }

    // *** PUBLIC HELPER METHODS ***
    
    /**
     * Check if the cat is currently on the ground.
     */
    public boolean isOnGround() {
        return this.isOnGround;
    }
    
    /**
     * Apply a jump impulse.
     * Only works if the cat is currently on the ground.
     * PRESERVES horizontal thrust and speed during jump.
     */
    public void jump(double jumpSpeed) {
        if (!isOnGround) {
            return;
        }
        
        PhysicsValuesDTO old = this.getPhysicsValues();
        
        // 🎯 CRÍTICO: Preservar thrust y speedX durante el salto
        PhysicsValuesDTO jumped = new PhysicsValuesDTO(
                old.timeStamp,
                old.posX, old.posY, old.angle,
                old.size,
                old.speedX,    // ✅ Mantener velocidad horizontal
                -jumpSpeed,    // ✅ Aplicar velocidad vertical (salto)
                old.accX, 0.0,
                old.angularSpeed,
                old.angularAcc,
                old.thrust);   // ✅ Mantener thrust activo
        
        this.setPhysicsValues(jumped);
        
        // Resetear estado de suelo
        this.isOnGround = false;
        this.framesStuckY = 0;
        this.airborneFrames = 0;
        this.lastPosY = old.posY;
        
        System.out.println("🐱 JUMP! speedY=" + (-jumpSpeed) + 
                         " from Y=" + String.format("%.1f", old.posY) +
                         " thrust=" + old.thrust +
                         " speedX=" + old.speedX);
    }

    // *** PRIVATES ***

    /**
     * Integrates platformer physics with ground detection by position tracking.
     * Preserves horizontal thrust during all states (ground, air, landing).
     */
    private PhysicsValuesDTO integratePlatformerPhysics(PhysicsValuesDTO phyVals, double dt) {
        
        // ========== GROUND DETECTION BY POSITION ==========
        double posYChange = Math.abs(phyVals.posY - lastPosY);
        
        if (!isOnGround) {
            // En el aire
            airborneFrames++;
            
            // CONDICIONES DE ATERRIZAJE:
            boolean hasBeenAirborneEnough = airborneFrames >= MIN_AIRBORNE_FRAMES;
            boolean notRising = phyVals.speedY >= 0.0;  // No subiendo (0 o positivo)
            boolean velocityLow = phyVals.speedY < 100.0;  // Velocidad baja (frenado por colisión)
            boolean positionStuck = posYChange < POSITION_EPSILON;  // Posición no cambia
            
            // Todas las condiciones deben cumplirse para aterrizar
            if (hasBeenAirborneEnough && notRising && velocityLow && positionStuck) {
                framesStuckY++;
                if (framesStuckY >= FRAMES_TO_CONFIRM_GROUND) {
                    isOnGround = true;
                    airborneFrames = 0;
                    framesStuckY = 0;
                }
            } else {
                framesStuckY = 0;
            }
        } else {
            // En el suelo - resetear contadores
            airborneFrames = 0;
            framesStuckY = 0;
        }
        
        lastPosY = phyVals.posY;

        // ========== HORIZONTAL MOVEMENT ==========
        double newSpeedX;
        double accX = 0.0;
        
        if (phyVals.thrust > 0.0) {
            newSpeedX = MAX_HORIZONTAL_SPEED;
            accX = phyVals.thrust;
        } else if (phyVals.thrust < 0.0) {
            newSpeedX = -MAX_HORIZONTAL_SPEED;
            accX = phyVals.thrust;
        } else {
            newSpeedX = 0.0;
            accX = 0.0;
        }

        double newPosX = phyVals.posX + newSpeedX * dt;

        // ========== VERTICAL MOVEMENT ==========
        // SIEMPRE aplicar gravedad cuando NO está en suelo
        double accY = isOnGround ? 0.0 : GRAVITY;
        
        double newSpeedY;
        if (isOnGround) {
            newSpeedY = 0.0;
        } else {
            newSpeedY = phyVals.speedY + accY * dt;
            
            if (newSpeedY > MAX_FALL_SPEED) {
                newSpeedY = MAX_FALL_SPEED;
            }
        }
        
        double avgSpeedY = (phyVals.speedY + newSpeedY) * 0.5;
        double newPosY = phyVals.posY + avgSpeedY * dt;

        // ========== ANGULAR ==========
        double newAngle = phyVals.angle;
        double newAngularSpeed = 0.0;

        // ========== CREATE NEW STATE ==========
        long newTimeStamp = phyVals.timeStamp + (long) (dt * 1_000_000_000.0d);

        return new PhysicsValuesDTO(
                newTimeStamp,
                newPosX, newPosY, newAngle,
                phyVals.size,
                newSpeedX, newSpeedY,
                accX, accY,
                newAngularSpeed,
                0.0,
                phyVals.thrust  // ✅ SIEMPRE PRESERVAR THRUST
        );
    }
}