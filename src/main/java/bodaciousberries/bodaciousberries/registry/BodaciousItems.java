package bodaciousberries.bodaciousberries.registry;

import bodaciousberries.bodaciousberries.Bodaciousberries;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

import static bodaciousberries.bodaciousberries.registry.Bushes.*;

public class BodaciousItems {
    //berry items
    public static Item SASKATOON_BERRIES;
    public static Item UNRIPE_SASKATOON_BERRIES;
    public static Item STRAWBERRIES;
    public static Item RASPBERRIES;
    public static Item BLACKBERRIES;

    public static void registerItems() {
        //create items for each berry
        SASKATOON_BERRIES = new AliasedBlockItem(SASKATOON_BERRY_BUSH, new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().hunger(2).saturationModifier(2f).snack().build()));
        UNRIPE_SASKATOON_BERRIES = new Item(new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().hunger(1).saturationModifier(2f).snack().build()));
        STRAWBERRIES = new AliasedBlockItem(STRAWBERRY_BUSH, new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().hunger(3).saturationModifier(1.5f).snack().build()));
        RASPBERRIES = new AliasedBlockItem(RASPBERRY_BUSH, new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().hunger(1).saturationModifier(4f).snack().build()));
        BLACKBERRIES = new AliasedBlockItem(BLACKBERRY_BUSH, new Item.Settings().group(ItemGroup.FOOD).food(new FoodComponent.Builder().hunger(1).saturationModifier(3.5f).snack().build()));

        //automatic stuffs
        Berries.addDoubleBushToList(SASKATOON_BERRY_BUSH, DOUBLE_SASKATOON_BERRY_BUSH, SASKATOON_BERRIES, UNRIPE_SASKATOON_BERRIES);
        Berries.addToList(STRAWBERRY_BUSH, STRAWBERRIES, null);
        Berries.addToList(RASPBERRY_BUSH, RASPBERRIES, null);
        Berries.addToList(BLACKBERRY_BUSH, BLACKBERRIES, null);
        Berries.initialiseBerries();

        //register
        Registry.register(Registry.ITEM, Bodaciousberries.getIdentifier("saskatoon_berries"), SASKATOON_BERRIES);
        Registry.register(Registry.ITEM, Bodaciousberries.getIdentifier("unripe_saskatoon_berries"), UNRIPE_SASKATOON_BERRIES);
        Registry.register(Registry.ITEM, Bodaciousberries.getIdentifier("strawberries"), STRAWBERRIES);
        Registry.register(Registry.ITEM, Bodaciousberries.getIdentifier("raspberries"), RASPBERRIES);
        Registry.register(Registry.ITEM, Bodaciousberries.getIdentifier("blackberries"), BLACKBERRIES);
    }
}
