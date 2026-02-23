package com.casiowatch123.vchunklib.generation.virtual.structure;

import com.casiowatch123.vchunklib.VChunkLib;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.structure.StructureTemplate;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class VStructureTemplateManager extends StructureTemplateManager {
    private static final Logger LOGGER = VChunkLib.LOGGER;
    private static final Map<Identifier, NbtCompound> NBT_COMPOUND_MAP = new ConcurrentHashMap<>();
        
    private final Map<Identifier, StructureTemplate> templates = new HashMap<>();
    
    static {
        ModContainer container = FabricLoader.getInstance().getModContainer(VChunkLib.MOD_ID).orElseThrow();
        
        for(var resourceRoot : container.getRootPaths()) {
            Path structureResourceRoot = resourceRoot
                    .resolve("data")
                    .resolve("vchunklib")
                    .resolve("structure");
            
            if (!Files.exists(structureResourceRoot) || !Files.isDirectory(structureResourceRoot)) {
                continue;
            }
            
            try (Stream<Path> stream = Files.walk(structureResourceRoot)) {
                stream.forEach(path -> {
                    if (!Files.isDirectory(path)) {
                        String identifierString =
                                StreamSupport.stream(structureResourceRoot.relativize(path).spliterator(), false)
                                        .map(Path::toString)
                                        .collect(Collectors.joining("/"))
                                        .replace(".nbt", "");
                        try {
                            NbtCompound nbt = NbtIo.readCompressed(path, NbtSizeTracker.ofUnlimitedBytes());
                            NBT_COMPOUND_MAP.put(Identifier.of(identifierString), nbt);
                        } catch (IOException ignored) {
                            LOGGER.error("Structure template file({}) loading failed: {}#static",
                                    structureResourceRoot.relativize(path), 
                                    VStructureTemplateManager.class.getName());
                        }
                    }
                });
            } catch (IOException ignored) {
                LOGGER.error("Structure template files(.nbt) loading failed: {}#static", VStructureTemplateManager.class.getName());
            }
        }
    }
    
    public VStructureTemplateManager(RegistryEntryLookup<Block> blockEntryLookup) {
        super(null, null, null, null);
        
        NBT_COMPOUND_MAP.forEach((identifier, nbtCompound) -> {
            StructureTemplate structureTemplate = new StructureTemplate();
            int version = NbtHelper.getDataVersion(nbtCompound, 500);
            
            structureTemplate.readNbt(blockEntryLookup, nbtCompound);
            templates.put(identifier, structureTemplate);
        });
    }
    
    @Override
    public StructureTemplate getTemplateOrBlank(Identifier id) {
        Optional<StructureTemplate> optional = this.getTemplate(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            StructureTemplate structureTemplate = new StructureTemplate();
            this.templates.put(id, structureTemplate);
            return structureTemplate;
        }
    }

    @Override
    public Optional<StructureTemplate> getTemplate(Identifier id) {
        return templates.containsKey(id) ? Optional.of(templates.get(id)) : Optional.empty();
    }

    @Override
    public Stream<Identifier> streamTemplates() {
        return templates.keySet().stream();
    }

    @Override
    public StructureTemplate createTemplate(NbtCompound nbt) {
        LOGGER.error(
                "Called unsupported method: {}#{}",
                this.getClass().getName(),
                "createTemplate");
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean saveTemplate(Identifier id) {
        LOGGER.warn(
                "Called unsupported method: {}#{}",
                this.getClass().getName(),
                "saveTemplate");
        return false;
    }

    @Override
    public Path getTemplatePath(Identifier id, String extension) {
        LOGGER.error(
                "Called unsupported method: {}#{}",
                this.getClass().getName(),
                "getTemplatePath");
        throw new UnsupportedOperationException();
    }

    @Override
    public void unloadTemplate(Identifier id) {
        LOGGER.warn(
                "Called unsupported method: {}#{}",
                this.getClass().getName(),
                "unloadTemplate");
    }
    
    @Override
    public void setResourceManager(ResourceManager rm) {
        LOGGER.warn(
                "Called unsupported method: {}#{}", 
                this.getClass().getName(), 
                "setResourceManager");
    }
}
