package com.redlimerl.mcsr.redmangomemory.mixin;

import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biome;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

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

    @Redirect(method = "<init>*", at=@At(value = "INVOKE", target = "Ljava/lang/ThreadLocal;withInitial(Ljava/util/function/Supplier;)Ljava/lang/ThreadLocal;"))
    public ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCacheReplacer(Supplier<?> supplier){
        return fixTemperatureCache;
    }

//    @Redirect(method = "<init>*",
//            at = @At(value = "FIELD", target = "Lnet/minecraft/world/biome/Biome;temperatureCache:Ljava/lang/ThreadLocal;", opcode = Opcodes.PUTFIELD))
//    public void temperatureCacheReplacer(Biome biome, ThreadLocal<?> threadLocal){
//        ((AccessorBiome) (Object) biome).setTemperatureThread(fixTemperatureCache);
//    }
}
