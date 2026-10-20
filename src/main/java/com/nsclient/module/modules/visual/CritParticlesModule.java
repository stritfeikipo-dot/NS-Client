package com.nsclient.module.modules.visual;

import com.nsclient.config.Setting;
import com.nsclient.module.Module;
import com.nsclient.module.ModuleCategory;
import com.nsclient.util.NSColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.DustParticleEffect;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.joml.Vector3f;

public class CritParticlesModule extends Module {

    private final Setting<Integer> count = register(Setting.sliderInt("Count", 14, 1, 40));
    private final Setting<Float> particleSize = register(Setting.slider("Size", 1.3f, 0.3f, 3.0f, 0.1f));
    private final Setting<Float> speed = register(Setting.slider("Speed", 0.25f, 0.0f, 0.8f, 0.01f));
    private final Setting<Integer> color = register(Setting.color("Color", NSColors.CRIT_PARTICLE));

    public CritParticlesModule() {
        super("Crit Particles", "Spawns a brighter particle burst on critical hits", ModuleCategory.PARTICLES, true);
    }

    public void spawn(Entity target) {
        if (!isEnabled()) return;
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        int argb = color.getValue();
        Vector3f rgb = new Vector3f(
                ((argb >> 16) & 0xFF) / 255f,
                ((argb >> 8) & 0xFF) / 255f,
                (argb & 0xFF) / 255f
        );
        DustParticleEffect effect = new DustParticleEffect(rgb, particleSize.getValue());

        double x = target.getX();
        double y = target.getBodyY(0.6);
        double z = target.getZ();

        for (int i = 0; i < count.getValue(); i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = Math.random() * target.getWidth();
            double ox = Math.cos(angle) * radius;
            double oz = Math.sin(angle) * radius;
            double vx = Math.cos(angle) * speed.getValue();
            double vy = Math.random() * speed.getValue() * 1.5;
            double vz = Math.sin(angle) * speed.getValue();
            world.addParticle(effect, x + ox, y, z + oz, vx, vy, vz);
        }
    }
}
