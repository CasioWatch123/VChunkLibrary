package com.casiowatch123.vchunklib.mixin;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlacedFeature.class)
public class PlacedFeatureTestMixin {
    
    @Shadow @Final private RegistryEntry<ConfiguredFeature<?, ?>> feature;
    @Inject(
            method = "generate(" + 
                    "Lnet/minecraft/world/gen/feature/FeaturePlacementContext;" +
                    "Lnet/minecraft/util/math/random/Random;" +
                    "Lnet/minecraft/util/math/BlockPos;)Z", 
            at = @At("RETURN")
    )
    private void generate(FeaturePlacementContext context, Random random, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        System.out.println(feature.value().feature() + ": " + cir.getReturnValue().toString());
    }
}
