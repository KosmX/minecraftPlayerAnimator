package dev.kosmx.playerAnim.api.firstPerson;

import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;

public enum FirstPersonMode {

    /**
     * The animation does not decide first person mode, this way, the animation will be transparent in first person mode.
     */
    NONE(false),
    /**
     * Use the vanilla renderer, most of the time broken, if you use this, please check your animation
     */
    VANILLA(true),

    /**
     * Use the 3rd person player model (only arms/items) to render accurate first-person perspective
     */
    THIRD_PERSON_MODEL(true),

    /**
     * First person animation is DISABLED, vanilla idle will be active.
     */
    DISABLED(false),

;
    @Getter
    private final boolean enabled;


    FirstPersonMode(boolean enabled) {
        this.enabled = enabled;
    }



    private static final ThreadLocal<Boolean> firstPersonPass = ThreadLocal.withInitial(() -> false);


    /**
     * @return is the current render pass a first person pass
     */
    public static boolean isFirstPersonPass() {
        return firstPersonPass.get();
    }

    @ApiStatus.Internal
    public static void setFirstPersonPass(boolean newValue) {
        firstPersonPass.set(newValue);
    }
}
