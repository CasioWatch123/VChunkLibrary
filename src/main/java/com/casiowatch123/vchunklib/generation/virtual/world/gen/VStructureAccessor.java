package com.casiowatch123.vchunklib.generation.virtual.world.gen;

import com.casiowatch123.vchunklib.VChunkLib;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.StructurePresence;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class VStructureAccessor extends StructureAccessor {
    private static final Logger LOGGER = VChunkLib.LOGGER;
    
    private final WorldAccess world;
    private final GeneratorOptions options;
    public VStructureAccessor(WorldAccess world, GeneratorOptions options) {
        super(null, null, null);
        
        this.world = world;
        this.options = options;
    }
    
    @Override
    public StructureAccessor forRegion(ChunkRegion region) {
        LOGGER.error(
                "Called unsupported method: {}#{}",
                this.getClass().getName(),
                "forRegion");
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<StructureStart> getStructureStarts(ChunkPos pos, Predicate<Structure> predicate) {
        Map<Structure, LongSet> map = this.world.getChunk(pos.x, pos.z, ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences();
        ImmutableList.Builder<StructureStart> builder = ImmutableList.builder();

        for (Map.Entry<Structure, LongSet> entry : map.entrySet()) {
            Structure structure = (Structure)entry.getKey();
            if (predicate.test(structure)) {
                this.acceptStructureStarts(structure, (LongSet)entry.getValue(), builder::add);
            }
        }

        return builder.build();
    }
    
    @Override
    public List<StructureStart> getStructureStarts(ChunkSectionPos sectionPos, Structure structure) {
        LongSet longSet = this.world.getChunk(sectionPos.getSectionX(), sectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences(structure);
        ImmutableList.Builder<StructureStart> builder = ImmutableList.builder();
        this.acceptStructureStarts(structure, longSet, builder::add);
        return builder.build();
    }
    
    @Override
    public void acceptStructureStarts(Structure structure, LongSet structureStartPositions, Consumer<StructureStart> consumer) {
        LongIterator var4 = structureStartPositions.iterator();

        while (var4.hasNext()) {
            long l = (Long)var4.next();
            ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(new ChunkPos(l), this.world.getBottomSectionCoord());
            StructureStart structureStart = this.getStructureStart(
                    chunkSectionPos, structure, this.world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_STARTS)
            );
            if (structureStart != null && structureStart.hasChildren()) {
                consumer.accept(structureStart);
            }
        }
    }

    @Override
    @Nullable
    public StructureStart getStructureStart(ChunkSectionPos pos, Structure structure, StructureHolder holder) {
        return holder.getStructureStart(structure);
    }

    @Override
    public void setStructureStart(ChunkSectionPos pos, Structure structure, StructureStart structureStart, StructureHolder holder) {
        holder.setStructureStart(structure, structureStart);
    }
    
    @Override
    public void addStructureReference(ChunkSectionPos pos, Structure structure, long reference, StructureHolder holder) {
        holder.addStructureReference(structure, reference);
    }
    
    @Override
    public boolean shouldGenerateStructures() {
        return this.options.shouldGenerateStructures();
    }
    
    @Override
    public StructureStart getStructureAt(BlockPos pos, Structure structure) {
        for (StructureStart structureStart : this.getStructureStarts(ChunkSectionPos.from(pos), structure)) {
            if (structureStart.getBoundingBox().contains(pos)) {
                return structureStart;
            }
        }

        return StructureStart.DEFAULT;
    }
    
    @Override
    public StructureStart getStructureContaining(BlockPos pos, TagKey<Structure> tag) {
        return this.getStructureContaining(pos, structure -> structure.isIn(tag));
    }
    
    @Override
    public StructureStart getStructureContaining(BlockPos pos, RegistryEntryList<Structure> structures) {
        return this.getStructureContaining(pos, structures::contains);
    }
    
    @Override
    public StructureStart getStructureContaining(BlockPos pos, Predicate<RegistryEntry<Structure>> predicate) {
        Registry<Structure> registry = this.getRegistryManager().get(RegistryKeys.STRUCTURE);

        for (StructureStart structureStart : this.getStructureStarts(
                new ChunkPos(pos), structure -> (Boolean)registry.getEntry(registry.getRawId(structure)).map(predicate::test).orElse(false)
        )) {
            if (this.structureContains(pos, structureStart)) {
                return structureStart;
            }
        }

        return StructureStart.DEFAULT;
    }
    
    @Override
    public StructureStart getStructureContaining(BlockPos pos, Structure structure) {
        for (StructureStart structureStart : this.getStructureStarts(ChunkSectionPos.from(pos), structure)) {
            if (this.structureContains(pos, structureStart)) {
                return structureStart;
            }
        }

        return StructureStart.DEFAULT;
    }
    
    @Override
    public boolean structureContains(BlockPos pos, StructureStart structureStart) {
        for (StructurePiece structurePiece : structureStart.getChildren()) {
            if (structurePiece.getBoundingBox().contains(pos)) {
                return true;
            }
        }

        return false;
    }
    
    @Override
    public boolean hasStructureReferences(BlockPos pos) {
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(pos);
        return this.world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).hasStructureReferences();
    }
    
    @Override
    public Map<Structure, LongSet> getStructureReferences(BlockPos pos) {
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(pos);
        return this.world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences();
    }
    
    @Override
    public StructurePresence getStructurePresence(ChunkPos chunkPos, Structure structure, StructurePlacement placement, boolean skipReferencedStructures) {
        return StructurePresence.CHUNK_LOAD_NEEDED;
    }
    
    @Override
    public void incrementReferences(StructureStart structureStart) {
        structureStart.incrementReferences();
    }
    
    @Override
    public DynamicRegistryManager getRegistryManager() {
        return this.world.getRegistryManager();
    }
}
