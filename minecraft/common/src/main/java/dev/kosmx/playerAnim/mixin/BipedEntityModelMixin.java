package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.impl.IMutableModel;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(HumanoidModel.class)
public abstract class BipedEntityModelMixin<T extends HumanoidRenderState> extends EntityModel<T> implements IMutableModel {
    @Final
    @Shadow
    public ModelPart rightArm;
    @Final
    @Shadow
    public ModelPart leftArm;

    @Unique
    @NotNull
    private AnimationProcessor playerAnimator$animation = new AnimationApplier(null);

    private BipedEntityModelMixin(Void v, ModelPart modelPart) {
        super(modelPart);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/model/geom/ModelPart;Ljava/util/function/Function;)V", at = @At("RETURN"))
    private void initBend(ModelPart modelPart, Function<ResourceLocation, RenderType> function, CallbackInfo ci){
        ((IUpperPartHelper)rightArm).playerAnimator$setUpperPart(true);
        ((IUpperPartHelper)leftArm).playerAnimator$setUpperPart(true);
        ((IUpperPartHelper)head).playerAnimator$setUpperPart(true);
        ((IUpperPartHelper)hat).playerAnimator$setUpperPart(true);
    }

    @Override
    public void playerAnimator$setAnimation(@NotNull AnimationProcessor emoteSupplier){
        this.playerAnimator$animation = emoteSupplier;
    }

    @Inject(method = "copyPropertiesTo", at = @At("RETURN"))
    private void copyMutatedAttributes(HumanoidModel<T> bipedEntityModel, CallbackInfo ci){
        ((IMutableModel) bipedEntityModel).playerAnimator$setAnimation(playerAnimator$animation);
    }

    @Final
    @Shadow public ModelPart body;

    @Shadow @Final public ModelPart head;

    @Shadow @Final public ModelPart hat;

}
