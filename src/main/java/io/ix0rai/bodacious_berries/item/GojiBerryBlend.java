package io.ix0rai.bodacious_berries.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Iterator;

public class GojiBerryBlend extends Juice {
    public GojiBerryBlend(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // note: a mod conflict somehow causes a cme here, so we use an iterator over an enhanced for
        if (!user.getStatusEffects().isEmpty()) {
            Iterator<StatusEffectInstance> iterator = user.getStatusEffects().iterator();
            do {
                StatusEffectInstance instance = iterator.next();
                if (instance.getEffectType().value().getType().equals(StatusEffectType.HARMFUL)) {
                    user.removeStatusEffect(instance.getEffectType());
                }
            } while (iterator.hasNext());
        }

        return super.finishUsing(stack, world, user);
    }
}
