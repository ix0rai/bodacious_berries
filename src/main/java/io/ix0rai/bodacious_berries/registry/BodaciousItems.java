package io.ix0rai.bodacious_berries.registry;

import io.ix0rai.bodacious_berries.item.ChorusBerries;
import io.ix0rai.bodacious_berries.item.GojiBerries;
import io.ix0rai.bodacious_berries.item.Rainberries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class BodaciousItems {
    public static final TagKey<Item> BERRY_TAG = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "berries"));

    // berry items
    public static final AliasedBlockItem SASKATOON_BERRIES = new AliasedBlockItem(BodaciousBushes.SASKATOON_BERRY_BUSH, settings(3, 0.5f));
    public static final AliasedBlockItem STRAWBERRIES = new AliasedBlockItem(BodaciousBushes.STRAWBERRY_BUSH, settings(3, 1f));
    public static final AliasedBlockItem RASPBERRIES = new AliasedBlockItem(BodaciousBushes.RASPBERRY_BUSH, settings(3, 0.5f));
    public static final AliasedBlockItem BLACKBERRIES = new AliasedBlockItem(BodaciousBushes.BLACKBERRY_BUSH, settings(1, 1.5f));
    public static final AliasedBlockItem CHORUS_BERRIES = new ChorusBerries(BodaciousBushes.CHORUS_BERRY_BUSH, settings(2, 1.6f));
    public static final AliasedBlockItem RAINBERRIES = new Rainberries(BodaciousBushes.RAINBERRY_BUSH, settings(3, 1.0f));
    public static final AliasedBlockItem LINGONBERRIES = new AliasedBlockItem(BodaciousBushes.LINGONBERRY_BUSH, settings(2, 0.5f));
    public static final AliasedBlockItem GRAPES = new AliasedBlockItem(BodaciousBushes.GRAPEVINE, settings(2, 1f));
    public static final AliasedBlockItem GOJI_BERRIES = new GojiBerries(BodaciousBushes.GOJI_BERRY_BUSH, settings(1, 2.2f));
    public static final AliasedBlockItem GOOSEBERRIES = new AliasedBlockItem(BodaciousBushes.GOOSEBERRY_BUSH, settings(2, 0.5f));
    public static final AliasedBlockItem CLOUDBERRIES = new AliasedBlockItem(BodaciousBushes.CLOUDBERRY_BUSH, new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturation(1f).statusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 600, 1), 1).snack().build()));

    public static void register() {
        register(SASKATOON_BERRIES, Berry.SASKATOON_BERRIES);
        register(STRAWBERRIES, Berry.STRAWBERRIES);
        register(RASPBERRIES, Berry.RASPBERRIES);
        register(BLACKBERRIES, Berry.BLACKBERRIES);
        register(CHORUS_BERRIES, Berry.CHORUS_BERRIES);
        register(RAINBERRIES, Berry.RAINBERRIES);
        register(LINGONBERRIES, Berry.LINGONBERRIES);
        register(GRAPES, Berry.GRAPES);
        register(GOJI_BERRIES, Berry.GOJI_BERRIES);
        register(GOOSEBERRIES, Berry.GOOSEBERRIES);
        register(CLOUDBERRIES, Berry.CLOUDBERRIES);

        BodaciousJuices.register();
    }

    /**
     * the chance that a berry will raise the level of compost in a composter
     * <br> out of one: 0.4 = 40%, etc
     */
    private static final float BERRY_COMPOST_LEVEL_INCREASE_CHANCE = 0.4f;

    private static void register(Item item, Berry id) {
        Item registeredItem = Registry.register(Registries.ITEM, id.get(), item);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.addItem(registeredItem));
        CompostingChanceRegistry.INSTANCE.add(item, BERRY_COMPOST_LEVEL_INCREASE_CHANCE);
    }

    private static Item.Settings settings(int hunger, float saturation) {
        return new Item.Settings().food(new FoodComponent.Builder().hunger(hunger).saturation(saturation).snack().build());
    }
}
