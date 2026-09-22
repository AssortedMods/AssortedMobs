package com.grim3212.assorted.mobs.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.common.MobsParts;
import com.grim3212.assorted.mobs.common.item.MobsItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class MobsRecipes extends ConditionalRecipeProvider {

    private static final TagKey<Item> SEEDS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, "seeds"));

    private final HolderGetter<Item> items;

    public MobsRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
        this.addConditions(partEnabled(MobsParts.EIGHT_BIT), Identifier.fromNamespaceAndPath(Constants.MOD_ID, "bobomb"));
        for (String recipe : new String[]{"narwhal_sword", "shell_helmet", "shell_chestplate", "shell_leggings", "shell_boots", "shell_shovel"}) {
            this.addConditions(partEnabled(MobsParts.SEA_CREATURES), Identifier.fromNamespaceAndPath(Constants.MOD_ID, recipe));
        }
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        // A lit fuse, the powder, and a parabuzzy's shell to put it in.
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.COMBAT, MobsItems.BOBOMB.get())
                .define('X', LibCommonTags.Items.DUSTS_REDSTONE).define('#', LibCommonTags.Items.GUNPOWDER).define('@', MobsItems.PARABUZZY_SHELL.get())
                .pattern("X").pattern("#").pattern("@")
                .unlockedBy("has_parabuzzy_shell", has(MobsItems.PARABUZZY_SHELL.get())).save(this.output, key("bobomb"));

        this.buildSeaCreatureRecipes();
    }

    private void buildSeaCreatureRecipes() {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.COMBAT, MobsItems.NARWHAL_SWORD.get())
                .define('N', MobsItems.NARWHAL_HORN.get()).define('S', LibCommonTags.Items.RODS_WOODEN)
                .pattern("N").pattern("N").pattern("S")
                .unlockedBy("has_narwhal_horn", has(MobsItems.NARWHAL_HORN.get())).save(this.output, key("narwhal_sword"));

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TOOLS, MobsItems.SHELL_SHOVEL.get())
                .define('S', MobsItems.SEA_SHELL.get()).define('T', LibCommonTags.Items.RODS_WOODEN)
                .pattern("S").pattern("T").pattern("T")
                .unlockedBy("has_sea_shell", has(MobsItems.SEA_SHELL.get())).save(this.output, key("shell_shovel"));

        this.shellArmor(MobsItems.SHELL_HELMET.get(), "shell_helmet", "SSS", "S S");
        this.shellArmor(MobsItems.SHELL_CHESTPLATE.get(), "shell_chestplate", "S S", "SSS", "SSS");
        this.shellArmor(MobsItems.SHELL_LEGGINGS.get(), "shell_leggings", "SSS", "S S", "S S");
        this.shellArmor(MobsItems.SHELL_BOOTS.get(), "shell_boots", "S S", "S S");
    }

    private void shellArmor(Item piece, String name, String... pattern) {
        ShapedRecipeBuilder recipe = ShapedRecipeBuilder.shaped(this.items, RecipeCategory.COMBAT, piece).define('S', MobsItems.SEA_SHELL.get());
        for (String row : pattern) {
            recipe.pattern(row);
        }
        recipe.unlockedBy("has_sea_shell", has(MobsItems.SEA_SHELL.get())).save(this.output, key(name));
    }

    private static ResourceKey<Recipe<?>> key(String name) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
    }

    /** Recipe providers are not data providers any more; this is what the datagen entry point adds. */
    public static class Runner extends ConditionalRecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries, Constants.MOD_ID);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new MobsRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
