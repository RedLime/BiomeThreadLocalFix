package com.redlimerl.mcsr.redmangomemory.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;

/**
 *  For fix worldPreview memory leak
 *  Added clear method newParticles Queue when changed world
 */
@Mixin(ParticleManager.class)
public class MixinParticleManager {

    @Shadow @Final private Queue<Particle> newParticles;

    @Inject(method = "setWorld", at = @At("TAIL"))
    public void onChangeWorld(ClientWorld world, CallbackInfo ci) {
        this.newParticles.clear();
    }
}
