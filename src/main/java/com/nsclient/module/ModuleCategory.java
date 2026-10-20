package com.nsclient.module;

public enum ModuleCategory {
    COMBAT("Combat Visuals"),
    HUD("HUD"),
    PARTICLES("Particles"),
    WORLD("World"),
    PLAYER("Player"),
    CLIENT("Client");

    private final String displayName;

    ModuleCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
