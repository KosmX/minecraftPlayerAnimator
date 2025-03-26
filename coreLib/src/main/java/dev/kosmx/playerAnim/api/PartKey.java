package dev.kosmx.playerAnim.api;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * PartKey object for faster model part ID comparison.
 * A part will be represented by partKeys, including in formats and render.
 * Finding the correct value for a key, if the key is a unique integer, is faster than doing the same with string hashing compare.
 * <p>
 * Usage: Instead of comparing for a string key, store a static final PartKey for the given part in your class, and compare (key) for that.
 * <br>
 * Common part static keys are also available in this class, but you can request your own.
 * <p>
 * API note
 * <br>
 * If two PartKey objects are different, their key must be different: instance comparison is safe for part key equality check.
 * partKeyA == partKeyB is completely safe and okay to do.
 * <p>
 * Implementation note
 * <br>
 * Please be aware that with Project Valhalla, the implementation of this class may change, but the behaviour and public functions must not.
 *
 */
@Getter
@Immutable
public class PartKey {

    //private static final AtomicInteger count = new AtomicInteger(0);
    private static final Map<String, PartKey> existingKeys = Collections.synchronizedMap(new HashMap<>());

    // static stuff (these fields will stay here for anyone to use):
    public static final @NotNull PartKey HEAD = keyForId("head");
    public static final @NotNull PartKey BODY = keyForId("body");
    public static final @NotNull PartKey TORSO = keyForId("torso");
    public static final @NotNull PartKey RIGHT_ARM = keyForId("rightArm");
    public static final @NotNull PartKey LEFT_ARM = keyForId("leftArm");
    public static final @NotNull PartKey RIGHT_LEG = keyForId("rightLeg");
    public static final @NotNull PartKey LEFT_LEG = keyForId("leftLeg");
    public static final @NotNull PartKey RIGHT_ITEM = keyForId("rightItem");
    public static final @NotNull PartKey LEFT_ITEM = keyForId("leftItem");
    public static final @NotNull PartKey CAPE = keyForId("cape");
    public static final @NotNull PartKey ELYTRA = keyForId("elytra");


    /**
     * The string key the PartKey is representing.
     * partKeyA.key.equals(partKeyB.key) is the same as partKeyA == partKeyB, except the latter is faster.
     * <p>
     * DO NOT COMPARE USING THIS STRING
     */
    @NotNull private final String key;


    /**
     * This CTOR must not be used directly.
     * use {@link PartKey#keyForId(String)} for getting a PartKey object.
     */
    private PartKey(@NotNull String key) {
        this.key = key;
        //this.id = count.getAndIncrement();
    }

    /**
     * The current implementation states that two PartKeys are equal if the two identity objects are the same. This may change after the Valhalla update.
     * @param o other object
     * @return true if and only if the two keys are equal.
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Clone is not supported by PartKey. copying a list of PartKeys should use the same PartKey objects.
     * @throws CloneNotSupportedException always, as cloning would break instance equals and instance hashCode methods.
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("PartKey is an object key type and must not be cloned.");
    }

    /**
     * Get the appropriate partKey object, or creates a new identity if needed.
     * It is safe to invoke this concurrently, or with the same id multiple times. However, doing so defeats the purpose of fast mapping.
     * @param id the model part string ID. (should be normalized to camelCase)
     * @return PartKey for the given ID. For the same ID the same object must be returned.
     */
    @NotNull
    public static PartKey keyForId(@NotNull String id) {
        return existingKeys.computeIfAbsent(id, PartKey::new);
    }
}
