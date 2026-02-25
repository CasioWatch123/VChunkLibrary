package com.casiowatch123.vchunklib;

import com.casiowatch123.vchunklib.generation.virtual.Builtin;
import com.casiowatch123.vchunklib.generation.virtual.world.VChunkLoadingManager;
import com.casiowatch123.vchunklib.generation.virtual.world.VWorldService;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.math.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VChunkLib implements ModInitializer {
	public static final String MOD_ID = "vchunklib";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    @Override
    public void onInitialize() {
        long seed = 123456L;
//
//        VWorldService worldService = new VWorldService(
//                Builtin.VANILLA_DRM, 
//                Builtin.OVERWORLD_ARGS, 
//                seed
//        );
//        
//        VChunkLoadingManager manager = new VChunkLoadingManager(worldService);
//        
//        for(int i = 0; i < 500; i++) {
//            manager.loadChunk(new ChunkPos(i, i), 0);
//            System.out.println(i + " load success");
//        }
    }
}