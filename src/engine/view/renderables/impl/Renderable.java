package engine.view.renderables.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import engine.utils.images.ImageCache;
import engine.view.renderables.ports.RenderDTO;

public class Renderable {

    private final String entityId;
    private final String assetId;
    private final ImageCache cache;

    private long lastFrameSeen;
    private RenderDTO renderData = null;
    private BufferedImage image = null;
    protected boolean showCollisionBox = true;

    public Renderable(RenderDTO renderData, String assetId, ImageCache cache, long currentFrame) {
        if (assetId == null || assetId.isEmpty()) {
            throw new IllegalArgumentException("Asset ID not set");
        }
        if (cache == null) {
            throw new IllegalArgumentException("Image cache not set");
        }

        this.entityId = renderData.entityId;
        this.assetId = assetId;
        this.lastFrameSeen = currentFrame;
        this.renderData = renderData;
        this.cache = cache;
        this.updateImageFromCache(this.assetId, (int) renderData.size, renderData.angle);
    }

    public Renderable(String entityId, String assetId, ImageCache cache, long currentFrame) {
        if (entityId == null || entityId.isEmpty()) {
            throw new IllegalArgumentException("Entity ID not set");
        }
        if (assetId == null || assetId.isEmpty()) {
            throw new IllegalArgumentException("Asset ID not set");
        }
        if (cache == null) {
            throw new IllegalArgumentException("Image cache not set");
        }

        this.entityId = entityId;
        this.assetId = assetId;
        this.lastFrameSeen = currentFrame;
        this.cache = cache;
        this.image = null;
        this.renderData = null;
    }

    /**
     * PUBLICS
     */
    public long getLastFrameSeen() {
        return this.lastFrameSeen;
    }

    public String getAssetId() {
        return this.assetId;
    }

    public String getEntityId() {
        return this.entityId;
    }

    public RenderDTO getRenderData() {
        return this.renderData;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public void setShowCollisionBox(boolean show) {
        this.showCollisionBox = show;
    }

    public void update(RenderDTO renderInfo, long currentFrame) {
        this.updateImageFromCache(this.assetId, (int) renderInfo.size, renderInfo.angle);
        this.lastFrameSeen = currentFrame;
        this.renderData = renderInfo;
    }

    public void paint(Graphics2D g, long currentFrame) {

            // ✅ FILTRO: No renderizar colliders invisibles
        if (this.assetId.startsWith("collider_") || 
            this.assetId.equals("invisible_collider") ||
            this.assetId.equals("invisible")){
            
            // Solo mostrar caja de debug si está activado
            if (this.showCollisionBox) {
                // Cambiar color para colliders invisibles
                Color oldColor = g.getColor();
                g.setColor(new Color(255, 0, 255, 100)); // Magenta para colliders
                this.paintCollisionBox(g);
                g.setColor(oldColor);
            }
            return; // ← NO DIBUJAR SPRITE
        }

        if (this.image == null) {
            return;
        }

        // Save the original (NOT rotated) transform
        AffineTransform old = g.getTransform();

        final double posX = this.renderData.posX;
        final double posY = this.renderData.posY;
        final double angleDeg = this.renderData.angle;

        // Using the REAL size of the sprite for the offset
        final double halfW = this.image.getWidth(null) * 0.5;
        final double halfH = this.image.getHeight(null) * 0.5;

        final int drawX = (int) (posX - halfW);
        final int drawY = (int) (posY - halfH);


        g.rotate(Math.toRadians(angleDeg), posX, posY);

        g.drawImage(this.image, drawX, drawY, null);

        // Restore original (NOT rotated) transform
        g.setTransform(old);

        if (this.showCollisionBox) {
            this.paintCollisionBox(g);
        }
    }

    public void updateImageFromCache(RenderDTO entityInfo) {
        this.updateImageFromCache(this.assetId, (int) entityInfo.size, entityInfo.angle);
    }

    private boolean updateImageFromCache(String assetId, int size, double angle) {
        boolean imageNeedsUpdate = this.image == null
                || this.renderData == null
                || !this.assetId.equals(assetId)
                || this.renderData.size != size
                || (int) this.renderData.angle != (int) angle;

        if (imageNeedsUpdate) {
            int normalizedAngle = ((int) angle % 360 + 360) % 360;
            this.image = this.cache.getImage(normalizedAngle, assetId, size);

            return true; // ====
        }

        return false;
    }


    protected void paintCollisionBox(Graphics2D g) {
        if (this.renderData == null) {
            return;
        }
        
        final double posX = this.renderData.posX;
        final double posY = this.renderData.posY;
        final double size = this.renderData.size;
        
        // Aplicar el mismo factor de margen que en la detección (90%)
        final double halfSize = (size * 0.5) * 0.9;
        
        // Guardar color y stroke originales
        Color oldColor = g.getColor();
        BasicStroke oldStroke = (BasicStroke) g.getStroke();
        
        // Dibujar rectángulo de colisión (verde semi-transparente)
        g.setColor(new Color(0, 255, 0, 100)); // Verde con 100/255 alpha
        g.setStroke(new BasicStroke(2.0f));
        
        // Calcular bordes del AABB
        final int left   = (int) (posX - halfSize);
        final int top    = (int) (posY - halfSize);
        final int width  = (int) (halfSize * 2);
        final int height = (int) (halfSize * 2);
        
        // Dibujar rectángulo
        g.drawRect(left, top, width, height);
        
        // Dibujar cruz amarilla en el centro (posX, posY)
        g.setColor(new Color(255, 255, 0, 200)); // Amarillo
        g.setStroke(new BasicStroke(1.5f));
        int crossSize = 8;
        g.drawLine((int) posX - crossSize, (int) posY, (int) posX + crossSize, (int) posY);
        g.drawLine((int) posX, (int) posY - crossSize, (int) posX, (int) posY + crossSize);
        
        // Dibujar tamaño y posición (opcional, útil para debug)
        g.setColor(new Color(255, 255, 255, 220)); // Blanco
        g.drawString(
            String.format("%.0f", size), 
            (int) posX + 10, 
            (int) posY - 10
        );
        
        // Restaurar color y stroke
        g.setColor(oldColor);
        g.setStroke(oldStroke);
    }
}
