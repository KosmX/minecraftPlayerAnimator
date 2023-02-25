package dev.kosmx.playerAnim.api.layered.modifier;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.util.Vec3f;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Adjusts body parts during animations.<br>
 * Make sure this instance is the very first one, over the KeyframeAnimationPlayer, in the animation stack.
 * <p>
 * Example use (adjusting the vertical angle of a custom attack animation):
 * <pre>
 * {@code
 * new AdjustmentModifier((partName) -> {
 *     float rotationX = 0;
 *     float rotationY = 0;
 *     float rotationZ = 0;
 *     float offsetX = 0;
 *     float offsetY = 0;
 *     float offsetZ = 0;
 *
 *     var pitch = player.getPitch() / 2F;
 *     pitch = (float) Math.toRadians(pitch);
 *     switch (partName) {
 *         case "body" -> {
 *             rotationX = (-1F) * pitch;
 *         }
 *         case "rightArm", "leftArm" -> {
 *             rotationX = pitch;
 *         }
 *         default -> {
 *             return Optional.empty();
 *         }
 *     }
 *
 *     return Optional.of(new AdjustmentModifier.PartModifier(
 *             new Vec3f(rotationX, rotationY, rotationZ),
 *             new Vec3f(offsetX, offsetY, offsetZ))
 *     );
 * });
 * }
 * </pre>
 */
public class AdjustmentModifier extends AbstractModifier {
    public static final class PartModifier {
        private final Vec3f rotation;
        private final Vec3f offset;

        public PartModifier(
                Vec3f rotation,
                Vec3f offset
        ) {
            this.rotation = rotation;
            this.offset = offset;
        }

        public Vec3f rotation() {
            return rotation;
        }

        public Vec3f offset() {
            return offset;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            PartModifier that = (PartModifier) obj;
            return Objects.equals(this.rotation, that.rotation) &&
                    Objects.equals(this.offset, that.offset);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rotation, offset);
        }

        @Override
        public String toString() {
            return "PartModifier[" +
                    "rotation=" + rotation + ", " +
                    "offset=" + offset + ']';
        }
    }

    public boolean enabled = true;

    protected Function<String, Optional<PartModifier>> source;

    public AdjustmentModifier(Function<String, Optional<PartModifier>> source) {
        this.source = source;
    }

    protected float getFadeIn(float delta) {
        float fadeIn = 1;
        IAnimation animation = this.getAnim();
        if(animation instanceof KeyframeAnimationPlayer) {
            KeyframeAnimationPlayer player = (KeyframeAnimationPlayer)anim;
            float currentTick = player.getTick() + delta;
            fadeIn = currentTick / (float) player.getData().beginTick;
            fadeIn = Math.min(fadeIn, 1F);
        }
        return fadeIn;
    }

    @Override
    public void tick() {
        super.tick();

        if (remainingFadeout > 0) {
            remainingFadeout -= 1;
            if(remainingFadeout <= 0) {
                instructedFadeout = 0;
            }
        }
    }

    protected int instructedFadeout = 0;
    private int remainingFadeout = 0;

    public void fadeOut(int fadeOut) {
        instructedFadeout = fadeOut;
        remainingFadeout = fadeOut + 1;
    }

    protected float getFadeOut(float delta) {
        float fadeOut = 1;
        if(remainingFadeout > 0 && instructedFadeout > 0) {
            float current = Math.max(remainingFadeout - delta , 0);
            fadeOut = current / ((float)instructedFadeout);
            fadeOut = Math.min(fadeOut, 1F);
            return fadeOut;
        }
        IAnimation animation = this.getAnim();
        if(animation instanceof KeyframeAnimationPlayer) {
            KeyframeAnimationPlayer player = (KeyframeAnimationPlayer)anim;

            float currentTick = player.getTick() + delta;
            float position = (-1F) * (currentTick - player.getData().stopTick);
            float length = player.getData().stopTick - player.getData().endTick;
            if (length > 0) {
                fadeOut = position / length;
                fadeOut = Math.min(fadeOut, 1F);
            }
        }
        return fadeOut;
    }

    @Override
    public Vec3f get3DTransform(String modelName, TransformType type, float tickDelta, Vec3f value0) {
        if (!enabled) {
            return super.get3DTransform(modelName, type, tickDelta, value0);
        }

        Optional<PartModifier> partModifier = source.apply(modelName);

        Vec3f modifiedVector = value0;
        float fade = getFadeIn(tickDelta) * getFadeOut(tickDelta);
        if (partModifier.isPresent()) {
            modifiedVector = super.get3DTransform(modelName, type, tickDelta, modifiedVector);
            return transformVector(modifiedVector, type, partModifier.get(), fade);
        } else {
            return super.get3DTransform(modelName, type, tickDelta, value0);
        }
    }

    protected Vec3f transformVector(Vec3f vector, TransformType type, PartModifier partModifier, float fade) {
        switch (type) {
            case POSITION:
                return vector.add(partModifier.offset().scale(fade));
            case ROTATION:
                return vector.add(partModifier.rotation().scale(fade));
            case BEND:
                break;
        }
        return vector;
    }
}