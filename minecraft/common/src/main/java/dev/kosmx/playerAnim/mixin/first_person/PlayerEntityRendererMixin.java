package dev.kosmx.playerAnim.mixin.first_person;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kosmx.playerAnim.api.first_person.FirstPersonRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayer,
        PlayerModel<AbstractClientPlayer>> {

    public PlayerEntityRendererMixin(EntityRendererProvider.Context ctx,
                                     PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void hideBonesInFirstPerson(AbstractClientPlayer entity,
                                        float f, float g, PoseStack matrixStack,
                                        MultiBufferSource vertexConsumerProvider,
                                        int i, CallbackInfo ci) {
        var animation = FirstPersonRenderState.getRenderCycleData();
        if (animation == null) {
            return;
        }

        if (entity == Minecraft.getInstance().getCameraEntity()) {
            // Hiding all parts, because they should not be visible in first person
            setAllPartsVisible(false);
            // Showing arms based on configuration
            var showRightArm = animation.config().showRightArm();
            var showLeftArm = animation.config().showLeftArm();
            this.model.rightArm.visible = showRightArm;
            this.model.rightSleeve.visible = showRightArm;
            this.model.leftArm.visible = showLeftArm;
            this.model.leftSleeve.visible = showLeftArm;
        }
        // No `else` case needed to show parts, since the default state should be correct already
    }

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
}