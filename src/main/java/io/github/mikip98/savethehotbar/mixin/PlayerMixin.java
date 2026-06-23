package io.github.mikip98.savethehotbar.mixin;

import io.github.mikip98.savethehotbar.config.enums.ContainDropMode;
import io.github.mikip98.savethehotbar.deathProcessing.DeathManager;
import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.mcVersionAgnosticUtils.PlayerUtils;
import io.github.mikip98.savethehotbar.modSupport.GravestoneConfiguration;
import io.github.mikip98.savethehotbar.modDetection.SupportedGraveMods;
import net.minecraft.server.MinecraftServer;
#if MC_VERSION >= 12106 import net.minecraft.world.entity.LivingEntity; #endif
#if MC_VERSION < 12106 import net.minecraft.world.entity.item.ItemEntity; #endif
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
#if MC_VERSION < 12106 import net.minecraft.world.item.ItemStack; #endif
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
#if MC_VERSION < 12111 import net.minecraft.world.level.GameRules; #endif
import net.minecraft.world.level.Level;
#if MC_VERSION < 12106 import org.jetbrains.annotations.Nullable; #endif
#if MC_VERSION >= 12111 import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules; #endif
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

@Mixin(Player.class)
public abstract class PlayerMixin {
    #if MC_VERSION < 12106
    @Shadow
    public abstract @Nullable ItemEntity drop(ItemStack stack, boolean throwRandomly, boolean retainOwnership);
    #endif

    @Shadow
    private @Final Inventory inventory;

    #if MC_VERSION >= 12106 @SuppressWarnings("ConstantConditions") #endif
    @Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
    private void dropInventory(CallbackInfo ci) {
        if (ModConfig.INSTANCE.enable) {
            try {
                final Player player = this.inventory.player;
                final Level world = player.level();

                // Enable KeepInventory if disabled
                keepInventoryCheck(world);
                // Enable Gravestone spawning with keepInventory if disabled
                graveStoneCheck(world);

                #if MC_VERSION < 12106
                final DeathManager.ItemDropper itemDropper = this::drop;
                #else
                final DeathManager.ItemDropper itemDropper = ((LivingEntity) (Object) this)::drop;
                #endif
                final DeathManager deathManager = new DeathManager(inventory, itemDropper);
                deathManager.managePlayerDeath();
                if (!(ModConfig.INSTANCE.containDrop && ModConfig.INSTANCE.containDropMode == ContainDropMode.GRAVE && SupportedGraveMods.PNEUMONO_GRAVESTONES.isLoaded())) ci.cancel();
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("An error occurred while dropping inventory", e);
            }
        }
    }

    @Unique
    private void doublePrintWarn(String message) {
        PlayerUtils.sendMessage(inventory.player, Component.literal(message).withStyle(ChatFormatting.YELLOW));
        LOGGER.warn(message);
    }

    @Unique
    private void keepInventoryCheck(Level world) {
        #if MC_VERSION < 12104
        final GameRules gameRules = world.getGameRules();
        #else
        final MinecraftServer server = world.getServer();
        if (server == null) {
            doublePrintWarn("Unable to determine the 'keepInventory' game rule state! Make sure 'keepInventory' is enabled!");
            return;
        }
            #if MC_VERSION < 12111 || MC_VERSION >= 260000
            final GameRules gameRules = server.getGameRules();
            #else
            final GameRules gameRules = server.getWorldData().getGameRules();
            #endif
        #endif

        #if MC_VERSION < 12111
        GameRules.BooleanValue keepInventory = gameRules.getRule(GameRules.RULE_KEEPINVENTORY);
        boolean isKeepInventory = keepInventory.get();
        #else
        boolean isKeepInventory = gameRules.get(GameRules.KEEP_INVENTORY);
        #endif

        if (!isKeepInventory) {
            doublePrintWarn("KeepInventory GameRule is False; 'SaveTheHotbar!' requires keepInventory to work; Changing keepInventory to True; If you want to disable 'SaveTheHotbar!', disable it in settings");
            if (!world.isClientSide()) {
                #if MC_VERSION < 12111
                keepInventory.set(true, world.getServer());
                #else
                gameRules.set(GameRules.KEEP_INVENTORY, true, server);
                #endif
            }
        }
    }

    @Unique
    private void graveStoneCheck(Level world) {
        if (ModConfig.INSTANCE.containDrop && ModConfig.INSTANCE.containDropMode == ContainDropMode.GRAVE && SupportedGraveMods.PNEUMONO_GRAVESTONES.isLoaded()) {
            GravestoneConfiguration.gravestoneCheck(this::doublePrintWarn, world.getServer());
        }
    }
}
