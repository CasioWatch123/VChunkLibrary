package com.casiowatch123.vchunklib.generation.virtual.world.chunk;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

public class VChunkHolder {
    private final Chunk chunk;
    private final ChunkPos pos;
    
    public VChunkHolder(ChunkPos pos, Chunk chunk) {
        this.chunk = chunk;
        this.pos = pos;
    }

    public Chunk getChunk() {
        return chunk;
    }
    
    public ChunkPos getPos() {
        return pos;
    }
}
