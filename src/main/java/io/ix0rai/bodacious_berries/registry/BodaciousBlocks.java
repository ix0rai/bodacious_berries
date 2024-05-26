package io.ix0rai.bodacious_berries.registry;

import io.ix0rai.bodacious_berries.BodaciousBerries;
import io.ix0rai.bodacious_berries.block.BerryHarvesterBlock;
import io.ix0rai.bodacious_berries.block.JuicerBlock;
import io.ix0rai.bodacious_berries.block.entity.BerryHarvesterBlockEntity;
import io.ix0rai.bodacious_berries.block.entity.BerryHarvesterScreenHandler;
import io.ix0rai.bodacious_berries.block.entity.JuicerBlockEntity;
import io.ix0rai.bodacious_berries.block.entity.JuicerScreenHandler;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.feature_flags.FeatureFlags;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class BodaciousBlocks {
    public static final Identifier BERRY_HARVESTER = BodaciousBerries.id("berry_harvester");
    public static final BerryHarvesterBlock BERRY_HARVESTER_BLOCK = new BerryHarvesterBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).strength(4.0f));
    public static final BlockEntityType<BerryHarvesterBlockEntity> BERRY_HARVESTER_ENTITY = BlockEntityType.Builder.create(BerryHarvesterBlockEntity::new, BERRY_HARVESTER_BLOCK).build(null);
    public static final ScreenHandlerType<BerryHarvesterScreenHandler> BERRY_HARVESTER_SCREEN_HANDLER = new ScreenHandlerType<>(BerryHarvesterScreenHandler::new, FeatureFlags.DEFAULT_SET);

    public static final Identifier JUICER = BodaciousBerries.id("juicer");
    public static final JuicerBlock JUICER_BLOCK = new JuicerBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).strength(4.0f));
    public static final BlockEntityType<JuicerBlockEntity> JUICER_ENTITY = BlockEntityType.Builder.create(JuicerBlockEntity::new, JUICER_BLOCK).build(null);
    public static final ScreenHandlerType<JuicerScreenHandler> JUICER_SCREEN_HANDLER = new ScreenHandlerType<>(JuicerScreenHandler::new, FeatureFlags.DEFAULT_SET);

    public static void register() {
        register(BERRY_HARVESTER, BERRY_HARVESTER_BLOCK, BERRY_HARVESTER_ENTITY, BERRY_HARVESTER_SCREEN_HANDLER, ItemGroups.REDSTONE_BLOCKS);
        register(JUICER, JUICER_BLOCK, JUICER_ENTITY, JUICER_SCREEN_HANDLER, ItemGroups.FUNCTIONAL_BLOCKS);

        BodaciousBushes.register();
    }

    private static void register(Identifier id, Block block, BlockEntityType<?> entity, ScreenHandlerType<?> handler, RegistryKey<ItemGroup> group) {
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.SCREEN_HANDLER_TYPE, id, handler);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, BodaciousBerries.id(id.getPath() + "_entity"), entity);
        Item item = Registry.register(Registries.ITEM, id, new BlockItem(block, new Settings()));
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.addItem(item));
    }
}
