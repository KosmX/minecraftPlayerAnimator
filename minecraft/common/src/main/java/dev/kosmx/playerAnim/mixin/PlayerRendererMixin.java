package dev.kosmx.playerAnim.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.IPlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
    }
    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void hideBonesInFirstPerson(AbstractClientPlayer entity,
                                        float f, float g, PoseStack matrixStack,
                                        MultiBufferSource vertexConsumerProvider,
                                        int i, CallbackInfo ci) {
        if (FirstPersonMode.isFirstPersonPass()) {
            var animationApplier = ((IAnimatedPlayer) entity).playerAnimator_getAnimation();
            var config = animationApplier.getFirstPersonConfiguration();

            if (entity == Minecraft.getInstance().getCameraEntity()) {
                // Hiding all parts, because they should not be visible in first person
                setAllPartsVisible(false);
                // Showing arms based on configuration
                var showRightArm = config.isShowRightArm();
                var showLeftArm = config.isShowLeftArm();
                this.model.rightArm.visible = showRightArm;
                this.model.rightSleeve.visible = showRightArm;
                this.model.leftArm.visible = showLeftArm;
                this.model.leftSleeve.visible = showLeftArm;
            }
        }

        // No `else` case needed to show parts, since the default state should be correct already
    }

    @Unique
    private void setAllPartsVisible(boolean visible) {
        this.model.head.visible = visible;
        this.model.body.visible = visible;
        this.model.leftLeg.visible = visible;
        this.model.rightLeg.visible = visible;
        this.model.rightArm.visible = visible;
        this.model.leftArm.visible = visible;

        this.model.hat.visible = visible;
        this.model.leftSleeve.visible = visible;
        this.model.rightSleeve.visible = visible;
        this.model.leftPants.visible = visible;
        this.model.rightPants.visible = visible;
        this.model.jacket.visible = visible;
    }


    @Inject(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At("RETURN"))
    private void applyBodyTransforms(AbstractClientPlayer abstractClientPlayerEntity, PoseStack matrixStack, float f, float bodyYaw, float tickDelta, CallbackInfo ci){
        var animationPlayer = ((IAnimatedPlayer) abstractClientPlayerEntity).playerAnimator_getAnimation();
        animationPlayer.setTickDelta(tickDelta);
        if(animationPlayer.isActive()){

            //These are additive properties
            Vec3f vec3d = animationPlayer.get3DTransform("body", TransformType.POSITION, Vec3f.ZERO);
            matrixStack.translate(vec3d.getX(), vec3d.getY() + 0.7, vec3d.getZ());
            Vec3f vec3f = animationPlayer.get3DTransform("body", TransformType.ROTATION, Vec3f.ZERO);
            matrixStack.mulPose(Axis.ZP.rotation(vec3f.getZ()));    //roll
            matrixStack.mulPose(Axis.YP.rotation(vec3f.getY()));    //pitch
            matrixStack.mulPose(Axis.XP.rotation(vec3f.getX()));    //yaw
            matrixStack.translate(0, - 0.7d, 0);
        }
    }

    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/PlayerModel;setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"))
    private void notifyModelOfFirstPerson(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, AbstractClientPlayer abstractClientPlayer, ModelPart modelPart, ModelPart modelPart2, CallbackInfo ci) {
        if (this.getModel() instanceof IPlayerModel playerModel && !((IAnimatedPlayer)abstractClientPlayer).playerAnimator_getAnimation().getFirstPersonMode().isEnabled()) {
            playerModel.playerAnimator_prepForFirstPersonRender();
        }
    }
}
