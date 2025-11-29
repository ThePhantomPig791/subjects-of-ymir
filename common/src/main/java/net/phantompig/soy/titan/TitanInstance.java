package net.phantompig.soy.titan;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TitanInstance {
    @Nullable
    public final Titan titan;
    public final ArrayList<Titan> stacks = new ArrayList<>();

    public int progress = 0;

    public boolean forceUnshift = false;

    public TitanInstance() {
        titan = null;
    }

    public TitanInstance(@Nullable Titan titan) {
        this.titan = titan;
    }

    public TitanInstance(@Nullable Titan titan, List<Titan> stacks) {
        this.titan = titan;
        this.stacks.addAll(stacks);
    }


    public float getScale() {
        if (titan == null) return 1;
        return titan.scale * progress / titan.maxProgress;
    }

    public boolean is(ResourceLocation id) {
        return this.titan != null && this.titan.id.equals(id);
    }


    public static TitanInstance fromTag(CompoundTag tag) {
        if (tag.isEmpty()) return new TitanInstance();
        ResourceLocation id = new ResourceLocation(tag.getString("Titan"));
        TitanInstance inst = new TitanInstance(
                TitanRegistry.getTitan(id),
                tag.getList("Stacks", 5).stream().map(t -> TitanRegistry.getTitan(new ResourceLocation(t.getAsString()))).toList()
        );
        inst.progress = tag.getInt("Progress");
        return inst;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        if (this.titan == null) return tag;
        tag.putString("Titan", this.titan.id.toString());
        ListTag stacks = new ListTag();
        stacks.addAll(this.stacks.stream().map(ti -> StringTag.valueOf(ti.id.toString())).toList());
        tag.put("Stacks", stacks);
        tag.putInt("Progress", this.progress);
        return tag;
    }
}
