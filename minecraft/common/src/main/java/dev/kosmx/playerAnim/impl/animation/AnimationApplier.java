package dev.kosmx.playerAnim.impl.animation;


import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.core.util.Vec3f;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class AnimationApplier extends AnimationProcessor {
    public AnimationApplier(IAnimation animation) {
        super(animation);
    }

    public void updatePart(String partName, ModelPart part) {
        Vec3f pos = this.get3DTransform(partName, TransformType.POSITION, new Vec3f(part.x, part.y, part.z));
        part.x = pos.getX();
        part.y = pos.getY();
        part.z = pos.getZ();
        Vec3f rot = this.get3DTransform(partName, TransformType.ROTATION, new Vec3f(part.xRot, part.yRot, part.zRot));
        part.xRot = rot.getX();
        part.yRot = rot.getY();
        part.zRot = rot.getZ();
    }

}
