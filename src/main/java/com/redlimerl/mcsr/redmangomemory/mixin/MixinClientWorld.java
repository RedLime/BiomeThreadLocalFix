package com.redlimerl.mcsr.redmangomemory.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 *
 *  NOTE THIS!
 *
 *
 *  THIS MIXIN INJECTION IS ONLY WORKS IN DEVELOPMENT ENVIRONMENT
 *  IF IT IS DONE, I'LL REMOVE THIS CLASS
 *
 *
 */
@Mixin(ClientWorld.class)
public abstract class MixinClientWorld {

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract void disconnect();

    private int i = 0;
    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment() || client.isWindowFocused() || client.isPaused()) return;

        boolean bl = this.client.isInSingleplayer();
        boolean bl2 = this.client.isConnectedToRealms();
        this.disconnect();
        if (bl) {
            this.client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
        } else {
            this.client.disconnect();
        }
        TitleScreen titleScreen = new TitleScreen();
        if (bl) {
            this.client.setScreen(titleScreen);
        } else if (bl2) {
            this.client.setScreen(new RealmsMainScreen(titleScreen));
        } else {
            this.client.setScreen(new MultiplayerScreen(titleScreen));
        }
    }

}
