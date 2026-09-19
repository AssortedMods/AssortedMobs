package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
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
