package dev.kosmx.playerAnim.mixin;

import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerEntityMixin implements IAnimatedPlayer {

    //Unique params might be renamed
    @Unique
    private final AnimationStack animationStack = new AnimationStack();
    @Unique
    private final AnimationApplier animationApplier = new AnimationApplier(animationStack);

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "<init>", at = @At("TAIL"))
    private void registerEventInvoker(Level level, BlockPos blockPos, float f, GameProfile gameProfile, ProfilePublicKey profilePublicKey, CallbackInfo ci) {
        if (AbstractClientPlayer.class.isInstance(this)) PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.invoker().registerAnimation((AbstractClientPlayer)(Object) this, ((IAnimatedPlayer)this).getAnimationStack());
    }

    @Override
    public AnimationStack getAnimationStack() {
        return animationStack;
    }

    @Override
    public AnimationApplier getAnimation() {
        return animationApplier;
    }


    @SuppressWarnings("ConstantConditions") // When injected into PlayerEntity, instance check can tell if a ClientPlayer or ServerPlayer
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (AbstractClientPlayer.class.isInstance(this)) {
            animationStack.tick();
        }
    }
}
