package dev.kosmx.playerAnim.mixin.first_person;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kosmx.playerAnim.api.first_person.FirstPersonRenderState;
import dev.kosmx.playerAnim.impl.animation.first_person.IAnimatedFirstPerson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Inject(method = "renderHandsWithItems", at = @At("HEAD"), cancellable = true)
    private void renderHandsWithItems_HEAD_PlayerAnimator(float tickDelta, PoseStack matrices, MultiBufferSource.BufferSource vertexConsumers,
                                LocalPlayer player, int light, CallbackInfo ci) {
        var currentAnimation = ((IAnimatedFirstPerson) player).getActiveFirstPersonAnimation(tickDelta);
        if (currentAnimation.isPresent()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void renderItem_HEAD(LivingEntity entity, ItemStack stack, ItemTransforms.TransformType renderMode, boolean leftHanded, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (entity != Minecraft.getInstance().getCameraEntity()) {
            return;
        }
        if (FirstPersonRenderState.isRenderCycleFirstPerson()) {
            var animation = FirstPersonRenderState.getRenderCycleData();
            var isMainHandStack = entity.getMainHandItem() == stack;
            // Hiding held items based on config
            if (isMainHandStack) {
                if (!animation.config().showRightItem()) {
                    ci.cancel();
                }
            } else {
                if (!animation.config().showLeftItem()) {
                    ci.cancel();
                }
            }
        }
    }
}
