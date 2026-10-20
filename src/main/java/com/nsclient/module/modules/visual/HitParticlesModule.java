package com.nsclient.module.modules.visual;

import com.nsclient.config.Setting;
import com.nsclient.module.Module;
import com.nsclient.module.ModuleCategory;
import com.nsclient.util.NSColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustParticleEffect;

public class HitParticlesModule extends Module {

    private final Setting<Integer> count = register(Setting.sliderInt("Count", 8, 1, 30));
    private final Setting<Float> particleSize = register(Setting.slider("Size", 1.0f, 0.3f, 2.5f, 0.1f));
    private final Setting<Float> speed = register(Setting.slider("Speed", 0.15f, 0.0f, 0.6f, 0.01f));
    private final Setting<Integer> color = register(Setting.color("Color", NSColors.HIT_PARTICLE));

    public HitParticlesModule() {
        super("Hit Particles", "Spawns colored particles on the target when you land a hit",
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
            double ox = (Math.random() - 0.5) * target.getWidth();
            double oy = (Math.random() - 0.2) * target.getHeight() * 0.6;
            double oz = (Math.random() - 0.5) * target.getWidth();
            double vx = (Math.random() - 0.5) * speed.getValue();
            double vy = Math.random() * speed.getValue();
            double vz = (Math.random() - 0.5) * speed.getValue();
            particleManager.addParticle(effect, x + ox, y + oy, z + oz, vx, vy, vz);
        }
    }
}
