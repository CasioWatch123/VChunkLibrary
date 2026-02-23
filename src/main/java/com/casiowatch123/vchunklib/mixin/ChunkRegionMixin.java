package com.casiowatch123.vchunklib.mixin;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkRegion.class)
public abstract class ChunkRegionMixin {
    @Redirect(method = "<init>", at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraft/server/world/ServerWorld;" + 
                    "getSeed()" + 
                    "J"))
    private long getSeed(ServerWorld world) {
        return world == null ? 0L : world.getSeed();
    }
    
    @Redirect(method = "<init>", at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraft/server/world/ServerWorld;" + 
                    "getLevelProperties()" +
                    "Lnet/minecraft/world/WorldProperties;"))
    private WorldProperties getLevelProperties(ServerWorld world) {
        return world == null ? null : world.getLevelProperties();
    }

    @Redirect(method = "<init>", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;" +
                    "getDimension()" +
                    "Lnet/minecraft/world/dimension/DimensionType;"))
    private DimensionType getDimension(ServerWorld world) {
        return world == null ? null : world.getDimension();
    }
    
    
    
    
    
    @Redirect(method = "<init>", at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraft/server/world/ServerWorld;" +
                    "getChunkManager()" +
                    "Lnet/minecraft/server/world/ServerChunkManager;"))
    private ServerChunkManager getChunkManager(ServerWorld world) {
        return world == null ? null : world.getChunkManager();
    }
    
    @Redirect(method = "<init>", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerChunkManager;" + 
                    "getNoiseConfig()" +
                    "Lnet/minecraft/world/gen/noise/NoiseConfig;"))
    private NoiseConfig getNoiseConfig(ServerChunkManager manager) {
        return manager == null ? null : manager.getNoiseConfig();
    }
    
    @Redirect(method = "<init>", at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraft/world/gen/noise/NoiseConfig;" +
                    "getOrCreateRandomDeriver(Lnet/minecraft/util/Identifier;)" +
                    "Lnet/minecraft/util/math/random/RandomSplitter;"))
    private RandomSplitter getOrCreateRandomDeriver(NoiseConfig noiseConfig, Identifier id) {
        return noiseConfig == null ? null : noiseConfig.getOrCreateRandomDeriver(id);
    }
    
    @Redirect(method = "<init>", at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraft/util/math/random/RandomSplitter;" +
                    "split(Lnet/minecraft/util/math/BlockPos;)" +
                    "Lnet/minecraft/util/math/random/Random;"))
    private Random split(RandomSplitter splitter, BlockPos pos) {
        return splitter == null ? null : splitter.split(pos);
    }
    
    
    @Redirect(method = "<init>", at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraft/world/chunk/Chunk;" + 
                    "getPos()" +
                    "Lnet/minecraft/util/math/ChunkPos;"))
    private ChunkPos getPos(Chunk chunk) {
        return chunk == null ? null : chunk.getPos();
    }
    
    @Redirect(method = "<init>", at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraft/util/math/ChunkPos;" + 
                    "getStartPos()" +
                    "Lnet/minecraft/util/math/BlockPos;"
    ))
    private BlockPos getStartPos(ChunkPos chunkPos) {
        return chunkPos == null ? null : chunkPos.getStartPos();
    }
}
