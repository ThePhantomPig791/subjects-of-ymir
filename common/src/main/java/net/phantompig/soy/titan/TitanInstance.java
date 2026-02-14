package net.phantompig.soy.titan;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.property.SoyProperties;
import org.jetbrains.annotations.Nullable;
import virtuoel.pehkui.api.ScaleTypes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TitanInstance {
    @Nullable
    public Titan titan;
    public ArrayList<Titan> stacks = new ArrayList<>();

    public String variant;

    public final LivingEntity entity;

    public boolean forceUnshift = false;

    public boolean isCorpse = false;

    public TitanInstance(LivingEntity entity) {
        this(entity, null);
    }

    public TitanInstance(LivingEntity entity, @Nullable Titan titan) {
        this(entity, titan, titan != null ? titan.variants.get(0) : "default");
    }

    public TitanInstance(LivingEntity entity, @Nullable Titan titan, String variant) {
        this.entity = entity;
        this.titan = titan;
        this.variant = variant;

        this.updateProperties();
    }

    public TitanInstance(LivingEntity entity, @Nullable Titan titan, String variant, List<Titan> stacks) {
        this(entity, titan, variant);
        this.stacks.addAll(stacks);
    }


    public int getProgress() {
        return SoyProperties.PROGRESS.get(this.entity);
    }
    public void setProgress(int progress) {
        if (this.titan == null) return;
        SoyProperties.PROGRESS.set(this.entity, progress);
    }

    public int getCharge() {
        return SoyProperties.CHARGE.get(this.entity);
    }
    public void setCharge(int charge) {
        if (this.titan == null) return;
        SoyProperties.CHARGE.set(this.entity, charge);
    }

    public int getDecay() {
        return SoyProperties.DECAY.get(this.entity);
    }
    public void setDecay(int decay) {
        SoyProperties.DECAY.set(this.entity, decay);
    }

    public Color getEyeColor() {
        return SoyProperties.EYE_COLOR.get(this.entity);
    }
    public void setEyeColor(Color color) {
        SoyProperties.EYE_COLOR.set(entity, color);
    }


    public void startScaleChange() {
        if (this.titan == null) return;
        setScaleTime(15);
        scale(this.titan.scale);
    }

    public void setScaleImmediate() {
        if (this.titan == null) return;
        setScaleTime(0);
        scale(this.titan.scale);
    }

    public void resetScale() {
        if (this.titan == null) return;
        setScaleTime(0);

        ScaleTypes.WIDTH.getScaleData(entity).setTargetScale(1);
        ScaleTypes.HEIGHT.getScaleData(entity).setTargetScale(1);
        ScaleTypes.THIRD_PERSON.getScaleData(entity).setTargetScale(1);
        ScaleTypes.REACH.getScaleData(entity).setTargetScale(1);
        ScaleTypes.HELD_ITEM.getScaleData(entity).setTargetScale(1);

        ScaleTypes.MOTION.getScaleData(entity).setTargetScale(1);
        ScaleTypes.JUMP_HEIGHT.getScaleData(entity).setTargetScale(1);
    }

    private void scale(float value) {
        if (this.titan == null) return;

        ScaleTypes.WIDTH.getScaleData(entity).setTargetScale(value);
        ScaleTypes.HEIGHT.getScaleData(entity).setTargetScale(value);
        ScaleTypes.THIRD_PERSON.getScaleData(entity).setTargetScale((float) (1 - 0.25 * Math.log(value)));
        ScaleTypes.REACH.getScaleData(entity).setTargetScale((float) Math.sqrt(Math.pow(value, 1.5)));
        ScaleTypes.HELD_ITEM.getScaleData(entity).setTargetScale(1 / value);

        ScaleTypes.MOTION.getScaleData(entity).setTargetScale(this.titan.speed);
        ScaleTypes.JUMP_HEIGHT.getScaleData(entity).setTargetScale(this.titan.jump);
    }
    private void setScaleTime(int value) {
        ScaleTypes.WIDTH.getScaleData(entity).setScaleTickDelay(value);
        ScaleTypes.HEIGHT.getScaleData(entity).setScaleTickDelay(value);
        ScaleTypes.THIRD_PERSON.getScaleData(entity).setScaleTickDelay(value);
        ScaleTypes.REACH.getScaleData(entity).setScaleTickDelay(value);
        ScaleTypes.HELD_ITEM.getScaleData(entity).setScaleTickDelay(value);
    }


    public boolean is(ResourceLocation id) {
        return this.titan != null && this.titan.id.equals(id);
    }

    public void updateProperties() {
        if (this.titan == null) return;
        SoyProperties.TITAN.set(this.entity, this.titan.id);
        SoyProperties.VARIANT.set(this.entity, this.variant);
    }

    public static void copyPropertiesToEntity(LivingEntity from, LivingEntity to) {
        SoyProperties.TITAN.set(to, SoyProperties.TITAN.get(from));
        SoyProperties.VARIANT.set(to, SoyProperties.VARIANT.get(from));
        SoyProperties.PROGRESS.set(to, SoyProperties.PROGRESS.get(from));
        SoyProperties.CHARGE.set(to, SoyProperties.CHARGE.get(from));
        SoyProperties.EYE_COLOR.set(to, SoyProperties.EYE_COLOR.get(from));
    }

    public static void copyPropertiesTo(TitanInstance from, TitanInstance to) {
        copyPropertiesToEntity(from.entity, to.entity);
        to.titan = from.titan;
        to.variant = from.variant;
        to.stacks = new ArrayList<>(from.stacks);
    }

    public static TitanInstance fromTag(LivingEntity entity, CompoundTag tag) {
        if (tag.isEmpty()) return new TitanInstance(entity);
        ResourceLocation id = new ResourceLocation(tag.getString("Titan"));

        TitanInstance inst = new TitanInstance(
                entity,
                TitanRegistry.getTitan(id),
                tag.getString("Variant"),
                tag.getList("Stacks", 5).stream().map(t -> TitanRegistry.getTitan(new ResourceLocation(t.getAsString()))).toList()
        );
        inst.isCorpse = tag.getBoolean("IsCorpse");
        inst.updateProperties();
        return inst;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        if (this.titan == null) return tag;
        tag.putString("Titan", this.titan.id.toString());
        tag.putString("Variant", this.variant);
        ListTag stacks = new ListTag();
        stacks.addAll(this.stacks.stream().map(ti -> StringTag.valueOf(ti.id.toString())).toList());
        tag.put("Stacks", stacks);
        tag.putBoolean("IsCorpse", this.isCorpse);
        return tag;
    }
}
