package com.casiowatch123.vchunklib.mixin;

import com.casiowatch123.vchunklib.generation.virtual.world.chunk.VChunkRegion;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.treedecorator.PaleMossTreeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(PaleMossTreeDecorator.class)
public class PaleMossTreeDecoratorMixin {
    @Redirect(
            method = "method_64812(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/registry/entry/RegistryEntry$Reference;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;" +
                            "getChunkManager()" +
                            "Lnet/minecraft/server/world/ServerChunkManager;"
            )
    )
    private static ServerChunkManager method(ServerWorld instance) {
        if (instance == null) {
            return null;
        }
        return instance.getChunkManager();
    }

    @Redirect(
            method = "method_64812(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/registry/entry/RegistryEntry$Reference;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerChunkManager;" +
                            "getChunkGenerator()" +
                            "Lnet/minecraft/world/gen/chunk/ChunkGenerator;"
            )
    )
    private static ChunkGenerator method(
            ServerChunkManager instance, 
            StructureWorldAccess structureWorldAccess) {
        if (instance == null
                && structureWorldAccess instanceof VChunkRegion region) {
            return region.getWorldService().worldContext().getGenerator();
        }
        return instance.getChunkGenerator();
    }
}
