package com.redlimerl.mcsr.redmangomemory.mixin;

import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 *  For fix vanilla memory leak
 *  make temperatureCache to static field
 */
@Mixin(Biome.class)
public abstract class MixinBiome {

    private final static ThreadLocal<Long2FloatLinkedOpenHashMap> fixTemperatureCache = ThreadLocal.withInitial(() -> Util.make(() -> {
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(1024, 0.25f) {
            @Override
            protected void rehash(int i) {
            }
        };
        long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
        return long2FloatLinkedOpenHashMap;
    }));

    @Shadow protected abstract float computeTemperature(BlockPos pos);

    @Inject(method = "getTemperature(Lnet/minecraft/util/math/BlockPos;)F", at = @At("HEAD"), cancellable = true)
    public void stopLeakPlease(BlockPos blockPos, CallbackInfoReturnable<Float> cir) {
        long l = blockPos.asLong();
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = fixTemperatureCache.get();
        float f = long2FloatLinkedOpenHashMap.get(l);
        if (!Float.isNaN(f)) {
            cir.setReturnValue(f);
            return;
        }
        float g = this.computeTemperature(blockPos);
        if (long2FloatLinkedOpenHashMap.size() == 1024) {
            long2FloatLinkedOpenHashMap.removeFirstFloat();
        }
        long2FloatLinkedOpenHashMap.put(l, g);
        cir.setReturnValue(g);
    }
}
