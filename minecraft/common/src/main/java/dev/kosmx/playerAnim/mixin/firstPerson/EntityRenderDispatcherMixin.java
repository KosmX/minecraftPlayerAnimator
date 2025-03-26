package dev.kosmx.playerAnim.mixin.firstPerson;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.impl.IPlayerAnimationState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private static void renderShadow_HEAD_PlayerAnimator(PoseStack poseStack, MultiBufferSource multiBufferSource, EntityRenderState entityRenderState, float f, float g, LevelReader levelReader, float h, CallbackInfo ci) {
        if (entityRenderState instanceof IPlayerAnimationState state && state.playerAnimator$isLocalPlayer() && FirstPersonMode.isFirstPersonPass()) {
            // Shadow doesn't render in first person,
            // so we don't want to make it appear during first person animation
            ci.cancel();
        }
    }
}
