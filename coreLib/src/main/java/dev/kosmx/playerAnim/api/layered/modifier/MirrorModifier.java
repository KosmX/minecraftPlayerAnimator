package dev.kosmx.playerAnim.api.layered.modifier;

import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.core.util.Vec3f;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class MirrorModifier extends AbstractModifier {

    public static final Map<PartKey, PartKey> mirrorMap;

    /**
     * Enable the modifier
     */
    @Getter
    @Setter
    private boolean enabled = true;

    @Override
    public @NotNull Vec3f get3DTransform(@NotNull String modelName, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
        return get3DTransform(PartKey.keyForId(modelName), type, tickDelta, value0);
    }

    @Override
    public @NotNull Vec3f get3DTransform(@NotNull PartKey partKey, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
        if (!isEnabled()) return super.get3DTransform(partKey, type, tickDelta, value0);

        if (mirrorMap.containsKey(partKey)) partKey = mirrorMap.get(partKey);
        value0 = transformVector(value0, type);

        Vec3f vec3f = super.get3DTransform(partKey, type, tickDelta, value0);
        return transformVector(vec3f, type);
    }

    // Override candidate
    @Override
    public @NotNull FirstPersonConfiguration getFirstPersonConfiguration(float tickDelta) {
        FirstPersonConfiguration configuration = super.getFirstPersonConfiguration(tickDelta);
        if (isEnabled()) {
            return new FirstPersonConfiguration()
                    .setShowLeftArm(configuration.isShowRightArm())
                    .setShowRightArm(configuration.isShowLeftArm())
                    .setShowLeftItem(configuration.isShowRightItem())
                    .setShowRightItem(configuration.isShowLeftItem());
        } else return configuration;
    }

    protected Vec3f transformVector(Vec3f value0, TransformType type) {
        switch (type) {
            case POSITION:
                return new Vec3f(-value0.getX(), value0.getY(), value0.getZ());
            case ROTATION:
                return new Vec3f(value0.getX(), -value0.getY(), -value0.getZ());
            case BEND:
                return new Vec3f(value0.getX(), -value0.getY(), value0.getZ());
            default:
                return value0; //why?!
        }
    }

    static {
        // this looks better in Kotlin
        mirrorMap = Map.of(
                PartKey.LEFT_ARM, PartKey.RIGHT_ARM,
                PartKey.LEFT_LEG, PartKey.RIGHT_LEG,
                PartKey.LEFT_ITEM, PartKey.RIGHT_ITEM,
                PartKey.RIGHT_ARM, PartKey.LEFT_ARM,
                PartKey.RIGHT_LEG, PartKey.LEFT_LEG,
                PartKey.RIGHT_ITEM, PartKey.LEFT_ITEM);
    }
}
