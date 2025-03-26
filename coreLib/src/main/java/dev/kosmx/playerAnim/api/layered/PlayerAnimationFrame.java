package dev.kosmx.playerAnim.api.layered;

import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.core.util.Vec3f;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Mixin it into a player, add to its Animation stack,
 * and override its tick,
 * <p>
 * It is a representation of your pose on the frame.
 * Override {@link IAnimation#setupAnim(float)} and set the pose there.
 */
public abstract class PlayerAnimationFrame implements IAnimation {

    protected PlayerPart head = new PlayerPart();
    protected PlayerPart body = new PlayerPart();
    protected PlayerPart rightArm = new PlayerPart();
    protected PlayerPart leftArm = new PlayerPart();
    protected PlayerPart rightLeg = new PlayerPart();
    protected PlayerPart leftLeg = new PlayerPart();
    protected PlayerPart rightItem = new PlayerPart();
    protected PlayerPart leftItem = new PlayerPart();

    HashMap<PartKey, PlayerPart> parts = new HashMap<>();

    public PlayerAnimationFrame() {
        parts.put(PartKey.HEAD, head);
        parts.put(PartKey.BODY, body);
        parts.put(PartKey.RIGHT_ARM, rightArm);
        parts.put(PartKey.LEFT_ARM, leftArm);
        parts.put(PartKey.RIGHT_LEG, rightLeg);
        parts.put(PartKey.LEFT_LEG, leftLeg);
        parts.put(PartKey.RIGHT_ITEM, rightItem);
        parts.put(PartKey.LEFT_ITEM, leftItem);
    }


    @Override
    public void tick() {
        IAnimation.super.tick();
    }

    @Override
    public boolean isActive() {
        for (Map.Entry<PartKey, PlayerPart> entry: parts.entrySet()) {
            PlayerPart part = entry.getValue();
            if (part.bend != null || part.pos != null || part.rot != null || part.scale != null) return true;
        }
        return false;
    }

    /**
     * Reset every part, those parts won't influence the animation
     * Don't use it if you don't want to set every part in every frame
     */
    public void resetPose() {
        for (Map.Entry<PartKey, PlayerPart> entry: parts.entrySet()) {
            entry.getValue().setNull();
        }
    }


    @Override
    public @NotNull Vec3f get3DTransform(@NotNull PartKey modelKey, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
        PlayerPart part = parts.get(modelKey);
        if (part == null) return value0;
        return switch (type) {
            case POSITION -> part.pos == null ? value0 : part.pos;
            case ROTATION -> part.rot == null ? value0 : part.rot;
            case SCALE -> part.scale == null ? value0 : part.scale;
            case BEND -> part.bend == null ? value0 : new Vec3f(part.bend.getLeft(), part.bend.getRight(), 0f);
            default -> value0;
        };
    }

    public static class PlayerPart {
        public Vec3f pos;
        public Vec3f scale;
        public Vec3f rot;
        public Pair<Float, Float> bend;

        protected void setNull() {
            pos = scale = rot = null;
            bend = null;
        }
    }
}
