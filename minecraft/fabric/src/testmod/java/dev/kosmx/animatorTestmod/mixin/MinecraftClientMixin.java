package dev.kosmx.animatorTestmod.mixin;

import dev.kosmx.animatorTestmod.AnimationRegistry;
import dev.kosmx.animatorTestmod.PlayerAnimTestmod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Shadow public abstract ResourceManager getResourceManager();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void finishInit(GameConfig gameConfig, CallbackInfo ci) {
        AnimationRegistry.load(getResourceManager());
    }

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void ATTACK(CallbackInfoReturnable<Boolean> cir) {
        System.out.println("MinecraftClientMixin - startAttack");
        PlayerAnimTestmod.playTestAnimation();
        cir.setReturnValue(true);
    }
}
