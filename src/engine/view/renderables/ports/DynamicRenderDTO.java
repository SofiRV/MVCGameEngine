package engine.view.renderables.ports;

public class DynamicRenderDTO extends RenderDTO {

    public final long timeStamp;
    public final double speedX;
    public final double speedY;
    public final double accX;
    public final double accY;
    
    // 👁️ Parpadeo visual durante invulnerabilidad
    public final boolean isVisible;

    // ✅ Constructor COMPLETO con isVisible
    public DynamicRenderDTO(
            String entityId,
            double posX, double posY,
            double angle,
            double size,
            long timeStamp,
            double speedX, double speedY,
            double accX, double accY,
            long timestamp,
            boolean isVisible) {

        super(entityId, posX, posY, angle, size, timestamp);

        this.timeStamp = timeStamp;
        this.speedX = speedX;
        this.speedY = speedY;
        this.accX = accX;
        this.accY = accY;
        this.isVisible = isVisible;
    }
    
    // 🔧 Constructor de compatibilidad (por si hay otros usos sin isVisible)
    public DynamicRenderDTO(
            String entityId,
            double posX, double posY,
            double angle,
            double size,
            long timeStamp,
            double speedX, double speedY,
            double accX, double accY,
            long timestamp) {
        
        // Llama al constructor completo con isVisible = true por defecto
        this(entityId, posX, posY, angle, size, timeStamp, 
             speedX, speedY, accX, accY, timestamp, true);
    }
}