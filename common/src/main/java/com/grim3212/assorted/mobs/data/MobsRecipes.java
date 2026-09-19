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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class MobsRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public MobsRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
        this.addConditions(partEnabled(MobsParts.EIGHT_BIT), Identifier.fromNamespaceAndPath(Constants.MOD_ID, "bobomb"));
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        // A lit fuse, the powder, and a parabuzzy's shell to put it in.
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.COMBAT, MobsItems.BOBOMB.get())
                .define('X', LibCommonTags.Items.DUSTS_REDSTONE).define('#', LibCommonTags.Items.GUNPOWDER).define('@', MobsItems.PARABUZZY_SHELL.get())
                .pattern("X").pattern("#").pattern("@")
                .unlockedBy("has_parabuzzy_shell", has(MobsItems.PARABUZZY_SHELL.get())).save(this.output, key("bobomb"));
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
