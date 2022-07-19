package dev.kosmx.playerAnim.mixin;

import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayer.class)
public class ClientPlayerMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void animationRegisterInvoker(ClientLevel clientLevel, GameProfile gameProfile, ProfilePublicKey profilePublicKey, CallbackInfo ci) {
        PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.invoker().registerAnimation((AbstractClientPlayer)(Object) this, ((IAnimatedPlayer)this).getAnimationStack());
    }
}
