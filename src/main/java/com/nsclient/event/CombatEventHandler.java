package com.nsclient.event;

import com.nsclient.module.ModuleManager;
import com.nsclient.module.modules.combat.ComboCounterModule;
import com.nsclient.module.modules.combat.HitmarkerModule;
import com.nsclient.module.modules.visual.CritParticlesModule;
import com.nsclient.module.modules.visual.HitParticlesModule;
import com.nsclient.module.modules.visual.KillEffectsModule;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Hooks client-side combat visuals onto the attack action.
 *
 * IMPORTANT (and disclosed honestly in the README): AttackEntityCallback
 * fires when the player *attempts* a left-click attack on an entity, not
 * necessarily on a confirmed server-side hit. For a purely visual mod this
 * is a reasonable approximation (the attack almost always connects at melee
 * range against a targeted entity), but it is not a guaranteed "hit landed"
 * signal.
 */
public final class CombatEventHandler {

    private static final Map<Integer, LivingEntity> recentlyAttacked = new HashMap<>();

    private CombatEventHandler() {
    }

    public static void register() {
        AttackEntityCallback.EVENT.register(CombatEventHandler::onAttack);
        ClientTickEvents.END_CLIENT_TICK.register(CombatEventHandler::onTick);
    }

    private static ActionResult onAttack(net.minecraft.entity.player.PlayerEntity player, net.minecraft.world.World world,
                                          net.minecraft.util.Hand hand, Entity target,
                                          net.minecraft.util.hit.EntityHitResult hitResult) {
        if (!(target instanceof LivingEntity living)) return ActionResult.PASS;
        if (!world.isClient) return ActionResult.PASS;

        boolean crit = player.fallDistance > 0.0F && !player.isOnGround() && !player.isClimbing();

        ModuleManager mm = ModuleManager.get();
        get(mm, HitmarkerModule.class).ifPresent(m -> m.trigger(crit));
        get(mm, ComboCounterModule.class).ifPresent(ComboCounterModule::onHit);
        get(mm, HitParticlesModule.class).ifPresent(m -> m.spawn(living));
        if (crit) {
            get(mm, CritParticlesModule.class).ifPresent(m -> m.spawn(living));
        }

        recentlyAttacked.put(living.getId(), living);
        return ActionResult.PASS;
    }

    private static void onTick(net.minecraft.client.MinecraftClient client) {
        if (recentlyAttacked.isEmpty()) return;
        ModuleManager mm = ModuleManager.get();

        recentlyAttacked.entrySet().removeIf(entry -> {
            LivingEntity entity = entry.getValue();
            if (entity.isDead() || entity.isRemoved()) {
                get(mm, KillEffectsModule.class).ifPresent(m -> m.spawn(entity));
                return true;
            }
            return false;
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> java.util.Optional<T> get(ModuleManager mm, Class<T> type) {
        for (var module : mm.getModules()) {
            if (type.isInstance(module)) return java.util.Optional.of((T) module);
        }
        return java.util.Optional.empty();
    }
}
