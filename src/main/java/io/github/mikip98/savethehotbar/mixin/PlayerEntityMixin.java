package io.github.mikip98.savethehotbar.mixin;

import io.github.mikip98.savethehotbar.config.enums.ContainDropMode;
import io.github.mikip98.savethehotbar.deathProcessing.DeathManager;
import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.modSupport.GravestoneConfiguration;
import io.github.mikip98.savethehotbar.modDetection.SupportedGraveMods;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Shadow
    public abstract @Nullable ItemEntity dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership);

    @Shadow
    private @Final PlayerInventory inventory;

    @Inject(method = "dropInventory", at = @At("HEAD"), cancellable = true)
    private void dropInventory(CallbackInfo ci) {
        if (ModConfig.enable) {
            try {
                final PlayerEntity player = this.inventory.player;
                final World world = player.getWorld();

                // Enable KeepInventory if disabled
                keepInventoryCheck(world);
                // Enable Gravestone spawning with keepInventory if disabled
                graveStoneCheck(world);

                final DeathManager deathManager = new DeathManager(inventory, this::dropItem);
                deathManager.managePlayerDeath();
                if (!(ModConfig.containDrop && ModConfig.containDropMode == ContainDropMode.GRAVE && SupportedGraveMods.PNEUMONO_GRAVESTONES.isLoaded())) ci.cancel();
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("An error occurred while dropping inventory", e);
            }
        }
    }

    @Unique
    private void doublePrintWarn(String message) {
        inventory.player.sendMessage(Text.literal(message).formatted(Formatting.YELLOW), false);
        LOGGER.warn(message);
    }

    @Unique
    private void keepInventoryCheck(World world) {
        if (!world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            doublePrintWarn("KeepInventory GameRule is False; 'SaveTheHotbar!' requires keepInventory to work; Changing keepInventory to True; If you want to disable 'SaveTheHotbar!', disable it in settings");
            if (!world.isClient()) {
                world.getGameRules().get(GameRules.KEEP_INVENTORY).set(true, world.getServer());
            }
        }
    }

    @Unique
    private void graveStoneCheck(World world) {
        if (ModConfig.containDrop && ModConfig.containDropMode == ContainDropMode.GRAVE && SupportedGraveMods.PNEUMONO_GRAVESTONES.isLoaded()) {
            GravestoneConfiguration.gravestoneCheck(this::doublePrintWarn, world.getServer());
        }
    }
}
