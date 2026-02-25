package com.casiowatch123.vchunklib.generation.virtual.world.chunk;

import com.casiowatch123.vchunklib.VChunkLib;
import com.casiowatch123.vchunklib.generation.virtual.world.VWorld;
import com.casiowatch123.vchunklib.generation.virtual.world.VWorldContext;
import com.casiowatch123.vchunklib.generation.virtual.world.VWorldService;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Util;
import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.MultiTickScheduler;
import net.minecraft.world.tick.QueryableTickScheduler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class VChunkRegion extends ChunkRegion{
    private static final Logger LOGGER = VChunkLib.LOGGER;
    private final BoundedRegionArray<Chunk> chunks;
    private final Chunk center;
    private final VWorldService worldService;
    private final VWorld world;
    private final VWorldContext worldContext;
    private final long seed;
//    private final WorldProperties levelProperties; todo: getter throws UnsupportedException
    private final Random random;
    private final DimensionType dimension;
    private final MultiTickScheduler<Block> blockTickScheduler = new MultiTickScheduler<>(pos -> this.getChunk(pos).getBlockTickScheduler());
    private final MultiTickScheduler<Fluid> fluidTickScheduler = new MultiTickScheduler<>(pos -> this.getChunk(pos).getFluidTickScheduler());
    private final BiomeAccess biomeAccess;
    private final VChunkGenerationStep generationStep;
    @Nullable
    private Supplier<String> currentlyGeneratingStructureName;
    private final AtomicLong tickOrder = new AtomicLong();
    private static final Identifier WORLDGEN_REGION_RANDOM_ID = Identifier.ofVanilla("worldgen_region_random");

    public VChunkRegion(
            VWorldService worldService, 
            BoundedRegionArray<Chunk> chunks, 
            VChunkGenerationStep generationStep, 
            Chunk center) {
        super(null, null, null, null);
        this.worldService = worldService;
        this.world = worldService.world();
        this.worldContext = worldService.worldContext();
        this.center = center;
        this.chunks = chunks;
        this.seed = worldContext.getSeed();
        this.random = worldContext.getNoiseConfig().getOrCreateRandomDeriver(WORLDGEN_REGION_RANDOM_ID).split(this.center.getPos().getStartPos());
        this.dimension = worldContext.getDimensionType();
        this.generationStep = generationStep;
        this.biomeAccess = new BiomeAccess(this, BiomeAccess.hashSeed(this.seed));
    }

    @Override
    public boolean needsBlending(ChunkPos chunkPos, int checkRadius) {
        return false;
    }

    @Override
    public ChunkPos getCenterPos() {
        return this.center.getPos();
    }

    @Override
    public void setCurrentlyGeneratingStructureName(@Nullable Supplier<String> structureName) {
        this.currentlyGeneratingStructureName = structureName;
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        int i = this.center.getPos().getChebyshevDistance(chunkX, chunkZ);
        ChunkStatus chunkStatus = i >= this.generationStep.directDependencies().size() ? null : this.generationStep.directDependencies().get(i);
        if (chunkStatus != null) {
            Chunk chunk = this.chunks.get(chunkX, chunkZ);
            if (chunk != null) {
                return chunk;
            }
        }

        throw new RuntimeException("Request outside valid region");
    }

    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        int i = this.center.getPos().getChebyshevDistance(chunkX, chunkZ);
        return i < this.generationStep.directDependencies().size();
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return this.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())).getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.getChunk(pos).getFluidState(pos);
    }

    @Nullable
    @Override
    public PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, Predicate<Entity> targetPredicate) {
        return null;
    }

    @Override
    public int getAmbientDarkness() {
        return 0;
    }

    @Override
    public BiomeAccess getBiomeAccess() {
        return this.biomeAccess;
    }

    @Override
    public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
        return this.worldContext.getGeneratorStoredBiome(biomeX, biomeY, biomeZ);
    }

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        return 1.0F;
    }

    @Override
    public LightingProvider getLightingProvider() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
        BlockState blockState = this.getBlockState(pos);
        if (blockState.isAir()) {
            return false;
        } else {
            return this.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL, maxUpdateDepth);
        }
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        Chunk chunk = this.getChunk(pos);
        BlockEntity blockEntity = chunk.getBlockEntity(pos);
        if (blockEntity != null) {
            return blockEntity;
        } else {
            NbtCompound nbtCompound = chunk.getBlockEntityNbt(pos);
            BlockState blockState = chunk.getBlockState(pos);
            if (nbtCompound != null) {
                if ("DUMMY".equals(nbtCompound.getString("id"))) {
                    if (!blockState.hasBlockEntity()) {
                        return null;
                    }

                    blockEntity = ((BlockEntityProvider)blockState.getBlock()).createBlockEntity(pos, blockState);
                } else {
                    throw new UnsupportedOperationException();
//                    blockEntity = BlockEntity.createFromNbt(pos, blockState, nbtCompound, worldContext.getRegistryManager());
                }

                if (blockEntity != null) {
                    chunk.setBlockEntity(blockEntity);
                    return blockEntity;
                }
            }

            return null;
        }
    }

    @Override
    public boolean isValidForSetBlock(BlockPos pos) {
        int i = ChunkSectionPos.getSectionCoord(pos.getX());
        int j = ChunkSectionPos.getSectionCoord(pos.getZ());
        ChunkPos chunkPos = this.getCenterPos();
        int k = Math.abs(chunkPos.x - i);
        int l = Math.abs(chunkPos.z - j);
        if (k <= this.generationStep.blockStateWriteRadius() && l <= this.generationStep.blockStateWriteRadius()) {
            if (this.center.hasBelowZeroRetrogen()) {
                HeightLimitView heightLimitView = this.center.getHeightLimitView();
                if (pos.getY() < heightLimitView.getBottomY() || pos.getY() >= heightLimitView.getTopY()) {
                    return false;
                }
            }
            return true;
        } else {
            Util.error(
                    "Detected setBlock in a far chunk ["
                            + i
                            + ", "
                            + j
                            + "], pos: "
                            + pos
                            + ", status: "
                            + this.generationStep.targetStatus()
                            + (this.currentlyGeneratingStructureName == null ? "" : ", currently generating: " + (String)this.currentlyGeneratingStructureName.get())
            );
            return false;
        }
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        if (!this.isValidForSetBlock(pos)) {
            return false;
        } else {
            Chunk chunk = this.getChunk(pos);
            BlockState blockState = chunk.setBlockState(pos, state, false);
            if (blockState != null) {
//                this.world.onBlockChanged(pos, blockState, state);
            }

            if (state.hasBlockEntity()) {
                if (chunk.getStatus().getChunkType() == ChunkType.LEVELCHUNK) {
                    BlockEntity blockEntity = ((BlockEntityProvider)state.getBlock()).createBlockEntity(pos, state);
                    if (blockEntity != null) {
                        chunk.setBlockEntity(blockEntity);
                    } else {
                        chunk.removeBlockEntity(pos);
                    }
                } else {
                    NbtCompound nbtCompound = new NbtCompound();
                    nbtCompound.putInt("x", pos.getX());
                    nbtCompound.putInt("y", pos.getY());
                    nbtCompound.putInt("z", pos.getZ());
                    nbtCompound.putString("id", "DUMMY");
                    chunk.addPendingBlockEntityNbt(nbtCompound);
                }
            } else if (blockState != null && blockState.hasBlockEntity()) {
                chunk.removeBlockEntity(pos);
            }

            if (state.shouldPostProcess(this, pos)) {
                this.markBlockForPostProcessing(pos);
            }

            return true;
        }
    }

    private void markBlockForPostProcessing(BlockPos pos) {
        this.getChunk(pos).markBlockForPostProcessing(pos);
    }

    @Override
    public boolean spawnEntity(Entity entity) {
        return false;
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean move) {
        return this.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.world.getWorldBorder();
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Deprecated
    @Override
    public ServerWorld toServerWorld() {
        LOGGER.warn(
                "Called unsupported method: {}#{}",
                this.getClass().getName(),
                "toServerWorld");
        
        return null;
    }

    @Override
    public DynamicRegistryManager getRegistryManager() {
        return this.worldContext.getRegistryManager();
    }

    @Override
    public FeatureSet getEnabledFeatures() {
        return this.worldContext.getFeatureSet();
    }

    @Override
    public WorldProperties getLevelProperties() {
        return worldContext.getWorldProperties();
    }

    @Override
    public LocalDifficulty getLocalDifficulty(BlockPos pos) {
        if (!this.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
            throw new RuntimeException("We are asking a region for a chunk out of bound");
        } else {
            return new LocalDifficulty(worldContext.getWorldProperties().getDifficulty(), worldContext.getWorldProperties().getTimeOfDay(), 0L, 0f);
        }
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ChunkManager getChunkManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getSeed() {
        return this.seed;
    }


    @Override
    public QueryableTickScheduler<Block> getBlockTickScheduler() {
        return this.blockTickScheduler;
    }

    @Override
    public QueryableTickScheduler<Fluid> getFluidTickScheduler() {
        return this.fluidTickScheduler;
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }

    @Override
    public Random getRandom() {
        return this.random;
    }

    @Override
    public int getTopY(Heightmap.Type heightmap, int x, int z) {
        return this.getChunk(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z)).sampleHeightmap(heightmap, x & 15, z & 15) + 1;
    }

    @Override
    public void playSound(@Nullable PlayerEntity source, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
    }

    @Override
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    @Override
    public void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {
    }

    @Override
    public void emitGameEvent(RegistryEntry<GameEvent> event, Vec3d emitterPos, GameEvent.Emitter emitter) {
    }

    @Override
    public DimensionType getDimension() {
        return this.dimension;
    }

    @Override
    public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
        return state.test(this.getBlockState(pos));
    }

    @Override
    public boolean testFluidState(BlockPos pos, Predicate<FluidState> state) {
        return state.test(this.getFluidState(pos));
    }

    @Override
    public <T extends Entity> List<T> getEntitiesByType(TypeFilter<Entity, T> filter, Box box, Predicate<? super T> predicate) {
        return Collections.emptyList();
    }

    @Override
    public List<Entity> getOtherEntities(@Nullable Entity except, Box box, @Nullable Predicate<? super Entity> predicate) {
        return Collections.emptyList();
    }

    @Override
    public List<PlayerEntity> getPlayers() {
        return Collections.emptyList();
    }

    @Override
    public int getBottomY() {
        return this.world.getBottomY();
    }

    @Override
    public int getHeight() {
        return this.world.getHeight();
    }

    @Override
    public long getTickOrder() {
        return this.tickOrder.getAndIncrement();
    }
    
    
    
    @Override
    public int getLightLevel(LightType type, BlockPos pos) {
        return 15;
    }
    
    @Override
    public int getBaseLightLevel(BlockPos pos, int ambientDarkness) {
        return 15;
    }
}
