package io.ix0rai.bodacious_berries.item;

import io.ix0rai.bodacious_berries.registry.BodaciousJuices;
import net.minecraft.client.item.TooltipConfig;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.security.InvalidParameterException;
import java.util.List;

public class Juice extends HoneyBottleItem {
    private final MutableText hintTooltip;

    public Juice(Item berry) {
        this(berry, new FoodComponent.Builder(), null);
    }

    public Juice(Item berry, MutableText hintTooltip) {
        this(berry, new FoodComponent.Builder(), hintTooltip);
    }

    public Juice(Item berry, FoodComponent.Builder builder) {
        this(berry, builder, null);
    }

    public Juice(Item berry, FoodComponent.Builder builder, MutableText hintTooltip) {
        super(settings(berry, builder));
        this.hintTooltip = hintTooltip;
    }

    public Juice(Item.Settings settings) {
        super(settings);
        this.hintTooltip = null;
    }

    public Juice(FoodComponent component) {
        super(new Settings().food(component));
        this.hintTooltip = null;
    }

    public static Settings settings(Item berry, FoodComponent.Builder builder) {
        FoodComponent foodComponent = berry.getComponents().get(DataComponentTypes.FOOD);
        if (foodComponent != null) {
            return BodaciousJuices.JUICE_SETTINGS.food(builder.hunger(foodComponent.nutrition() * 2).saturation(foodComponent.saturation() * 1.5f).build());
        }

        throw new InvalidParameterException("item: " + berry + " does not have a food component");
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<net.minecraft.text.Text> tooltip, TooltipConfig config) {
        super.appendTooltip(stack, context, tooltip, config);
        if (hintTooltip != null) {
            tooltip.add(hintTooltip.formatted(Formatting.DARK_PURPLE, Formatting.ITALIC));
        }
    }
}
