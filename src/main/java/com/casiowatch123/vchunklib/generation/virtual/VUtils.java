package com.casiowatch123.vchunklib.generation.virtual;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.command.CommandManager;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class VUtils {
    private VUtils() {}
    
    public static DynamicRegistryManager.Immutable createVanillaRegistryManager() {
        ResourcePackManager rpm = VanillaDataPackProvider.createClientManager();
        rpm.scanPacks();

        rpm.setEnabledProfiles(Set.of("vanilla"));

        SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(
                rpm,
                DataConfiguration.SAFE_MODE,
                true,
                true
        );

        SaveLoading.ServerConfig serverConfig = new SaveLoading.ServerConfig(
                dataPacks,
                CommandManager.RegistrationEnvironment.INTEGRATED,
                0
        );
        try (ExecutorService executor = Executors.newWorkStealingPool()) {
            return SaveLoading.load(
                            serverConfig,
                            loadContext -> new SaveLoading.LoadContext<>(null, loadContext.dimensionsRegistryManager()),
                            (rm, dp, combdRegistries, loadContext) ->
                                    combdRegistries,
                            executor,
                            executor)
                    .join()
                    .getCombinedRegistryManager();
        }
    }
}
