package dev.kosmx.playerAnim.minecraftApi;

import dev.kosmx.playerAnim.api.IPlayer;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.core.impl.event.Event;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class PlayerAnimationAccess {

    /**
     * Get the animation stack for a player entity on the client.
     * <p>
     * Or you can use {@code ((IPlayer) player).getAnimationStack();}
     *
     * @param player The ClientPlayer object
     * @return The players' animation stack
     */
    public static AnimationStack getPlayerAnimLayer(AbstractClientPlayer player) throws IllegalArgumentException {
        if (player instanceof IPlayer) {
            return ((IPlayer) player).getAnimationStack();
        } else throw new IllegalArgumentException(player + " is not a player or library mixins failed");
    }

    /**
     * Allows mods to store animation layers associated with player.
     * Stored data does not get automatically registered.
     * @param player player entity
     * @return  data accessor type, you can use get() and set() on it (kotlin getter/setter compatible)
     * @throws IllegalArgumentException if the given argument is not a player, or api mixins have failed (normally never)
     * @implNote data is stored in the player object (using mixins), using it is more efficient than any objectMap as objectMap solution does not know when to delete the data.
     */
    public static PlayerAssociatedAnimationData getPlayerAssociatedData(@NotNull AbstractClientPlayer player) {
        if (player instanceof IAnimatedPlayer animatedPlayer) {
            return new PlayerAssociatedAnimationData(animatedPlayer);
        } else throw new IllegalArgumentException(player + " is not a player or library mixins failed");
    }

    /**
     * If you don't want to create your own mixin, you can use this event to add animation to players<br>
     * <b>The event will fire for every player</b> and if the player reloads, it will fire again.<br>
     * <hr>
     * NOTE: When the event fires, <code>player.getAnimationStack()</code> will be <code>null</code>, you'll have to use the given stack.
     */
    public static final Event<AnimationRegister> REGISTER_ANIMATION_EVENT = new Event<>(AnimationRegister.class, listeners -> (player, animationStack) -> {
        for (AnimationRegister listener : listeners) {
            listener.registerAnimation(player, animationStack);
        }
    });

    /**
     * Animation factory, the factory will be invoked whenever a client-player is constructed.
     * The returned animation will be automatically registered and added to playerAssociated data.
     * <p>
     * <code>REGISTER_ANIMATION_EVENT</code> is invoked <bold>after</bold> factories are done.
     */
    public static final Event<AnimationDataFactory> ANIMATION_DATA_FACTORY = new Event<>(AnimationDataFactory.class, factories -> ((player) -> {
        for (AnimationDataFactory factory : factories) {
            @Nullable RegisteredAnimationData data = factory.invoke(player);
            if (data != null) {
                getPlayerAnimLayer(player).addAnimLayer(data.priority, data.animation);
                if (data.id != null) {
                    getPlayerAssociatedData(player).set(data.id, data.animation);
                }
            }
        }
        return null;
    }));

    @FunctionalInterface
    public interface AnimationRegister {
        /**
         * Player object is in construction, it will be invoked when you can register animation
         * It will be invoked for every player only ONCE (it isn't a tick function)
         * @param player         Client player object, can be the main player or other player
         * @param animationStack the players AnimationStack, unique for every player
         */
        void registerAnimation(@NotNull AbstractClientPlayer player, @NotNull AnimationStack animationStack);
    }

    @FunctionalInterface
    public interface AnimationDataFactory {

        /**
         * Player object is in construction, it will be invoked when you can register animation
         * It will be invoked for every player only ONCE (it isn't a tick function)
         * @param player         Client player object, can be the main player or other player
         * @return data and associated properties. See {@code RegisteredAnimationData}. return <code>null</code> to skip the step
         */
        RegisteredAnimationData invoke(@NotNull AbstractClientPlayer player);
    }

    /**
     * event return type when registering an animation
     * @param id        animation id, or null if you don't want to store in player associated data
     * @param animation animation, optionally {@code ModifierLayer}
     * @param priority  registration priority
     * @apiNote This event will be fired when a playerEntity is constructed on client-side
     */
    public record RegisteredAnimationData(ResourceLocation id, @NotNull IAnimation animation, int priority) {}

    public static class PlayerAssociatedAnimationData {
        @NotNull
        private final IAnimatedPlayer player;

        public PlayerAssociatedAnimationData(@NotNull IAnimatedPlayer player) {
            this.player = player;
        }

        /**
         * Get an animation associated with the player
         * @param id    Animation identifier, please start with your modid to avoid collision
         * @return      animation or <code>null</code> if not exists
         * @apiNote     This function does <bold>not</bold> register the animation, just store it.
         */
        @Nullable public IAnimation get(@NotNull ResourceLocation id) {
            return player.playerAnimator_getAnimation(id);
        }

        /**
         * Set an animation associated with the player
         *
         * @param id        Animation identifier. Please don't override/remove other mod animations, always use your modid!
         * @param animation animation to store in the player, <code>null</code> to clear stored animation
         * @return          The previously stored animation.
         */
        @Nullable public IAnimation set(@NotNull ResourceLocation id, @Nullable IAnimation animation) {
            return player.playerAnimator_setAnimation(id, animation);
        }
    }
}
