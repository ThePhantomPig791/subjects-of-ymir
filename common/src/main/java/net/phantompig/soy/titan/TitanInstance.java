package net.phantompig.soy.titan;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.property.SubjectsOfYmirProperties;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TitanInstance {
    @Nullable
    public final Titan titan;
    public final ArrayList<Titan> stacks = new ArrayList<>();

    public final LivingEntity entity;

    public boolean forceUnshift = false;

    public TitanInstance(LivingEntity entity) {
        this(entity, null);
    }

    public TitanInstance(LivingEntity entity, @Nullable Titan titan) {
        this.entity = entity;
        this.titan = titan;
    }

    public TitanInstance(LivingEntity entity, @Nullable Titan titan, List<Titan> stacks) {
        this(entity, titan);
        this.stacks.addAll(stacks);
    }


    public int getProgress() {
        return SubjectsOfYmirProperties.PROGRESS.get(this.entity);
    }

    public void setProgress(int progress) {
        SubjectsOfYmirProperties.PROGRESS.set(this.entity, progress);
    }


    public boolean is(ResourceLocation id) {
        return this.titan != null && this.titan.id.equals(id);
    }



    public static TitanInstance fromTag(LivingEntity entity, CompoundTag tag) {
        if (tag.isEmpty()) return new TitanInstance(entity);
        ResourceLocation id = new ResourceLocation(tag.getString("Titan"));
        return new TitanInstance(
                entity,
                TitanRegistry.getTitan(id),
                tag.getList("Stacks", 5).stream().map(t -> TitanRegistry.getTitan(new ResourceLocation(t.getAsString()))).toList()
        );
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        if (this.titan == null) return tag;
        tag.putString("Titan", this.titan.id.toString());
        ListTag stacks = new ListTag();
        stacks.addAll(this.stacks.stream().map(ti -> StringTag.valueOf(ti.id.toString())).toList());
        tag.put("Stacks", stacks);
        return tag;
    }
}
