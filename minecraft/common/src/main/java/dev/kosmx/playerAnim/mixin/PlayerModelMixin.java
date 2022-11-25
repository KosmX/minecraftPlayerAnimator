package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.core.util.SetableSupplier;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.IMutableModel;
import dev.kosmx.playerAnim.impl.IPlayerModel;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.impl.IBendHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    
    //private BendableModelPart mutatedTorso;
    @Unique

    private IBendHelper mutatedJacket;
    @Unique

    private IBendHelper mutatedRightSleeve;
    @Unique

    private IBendHelper mutatedLeftSleeve;
    @Unique

    private IBendHelper mutatedRightPantLeg;
    @Unique

    private IBendHelper mutatedLeftPantLeg;

    @Unique
    private IMutableModel thisWithMixin;

    public PlayerModelMixin(float f) {
        super(f);
    }
    //private BendHelper mutatedTorso;
    //private MutableModelPart head :D ... it were be funny XD


    @Inject(method = "<init>", at = @At("RETURN"))
    private void initBendableStuff(float scale, boolean thinArms, CallbackInfo ci){
        thisWithMixin = (IMutableModel) this;
        emoteSupplier.set(null);
        this.mutatedJacket = IBendHelper.create(this.jacket, false, emoteSupplier);
        this.mutatedRightSleeve = IBendHelper.create(this.rightSleeve, true, emoteSupplier);
        this.mutatedLeftSleeve = IBendHelper.create(this.leftSleeve, true, emoteSupplier);
        this.mutatedRightPantLeg = IBendHelper.create(this.rightPants, emoteSupplier);
        this.mutatedLeftPantLeg = IBendHelper.create(this.leftPants, emoteSupplier);

        thisWithMixin.setLeftArm(IBendHelper.create(this.leftArm, true));
        thisWithMixin.setRightArm(IBendHelper.create(this.rightArm, true));

        thisWithMixin.setEmoteSupplier(emoteSupplier);

        thisWithMixin.setLeftLeg(IBendHelper.create(this.leftLeg, false, emoteSupplier));
        thisWithMixin.getLeftLeg().addBendedCuboid(- 2, 0, - 2, 4, 12, 4, scale, Direction.UP);

        mutatedJacket.addBendedCuboid(- 4, 0, - 2, 8, 12, 4, scale + 0.25f, Direction.DOWN);
        mutatedRightPantLeg.addBendedCuboid(- 2, 0, - 2, 4, 12, 4, scale + 0.25f, Direction.UP);
        mutatedLeftPantLeg.addBendedCuboid(- 2, 0, - 2, 4, 12, 4, scale + 0.25f, Direction.UP);
        if(thinArms){
            thisWithMixin.getLeftArm().addBendedCuboid(- 1, - 2, - 2, 3, 12, 4, scale, Direction.UP);
            thisWithMixin.getRightArm().addBendedCuboid(- 2, - 2, - 2, 3, 12, 4, scale, Direction.UP);
            mutatedLeftSleeve.addBendedCuboid(- 1, - 2, - 2, 3, 12, 4, scale + 0.25f, Direction.UP);
            mutatedRightSleeve.addBendedCuboid(- 2, - 2, - 2, 3, 12, 4, scale + 0.25f, Direction.UP);
        }else{
            thisWithMixin.getLeftArm().addBendedCuboid(- 1, - 2, - 2, 4, 12, 4, scale, Direction.UP);
            thisWithMixin.getRightArm().addBendedCuboid(- 3, - 2, - 2, 4, 12, 4, scale, Direction.UP);
            mutatedLeftSleeve.addBendedCuboid(- 1, - 2, - 2, 4, 12, 4, scale + 0.25f, Direction.UP);
            mutatedRightSleeve.addBendedCuboid(- 3, - 2, - 2, 4, 12, 4, scale + 0.25f, Direction.UP);
        }

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
        if(!firstPersonNext && livingEntity instanceof AbstractClientPlayer && ((IAnimatedPlayer)livingEntity).playerAnimator_getAnimation().isActive()){
            AnimationApplier emote = ((IAnimatedPlayer) livingEntity).playerAnimator_getAnimation();
            emoteSupplier.set(emote);

            emote.updatePart("head", this.head);
            this.hat.copyFrom(this.head);

            emote.updatePart("leftArm", this.leftArm);
            emote.updatePart("rightArm", this.rightArm);
            emote.updatePart("leftLeg", this.leftLeg);
            emote.updatePart("rightLeg", this.rightLeg);
            emote.updatePart("torso", this.body);


            Pair<Float, Float> torsoBend = emote.getBend("torso");
            Pair<Float, Float> bodyBend = emote.getBend("body");
            thisWithMixin.getTorso().bend(new Pair<>(torsoBend.getLeft() + bodyBend.getLeft(), torsoBend.getRight() + bodyBend.getRight()));
            thisWithMixin.getLeftArm().bend(emote.getBend("leftArm"));
            thisWithMixin.getLeftLeg().bend(emote.getBend("leftLeg"));
            thisWithMixin.getRightArm().bend(emote.getBend("rightArm"));
            thisWithMixin.getRightLeg().bend(emote.getBend("rightLeg"));

            mutatedJacket.copyBend(thisWithMixin.getTorso());
            mutatedLeftPantLeg.copyBend(thisWithMixin.getLeftLeg());
            mutatedRightPantLeg.copyBend(thisWithMixin.getRightLeg());
            mutatedLeftSleeve.copyBend(thisWithMixin.getLeftArm());
            mutatedRightSleeve.copyBend(thisWithMixin.getRightArm());
        }
        else {
            firstPersonNext = false;
            emoteSupplier.set(null);
        }
    }


    /**
     * @author KosmX - Player Animator library
     */
    @Override
    public void playerAnimator_prepForFirstPersonRender() {
        firstPersonNext = true;
    }
}
