package com.grim3212.assorted.seacreatures.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.seacreatures.Constants;
import com.grim3212.assorted.seacreatures.common.item.SeaCreaturesItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class SeaCreaturesRecipes extends ConditionalRecipeProvider {

    public SeaCreaturesRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Constants.MOD_ID);
    }

    /** None: a part's recipes are there whenever it is installed. */
    @Override
    public void registerConditions() {
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, SeaCreaturesItems.NARWHAL_SWORD.get())
                .define('N', SeaCreaturesItems.NARWHAL_HORN.get()).define('S', LibCommonTags.Items.RODS_WOODEN)
                .pattern("N").pattern("N").pattern("S")
                .unlockedBy("has_narwhal_horn", has(SeaCreaturesItems.NARWHAL_HORN.get())).save(this.output, key("narwhal_sword"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SeaCreaturesItems.SHELL_SHOVEL.get())
                .define('S', SeaCreaturesItems.SEA_SHELL.get()).define('T', LibCommonTags.Items.RODS_WOODEN)
                .pattern("S").pattern("T").pattern("T")
                .unlockedBy("has_sea_shell", has(SeaCreaturesItems.SEA_SHELL.get())).save(this.output, key("shell_shovel"));

        this.shellArmor(SeaCreaturesItems.SHELL_HELMET.get(), "shell_helmet", "SSS", "S S");
        this.shellArmor(SeaCreaturesItems.SHELL_CHESTPLATE.get(), "shell_chestplate", "S S", "SSS", "SSS");
        this.shellArmor(SeaCreaturesItems.SHELL_LEGGINGS.get(), "shell_leggings", "SSS", "S S", "S S");
        this.shellArmor(SeaCreaturesItems.SHELL_BOOTS.get(), "shell_boots", "S S", "S S");
    }

    private void shellArmor(Item piece, String name, String... pattern) {
        ShapedRecipeBuilder recipe = ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, piece).define('S', SeaCreaturesItems.SEA_SHELL.get());
        for (String row : pattern) {
            recipe.pattern(row);
        }
        recipe.unlockedBy("has_sea_shell", has(SeaCreaturesItems.SEA_SHELL.get())).save(this.output, key(name));
    }

    private static ResourceLocation key(String name) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name);
    }
}
