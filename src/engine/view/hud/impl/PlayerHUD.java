package engine.view.hud.impl;

import java.awt.Color;

import engine.view.hud.core.DataHUD;

public class PlayerHUD extends DataHUD {
    public PlayerHUD() {
        super(
                new Color(78, 4, 135, 255), // Title color
                Color.GRAY, // Highlight color
                new Color(13, 13, 13, 150), // Label color
                new Color(13, 13, 13, 255), // Data color
                25, 12, 16);

        this.addItems();
    }

    private void addItems() {
        this.addTitle("PLAYER STATUS");
        this.addSkipValue(); // Entity ID
        this.addSkipValue(); // Player name
        this.addBarItem("Health", 125, true);  // ✅ Mostrar porcentaje
        this.addTitle("Weapons");
        this.addSkipValue(); // Active weapon
        this.addBarItem("Guns", 125, false);
        this.addBarItem("Burst", 125, false);
        this.addBarItem("Mines", 125, false);
        this.addBarItem("Missiles", 125, false);
        this.prepareHud();
    }
}