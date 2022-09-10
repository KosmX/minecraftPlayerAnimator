package dev.kosmx.animatorTestmod.mixin;


import dev.kosmx.animatorTestmod.PlayerAnimTestmod;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class ClientPlayerMixin {


    private void playTestAnimation(InteractionHand interactionHand, CallbackInfo ci) {
        PlayerAnimTestmod.playTestAnimation();
    }
}
