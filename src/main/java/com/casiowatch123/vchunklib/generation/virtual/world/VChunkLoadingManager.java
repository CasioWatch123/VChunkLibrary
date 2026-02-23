package com.casiowatch123.vchunklib.generation.virtual.world;

import com.casiowatch123.vchunklib.generation.virtual.world.chunk.VChunkGenerationContext;
import com.casiowatch123.vchunklib.generation.virtual.world.chunk.VChunkGenerationStep;
import com.casiowatch123.vchunklib.generation.virtual.world.chunk.VChunkGenerationSteps;
import com.casiowatch123.vchunklib.generation.virtual.world.chunk.VChunkHolder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VChunkLoadingManager {
    private static final ChunkStatus START_STATUS = ChunkStatus.EMPTY;
    private static final ChunkStatus FINAL_STATUS = ChunkStatus.FEATURES;
    private static final List<Integer> GENERATE_RADIUS_LIST = List.of(10, 10, 2, 2, 1, 1, 1, 0);
    private static final List<ChunkStatus> CHUNK_STATUS_LIST = ChunkStatus.createOrderedList()
            .subList(START_STATUS.getIndex(), FINAL_STATUS.getIndex() + 1);
            
    public static final int MAX_DEPENDENT_REGION_RADIUS = 10;
    private final VWorldService world;
    private final VChunkGenerationContext generationContext;
    
    public VChunkLoadingManager(VWorldService world) {
        this.world = world;
        this.generationContext = world.getGenerationContext();
    }
    
    //return custom chunk type 
    public VChunkHolder loadChunk(ChunkPos centerPos, int centerRadius) {
        if (centerRadius < 0) {
            throw new IllegalArgumentException("center radius is wrong: " + centerRadius);
        }
        
        int size = 2 * (MAX_DEPENDENT_REGION_RADIUS + centerRadius) + 1;
        int radius = size/2;
        
        
        BoundedRegionArray<VChunkHolder> chunks = this.generateRegion(centerPos, centerRadius);
        
        for(ChunkStatus status : CHUNK_STATUS_LIST) {
            int currentGeneratingSize = 2 * (GENERATE_RADIUS_LIST.get(status.getIndex()) + centerRadius) + 1;
            int blank = (size - currentGeneratingSize) / 2;
//            System.out.println("current status: " + status + "(" + GENERATE_RADIUS_LIST.get(status.getIndex()) + ")");
//            System.out.println("blank: " + blank);
//            System.out.println("currentGeneratingSize: " + currentGeneratingSize);
            
            VChunkGenerationStep step = VChunkGenerationSteps.GENERATION.get(status);
            for(int j = blank; j < size - blank; j++) {
                for(int k = blank; k < size - blank; k++) {
                    int x = centerPos.x - radius + k;
                    int z = centerPos.z - radius + j;
                    generate(chunks.get(x, z), step, chunks);
                }
            }
        }
        
        return chunks.get(centerPos.x, centerPos.z);
    }
    
    //generate VChunkHolder chunks;
    private BoundedRegionArray<VChunkHolder> generateRegion(ChunkPos centerPos, int centerRadius) {
        int size = 2 * (MAX_DEPENDENT_REGION_RADIUS + centerRadius) + 1;
        int radius = size/2;
        
        VChunkHolder[][] chunkHolders = new VChunkHolder[size][size];
        
        for(int i = 0; i < size ; i++) {
            for(int j = 0; j < size ; j++) {
                ChunkPos pos = new ChunkPos(centerPos.x - radius + i, centerPos.z - radius + j);
                chunkHolders[i][j] = new VChunkHolder(pos, generateProtoChunk(pos));
            }
        }
        
        return BoundedRegionArray.create(centerPos.x, centerPos.z, radius, 
                (chunkX, chunkZ) -> chunkHolders[chunkX - centerPos.x + radius][chunkZ - centerPos.z + radius]
        );
    }
    
    
    private CompletableFuture<Chunk> generate(VChunkHolder chunkHolder, VChunkGenerationStep step, BoundedRegionArray<VChunkHolder> chunks) {
        return step.run(this.generationContext, chunks, chunkHolder.getChunk());
    }
    
    private ProtoChunk generateProtoChunk(ChunkPos chunkPos) {
        return new ProtoChunk(
                chunkPos, 
                UpgradeData.NO_UPGRADE_DATA, 
                this.world.world(), 
                this.world.worldContext().getRegistryManager().get(RegistryKeys.BIOME), 
                null
        );
    }
}
