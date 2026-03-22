package com.casiowatch123.vchunklib.generation.virtual.world;

import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.noise.NoiseConfig;

public class VWorldContext {
    private final boolean generateStructures;
    private final long seed;
    private final DynamicRegistryManager registryManager;
    private final GeneratorOptions generatorOptions;
    private final DimensionType dimensionType;
    private final NoiseChunkGenerator generator;
    private final BiomeSource biomeSource;
    private final NoiseConfig noiseConfig;
    private final FeatureSet featureSet = FeatureFlags.VANILLA_FEATURES;
    private final WorldProperties worldProperties;
    private final StructurePlacementCalculator structurePlacementCalculator;
    private final RegistryKey<World> dimensionKey;
    
    
    public VWorldContext(DynamicRegistryManager registryManager, VDimensionArgs dimensionArgs, long seed) {
        this.generateStructures = true;
        this.seed = seed;
        this.registryManager = registryManager;
        this.generatorOptions = new GeneratorOptions(seed, generateStructures, false);
        this.dimensionType = dimensionArgs.dimensionType();
        this.biomeSource = dimensionArgs.biomeSource();
        this.dimensionKey = dimensionArgs.dimensionKey();
        
        this.generator = new NoiseChunkGenerator(biomeSource, dimensionArgs.chunkGeneratorSettingEntry());
        this.noiseConfig = NoiseConfig.create(
                generator.getSettings().value(),
                registryManager.getOrThrow(RegistryKeys.NOISE_PARAMETERS), 
                seed
        );
        this.worldProperties = new WorldProperties() {
            private final SpawnPoint spawnPoint = new SpawnPoint(GlobalPos.create(
                    dimensionKey, new BlockPos(0, 63, 0)), 
                    0, 0);
            @Override
            public SpawnPoint getSpawnPoint() {
                return spawnPoint;
            }

            @Override
            public long getTime() {
                return 0;
            }

            @Override
            public long getTimeOfDay() {
                return 0;
            }

            @Override
            public boolean isThundering() {
                return false;
            }

            @Override
            public boolean isRaining() {
                return false;
            }

            @Override
            public void setRaining(boolean raining) {
            }

            @Override
            public boolean isHardcore() {
                return false;
            }

            @Override
            public Difficulty getDifficulty() {
                return Difficulty.PEACEFUL;
            }

            @Override
            public boolean isDifficultyLocked() {
                return true;
            }
        };
        this.structurePlacementCalculator = 
                StructurePlacementCalculator.create(
                        noiseConfig,
                        seed,
                        biomeSource,
                        registryManager.getOrThrow(RegistryKeys.STRUCTURE_SET)
                );
    }

    public boolean shouldGenerateStructures() {
        return generateStructures;
    }
    
    public DynamicRegistryManager getRegistryManager() {
        return registryManager;
    }

    public StructurePlacementCalculator getStructurePlacementCalculator() {
        return this.structurePlacementCalculator;
    }

    public GeneratorOptions getGeneratorOptions() {
        return generatorOptions;
    }
    
    public DimensionType getDimensionType() {
        return dimensionType;
    }
    
    public long getSeed() {
        return seed;
    }
    
    public NoiseConfig getNoiseConfig() {
        return noiseConfig;
    }

    public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
        return this.biomeSource
                .getBiome(biomeX, biomeY, biomeZ, this.noiseConfig.getMultiNoiseSampler());
    }
    
    public FeatureSet getFeatureSet() {
        return featureSet;
    }
    
    public WorldProperties getWorldProperties() {
        return worldProperties;
    }
    
    public ChunkGenerator getGenerator() {
        return this.generator;
    }
    
    public RegistryKey<World> getDimensionKey() {
        return this.dimensionKey;
    }
}
