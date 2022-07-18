package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class ClientPlayerMixin implements IAnimatedPlayer {

    //Unique params might be renamed
    @Unique
    private final AnimationStack animationStack = new AnimationStack();
    @Unique
    private final AnimationApplier animationApplier = new AnimationApplier(animationStack);



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
