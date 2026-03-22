package com.casiowatch123.vchunklib.generation.virtual;

import com.casiowatch123.vchunklib.generation.virtual.structure.VStructureTemplateManager;
import com.casiowatch123.vchunklib.generation.virtual.world.VDimensionArgs;
import net.minecraft.command.permission.LeveledPermissionPredicate;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.registry.*;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.ParsedSaveProperties;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class VUtils {
    public static VStructureTemplateManager STRUCTURE_TEMPLATE_MANAGER = new VStructureTemplateManager(Registries.BLOCK);
    
    private VUtils() {}
    
    //Must be used after client starting!!
    public static DynamicRegistryManager.Immutable createVanillaRegistryManager() {
        ResourcePackManager rpm = VanillaDataPackProvider.createClientManager();
        rpm.scanPacks();

        rpm.setEnabledProfiles(Set.of("vanilla"));

        SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(
                rpm,
                DataConfiguration.SAFE_MODE,
                true,
                true
        );

        SaveLoading.ServerConfig serverConfig = new SaveLoading.ServerConfig(
                dataPacks,
                CommandManager.RegistrationEnvironment.DEDICATED,
                LeveledPermissionPredicate.fromLevel(PermissionLevel.ALL)
        );
        
        try (ExecutorService executor = Executors.newWorkStealingPool()) {
            return SaveLoading.load(
                            serverConfig,
                            loadContext -> new SaveLoading.LoadContext<>(null, loadContext.dimensionsRegistryManager()),
                            (rm, dp, combdRegistries, loadContext) ->
                                    combdRegistries,
                            executor,
                            executor)
                    .join()
                    .getCombinedRegistryManager();
        }
    }
    
    public static VDimensionArgs createDimensionArg(RegistryKey<World> worldKey, DynamicRegistryManager drm) {
        if (worldKey == World.OVERWORLD) {
            return new VDimensionArgs(
                    World.OVERWORLD,
                    drm.getOrThrow(RegistryKeys.DIMENSION_TYPE)
                            .get(DimensionTypes.OVERWORLD),
                    MultiNoiseBiomeSource.create(
                            drm.getEntryOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD)),
                    drm.getEntryOrThrow(ChunkGeneratorSettings.OVERWORLD),
                    STRUCTURE_TEMPLATE_MANAGER
            );
        } else if (worldKey == World.NETHER) {
            return new VDimensionArgs(
                    World.NETHER,
                    drm.getOrThrow(RegistryKeys.DIMENSION_TYPE)
                            .get(DimensionTypes.THE_NETHER),
                    MultiNoiseBiomeSource.create(
                            drm.getEntryOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER)),
                    drm.getEntryOrThrow(ChunkGeneratorSettings.NETHER),
                    STRUCTURE_TEMPLATE_MANAGER
            );
        } else {
            return new VDimensionArgs(
                    World.END,
                    drm.getOrThrow(RegistryKeys.DIMENSION_TYPE)
                            .get(DimensionTypes.THE_END),
                    TheEndBiomeSource.createVanilla(
                            drm.getOrThrow(RegistryKeys.BIOME)),
                    drm.getEntryOrThrow(ChunkGeneratorSettings.END),
                    STRUCTURE_TEMPLATE_MANAGER
            );
        }
    }
}
