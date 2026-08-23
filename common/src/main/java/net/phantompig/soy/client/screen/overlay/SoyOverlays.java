package net.phantompig.soy.client.screen.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.phantompig.soy.SoyConfig;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.entity.TitanCorpseEntity;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.titan.hardening.HardeningSystemHolder;
import net.threetag.palladium.util.Easing;

public class SoyOverlays {
    public static final ResourceLocation EXHAUSTION_LOCATION = SubjectsOfYmir.rsrc("textures/gui/overlay/titan_exhaustion.png");
    public static final ResourceLocation HARDENING_LOCATION = SubjectsOfYmir.rsrc("textures/gui/overlay/hardening.png");
    public static final ResourceLocation WHITE_LOCATION = SubjectsOfYmir.rsrc("textures/white.png");

    public static void renderExhaustionOverlay(Minecraft minecraft, Gui gui, GuiGraphics guiGraphics, float partialTicks, int width, int height) {
        if (!(minecraft.player instanceof SoyPlayerExtension ext) || ext.soy$getTitanInstance().getMaxStamina() == 0) return;
        float a = 1 - Mth.clamp((float) ext.soy$getTitanInstance().getStamina() / ext.soy$getTitanInstance().getMaxStamina(), 0, 1);
        if (ext.soy$getTitanInstance().getProgress() == 0) {
            a = Easing.outCubic(a);
        } else {
            a = Easing.inQuad(a);
        }
        if (a > 0) guiGraphics.innerBlit(EXHAUSTION_LOCATION, 0, width, 0, height, 0, 0, 1, 0, 1, 1, 1, 1, a);
    }

    public static void renderAllHardeningOverlay(Minecraft minecraft, Gui gui, GuiGraphics guiGraphics, float partialTicks, int width, int height) {
        if (!(minecraft.player instanceof HardeningSystemHolder h) || !(minecraft.player instanceof SoyPlayerExtension ext) || ext.soy$getTitanInstance().getProgress() == 0) return;
        float num = h.soy$getHardeningSystem().getAllHardening();
        if (num > 0) guiGraphics.innerBlit(HARDENING_LOCATION, 0, width, 0, height, 0, 0, 1, 0, 1, 1, 1, 1, num);
    }

    public static void renderOtherShiftingOverlay(Minecraft minecraft, Gui gui, GuiGraphics guiGraphics, float partialTicks, int width, int height) {
        if (SoyConfig.Client.shouldWorldTintOnShift() && minecraft.level != null) {
            int max = 0;
            for (Entity e : minecraft.level.entitiesForRendering()) {
                if (e instanceof TitanCorpseEntity) continue;
                if (e instanceof SoyPlayerExtension ext) {
                    int progress = ext.soy$getTitanInstance().getProgress();
                    if (progress < 20 && progress > max) {
                        max = progress;
                    }
                }
            }
            if (max > 0) {
                guiGraphics.innerBlit(WHITE_LOCATION, 0, width, 0, height, 0, 0, 1, 0, 1, 0.7f, 0.7f, 0.1f, ease(max + partialTicks));
            }
        }
    }

    private static float ease(float x) {
        return 0.2f * Math.max(-0.003125f * x * x * x + 0.0549107f * x * x - 0.120536f * x, 0);
    }
}
