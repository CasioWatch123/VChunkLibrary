package com.casiowatch123.vchunklib.mixin;

import com.mojang.datafixers.DataFixer;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.file.Path;
import java.nio.file.Paths;

@Mixin(StructureTemplateManager.class)
public abstract class StructureTemplateManagerMixin {

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/LevelStorage$Session;getDirectory(Lnet/minecraft/util/WorldSavePath;)Ljava/nio/file/Path;"
            )
    )
    private Path redirectGetDirectory(LevelStorage.Session session, WorldSavePath savePath) {
        if (session == null) {
            return Paths.get(".").toAbsolutePath();
        }
        return session.getDirectory(savePath);
    }
}