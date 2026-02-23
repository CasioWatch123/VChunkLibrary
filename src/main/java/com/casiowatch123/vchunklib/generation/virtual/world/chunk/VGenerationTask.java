package com.casiowatch123.vchunklib.generation.virtual.world.chunk;

import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.world.chunk.Chunk;

import java.util.concurrent.CompletableFuture;

public interface VGenerationTask {
    CompletableFuture<Chunk> doWork(
            VChunkGenerationContext context, VChunkGenerationStep step, BoundedRegionArray<VChunkHolder> boundedRegionArray, Chunk chunk
    );
}