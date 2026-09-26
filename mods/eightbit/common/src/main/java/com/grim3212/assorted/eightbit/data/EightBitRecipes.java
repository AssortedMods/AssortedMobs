package com.grim3212.assorted.eightbit.data;

import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.eightbit.common.item.EightBitItems;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
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

public class EightBitRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public EightBitRecipes(HolderLookup.Provider registries, RecipeOutput output) {
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

        // A lit fuse, the powder, and a parabuzzy's shell to put it in.
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.COMBAT, EightBitItems.BOBOMB.get())
                .define('X', LibCommonTags.Items.DUSTS_REDSTONE).define('#', LibCommonTags.Items.GUNPOWDER).define('@', EightBitItems.PARABUZZY_SHELL.get())
                .pattern("X").pattern("#").pattern("@")
                .unlockedBy("has_parabuzzy_shell", has(EightBitItems.PARABUZZY_SHELL.get())).save(this.output, key("bobomb"));
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
            return new EightBitRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
