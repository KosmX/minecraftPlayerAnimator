package dev.kosmx.playerAnim.core.data;

import dev.kosmx.playerAnim.core.util.Ease;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class KeyframeAnimationTest {
    @Test
    public void testKeyframeAnimation() {
        KeyframeAnimation.StateCollection.State state = new KeyframeAnimation.StateCollection(0).x;
        // Easy case
        state.addKeyFrame(1, 0, Ease.CONSTANT);
        state.addKeyFrame(5, 0, Ease.CONSTANT);
        state.addKeyFrame(10, 10, Ease.CONSTANT);
        state.addKeyFrame(10, 10, Ease.CONSTANT);

        state.addKeyFrame(15, 10, Ease.CONSTANT);

        verify(state);
        state.getKeyFrames().clear();


        // random case
        Random random = new Random();

        for (int i = 0; i < 10000; i += random.nextInt(100)) {
            state.addKeyFrame(i, i, Ease.CONSTANT);
        }

        verify(state);
    }

    public static void verify(KeyframeAnimation.StateCollection.State state) {

        for (int t = 0; t < state.getKeyFrames().size(); t++) {
            // Iterative, 100% works algorithm

            int i = -1;
            while (state.getKeyFrames().size() > i + 1 && state.getKeyFrames().get(i + 1).tick <= t) {
                i++;
            }

            Assertions.assertEquals(i, state.findAtTick(t), "KeyframeAnimationTest failed at tick " + t);
        }
    }
}
