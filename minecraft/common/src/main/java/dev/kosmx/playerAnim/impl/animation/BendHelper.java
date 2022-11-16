package dev.kosmx.playerAnim.impl.animation;

import com.mojang.math.Matrix4f;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.SetableSupplier;
import dev.kosmx.playerAnim.impl.IBendHelper;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import io.github.kosmx.bendylib.IModelPart;
import io.github.kosmx.bendylib.MutableModelPart;
import io.github.kosmx.bendylib.impl.BendableCuboid;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class BendHelper extends MutableModelPart implements IBendHelper {

    @Nullable
    protected SetableSupplier<AnimationProcessor> emote;
    protected float axis = 0;
    protected float angl = 0;


    public BendHelper(ModelPart modelPart, boolean isUpperPart, @Nullable SetableSupplier<AnimationProcessor> emote){
        super(modelPart);
        this.emote = emote;
        ((IModelPart) modelPart).mutate(this);
        ((IUpperPartHelper) modelPart).setUpperPart(isUpperPart);
    }

    /**
     * function to avoid impossible type resolve
     */
    public static IBendHelper createNew(ModelPart modelPart, boolean isUpper, @Nullable SetableSupplier<AnimationProcessor> emote) {
        return new BendHelper(modelPart, isUpper, emote);
    }

    @Override
    public String modId(){
        return "playerAnimator";
    }


    /**
     * This mod has always 4 priority, but not always active.
     *
     * @return 0
     */
    @Override
    public int getPriority(){
        return 4;
    }

    public Matrix4f getMatrix4f(){
        return ((BendableCuboid) this.iCuboids.get(0)).getLastPosMatrix();
    }

    public BendableCuboid getCuboid(){
        return (BendableCuboid) this.iCuboids.get(0);
    }

    @Override
    public boolean isActive(){
        return this.emote != null && this.emote.get() != null && this.emote.get().isActive() && angl != 0;
    }

    @Override
    public void setAnimation(@Nullable SetableSupplier<AnimationProcessor> emote){
        this.emote = emote;
    }

    @Nullable
    public SetableSupplier<AnimationProcessor> getEmote(){
        return emote;
    }

    @Override
    public void bend(float a, float b){
        this.axis = a;
        this.angl = b;
        ((BendableCuboid) this.iCuboids.get(0)).applyBend(a, b);
    }


    public void copyBend(@NotNull IBendHelper mutableModelPart){
        if (mutableModelPart instanceof BendHelper) {
            BendHelper modelPart = (BendHelper) mutableModelPart;
            this.bend(modelPart.axis, modelPart.angl);
        }
    }

    @Override
    public void addBendedCuboid(int i, int i1, int i2, int i3, int i4, int i5, float scale, Direction up) {
        this.addCuboid(i, i1, i2, i3, i4, i5, scale, up);
    }
}
