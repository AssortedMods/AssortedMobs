package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Helpers and constants shared by Assorted Mobs's gametest classes, which import them statically,
 * alongside AssortedLib's {@code TestSupport}.
 */
final class MobsTestSupport {

    private MobsTestSupport() {
    }

    /** Middle of the 9x9x9 box, one block above its floor - room on every side for a drop. */
    static final BlockPos CENTRE = new BlockPos(4, 1, 4);

    /**
     * Writes an entity out and reads it into a fresh one of the same type, as a chunk save and load
     * would, failing the test on any serialization problem.
     */
    static <T extends Entity> T afterReload(GameTestHelper helper, T entity, EntityType<T> type) {
        ProblemReporter.Collector problems = new ProblemReporter.Collector();
        TagValueOutput output = TagValueOutput.createWithContext(problems, helper.getLevel().registryAccess());
        entity.saveWithoutId(output);
        helper.assertTrue(problems.isEmpty(), type.getDescriptionId() + " reported serialization errors: " + problems.getReport());
        CompoundTag saved = output.buildResult();

        T reloaded = type.create(helper.getLevel(), EntitySpawnReason.LOAD);
        helper.assertTrue(reloaded != null, "could not create a " + type.getDescriptionId() + " to load into");
        reloaded.load(TagValueInput.create(problems, helper.getLevel().registryAccess(), saved));
        helper.assertTrue(problems.isEmpty(), type.getDescriptionId() + " reported problems loading: " + problems.getReport());
        return reloaded;
    }

    /**
     * Lays {@code key} out at the box, as the world would for a structure generated there, without
     * building a block of it, and takes it out of the chunk again when the test ends. The structure
     * manager then finds it at {@link LaidOut#inside()}, the middle of its first piece, and not at
     * {@link LaidOut#above()}, a little way over the top of it in the same chunk.
     */
    static LaidOut layOutStructure(GameTestHelper helper, ResourceKey<Structure> key) {
        return layOutStructure(helper, key, 0);
    }

    /** {@code lift} blocks higher than it generated, clear of other tests' players and mobs. */
    static LaidOut layOutStructure(GameTestHelper helper, ResourceKey<Structure> key, int lift) {
        ServerLevel level = helper.getLevel();
        Holder.Reference<Structure> structure = level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getOrThrow(key);
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        ChunkPos origin = ChunkPos.containing(helper.absolutePos(CENTRE));
        StructureStart start = structure.value().generate(structure, level.dimension(), level.registryAccess(), generator, generator.getBiomeSource(),
                level.getChunkSource().randomState(), level.getStructureManager(), level.getSeed(), origin, 0, level, biome -> true);
        helper.assertTrue(start.isValid(), "could not lay out " + key.identifier() + " to test in");

        start.getPieces().forEach(piece -> piece.move(0, lift, 0));
        BoundingBox bounds = start.getPieces().getFirst().getBoundingBox();
        BlockPos inside = bounds.getCenter();
        level.getChunk(origin.x(), origin.z()).setStartForStructure(structure.value(), start);
        level.getChunk(inside).addReferenceForStructure(structure.value(), origin.pack());
        helper.runBeforeTestEnd(() -> level.getChunk(origin.x(), origin.z()).setStartForStructure(structure.value(), StructureStart.INVALID_START));
        return new LaidOut(inside, new BlockPos(inside.getX(), bounds.maxY() + 8, inside.getZ()));
    }

    record LaidOut(BlockPos inside, BlockPos above) {
    }

    static boolean resourceExists(String path) {
        try (InputStream in = MobsTestSupport.class.getResourceAsStream(path)) {
            return in != null;
        } catch (IOException e) {
            return false;
        }
    }

    /** A json the mod ships, read off the classpath rather than through a resource pack. */
    static JsonObject readJson(GameTestHelper helper, String path) {
        try (InputStream in = MobsTestSupport.class.getResourceAsStream(path)) {
            helper.assertTrue(in != null, path + " is not on the classpath");
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            throw helper.assertionException("could not read " + path + ": " + e);
        }
    }

    static JsonObject readLang(GameTestHelper helper) {
        return readJson(helper, "/assets/" + Constants.MOD_ID + "/lang/en_us.json");
    }
}
