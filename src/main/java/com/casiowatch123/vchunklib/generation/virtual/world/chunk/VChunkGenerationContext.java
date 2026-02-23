package com.casiowatch123.vchunklib.generation.virtual.world.chunk;

import com.casiowatch123.vchunklib.generation.virtual.world.VWorldService;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public record VChunkGenerationContext(
        VWorldService worldService,
        ChunkGenerator generator,
        StructureTemplateManager structureManager
) {
}
