package net.phantompig.soy.titan;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class TitanInstance {
    public final Titan titan;
    public final ArrayList<Titan> stacks = new ArrayList<>();

    public int progress = 0;

    public TitanInstance() {
        titan = null;
    }

    public TitanInstance(Titan titan, List<Titan> stacks) {
        this.titan = titan;
        this.stacks.addAll(stacks);
    }

    public static TitanInstance fromTag(CompoundTag tag) {
        TitanInstance inst = new TitanInstance(
                TitanRegistry.getTitan(new ResourceLocation(tag.getString("Titan"))),
                tag.getList("Stacks", 5).stream().map(t -> TitanRegistry.getTitan(new ResourceLocation(t.getAsString()))).toList()
        );
        inst.progress = tag.getInt("Progress");
        return inst;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Titan", this.titan.id.toString());
        ListTag stacks = new ListTag();
        stacks.addAll(this.stacks.stream().map(ti -> StringTag.valueOf(ti.id.toString())).toList());
        tag.put("Stacks", stacks);
        tag.putInt("Progress", this.progress);
        return tag;
    }
}
