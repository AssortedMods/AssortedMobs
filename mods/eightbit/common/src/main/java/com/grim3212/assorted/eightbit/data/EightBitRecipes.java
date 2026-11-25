package com.grim3212.assorted.eightbit.data;

import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.eightbit.common.item.EightBitItems;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class EightBitRecipes extends ConditionalRecipeProvider {

    public EightBitRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Constants.MOD_ID);
    }

    /** None: a part's recipes are there whenever it is installed. */
    @Override
    public void registerConditions() {
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        // A lit fuse, the powder, and a parabuzzy's shell to put it in.
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, EightBitItems.BOBOMB.get())
                .define('X', LibCommonTags.Items.DUSTS_REDSTONE).define('#', LibCommonTags.Items.GUNPOWDER).define('@', EightBitItems.PARABUZZY_SHELL.get())
                .pattern("X").pattern("#").pattern("@")
                .unlockedBy("has_parabuzzy_shell", has(EightBitItems.PARABUZZY_SHELL.get())).save(this.output, key("bobomb"));
    }

    private static ResourceLocation key(String name) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name);
    }
}
