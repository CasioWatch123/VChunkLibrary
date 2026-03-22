package com.casiowatch123.vchunklib.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin {
    
    @Inject(
            method = "create(Lnet/minecraft/world/World;Lnet/minecraft/entity/SpawnReason;)Lnet/minecraft/entity/Entity;", 
            at = @At("HEAD"), 
            cancellable = true)
    private void create(World world, SpawnReason reason, CallbackInfoReturnable<Entity> cir) {
        if (world == null) {
            cir.setReturnValue(null);
        }
    }
}
