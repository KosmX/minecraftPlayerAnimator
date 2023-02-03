package dev.kosmx.playerAnim.mixin.first_person;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kosmx.playerAnim.api.first_person.FirstPersonAnimation;
import dev.kosmx.playerAnim.api.first_person.FirstPersonRenderState;
import dev.kosmx.playerAnim.impl.animation.first_person.IAnimatedFirstPerson;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Redirect(method = "renderLevel", at = @At(ordinal = 0, value = "INVOKE", target = "Lnet/minecraft/client/Camera;isDetached()Z"))
    private boolean renderInFirstPerson(Camera instance) {
        if (Minecraft.getInstance().player.isSleeping()) {
            // Use vanilla behaviour special cases
            return instance.isDetached();
        }
        // This `return true` kicks off rendering of the player model in first person
        return true;
    }

    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    private void dontRenderEntity_Begin(Entity entity, double cameraX, double cameraY, double cameraZ,
                                        float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, CallbackInfo ci) {
        if(entity instanceof Player player) {
            if (player.isSleeping()) {
                return;
            }
        }

        Optional<FirstPersonAnimation> currentAnimation = Optional.empty();
        if (entity instanceof IAnimatedFirstPerson animated) {
             currentAnimation = animated.getActiveFirstPersonAnimation(tickDelta);
        }

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (entity == camera.getEntity() && !camera.isDetached()) {
            if(currentAnimation.isPresent()) {
                // Mark this render cycle as First Person Render, with given configuration
                FirstPersonRenderState.setFirstPersonRenderCycle(currentAnimation.get());
                // Do nothing -> Fallthrough (allow render)
                return;
            } else {
                // Don't render anything
                ci.cancel();
            }
        } else {
            // Do nothing -> Fallthrough (allow render)
            return;
        }
    }

    @Inject(method = "renderEntity", at = @At("TAIL"), cancellable = true)
    private void dontRenderEntity_End(Entity entity, double cameraX, double cameraY, double cameraZ,
                                      float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, CallbackInfo ci) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (entity == camera.getEntity()) {
            FirstPersonRenderState.clearFirstPersonRenderCycle(); // Unmark this render cycle
        }
    }
}
