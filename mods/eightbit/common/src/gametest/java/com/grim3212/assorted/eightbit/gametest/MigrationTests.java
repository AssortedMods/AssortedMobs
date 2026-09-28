package com.grim3212.assorted.eightbit.gametest;

import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.eightbit.common.item.EightBitItems;
import com.grim3212.assorted.lib.test.TestSupport;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.stats.RecipeBookSettings;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.Recipe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** A player who played when this was all Assorted Mobs keeps their recipe book and advancements. */
final class MigrationTests {

    private static final Identifier ROOT = Identifier.fromNamespaceAndPath(Constants.FAMILY_ID, "root");

    private MigrationTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("assortedmobs_recipe_book_carries_over", MigrationTests::recipeBookCarriesOver);
        out.accept("assortedmobs_advancements_carry_over", MigrationTests::advancementsCarryOver);
        out.accept("advancement_root_shows_the_bobomb", MigrationTests::advancementRootShowsTheBobomb);
    }

    private static void recipeBookCarriesOver(GameTestHelper helper) {
        ServerRecipeBook book = TestSupport.survivalPlayer(helper).getRecipeBook();
        book.loadUntrusted(new ServerRecipeBook.Packed(new RecipeBookSettings(), List.of(recipe(Constants.FAMILY_ID, "bobomb")), List.of()),
                key -> helper.getLevel().recipeAccess().byKey(key).isPresent());
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
                    + "\"DataVersion\": " + SharedConstants.getCurrentVersion().dataVersion().version() + "}");
        } catch (IOException e) {
            throw helper.assertionException("could not write a progress file: " + e);
        }

        PlayerAdvancements advancements = new PlayerAdvancements(server.getFixerUpper(), server.getPlayerList(), server.getAdvancements(), saved,
                TestSupport.survivalPlayer(helper));
        Identifier shellGame = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "shell_game");
        helper.assertTrue(advancements.getOrStartProgress(server.getAdvancements().get(shellGame)).isDone(), "Shell Game did not carry over to " + shellGame);
        helper.assertTrue(advancements.getOrStartProgress(server.getAdvancements().get(ROOT)).isDone(), "the root, done by a criterion since renamed, is not done");
        helper.succeed();
    }

    private static void advancementRootShowsTheBobomb(GameTestHelper helper) {
        helper.assertTrue(helper.getLevel().getServer().getAdvancements().get(ROOT).value().display().orElseThrow().getIcon().item().value() == EightBitItems.BOBOMB.get(),
                "the Assorted Mobs advancement root is not drawn with the Bob-omb, the family's first icon");
        helper.succeed();
    }

    private static ResourceKey<Recipe<?>> recipe(String namespace, String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(namespace, path));
    }
}
