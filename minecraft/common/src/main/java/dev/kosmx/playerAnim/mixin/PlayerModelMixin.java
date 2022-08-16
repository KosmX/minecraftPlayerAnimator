package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.SetableSupplier;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.IMutableModel;
import dev.kosmx.playerAnim.impl.IPlayerModel;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
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

@Mixin(value = PlayerModel.class, priority = 2000)//Apply after NotEnoughAnimation's inject
public class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> implements IPlayerModel {
    @Shadow
    @Final
    public ModelPart jacket;
    @Shadow
    @Final
    public ModelPart rightSleeve;
    @Shadow
    @Final
    public ModelPart leftSleeve;
    @Shadow @Final public ModelPart rightPants;
    @Shadow @Final public ModelPart leftPants;
    @Unique
    private final SetableSupplier<AnimationProcessor> emoteSupplier = new SetableSupplier<>();

    @Unique
    private boolean firstPersonNext = false;

    public PlayerModelMixin(ModelPart modelPart, Function<ResourceLocation, RenderType> function) {
        super(modelPart, function);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initBendableStuff(ModelPart modelPart, boolean bl, CallbackInfo ci){
        IMutableModel thisWithMixin = (IMutableModel) this;
        emoteSupplier.set(null);

        thisWithMixin.setEmoteSupplier(emoteSupplier);

        addBendMutator(this.jacket, Direction.DOWN);
        addBendMutator(this.rightPants, Direction.UP);
        addBendMutator(this.rightSleeve, Direction.UP);
        addBendMutator(this.leftPants, Direction.UP);
        addBendMutator(this.leftSleeve, Direction.UP);

        ((IUpperPartHelper)rightSleeve).setUpperPart(true);
        ((IUpperPartHelper)leftSleeve).setUpperPart(true);

    }

    @Unique
    private void addBendMutator(ModelPart part, Direction d){
        IBendHelper.INSTANCE.initBend(part, d);
    }

    @Unique
    private void setDefaultPivot(){
        this.leftLeg.setPos(1.9F, 12.0F, 0.0F);
        this.rightLeg.setPos(- 1.9F, 12.0F, 0.0F);
        this.head.setPos(0.0F, 0.0F, 0.0F);
        this.rightArm.z = 0.0F;
        this.rightArm.x = - 5.0F;
        this.leftArm.z = 0.0F;
        this.leftArm.x = 5.0F;
        this.body.xRot = 0.0F;
        this.rightLeg.z = 0.1F;
        this.leftLeg.z = 0.1F;
        this.rightLeg.y = 12.0F;
        this.leftLeg.y = 12.0F;
        this.head.y = 0.0F;
        this.head.zRot = 0f;
        this.body.y = 0.0F;
        this.body.x = 0f;
        this.body.z = 0f;
        this.body.yRot = 0;
        this.body.zRot = 0;
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "HEAD"))
    private void setDefaultBeforeRender(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci){
        setDefaultPivot(); //to not make everything wrong
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;copyFrom(Lnet/minecraft/client/model/geom/ModelPart;)V", ordinal = 0))
    private void setEmote(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci){
        if(!firstPersonNext && livingEntity instanceof AbstractClientPlayer && ((IAnimatedPlayer)livingEntity).getAnimation().isActive()){
            AnimationApplier emote = ((IAnimatedPlayer) livingEntity).getAnimation();
            emoteSupplier.set(emote);

            emote.updatePart("head", this.head);
            this.hat.copyFrom(this.head);

            emote.updatePart("leftArm", this.leftArm);
            emote.updatePart("rightArm", this.rightArm);
            emote.updatePart("leftLeg", this.leftLeg);
            emote.updatePart("rightLeg", this.rightLeg);
            emote.updatePart("torso", this.body);


        }
        else {
            firstPersonNext = false;
            emoteSupplier.set(null);
            resetBend(this.body);
            resetBend(this.leftArm);
            resetBend(this.rightArm);
            resetBend(this.leftLeg);
            resetBend(this.rightLeg);
        }
    }

    @Unique
    private static void resetBend(ModelPart part) {
        IBendHelper.INSTANCE.bend(part, null);
    }

    /**
     * @author KosmX - Player Animator library
     */
    @Override
    public void playerAnimator_prepForFirstPersonRender() {
        firstPersonNext = true;
    }
}
