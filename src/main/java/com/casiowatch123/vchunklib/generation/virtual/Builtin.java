package com.casiowatch123.vchunklib.generation.virtual;

import com.casiowatch123.vchunklib.generation.virtual.structure.VStructureTemplateManager;
import com.casiowatch123.vchunklib.generation.virtual.world.VDimensionArgs;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public final class Builtin {
    private Builtin() {}

    public static VStructureTemplateManager STRUCTURE_TEMPLATE_MANAGER = new VStructureTemplateManager(Registries.BLOCK.getReadOnlyWrapper());

    public static DynamicRegistryManager VANILLA_DRM = VUtils.createVanillaRegistryManager();

    public static VDimensionArgs OVERWORLD_ARGS = new VDimensionArgs(
            VANILLA_DRM
                    .get(RegistryKeys.DIMENSION_TYPE)
                    .get(DimensionTypes.OVERWORLD),
            MultiNoiseBiomeSource.create(
                    VANILLA_DRM
                            .get(RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST)
                            .getEntry(MultiNoiseBiomeSourceParameterLists.OVERWORLD)
                            .orElseThrow()),
            VANILLA_DRM
                    .get(RegistryKeys.CHUNK_GENERATOR_SETTINGS)
                    .getEntry(ChunkGeneratorSettings.OVERWORLD)
                    .orElseThrow(),
            STRUCTURE_TEMPLATE_MANAGER
    );
    public static VDimensionArgs NETHER_ARGS = new VDimensionArgs(
            VANILLA_DRM
                    .get(RegistryKeys.DIMENSION_TYPE)
                    .get(DimensionTypes.THE_NETHER),
            MultiNoiseBiomeSource.create(
                    VANILLA_DRM
                            .get(RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST)
                            .getEntry(MultiNoiseBiomeSourceParameterLists.NETHER)
                            .orElseThrow()),
            VANILLA_DRM
                    .get(RegistryKeys.CHUNK_GENERATOR_SETTINGS)
                    .getEntry(ChunkGeneratorSettings.NETHER)
                    .orElseThrow(),
            STRUCTURE_TEMPLATE_MANAGER
    );
    public static VDimensionArgs END_ARGS = new VDimensionArgs(
            VANILLA_DRM
                    .get(RegistryKeys.DIMENSION_TYPE)
                    .get(DimensionTypes.THE_END),
            TheEndBiomeSource.createVanilla(
                    VANILLA_DRM
                            .get(RegistryKeys.BIOME)
                            .getReadOnlyWrapper()
            ),
            VANILLA_DRM
                    .get(RegistryKeys.CHUNK_GENERATOR_SETTINGS)
                    .getEntry(ChunkGeneratorSettings.END)
                    .orElseThrow(),
            STRUCTURE_TEMPLATE_MANAGER
    );
}
