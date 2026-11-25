package com.grim3212.assorted.eightbit.gametest;

import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.eightbit.common.item.EightBitItems;
import com.grim3212.assorted.lib.test.TestSupport;
import net.minecraft.SharedConstants;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.stats.ServerRecipeBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** A player who played when this was all Assorted Mobs keeps their recipe book and advancements. */
final class MigrationTests {

    private static final ResourceLocation ROOT = ResourceLocation.fromNamespaceAndPath(Constants.FAMILY_ID, "root");

    private MigrationTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("assortedmobs_recipe_book_carries_over", MigrationTests::recipeBookCarriesOver);
        out.accept("assortedmobs_advancements_carry_over", MigrationTests::advancementsCarryOver);
        out.accept("advancement_root_shows_the_bobomb", MigrationTests::advancementRootShowsTheBobomb);
    }

    private static void recipeBookCarriesOver(GameTestHelper helper) {
        ServerRecipeBook book = TestSupport.survivalPlayer(helper).getRecipeBook();
        // Laid out the way a player's saved recipe book is.
        ListTag known = new ListTag();
        known.add(StringTag.valueOf(recipe(Constants.FAMILY_ID, "bobomb").toString()));
        CompoundTag saved = new CompoundTag();
        saved.put("recipes", known);
        book.fromNbt(saved, helper.getLevel().getRecipeManager());
        helper.assertTrue(book.contains(recipe(Constants.MOD_ID, "bobomb")), "the Bob-omb recipe a player had unlocked as assortedmobs:bobomb was not carried over");
        helper.succeed();
    }

    /** The progress file of a player who tamed a parabuzzy and picked up its shell, read the way it is when they join. */
    private static void advancementsCarryOver(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        String date = "\"2026-01-01 00:00:00 +0000\"";
        Path saved;
        try {
            saved = Files.createTempFile("assortedeightbit-advancements", ".json");
            Files.writeString(saved, "{\"assortedmobs:shell_game\": {\"criteria\": {\"tamed_parabuzzy\": " + date + "}, \"done\": true}, "
                    + "\"assortedmobs:root\": {\"criteria\": {\"has_parabuzzy_shell\": " + date + "}, \"done\": true}, "
                    + "\"DataVersion\": " + SharedConstants.getCurrentVersion().getDataVersion().getVersion() + "}");
        } catch (IOException e) {
            throw new GameTestAssertException("could not write a progress file: " + e);
        }

        PlayerAdvancements advancements = new PlayerAdvancements(server.getFixerUpper(), server.getPlayerList(), server.getAdvancements(), saved,
                TestSupport.survivalPlayer(helper));
        ResourceLocation shellGame = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "shell_game");
        helper.assertTrue(advancements.getOrStartProgress(server.getAdvancements().get(shellGame)).isDone(), "Shell Game did not carry over to " + shellGame);
        helper.assertTrue(advancements.getOrStartProgress(server.getAdvancements().get(ROOT)).isDone(), "the root, done by a criterion since renamed, is not done");
        helper.succeed();
    }

    private static void advancementRootShowsTheBobomb(GameTestHelper helper) {
        helper.assertTrue(helper.getLevel().getServer().getAdvancements().get(ROOT).value().display().orElseThrow().getIcon().getItem() == EightBitItems.BOBOMB.get(),
                "the Assorted Mobs advancement root is not drawn with the Bob-omb, the family's first icon");
        helper.succeed();
    }

    private static ResourceLocation recipe(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
