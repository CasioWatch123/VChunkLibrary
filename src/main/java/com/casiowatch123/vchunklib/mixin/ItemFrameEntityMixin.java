package com.casiowatch123.vchunklib.mixin;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemFrameEntity.class)
public class ItemFrameEntityMixin {
    @Redirect(
            method = "setHeldItemStack(Lnet/minecraft/item/ItemStack;Z)" +
                    "V",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/entity/decoration/ItemFrameEntity.playSound(Lnet/minecraft/sound/SoundEvent;FF)" +
                            "V"
            )
    )
    private void redirectPlaySound(ItemFrameEntity instance,
                                   SoundEvent sound,
                                   float volume,
                                   float pitch) {
        if (instance.getEntityWorld() != null) {
            instance.playSound(sound, volume, pitch);
        }
    }
}
