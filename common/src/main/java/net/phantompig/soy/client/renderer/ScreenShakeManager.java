package net.phantompig.soy.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.threetag.palladium.util.Easing;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ScreenShakeManager {
    public static final List<ScreenShake> screenShakes = new ArrayList<>(4);

    public static void update(PoseStack stack) {
        screenShakes.removeIf(ScreenShake::expired);
        screenShakes.forEach(s -> s.apply(stack));
    }

    public record ScreenShake(long startTime, int duration, Vector3f strength, @Nullable Easing easing) {
        public void apply(PoseStack stack) {
            this.translate(stack, this.strength.mul(easing != null ?
                    easing.apply(1 - (getTimeElapsed() / duration))
                    : 1
            ));
        }

        public float getTimeElapsed() {
            return (System.currentTimeMillis() - startTime);
        }

        public boolean expired() {
            return getTimeElapsed() > duration;
        }

        private void translate(PoseStack stack, Vector3f vec) {
            stack.translate(vec.x * (float) (2 * Math.random() - 1), vec.y * (float) (2 * Math.random() - 1), vec.z * (float) (2 * Math.random() - 1));
        }
    }

    public static void addScreenShake(int duration, float strength) {
        addScreenShake(duration, new Vector3f(strength));
    }
    public static void addScreenShake(int duration, Vector3f strength) {
        addScreenShake(duration, strength, Easing.OUTCUBIC);
    }
    public static void addScreenShake(int duration, Vector3f strength, Easing easing) {
        screenShakes.add(new ScreenShake(System.currentTimeMillis(), duration, strength, easing));
    }
}
