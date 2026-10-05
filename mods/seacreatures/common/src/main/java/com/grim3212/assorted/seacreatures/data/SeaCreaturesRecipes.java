package com.grim3212.assorted.seacreatures.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.seacreatures.Constants;
import com.grim3212.assorted.seacreatures.common.item.SeaCreaturesItems;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class SeaCreaturesRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public SeaCreaturesRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    /** None: a part's recipes are there whenever it is installed. */
    @Override
    public void registerConditions() {
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.COMBAT, SeaCreaturesItems.NARWHAL_SWORD.get())
                .define('N', SeaCreaturesItems.NARWHAL_HORN.get()).define('S', LibCommonTags.Items.RODS_WOODEN)
                .pattern("N").pattern("N").pattern("S")
                .unlockedBy("has_narwhal_horn", has(SeaCreaturesItems.NARWHAL_HORN.get())).save(this.output, key("narwhal_sword"));

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TOOLS, SeaCreaturesItems.SHELL_SHOVEL.get())
                .define('S', SeaCreaturesItems.SEA_SHELL.get()).define('T', LibCommonTags.Items.RODS_WOODEN)
                .pattern("S").pattern("T").pattern("T")
                .unlockedBy("has_sea_shell", has(SeaCreaturesItems.SEA_SHELL.get())).save(this.output, key("shell_shovel"));

        this.shellArmor(SeaCreaturesItems.SHELL_HELMET.get(), "shell_helmet", "SSS", "S S");
        this.shellArmor(SeaCreaturesItems.SHELL_CHESTPLATE.get(), "shell_chestplate", "S S", "SSS", "SSS");
        this.shellArmor(SeaCreaturesItems.SHELL_LEGGINGS.get(), "shell_leggings", "SSS", "S S", "S S");
        this.shellArmor(SeaCreaturesItems.SHELL_BOOTS.get(), "shell_boots", "S S", "S S");
    }

    private void shellArmor(Item piece, String name, String... pattern) {
        ShapedRecipeBuilder recipe = ShapedRecipeBuilder.shaped(this.items, RecipeCategory.COMBAT, piece).define('S', SeaCreaturesItems.SEA_SHELL.get());
        for (String row : pattern) {
            recipe.pattern(row);
        }
        recipe.unlockedBy("has_sea_shell", has(SeaCreaturesItems.SEA_SHELL.get())).save(this.output, key(name));
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
            return new SeaCreaturesRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
