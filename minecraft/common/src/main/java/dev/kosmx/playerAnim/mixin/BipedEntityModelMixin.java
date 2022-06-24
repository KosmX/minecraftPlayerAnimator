package dev.kosmx.playerAnim.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.kosmx.playerAnim.core.impl.AnimationPlayer;
import dev.kosmx.playerAnim.core.util.SetableSupplier;
import dev.kosmx.playerAnim.impl.IMutableModel;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import io.github.kosmx.bendylib.ModelPartAccessor;
import io.github.kosmx.bendylib.impl.BendableCuboid;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(HumanoidModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AgeableListModel<T> implements IMutableModel {

    @Final
    @Shadow
    public ModelPart rightLeg;
    @Final
    @Shadow
    public ModelPart rightArm;
    @Final
    @Shadow
    public ModelPart leftLeg;
    @Final
    @Shadow
    public ModelPart leftArm;
    @Unique
    private SetableSupplier<AnimationPlayer> animation = new SetableSupplier<>();

    @Inject(method = "<init>(Lnet/minecraft/client/model/geom/ModelPart;Ljava/util/function/Function;)V", at = @At("RETURN"))
    private void initBend(ModelPart modelPart, Function<ResourceLocation, RenderType> function, CallbackInfo ci){
        ModelPartAccessor.optionalGetCuboid(modelPart.getChild("body"), 0).ifPresent(mutableModelPart -> mutableModelPart.registerMutator("bend", data -> new BendableCuboid.Builder().setDirection(Direction.DOWN).build(data)));
        ModelPartAccessor.optionalGetCuboid(modelPart.getChild("right_arm"), 0).ifPresent(mutableModelPart -> mutableModelPart.registerMutator("bend", data -> new BendableCuboid.Builder().setDirection(Direction.UP).build(data)));
        ModelPartAccessor.optionalGetCuboid(modelPart.getChild("left_arm"), 0).ifPresent(mutableModelPart -> mutableModelPart.registerMutator("bend", data -> new BendableCuboid.Builder().setDirection(Direction.UP).build(data)));
        ModelPartAccessor.optionalGetCuboid(modelPart.getChild("right_leg"), 0).ifPresent(mutableModelPart -> mutableModelPart.registerMutator("bend", data -> new BendableCuboid.Builder().setDirection(Direction.UP).build(data)));
        ModelPartAccessor.optionalGetCuboid(modelPart.getChild("left_leg"), 0).ifPresent(mutableModelPart -> mutableModelPart.registerMutator("bend", data -> new BendableCuboid.Builder().setDirection(Direction.UP).build(data)));
        ((IUpperPartHelper)rightArm).setUpperPart(true);
        ((IUpperPartHelper)leftArm).setUpperPart(true);
        ((IUpperPartHelper)head).setUpperPart(true);
        ((IUpperPartHelper)hat).setUpperPart(true);
    }

    @Override
    public void setEmoteSupplier(SetableSupplier<AnimationPlayer> emoteSupplier){
        this.animation = emoteSupplier;
    }

    @Inject(method = "copyPropertiesTo", at = @At("RETURN"))
    private void copyMutatedAttributes(HumanoidModel<T> bipedEntityModel, CallbackInfo ci){
        if(animation != null) {
            ((IMutableModel) bipedEntityModel).setEmoteSupplier(animation);
        }
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha){
        if(this.animation.get() != null && this.animation.get().isActive()){
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

            SetableSupplier<AnimationPlayer> emoteSupplier = this.animation;
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
        }else super.renderToBuffer(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Final
    @Shadow public ModelPart body;

    @Shadow @Final public ModelPart head;

    @Shadow @Final public ModelPart hat;

    @Override
    public IBendHelper getTorso(){
        return IBendHelper.createNew(body);
    }

    @Override
    public IBendHelper getRightArm(){
        return IBendHelper.createNew(rightArm);
    }

    @Override
    public IBendHelper getLeftArm(){
        return IBendHelper.createNew(leftArm);
    }

    @Override
    public IBendHelper getRightLeg(){
        return IBendHelper.createNew(rightLeg);
    }

    @Override
    public IBendHelper getLeftLeg(){
        return IBendHelper.createNew(leftLeg);
    }

    @Override
    public SetableSupplier<AnimationPlayer> getEmoteSupplier(){
        return animation;
    }
}
