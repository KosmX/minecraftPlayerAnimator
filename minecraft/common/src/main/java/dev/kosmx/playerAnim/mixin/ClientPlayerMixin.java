package dev.kosmx.playerAnim.mixin;

import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractClientPlayer.class)
public abstract class ClientPlayerMixin extends Player implements IAnimatedPlayer {

    //Unique params might be renamed
    @Unique
    private final AnimationStack animationStack = new AnimationStack();
    @Unique
    private final AnimationApplier animationApplier = new AnimationApplier(animationStack);


    public ClientPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(level, blockPos, f, gameProfile, profilePublicKey);
    }

    @Override
    public AnimationStack getAnimationStack() {
        return animationStack;
    }

    @Override
    public AnimationApplier getAnimation() {
        return animationApplier;
    }

    @Intrinsic(displace = true)
    @Override
    public void tick() {
        super.tick();
        animationStack.tick();
    }
}
