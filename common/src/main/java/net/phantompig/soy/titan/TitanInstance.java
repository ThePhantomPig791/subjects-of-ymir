package net.phantompig.soy.titan;

import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.phantompig.soy.item.SoyItems;
import net.phantompig.soy.item.SpineItem;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladiumcore.util.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import virtuoel.pehkui.api.ScaleTypes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TitanInstance {
    public static final int START_CORPSE_DECAY = -3600;
    public static final int MAX_CORPSE_DECAY = 1800;

    @Nullable
    public Titan titan;
    public ArrayList<Titan> stacks = new ArrayList<>();

    public String variant;

    public final LivingEntity entity;

    @Nullable
    public TitanMemoryManager memoryManager = null;

    public boolean forceUnshift = false;

    public boolean isCorpse = false;

    public int canShiftTicks = 0;

    @Nullable
    public ListTag playerInventory;

    public int regainStaminaCooldown = 0;

    public int deaths = 0;


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

    public void tick() {
        if (this.canShiftTicks > 0) this.canShiftTicks--;
        staminaTick();

        if (!Platform.isProduction() && entity instanceof ServerPlayer player) {
            player.displayClientMessage(Component.literal("Stamina: " + getStamina() + " / " + getMaxStamina() + ", Deaths: " + this.deaths), true);
        }
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
    public void randomizeEyeColor() {
        float red = ((float) Math.random() + 1) / 2;
        float green = ((float) Math.random() + 1) / 2;
        float blue = ((float) Math.random() + 1) / 2;
        this.setEyeColor(mixColorWithBase(new Color(red, green, blue)));
    }
    public void setEyeColorToBase() {
        if (this.titan == null || this.titan.baseEyeColor == null) {
            setEyeColor(Color.WHITE);
        } else setEyeColor(this.titan.baseEyeColor);
    }
    public Color mixColorWithBase(Color color) {
        if (this.titan == null || this.titan.baseEyeColor == null) return color;
        float[] c1 = color.getRGBColorComponents(null);
        float[] c2 = this.titan.baseEyeColor.getRGBColorComponents(null);
        float red = (float) Math.sqrt((Math.pow(c1[0], 2) + Math.pow(c2[0], 2)) / 2);
        float green = (float) Math.sqrt((Math.pow(c1[1], 2) + Math.pow(c2[1], 2)) / 2);
        float blue = (float) Math.sqrt((Math.pow(c1[2], 2) + Math.pow(c2[2], 2)) / 2);
        return new Color(red, green, blue);
    }

    public int getStamina() {
        return SoyProperties.STAMINA.get(this.entity);
    }
    public void setStamina(int stamina) {
        int m = getMaxStamina();
        if (stamina > m) stamina = m;
        if (stamina < 0) stamina = 0;
        SoyProperties.STAMINA.set(this.entity, stamina);
    }
    public int getMaxStamina() {
        return SoyProperties.MAX_STAMINA.get(this.entity);
    }
    public void setMaxStamina(int max) {
        SoyProperties.MAX_STAMINA.set(this.entity, max);
    }
    public void exhaust(int stamina) {
        setStamina(getStamina() - stamina);
        regainStaminaCooldown = (int) (200 * stamina / (stamina + 20f));
    }
    public void exhaustSafe(int stamina) {
        if (stamina >= getStamina()) exhaust(getStamina() - 1);
        else exhaust(stamina);
    }
    public void regainStamina(int stamina) {
        setStamina(getStamina() + stamina);
    }
    public void staminaTick() {
        if (regainStaminaCooldown > 0) regainStaminaCooldown--;
        else {
            int stam = getStamina(), max = getMaxStamina();
            if (stam < max) {
                if (this.getProgress() > 0) {
                    if (stam < 0.4f * max) SoyProperties.PATH_POINTS.set(entity, SoyProperties.PATH_POINTS.get(entity) + 1);
                    regainStamina(1);
                    if (Math.random() < 0.1) setMaxStamina(max + 1);
                    if (Math.random() > entity.getHealth() / entity.getMaxHealth()) exhaust(1);
                } else {
                    if (Math.random() < 0.4) regainStamina(1);
                    if (Math.random() < 0.02) setMaxStamina(max + 1);
                }
            }
        }

        if (getStamina() <= 0 && this.titan != null && this.getProgress() > 0) {
            this.titan.unshiftWithAdverseEffects(entity);
        }
    }

    @NotNull
    public TitanMemoryManager getMemoryManager() {
        if (this.memoryManager == null) return this.memoryManager = new TitanMemoryManager();
        return this.memoryManager;
    }


    public boolean canShift() {
        return this.canShiftTicks > 0 && getStamina() > 0.7 * getMaxStamina();
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

        ScaleTypes.MOTION.getScaleData(entity).setTargetScale(this.titan.stats.speed);
        ScaleTypes.JUMP_HEIGHT.getScaleData(entity).setTargetScale(this.titan.stats.jump);
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
        SoyProperties.ATTACK_TIME.set(this.entity, this.titan.stats.attackSpeed);
    }

    public static Tuple<Titan, String> randomizeFor(LivingEntity entity) {
        Tuple<Titan, String> randomTitanAndVariant = TitanRegistry.getRandomTitan();
        setFor(entity, randomTitanAndVariant);
        return randomTitanAndVariant;
    }

    public static Tuple<Titan, String> sequentialRandomizeFor(LivingEntity entity) {
        Tuple<Titan, String> randomTitanAndVariant = TitanRegistry.getSequentialRandomTitan(entity.getServer());
        setFor(entity, randomTitanAndVariant);
        return randomTitanAndVariant;
    }

    public static void setFor(LivingEntity entity, Tuple<Titan, String> titan) {
        if (!(entity instanceof SoyPlayerExtension playerExt)) return;
        TitanInstance newTitan = new TitanInstance(entity, titan.getA(), titan.getB());
        playerExt.setTitanInstance(newTitan);
        playerExt.getTitanInstance().randomizeEyeColor();
        playerExt.getTitanInstance().setProgress(0);
        playerExt.getTitanInstance().setCharge(0);
        playerExt.getTitanInstance().setDecay(TitanInstance.START_CORPSE_DECAY);
        SoyProperties.STAMINA.set(entity, titan.getA().defaultMaxStamina);
        SoyProperties.MAX_STAMINA.set(entity, titan.getA().defaultMaxStamina);
    }

    // removes stamina and all properties
    public static void clearTitanFor(LivingEntity entity) {
        if (!(entity instanceof SoyPlayerExtension playerExt)) return;
        playerExt.setTitanInstance(new TitanInstance(entity));
        playerExt.getTitanInstance().setStamina(0);
        playerExt.getTitanInstance().setMaxStamina(0);
        SoyProperties.PATH_POINTS.set(entity, 0);
    }

    // WILL inherit memories! aka you can't go backwards to the exact same titan instance from the item since the MemoryManager will be different
    public static ItemStack toSpineIem(TitanInstance inst) {
        ItemStack stack = SoyItems.SPINE.get().getDefaultInstance();
        CompoundTag tag = inst.toTag();
        tag.remove("PlayerInventory");
        tag.remove("Deaths");
        tag.remove("IsCorpse");
        Color eye = inst.getEyeColor();
        IntArrayTag rgb = new IntArrayTag(new int[]{eye.getRed(), eye.getGreen(), eye.getBlue()});
        tag.put("EyeColor", rgb);
        var pi = new CompoundTag();
        pi.putString("Name", inst.entity.getDisplayName().getString());
        pi.putUUID("UUID", inst.entity.getUUID());
        tag.put("PreviousInheritor", pi);
        if (inst.memoryManager != null) tag.put("MemoryManager", inst.memoryManager.inherited().toTag());
        stack.getOrCreateTag().put("TitanInstance", tag);
        return stack;
    }

    public static void setFromSpineItem(LivingEntity forEntity, ItemStack stack) {
        if (!(forEntity instanceof SoyPlayerExtension ext)) return;
        var tag = SpineItem.getTitanInstanceTag(stack);
        if (tag.isEmpty()) return;
        var inst = fromTag(forEntity, tag);
        ext.setTitanInstance(inst);
        int[] rgb = tag.getIntArray("EyeColor");
        inst.setEyeColor(new Color(rgb[0], rgb[1], rgb[2]));
        inst.setMaxStamina(inst.titan.defaultMaxStamina);
        inst.setStamina(inst.titan.defaultMaxStamina);
        inst.memoryManager = tag.contains("MemoryManager") ? TitanMemoryManager.fromTag(tag.getCompound("MemoryManager")) : null;
    }

    public static void copyPropertiesToEntity(LivingEntity from, LivingEntity to) {
        SoyProperties.TITAN.set(to, SoyProperties.TITAN.get(from));
        SoyProperties.VARIANT.set(to, SoyProperties.VARIANT.get(from));
        SoyProperties.PROGRESS.set(to, SoyProperties.PROGRESS.get(from));
        SoyProperties.CHARGE.set(to, SoyProperties.CHARGE.get(from));
        SoyProperties.EYE_COLOR.set(to, SoyProperties.EYE_COLOR.get(from));
        SoyProperties.ATTACK_TIME.set(to, SoyProperties.ATTACK_TIME.get(from));
    }

    public static void copyTo(TitanInstance from, TitanInstance to) {
        copyPropertiesToEntity(from.entity, to.entity);
        to.titan = from.titan;
        to.variant = from.variant;
        to.stacks = new ArrayList<>(from.stacks);
        to.playerInventory = from.playerInventory;
        to.deaths = from.deaths;
        to.memoryManager = from.memoryManager;
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
        inst.isCorpse = tag.contains("IsCorpse") && tag.getBoolean("IsCorpse");
        if (entity instanceof Player) {
            inst.playerInventory = tag.contains("PlayerInventory", Tag.TAG_LIST) ? tag.getList("PlayerInventory", Tag.TAG_COMPOUND) : null;
        }
        inst.deaths = tag.contains("Deaths") ? tag.getInt("Deaths") : 0;
        inst.memoryManager = tag.contains("MemoryManager") ? TitanMemoryManager.fromTag(tag.getCompound("MemoryManager")) : null;
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
        if (this.playerInventory != null) tag.put("PlayerInventory", this.playerInventory);
        tag.putInt("Deaths", this.deaths);
        if (this.memoryManager != null) tag.put("MemoryManager", this.memoryManager.toTag());
        return tag;
    }

    @Override
    public String toString() {
        return "TitanInstance{" +
                "titan=" + titan +
                ", stacks=" + stacks +
                ", variant='" + variant + '\'' +
                ", entity=" + entity +
                ", isCorpse=" + isCorpse +
                '}';
    }
}
