package com.casiowatch123.vchunklib.mixin;

import com.casiowatch123.vchunklib.generation.virtual.world.chunk.VChunkRegion;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.CappedStructureProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(CappedStructureProcessor.class)
public class CappedStructureProcessorMixin {
    
    @Redirect(
            method = "reprocess(" +
                    "Lnet/minecraft/world/ServerWorldAccess;" +
                    "Lnet/minecraft/util/math/BlockPos;" +
                    "Lnet/minecraft/util/math/BlockPos;" +
                    "Ljava/util/List;" +
                    "Ljava/util/List;" +
                    "Lnet/minecraft/structure/StructurePlacementData;)" +
                    "Ljava/util/List;", 
            at = @At(
                    value = "INVOKE", 
                    target = "Lnet/minecraft/server/world/ServerWorld;getSeed()J"
            )
    )
    private long redirectedGetSeed(
            ServerWorld instance, 
            ServerWorldAccess world) {
        if (world instanceof VChunkRegion vChunkRegion) {
            return vChunkRegion.getSeed();
        }
        return instance.getSeed();
    }
}
