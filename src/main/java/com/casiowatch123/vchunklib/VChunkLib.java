package com.casiowatch123.vchunklib;

import com.casiowatch123.vchunklib.generation.virtual.VUtils;
import com.casiowatch123.vchunklib.generation.virtual.world.VChunkLoadingManager;
import com.casiowatch123.vchunklib.generation.virtual.world.VWorldService;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;


public class VChunkLib implements ModInitializer {
	public static final String MOD_ID = "vchunklib";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    @Override
    public void onInitialize() {
//        long seed = 263674943826304593L;
//
//        LOGGER.info("hello fabric world! {}", seed);
//        ClientLifecycleEvents.CLIENT_STARTED.register(c -> {
//            var drm = VUtils.createVanillaRegistryManager();
//            VWorldService worldService = new VWorldService(
//                    drm,
//                    VUtils.createDimensionArg(World.END, drm),
//                    seed);
//
//            VChunkLoadingManager manager = new VChunkLoadingManager(worldService);
//
//            BoundedRegionArray<Chunk> chunkRegion = manager.loadChunk(new ChunkPos(10000, 20000), 62);
//
//            LOGGER.info("{}", chunkRegion.get(10000,20000).getBlockState(new BlockPos(0, 20, 0)));
//        });
//        System.out.println("escape");
    }
}