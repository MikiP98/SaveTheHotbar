package io.github.mikip98.savethehotbar.blockentities;

import io.github.mikip98.savethehotbar.SaveTheHotbar;
import io.github.mikip98.savethehotbar.modDetection.SupportedSlotMods;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
#if MC_VERSION >= 12006
import net.minecraft.registry.RegistryWrapper;
#endif
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class GraveContainerBlockEntity extends BlockEntity implements GraveContainerInventory, SidedInventory {
    protected final DefaultedList<ItemStack> items = DefaultedList.ofSize(41, ItemStack.EMPTY);
    protected final EnumMap<SupportedSlotMods, DefaultedList<ItemStack>> moddedItems = createEnumMap();
    protected int exp = 0;

    protected EnumMap<SupportedSlotMods, DefaultedList<ItemStack>> createEnumMap() {
        EnumMap<SupportedSlotMods, DefaultedList<ItemStack>> moddedItems = new EnumMap<>(SupportedSlotMods.class);
        for (SupportedSlotMods mod : SupportedSlotMods.values()) {
            if (mod.isLoaded()) moddedItems.put(mod, DefaultedList.ofSize(mod.slotAmount, ItemStack.EMPTY));
        }
        return moddedItems;
    }

    public GraveContainerBlockEntity(BlockPos pos, BlockState state) {
        super(SaveTheHotbar.GRAVE_CONTAINER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        final List<ItemStack> allItems = Stream.concat(this.items.stream(), this.moddedItems.values().stream().flatMap(List::stream)).toList();
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(allItems.size(), ItemStack.EMPTY);
        for (int i = 0; i < allItems.size(); i++) { defaultedList.set(i, allItems.get(i)); }
        return defaultedList;
    }
    public int getExp() {
        return exp;
    }

    public void setItems(List<ItemStack> vanillaItems, Map<SupportedSlotMods, List<ItemStack>> moddedItems) {
        this.items.clear();
        for (int i = 0; i < vanillaItems.size(); i++) {
            this.items.set(i, vanillaItems.get(i));
        }
        for (SupportedSlotMods mod : SupportedSlotMods.values()) {
            if (mod.isLoaded()) {
                final List<ItemStack> items = moddedItems.get(mod);
                DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(items.size(), ItemStack.EMPTY);
                for (int i = 0; i < items.size(); i++) {
                    defaultedList.set(i, items.get(i));
                }
                this.moddedItems.put(mod, defaultedList);
            }
        }
    }
    public void setExp(int exp) {
        this.exp = exp;
    }

    @Override
    public void readNbt(NbtCompound nbt #if MC_VERSION >= 12006, RegistryWrapper.WrapperLookup registryLookup #endif) {
        super.readNbt(nbt #if MC_VERSION >= 12006, registryLookup #endif);
        Inventories.readNbt(nbt, items #if MC_VERSION >= 12006, registryLookup #endif);
        for (Map.Entry<SupportedSlotMods, DefaultedList<ItemStack>> entry : moddedItems.entrySet()) {
            tryReadModdedItemNbt(nbt, entry.getValue(), entry.getKey() #if MC_VERSION >= 12006, registryLookup #endif);
        }
        this.exp = nbt.getInt("Experience");
    }

    /**
     * Code taken from 'Inventories.readNBT()'
     * Located in package 'net.minecraft.inventory'
     * Modified to accept a mod
     */
    public static void tryReadModdedItemNbt(
            NbtCompound nbt, DefaultedList<ItemStack> stacks, SupportedSlotMods mod
            #if MC_VERSION >= 12006, RegistryWrapper.WrapperLookup registryLookup #endif
    ) {
        if (!mod.isLoaded()) return;

        final String nbtId = "Items" + mod.modName;
        if (!nbt.contains(nbtId)) return;

        NbtList nbtList = nbt.getList(nbtId, 10);

        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            if (j < stacks.size()) {
                #if MC_VERSION < 12006
                stacks.set(j, ItemStack.fromNbt(nbtCompound));
                #else
                stacks.set(j, ItemStack.fromNbt(registryLookup, nbtCompound).get());
                #endif
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt #if MC_VERSION >= 12006, RegistryWrapper.WrapperLookup registryLookup #endif) {
        Inventories.writeNbt(nbt, items #if MC_VERSION >= 12006, registryLookup #endif);
        for (Map.Entry<SupportedSlotMods, DefaultedList<ItemStack>> entry : moddedItems.entrySet()) {
            writeNbt(nbt, entry.getValue(), entry.getKey() #if MC_VERSION >= 12006, registryLookup #endif);
        }
        nbt.putInt("Experience", this.exp);
        super.writeNbt(nbt #if MC_VERSION >= 12006, registryLookup #endif);
    }

    /**
     * Code taken from 'Inventories.writeNbt()'
     * Located in package 'net.minecraft.inventory'
     * Modified to accept a mod
     */
    public static void writeNbt(
            NbtCompound nbt, DefaultedList<ItemStack> stacks, SupportedSlotMods mod
            #if MC_VERSION >= 12006, RegistryWrapper.WrapperLookup registryLookup #endif
    ) {
        if (!mod.isLoaded() || stacks.isEmpty() || stacks.stream().allMatch((stack) -> stack == ItemStack.EMPTY)) return;
        NbtList nbtList = new NbtList();

        for (int i = 0; i < stacks.size(); i++) {
            ItemStack itemStack = stacks.get(i);
            if (!itemStack.isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte) i);
                #if MC_VERSION < 12006
                itemStack.writeNbt(nbtCompound);
                #else
                itemStack.encode(registryLookup, nbtCompound);
                #endif
                nbtList.add(nbtCompound);
            }
        }

        nbt.put("Items" + mod.modName, nbtList);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    #if MC_VERSION < 12006
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
    #else
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
    #endif


    @Override
    public int[] getAvailableSlots(Direction side) { return new int[0]; }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        // No input
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        // Output from the bottom
        return dir == Direction.DOWN;
    }
}
