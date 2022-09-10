package dev.kosmx.animatorTestmod.mixin;

import dev.kosmx.animatorTestmod.PlayerAnimTestmod;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void ATTACK(CallbackInfo cir) {
        System.out.println("MinecraftClientMixin - startAttack");
        PlayerAnimTestmod.playTestAnimation();
        cir.cancel();
    }
}
