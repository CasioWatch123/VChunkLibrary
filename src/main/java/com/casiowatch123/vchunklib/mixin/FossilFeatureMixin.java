package com.casiowatch123.vchunklib.mixin;

import com.casiowatch123.vchunklib.generation.virtual.Builtin;
import com.casiowatch123.vchunklib.generation.virtual.structure.VStructureTemplateManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.world.gen.feature.FossilFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FossilFeature.class)
public class FossilFeatureMixin {
    @Redirect(
            method = "generate", 
            at = @At(
                    value = "INVOKE", 
                    target = "Lnet/minecraft/server/world/ServerWorld;" +
                            "getServer()" +
                            "Lnet/minecraft/server/MinecraftServer;"
            )
    )
    private MinecraftServer redirectedGetServer(ServerWorld instance) {
        if (instance == null) {
            return null;
        }
        return instance.getServer();
    }
    
    @Redirect(
            method = "generate", 
            at = @At(
                    value = "INVOKE", 
                    target = "Lnet/minecraft/server/MinecraftServer;" +
                            "getStructureTemplateManager()" +
                            "Lnet/minecraft/structure/StructureTemplateManager;"
            )
    )
    private StructureTemplateManager redirectedGetStructureTemplateManager(MinecraftServer instance) {
        if (instance == null) {
            return Builtin.STRUCTURE_TEMPLATE_MANAGER;
        }
        return instance.getStructureTemplateManager();
    }
}
