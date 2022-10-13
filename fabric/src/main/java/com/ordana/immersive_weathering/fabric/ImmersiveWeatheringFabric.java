package com.ordana.immersive_weathering.fabric;

import com.ordana.immersive_weathering.ImmersiveWeathering;
import com.ordana.immersive_weathering.configs.CommonConfigs;
import com.ordana.immersive_weathering.events.ModEvents;
import com.ordana.immersive_weathering.events.ModLootInjects;
import com.ordana.immersive_weathering.reg.ModWaxables;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.mehvahdjukaar.moonlight.api.platform.fabric.RegHelperImpl;
import net.mehvahdjukaar.moonlight.fabric.FabricSetupCallbacks;
import net.mehvahdjukaar.moonlight.fabric.MoonlightFabric;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;

public class ImmersiveWeatheringFabric implements ModInitializer {

    public static MinecraftServer currentServer;

    @Override
    public void onInitialize() {

        ServerLifecycleEvents.SERVER_STARTING.register(s -> currentServer = s);

        //loads registries
        ImmersiveWeathering.commonInit();

        //runs everything else

        ImmersiveWeathering.commonSetup();

        //fabric only stuff here


        if (CommonConfigs.FLAMMABLE_COBWEBS.get()) {
            FlammableBlockRegistry.getDefaultInstance().add(Blocks.COBWEB, 100, 100);
        }

        FabricLoader.getInstance().getModContainer(ImmersiveWeathering.MOD_ID).ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(ImmersiveWeathering.res("better_brick_items"), modContainer, ResourcePackActivationType.NORMAL);
            ResourceManagerHelper.registerBuiltinResourcePack(ImmersiveWeathering.res("better_brick_blocks"), modContainer, ResourcePackActivationType.NORMAL);
            ResourceManagerHelper.registerBuiltinResourcePack(ImmersiveWeathering.res("visual_waxed_iron_items"), modContainer, ResourcePackActivationType.NORMAL);
            ResourceManagerHelper.registerBuiltinResourcePack(ImmersiveWeathering.res("biome_tinted_mossy_blocks"), modContainer, ResourcePackActivationType.NORMAL);
        });

        //events
        UseBlockCallback.EVENT.register(ImmersiveWeatheringFabric::onRightClickBlock);

        LootTableEvents.MODIFY.register((m, t, r, b, s) -> ModLootInjects.onLootInject(t, r, b::withPool));

        FabricSetupCallbacks.COMMON_SETUP.add(ImmersiveWeatheringFabric::onSetup);
    }

    public static void onSetup(){
        ModWaxables.getValues().forEach(OxidizableBlocksRegistry::registerWaxableBlockPair);
    }

    public static InteractionResult onRightClickBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        return ModEvents.onBlockCLicked(player.getItemInHand(hand), player, level, hand, hitResult);
    }

}
