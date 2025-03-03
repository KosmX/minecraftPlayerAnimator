package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.entity.EquipmentSlot.CHEST;

@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {

    protected ArmorFeatureRendererMixin(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initInject(RenderLayerParent<T, M> context, A leggingsModel, A bodyModel, ModelManager modelManager, CallbackInfo ci) {
        ((IUpperPartHelper) this).setUpperPart(false);
    }

    @Inject(
            method = "setPartVisibility",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyArmorVisibility(A humanoidModel, EquipmentSlot equipmentSlot, CallbackInfo ci) {
        AnimationApplier emote = ((IAnimatedPlayer) Minecraft.getInstance().player).playerAnimator_getAnimation();
        if (emote.isActive() && emote.getFirstPersonMode() == FirstPersonMode.THIRD_PERSON_MODEL && emote.getFirstPersonConfiguration().isShowArmor() && FirstPersonMode.isFirstPersonPass()) {
            humanoidModel.setAllVisible(false);
            if (equipmentSlot == CHEST) {
                humanoidModel.rightArm.visible = emote.getFirstPersonConfiguration().isShowRightArm();
                humanoidModel.leftArm.visible = emote.getFirstPersonConfiguration().isShowLeftArm();
                humanoidModel.body.visible = false;
            }
            ci.cancel();
        }
    }
}
