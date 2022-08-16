package dev.kosmx.playerAnim.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.SetableSupplier;
import dev.kosmx.playerAnim.impl.Helper;
import dev.kosmx.playerAnim.impl.IMutableModel;
import dev.kosmx.playerAnim.impl.IBendHelper;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(HumanoidModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AgeableListModel<T> implements IMutableModel {
    @Shadow
    public ModelPart rightArm;
    @Shadow
    public ModelPart leftArm;
    @Unique
    private SetableSupplier<AnimationProcessor> animation = new SetableSupplier<>();

    @Unique
    private IBendHelper mutatedTorso;
    @Unique
    private IBendHelper mutatedRightArm;
    @Unique
    private IBendHelper mutatedLeftArm;
    @Unique
    private IBendHelper mutatedLeftLeg;
    @Unique
    private IBendHelper mutatedRightLeg;

    @Inject(method = "<init>(Ljava/util/function/Function;FFII)V", at = @At("RETURN"))
    private void initBend(Function<ResourceLocation, RenderType> texturedLayerFactory, float scale, float pivotY, int textureWidth, int textureHeight, CallbackInfo ci){
        mutatedLeftArm = IBendHelper.create(this.leftArm, true);
        mutatedLeftLeg = IBendHelper.create(this.leftLeg, false);
        mutatedRightArm = IBendHelper.create(this.rightArm, true);
        mutatedRightLeg = IBendHelper.create(this.rightLeg, false);
        mutatedTorso = IBendHelper.create(this.body, false);
        ((IUpperPartHelper) this.head).setUpperPart(true);
        ((IUpperPartHelper) this.hat).setUpperPart(true);

        mutatedTorso.addBendedCuboid(- 4, 0, - 2, 8, 12, 4, scale, Direction.DOWN);
        mutatedRightLeg.addBendedCuboid(- 2, 0, - 2, 4, 12, 4, scale, Direction.UP);
        mutatedLeftLeg.addBendedCuboid(- 2, 0, - 2, 4, 12, 4, scale, Direction.UP);

        mutatedLeftArm.addBendedCuboid(- 1, - 2, - 2, 4, 12, 4, scale, Direction.UP);
        mutatedRightArm.addBendedCuboid(- 3, - 2, - 2, 4, 12, 4, scale, Direction.UP);
    }

    @Override
    public void setEmoteSupplier(SetableSupplier<AnimationProcessor> emoteSupplier) {
        this.mutatedLeftLeg.setAnimation(emoteSupplier);
        this.mutatedRightLeg.setAnimation(emoteSupplier);
        this.mutatedLeftArm.setAnimation(emoteSupplier);
        this.mutatedRightArm.setAnimation(emoteSupplier);
        this.mutatedTorso.setAnimation(emoteSupplier);
        this.animation = emoteSupplier;
    }

    @Inject(method = "copyPropertiesTo", at = @At("RETURN"))
    private void copyMutatedAttributes(HumanoidModel<T> bipedEntityModel, CallbackInfo ci){
        if(animation != null) {
            ((IMutableModel) bipedEntityModel).setEmoteSupplier(animation);
            if (animation.get() != null && animation.get().isActive()) {
                IMutableModel thisWithMixin = (IMutableModel) bipedEntityModel;
                AnimationProcessor playedEmote = animation.get();
                thisWithMixin.getTorso().bend(playedEmote.getBend("torso"));
                thisWithMixin.getLeftArm().bend(playedEmote.getBend("leftArm"));
                thisWithMixin.getLeftLeg().bend(playedEmote.getBend("leftLeg"));
                thisWithMixin.getRightArm().bend(playedEmote.getBend("rightArm"));
                thisWithMixin.getRightLeg().bend(playedEmote.getBend("rightLeg"));
            }
        }
    }

    @Intrinsic(displace = true)
    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha){
        if(Helper.isBendEnabled() && this.animation.get() != null && this.animation.get().isActive()){
            this.headParts().forEach((part)->{
                if(! ((IUpperPartHelper) part).isUpperPart()){
                    part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }
            });
            this.bodyParts().forEach((part)->{
                if(! ((IUpperPartHelper) part).isUpperPart()){
                    part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }
            });

            SetableSupplier<AnimationProcessor> emoteSupplier = this.animation;
            matrices.pushPose();
            IBendHelper.rotateMatrixStack(matrices, emoteSupplier.get().getBend("body"));
            this.headParts().forEach((part)->{
                if(((IUpperPartHelper) part).isUpperPart()){
                    part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }
            });
            this.bodyParts().forEach((part)->{
                if(((IUpperPartHelper) part).isUpperPart()){
                    part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }
            });
            matrices.popPose();
        } else super.renderToBuffer(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Shadow public ModelPart body;

    @Shadow
    public ModelPart head;

    @Shadow
    public ModelPart hat;

    @Shadow
    public ModelPart leftLeg;

    @Shadow
    public ModelPart rightLeg;

    @Override
    public SetableSupplier<AnimationProcessor> getEmoteSupplier(){
        return animation;
    }

    @Override
    public IBendHelper getTorso() {
        return mutatedTorso;
    }

    @Override
    public IBendHelper getRightArm() {
        return mutatedRightArm;
    }

    @Override
    public IBendHelper getLeftArm() {
        return mutatedLeftArm;
    }

    @Override
    public IBendHelper getRightLeg() {
        return mutatedRightLeg;
    }

    @Override
    public IBendHelper getLeftLeg() {
        return mutatedLeftLeg;
    }

    @Override
    public void setTorso(IBendHelper v) {
        mutatedTorso = v;
    }

    @Override
    public void setRightArm(IBendHelper v) {
        mutatedRightArm= v;
    }

    @Override
    public void setLeftArm(IBendHelper v) {
        mutatedLeftArm = v;
    }

    @Override
    public void setRightLeg(IBendHelper v) {
        mutatedRightLeg = v;
    }

    @Override
    public void setLeftLeg(IBendHelper v) {
        mutatedLeftLeg = v;
    }
}
