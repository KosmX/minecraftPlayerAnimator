package dev.kosmx.playerAnim.minecraftApi;

import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * Animation factory, the factory will be invoked whenever a client-player is constructed.
 * The returned animation will be automatically registered and added to playerAssociated data.
 * <p>
 * {@link PlayerAnimationAccess#REGISTER_ANIMATION_EVENT} is invoked <strong>after</strong> factories are done.
 */
public interface PlayerAnimationFactory {

    FactoryHolder ANIMATION_DATA_FACTORY = new FactoryHolder();

    @Nullable IAnimation invoke(@NotNull AbstractClientPlayer player);

    class FactoryHolder {
        private FactoryHolder() {}

        private static final List<Function<AbstractClientPlayer, DataHolder>> factories = new ArrayList<>();

        /**
         * Animation factory
         * @param id       animation id or <code>null</code> if you don't want to add to playerAssociated data
         * @param priority animation priority
         * @param factory  animation factory
         */
        public void registerFactory(@Nullable ResourceLocation id, int priority, @NotNull PlayerAnimationFactory factory) {
            factories.add(player -> Optional.ofNullable(factory.invoke(player)).map(animation -> new DataHolder(id, priority, animation)).orElse(null));
        }

        @ApiStatus.Internal
        private final static class DataHolder {
            @Nullable final ResourceLocation id;
            final int priority;
            @NotNull final IAnimation animation;

            private DataHolder(@Nullable ResourceLocation id, int priority, @NotNull IAnimation animation) {
                this.id = id;
                this.priority = priority;
                this.animation = animation;
            }

            @Nullable public ResourceLocation id() {
                return id;
            }
            public int priority() {
                return priority;
            }
            @NotNull public IAnimation animation() {
                return animation;
            }
        }

        @ApiStatus.Internal
        public void prepareAnimations(AbstractClientPlayer player, AnimationStack playerStack, Map<ResourceLocation, IAnimation> animationMap) {
            for (Function<AbstractClientPlayer, DataHolder> factory: factories) {
                DataHolder dataHolder = factory.apply(player);
                if (dataHolder != null) {
                    playerStack.addAnimLayer(dataHolder.priority(), dataHolder.animation());
                    if (dataHolder.id() != null) {
                        animationMap.put(dataHolder.id(), dataHolder.animation());
                    }
                }
            }
        }
    }

}
