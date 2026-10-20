package com.nsclient.module.modules.visual;

import com.nsclient.config.Setting;
import com.nsclient.module.Module;
import com.nsclient.module.ModuleCategory;
import com.nsclient.util.NSColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustParticleEffect;

public class KillEffectsModule extends Module {

    private final Setting<Integer> count = register(Setting.sliderInt("Count", 30, 5, 80));
    private final Setting<Float> particleSize = register(Setting.slider("Size", 1.6f, 0.5f, 3.5f, 0.1f));
    private final Setting<Float> speed = register(Setting.slider("Speed", 0.35f, 0.0f, 1.0f, 0.01f));
    private final Setting<Integer> color = register(Setting.color("Color", NSColors.KILL_PARTICLE));

    public KillEffectsModule() {
        super("Kill Effects", "Plays a larger particle burst when you finish off a target",
                ModuleCategory.PARTICLES, true);
    }

    public void spawn(Entity target) {
        if (!isEnabled()) return;
        ParticleManager particleManager = MinecraftClient.getInstance().particleManager;
        if (particleManager == null) return;

        int rgb = color.getValue() & 0x00FFFFFF;
        DustParticleEffect effect = new DustParticleEffect(rgb, particleSize.getValue());

        double x = target.getX();
        double y = target.getBodyY(0.5);
        double z = target.getZ();

        for (int i = 0; i < count.getValue(); i++) {
            double angle = Math.random() * Math.PI * 2;
            double pitch = Math.random() * Math.PI - Math.PI / 2;
            double vx = Math.cos(angle) * Math.cos(pitch) * speed.getValue();
            double vy = Math.sin(pitch) * speed.getValue();
            double vz = Math.sin(angle) * Math.cos(pitch) * speed.getValue();
            particleManager.addParticle(effect, x, y, z, vx, vy, vz);
        }
    }
}
