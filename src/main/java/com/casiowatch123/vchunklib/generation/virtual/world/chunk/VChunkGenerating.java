package com.casiowatch123.vchunklib.generation.virtual.world.chunk;

import com.casiowatch123.vchunklib.generation.virtual.world.VWorldContext;
import com.casiowatch123.vchunklib.generation.virtual.world.VWorldService;
import com.casiowatch123.vchunklib.generation.virtual.world.gen.VStructureAccessor;

import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.Blender;

import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

public final class VChunkGenerating {
    private VChunkGenerating() {};

//    /*remove func list:
//        isLightOn
//        initializeLight
//        light
//        generateEntities
//        convertToFullChunk
//        addEntities
//     */
    static CompletableFuture<Chunk> noop(
            VChunkGenerationContext context, VChunkGenerationStep step, BoundedRegionArray<Chunk> chunks, Chunk chunk
    ) {
        return CompletableFuture.completedFuture(chunk);
    }

    static CompletableFuture<Chunk> generateStructures(
            VChunkGenerationContext context, VChunkGenerationStep step, BoundedRegionArray<Chunk> chunks, Chunk chunk
    ) {
        VWorldService worldService = context.worldService();
        VWorldContext worldContext = worldService.worldContext();

        VStructureAccessor structureAccessor = 
                new VStructureAccessor(new VChunkRegion(worldService, chunks, step, chunk), worldContext.getGeneratorOptions());
        
        if (worldContext.shouldGenerateStructures()) {
            context.generator()
                    .setStructureStarts(
                            worldContext.getRegistryManager(),
                            worldContext.getStructurePlacementCalculator(),
                            structureAccessor,
                            chunk,
                            context.structureManager()
                    );
        }

        return CompletableFuture.completedFuture(chunk);
    }

    static CompletableFuture<Chunk> loadStructures(
            VChunkGenerationContext context, ChunkGenerationStep step, BoundedRegionArray<Chunk> chunks, Chunk chunk
    ) {
        return CompletableFuture.completedFuture(chunk);
    }

    static CompletableFuture<Chunk> generateStructureReferences(
            VChunkGenerationContext context, VChunkGenerationStep step, BoundedRegionArray<Chunk> chunks, Chunk chunk
    ) {
        VWorldService worldService = context.worldService();
        ChunkRegion chunkRegion = new VChunkRegion(worldService, chunks, step, chunk);
        VStructureAccessor structureAccessor = 
                new VStructureAccessor(chunkRegion, worldService.worldContext().getGeneratorOptions());
        
        context.generator().addStructureReferences(chunkRegion, structureAccessor, chunk);
        return CompletableFuture.completedFuture(chunk);
    }

    static CompletableFuture<Chunk> populateBiomes(
            VChunkGenerationContext context, VChunkGenerationStep step, BoundedRegionArray<Chunk> chunks, Chunk chunk
    ) {
        VWorldService worldService = context.worldService();
        ChunkRegion chunkRegion = new VChunkRegion(worldService, chunks, step, chunk);
        VStructureAccessor structureAccessor =
                new VStructureAccessor(chunkRegion, worldService.worldContext().getGeneratorOptions());

        return context.generator()
                .populateBiomes(
                        worldService.worldContext().getNoiseConfig(), Blender.getBlender(chunkRegion), structureAccessor, chunk
                );
    }

    static CompletableFuture<Chunk> populateNoise(
            VChunkGenerationContext context, VChunkGenerationStep step, BoundedRegionArray<Chunk> chunks, Chunk chunk
    ) {
        VWorldService worldService = context.worldService();
        ChunkRegion chunkRegion = new VChunkRegion(worldService, chunks, step, chunk);
        VStructureAccessor structureAccessor =
                new VStructureAccessor(chunkRegion, worldService.worldContext().getGeneratorOptions());

        return context.generator()
                .populateNoise(
                        Blender.getBlender(chunkRegion), worldService.worldContext().getNoiseConfig(), structureAccessor, chunk
                );
    }

    static CompletableFuture<Chunk> buildSurface(
            VChunkGenerationContext context, VChunkGenerationStep step, BoundedRegionArray<Chunk> chunks, Chunk chunk
    ) {
        VWorldService worldService = context.worldService();
        ChunkRegion chunkRegion = new VChunkRegion(worldService, chunks, step, chunk);
        VStructureAccessor structureAccessor =
                new VStructureAccessor(chunkRegion, worldService.worldContext().getGeneratorOptions());

        context.generator()
                .buildSurface(chunkRegion, structureAccessor, worldService.worldContext().getNoiseConfig(), chunk);
        return CompletableFuture.completedFuture(chunk);
    }

    static CompletableFuture<Chunk> carve(
            VChunkGenerationContext context, VChunkGenerationStep step, BoundedRegionArray<Chunk> chunks, Chunk chunk
    ) {
        VWorldService worldService = context.worldService();
        VWorldContext worldContext = worldService.worldContext();
        ChunkRegion chunkRegion = new VChunkRegion(worldService, chunks, step, chunk);
        VStructureAccessor structureAccessor =
                new VStructureAccessor(chunkRegion, worldService.worldContext().getGeneratorOptions());


        context.generator()
                .carve(
                        chunkRegion,
                        worldContext.getSeed(),
                        worldContext.getNoiseConfig(),
                        new BiomeAccess(chunkRegion, BiomeAccess.hashSeed(worldContext.getSeed())),
                        structureAccessor,
                        chunk,
                        GenerationStep.Carver.AIR
                );
        return CompletableFuture.completedFuture(chunk);
    }

    static CompletableFuture<Chunk> generateFeatures(
            VChunkGenerationContext context, VChunkGenerationStep step, BoundedRegionArray<Chunk> chunks, Chunk chunk
    ) {
        Heightmap.populateHeightmaps(
                chunk, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE)
        );
        VWorldService worldService = context.worldService();
        VWorldContext worldContext = worldService.worldContext();
        ChunkRegion chunkRegion = new VChunkRegion(worldService, chunks, step, chunk);
        VStructureAccessor structureAccessor =
                new VStructureAccessor(chunkRegion, worldService.worldContext().getGeneratorOptions());

        context.generator().generateFeatures(chunkRegion, chunk, structureAccessor);
        
        return CompletableFuture.completedFuture(chunk);
    }
}
