package io.ix0rai.bodacious_berries.block.entity;

import io.ix0rai.bodacious_berries.block.BerryBush;
import io.ix0rai.bodacious_berries.block.BerryHarvesterBlock;
import io.ix0rai.bodacious_berries.block.DoubleBerryBush;
import io.ix0rai.bodacious_berries.client.particle.Particles;
import io.ix0rai.bodacious_berries.registry.BodaciousBlocks;
import io.ix0rai.bodacious_berries.registry.BodaciousSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.HolderLookup;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BerryHarvesterBlockEntity extends BlockEntity implements ImplementedInventory, SidedInventory, NamedScreenHandlerFactory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
    private int tickCounter;
    private static final int ATTEMPT_HARVEST_ON = 100;

    public BerryHarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(BodaciousBlocks.BERRY_HARVESTER_ENTITY, pos, state);
    }

    @Override
    public void method_11014(NbtCompound nbt, HolderLookup.Provider lookupProvider) {
        Inventories.readNbt(nbt, inventory, lookupProvider);
    }

    @Override
    public void writeNbt(NbtCompound nbt, HolderLookup.Provider lookupProvider) {
        Inventories.writeNbt(nbt, inventory, lookupProvider);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.of(this);
    }

    @Override
    public NbtCompound toSyncedNbt(HolderLookup.Provider lookupProvider) {
        return toNbt(lookupProvider);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BerryHarvesterBlockEntity harvester) {
        if (harvester.tickCounter++ >= ATTEMPT_HARVEST_ON && !world.isReceivingRedstonePower(pos)) {
            if (!harvester.isInventoryFull()) {
                // block the harvester is facing must be a berry bush
                BlockPos bushPos = pos.offset(state.get(BerryHarvesterBlock.FACING));
                BlockState bush = world.getBlockState(bushPos);
                Block block = bush.getBlock();

                // also ensure bush is ready to harvest
                if (block instanceof BerryBush berryBush && berryBush.isFullyGrown(bush)) {
                    BlockPos secondaryBushPos = null;
                    BlockState secondaryBushState = null;

                    if (block instanceof DoubleBerryBush) {
                        secondaryBushPos = world.getBlockState(bushPos.up()).getBlock() instanceof DoubleBerryBush ? bushPos.up() : bushPos.down();
                        BlockState secondaryBush = world.getBlockState(secondaryBushPos);
                        if (secondaryBush.getBlock() instanceof DoubleBerryBush doubleBerryBush && doubleBerryBush.isFullyGrown(secondaryBush)) {
                            secondaryBushState = secondaryBush;
                        }
                    }

                    // insert items, doubling if we have a double bush
                    harvester.insert(new ItemStack(berryBush.getBerryItem(), berryBush.getMaxBerryAmount() * (secondaryBushState == null ? 1 : 2) - 1));

                    // play pick sound and reset bush growth
                    world.playSound(null, pos, BodaciousSounds.BERRY_PICK, SoundCategory.BLOCKS, 0.3F, 1.5F);
                    addParticles(world, bushPos);
                    berryBush.resetAge(world, bushPos, bush);
                    if (secondaryBushState != null) {
                        addParticles(world, secondaryBushPos);
                        berryBush.resetAge(world, secondaryBushPos, secondaryBushState);
                    }
                } else if (block instanceof SweetBerryBushBlock) {
                    handleSweetBerry(world, bushPos, bush, harvester);
                }
            }

            harvester.tickCounter = 0;
        }
    }

    private static void handleSweetBerry(World world, BlockPos bushPos, BlockState bushState, BerryHarvesterBlockEntity harvester) {
        // is fully grown
        if (bushState.get(SweetBerryBushBlock.AGE) >= 3) {
            harvester.insert(new ItemStack(Items.SWEET_BERRIES, 3));
            addParticles(world, bushPos);

            // reset bush
            world.setBlockState(bushPos, bushState.with(SweetBerryBushBlock.AGE, 1));
        }
    }

    private static void addParticles(World world, BlockPos pos) {
        for (int i = 0; i < 6; i++) {
            world.addParticle(Particles.SLICEY_PARTICLE, pos.getX() + 0.5D, pos.getY() + 0.3D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
        }
    }

    public boolean isInventoryFull() {
        for (ItemStack stack : inventory) {
            if (stack.getCount() <= stack.getMaxCount()) {
                return false;
            }
        }

        return true;
    }

    /**
     * inserts an {@link ItemStack} into the inventory
     * <br> WARNING: this method will ignore items that do not fit
     * @param stack the items to insert
     */
    public void insert(ItemStack stack) {
        // find an open slot and insert items
        for (int i = 0; i < this.getInventory().size(); i++) {
            // get items currently in slot
            ItemStack slot = this.getInventory().get(i);
            int maxAmount = slot.getMaxCount();

            if ((slot.isEmpty() || slot.getItem().equals(stack.getItem())) && slot.getCount() <= maxAmount) {
                stack.setCount(slot.getCount() + stack.getCount());
                // if stack overflows available space
                if (stack.getCount() > maxAmount) {
                    // split the stack, inserting one stack of 64 and running the remainder back through the loop
                    this.setStack(i, new ItemStack(stack.getItem(), maxAmount));
                    stack.decrement(maxAmount);
                } else {
                    // if stack fits, insert it
                    this.setStack(i, stack);
                    break;
                }
            }
        }
    }

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        int[] slots = new int[getInventory().size()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }

        return slots;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BerryHarvesterScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public Text getDisplayName() {
        return Text.of(getCachedState().getBlock().getTranslationKey());
    }
}
