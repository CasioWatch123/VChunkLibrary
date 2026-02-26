package com.casiowatch123.vchunklib.generation.virtual.world;

import com.casiowatch123.vchunklib.generation.virtual.structure.VStructureTemplateManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public record VDimensionArgs(
        Identifier dimensionId, 
        DimensionType dimensionType,
        BiomeSource biomeSource,
        RegistryEntry<ChunkGeneratorSettings> chunkGeneratorSettingEntry, 
        VStructureTemplateManager structureTemplateManager) {
}
