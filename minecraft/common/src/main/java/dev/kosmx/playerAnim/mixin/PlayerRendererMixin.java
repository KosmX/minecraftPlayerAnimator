package dev.kosmx.playerAnim.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.IPlayerAnimationState;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel entityModel, float f, Void v) {
        super(context, entityModel, f);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("HEAD"))
    private void modifyRenderState(AbstractClientPlayer abstractClientPlayer, PlayerRenderState playerRenderState, float f, CallbackInfo ci) {
        AnimationApplier animation = ((IAnimatedPlayer) abstractClientPlayer).playerAnimator_getAnimation();
        animation.setTickDelta(f);

        ((IPlayerAnimationState)playerRenderState).playerAnimator$setAnimationApplier(animation);
        ((IPlayerAnimationState)playerRenderState).playerAnimator$setLocalPlayer(abstractClientPlayer.isLocalPlayer());
        ((IPlayerAnimationState)playerRenderState).playerAnimator$setLocalPlayer(abstractClientPlayer == Minecraft.getInstance().cameraEntity);
    }

    @Inject(method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V", at = @At("RETURN"))
    private void applyBodyTransforms(PlayerRenderState playerRenderState, PoseStack matrixStack, float f, float g, CallbackInfo ci){
        var animationPlayer = ((IPlayerAnimationState) playerRenderState).playerAnimator$getAnimationApplier();
        if(animationPlayer.isActive()){

            //These are additive properties
            Vec3f vec3e = animationPlayer.get3DTransform(PartKey.BODY, TransformType.SCALE,
                    new Vec3f(ModelPart.DEFAULT_SCALE, ModelPart.DEFAULT_SCALE, ModelPart.DEFAULT_SCALE)
            );
            matrixStack.scale(vec3e.getX(), vec3e.getY(), vec3e.getZ());
            Vec3f vec3d = animationPlayer.get3DTransform(PartKey.BODY, TransformType.POSITION, Vec3f.ZERO);
            matrixStack.translate(vec3d.getX(), vec3d.getY() + 0.7, vec3d.getZ());
            Vec3f vec3f = animationPlayer.get3DTransform(PartKey.BODY, TransformType.ROTATION, Vec3f.ZERO);
            matrixStack.mulPose(Axis.ZP.rotation(vec3f.getZ()));    //roll
            matrixStack.mulPose(Axis.YP.rotation(vec3f.getY()));    //pitch
            matrixStack.mulPose(Axis.XP.rotation(vec3f.getX()));    //yaw
            matrixStack.translate(0, - 0.7d, 0);
        }
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("TAIL"))
    private void extractRenderState(AbstractClientPlayer abstractClientPlayer, PlayerRenderState playerRenderState, float f, CallbackInfo ci) {
        ((IPlayerAnimationState)playerRenderState).playerAnimator$setCameraEntity(abstractClientPlayer == Minecraft.getInstance().cameraEntity);
    }
}
