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
//        long seed = new Random().nextLong();
        
//        LOGGER.info("hello fabric world! {}", seed);
//        ClientLifecycleEvents.CLIENT_STARTED.register(c -> {
//            var drm = VUtils.createVanillaRegistryManager();
//            VWorldService worldService = new VWorldService(
//                    drm, 
//                    VUtils.createDimensionArg(World.OVERWORLD, drm), 
//                    seed);
//
//            VChunkLoadingManager manager = new VChunkLoadingManager(worldService);
//
//            Chunk chunk = manager.loadChunk(new ChunkPos(0, 0), 0).get(0, 0);
//            
//            for(int i = -64; i < 100; i++) {
//                LOGGER.info("{}: {}", i, chunk.getBlockState(new BlockPos(0, i, 0)));
//            }
//            for(int i = 0; i < 5000; i++) {
//                manager.loadChunk(new ChunkPos(i, i), 0);
//                System.out.println(i + " load success");
//            }
//        });
//        System.out.println("escape");
    }
}