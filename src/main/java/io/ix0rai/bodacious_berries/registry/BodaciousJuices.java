package io.ix0rai.bodacious_berries.registry;

import io.ix0rai.bodacious_berries.BodaciousBerries;
import io.ix0rai.bodacious_berries.item.ChorusBerryJuice;
import io.ix0rai.bodacious_berries.item.EndBlend;
import io.ix0rai.bodacious_berries.item.GojiBerryBlend;
import io.ix0rai.bodacious_berries.item.Juice;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import static net.minecraft.world.biome.Biomes.*;

public class BodaciousJuices {
    public static final Item JUICE_RECEPTACLE = Items.GLASS_BOTTLE;
    public static final Item.Settings JUICE_SETTINGS = new Item.Settings().recipeRemainder(JUICE_RECEPTACLE).maxCount(16);
    public static final Juice DUBIOUS_JUICE = new Juice(JUICE_SETTINGS.food(new FoodComponent.Builder().hunger(2).saturation(2F).build()));

    public static void register() {
        register(BodaciousBerries.id("dubious_juice"), DUBIOUS_JUICE);
        register("saskatoon_berry_juice", new Juice(BodaciousItems.SASKATOON_BERRIES, BodaciousBerries.translatableText("hint.more_purple")));
        register("strawberry_juice", new Juice(BodaciousItems.STRAWBERRIES));
        register("raspberry_juice", new Juice(BodaciousItems.RASPBERRIES));
        register("blackberry_juice", new Juice(BodaciousItems.BLACKBERRIES, BodaciousBerries.translatableText("hint.blackberries_and_sweetness")));
        register("rainberry_juice", new Juice(BodaciousItems.RAINBERRIES));
        register("lingonberry_juice", new Juice(BodaciousItems.LINGONBERRIES, BodaciousBerries.translatableText("hint.more_red")));
        register("grape_juice", new Juice(BodaciousItems.GRAPES));
        register("goji_berry_juice", new Juice(BodaciousItems.GOJI_BERRIES, BodaciousBerries.translatableText("hint.sweetness")));
        register("gooseberry_juice", new Juice(BodaciousItems.GOOSEBERRIES, BodaciousBerries.translatableText("hint.red_and_yellow")));
        register("glow_berry_juice", new Juice(Items.GLOW_BERRIES, new FoodComponent.Builder().statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 90, 1), 1.0F)));
        register("sweet_berry_juice", new Juice(Items.SWEET_BERRIES, BodaciousBerries.translatableText("hint.vanilla")));
        register("chorus_berry_juice", new ChorusBerryJuice(BodaciousItems.CHORUS_BERRIES, null));
        register("cloudberry_juice", new Juice(BodaciousItems.CLOUDBERRIES, new FoodComponent.Builder().statusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1200, 1), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 600, 1), 1.0f)));

        createBiomeChorusJuice();
        registerBlends();
    }

    private static void createBiomeChorusJuice() {
        RegistryKey<?>[] biomes = new RegistryKey<?>[]{
                PLAINS, SNOWY_SLOPES, SWAMP,
                DESERT, TAIGA, BIRCH_FOREST,
                OCEAN, MUSHROOM_FIELDS, SUNFLOWER_PLAINS,
                FOREST, FLOWER_FOREST, DARK_FOREST,
                SAVANNA, BADLANDS, MEADOW,
                LUSH_CAVES, DRIPSTONE_CAVES, JUNGLE
        };

        for (RegistryKey<?> key : biomes) {
            ChorusBerryJuice juice = new ChorusBerryJuice(BodaciousItems.CHORUS_BERRIES, key.getValue());
            Identifier id = BodaciousBerries.id("chorus_berry_juice_" + key.getValue().getPath());
            Registry.register(Registries.ITEM, id, juice);
        }
    }

    private static void registerBlends() {
        register("goji_berry_blend", new GojiBerryBlend(JUICE_SETTINGS.food(new FoodComponent.Builder().hunger(5).saturation(1.8F).statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 800, 1), 1.0F).build())));
        register("opposite_juice", new Juice(new FoodComponent.Builder().hunger(4).saturation(1.2F).build()));
        register("rainberry_blend", new Juice(JUICE_SETTINGS.food(new FoodComponent.Builder().hunger(6).saturation(1.6F).build())));
        register("gooseberry_rum", new Juice(JUICE_SETTINGS.food(new FoodComponent.Builder().hunger(7).saturation(1.0f).build())));
        register("red_juice", new Juice(JUICE_SETTINGS.food(new FoodComponent.Builder().hunger(5).saturation(1.3F).build())));
        register("end_blend", new EndBlend(JUICE_SETTINGS.food(new FoodComponent.Builder().hunger(3).saturation(3.3F).build())));
        register("purple_delight", new Juice(JUICE_SETTINGS.food(new FoodComponent.Builder().hunger(6).saturation(1.6F).build())));
        register("traffic_light_juice", new Juice(JUICE_SETTINGS.food(new FoodComponent.Builder().hunger(5).saturation(1.4F).build())));
        register("vanilla_delight", new Juice(JUICE_SETTINGS.food(new FoodComponent.Builder().hunger(4).saturation(2.0F).build())));
    }

    private static void register(String name, Juice juice) {
        Identifier id = BodaciousBerries.id(name);
        register(id, juice);
    }

    private static void register(Identifier id, Juice juice) {
        Item item = Registry.register(Registries.ITEM, id, juice);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINKS).register(entries -> entries.addItem(item));
    }
}
