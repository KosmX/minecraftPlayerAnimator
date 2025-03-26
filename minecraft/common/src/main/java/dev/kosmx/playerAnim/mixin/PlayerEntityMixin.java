package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(Player.class)
public abstract class PlayerEntityMixin implements IAnimatedPlayer {

    //Unique params might be renamed
    @Unique
    private final Map<ResourceLocation, IAnimation> playerAnimator$modAnimationData = new HashMap<>();
    @Unique
    private final AnimationStack playerAnimator$animationStack = playerAnimator$createAnimationStack();
    @Unique
    private final AnimationApplier playerAnimator$animationApplier = new AnimationApplier(playerAnimator$animationStack);



    @SuppressWarnings("ConstantConditions")
    @Unique
    private AnimationStack playerAnimator$createAnimationStack() {
        AnimationStack stack = new AnimationStack();
        if (AbstractClientPlayer.class.isInstance(this)) {
            PlayerAnimationFactory.ANIMATION_DATA_FACTORY.prepareAnimations((AbstractClientPlayer)(Object) this, stack, playerAnimator$modAnimationData);
            PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.invoker().registerAnimation((AbstractClientPlayer)(Object) this, stack);
        }
        return stack;
    }

    @Override
    public @NotNull AnimationStack playerAnimator$getAnimationStack() {
        return playerAnimator$animationStack;
    }

    @Override
    public AnimationApplier playerAnimator_getAnimation() {
        return playerAnimator$animationApplier;
    }

    @Override
    public @Nullable IAnimation playerAnimator_getAnimation(@NotNull ResourceLocation id) {
        return playerAnimator$modAnimationData.get(id);
    }

    @Override
    public @Nullable IAnimation playerAnimator_setAnimation(@NotNull ResourceLocation id, @Nullable IAnimation animation) {
        if (animation == null) {
            return playerAnimator$modAnimationData.remove(id);
        } else {
            return playerAnimator$modAnimationData.put(id, animation);
        }
    }

    @SuppressWarnings("ConstantConditions") // When injected into PlayerEntity, instance check can tell if a ClientPlayer or ServerPlayer
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (AbstractClientPlayer.class.isInstance(this)) {
            playerAnimator$animationStack.tick();
        }
    }
}
