package net.phantompig.soy.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoyBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.BLOCK);

    public static final RegistrySupplier<Block> ICEBURST_ORE = BLOCKS.register("iceburst_ore", () -> new IceburstBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).requiresCorrectToolForDrops().sound(SoundType.STONE).strength(2.8F, 4), 10));
    public static final RegistrySupplier<Block> DEEPSLATE_ICEBURST_ORE = BLOCKS.register("deepslate_iceburst_ore", () -> new IceburstBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE).strength(4.3f, 6), 5));

    public static final RegistrySupplier<Block> IRON_BAMBOO = BLOCKS.register("iron_bamboo", () -> new IronBambooBlock(BlockBehaviour.Properties.of().forceSolidOn().mapColor(MapColor.COLOR_GRAY).offsetType(BlockBehaviour.OffsetType.XZ).noOcclusion().dynamicShape().sound(SoundType.METAL).strength(1.5f, 0.5f).pushReaction(PushReaction.DESTROY).isRedstoneConductor((e, g, p) -> true)));
    public static final RegistrySupplier<Block> PACKED_IRON_BAMBOO = BLOCKS.register("packed_iron_bamboo", () -> new PackedIronBambooBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().sound(SoundType.METAL).strength(24, 14)));

    public static final RegistrySupplier<Block> COMPRESSION_TABLE = BLOCKS.register("compression_table", () -> new CompressionTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOD).strength(2f, 1f)));

    public static final RegistrySupplier<Block> HARDENING_BLOCK = BLOCKS.register("hardening_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.AMETHYST).strength(40, 24).requiresCorrectToolForDrops()));

    public static void init() {
        BLOCKS.register();
        SoyBlockTags.init();
    }
}
