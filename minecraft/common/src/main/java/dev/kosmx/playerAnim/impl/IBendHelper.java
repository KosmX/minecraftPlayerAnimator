package dev.kosmx.playerAnim.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.core.util.SetableSupplier;
import dev.kosmx.playerAnim.impl.animation.BendHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;

public interface IBendHelper {

    static void rotateMatrixStack(PoseStack matrices, Pair<Float, Float> pair){
        float offset = 0.375f;
        matrices.translate(0, offset, 0);
        float bend = pair.getRight();
        float axisf = - pair.getLeft();
        Vector3f axis = new Vector3f((float) Math.cos(axisf), 0, (float) Math.sin(axisf));
        //return this.setRotation(axis.getRadialQuaternion(bend));
        matrices.mulPose(axis.rotation(bend));
        matrices.translate(0, - offset, 0);
    }

    static IBendHelper create(ModelPart modelPart, boolean isUpperPart, @Nullable SetableSupplier<AnimationProcessor> emote){
        return Helper.isBendEnabled() ? BendHelper.createNew(modelPart, isUpperPart, emote) : new DummyBendable();
    }

    static IBendHelper create(ModelPart modelPart, @Nullable SetableSupplier<AnimationProcessor> emote){
        return create(modelPart, false, emote);
    }

    static IBendHelper create(ModelPart modelPart, boolean isUpperPart) {
        return create(modelPart, isUpperPart, null);
    }

    void addBendedCuboid(int i, int i1, int i2, int i3, int i4, int i5, float scale, Direction up);

    void setAnimation(SetableSupplier<AnimationProcessor> emoteSupplier);

    default void bend(Pair<Float, Float> vec) {
        this.bend(vec.getLeft(), vec.getRight());
    }

    void bend(float a, float b);

    void copyBend(IBendHelper torso);


    class DummyBendable implements IBendHelper {

        @Override
        public void addBendedCuboid(int i, int i1, int i2, int i3, int i4, int i5, float scale, Direction up) {

        }

        @Override
        public void setAnimation(SetableSupplier<AnimationProcessor> emoteSupplier) {

        }

        @Override
        public void bend(float a, float b) {

        }

        @Override
        public void copyBend(IBendHelper torso) {

        }
    }
}
