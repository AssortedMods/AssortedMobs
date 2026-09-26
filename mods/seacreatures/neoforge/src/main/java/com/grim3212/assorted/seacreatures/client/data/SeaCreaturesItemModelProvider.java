package com.grim3212.assorted.seacreatures.client.data;

import com.grim3212.assorted.seacreatures.Constants;
import com.grim3212.assorted.seacreatures.common.item.SeaCreaturesItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/** Every item here is a flat sprite named after it, spawn eggs included, as vanilla's are now. */
public class SeaCreaturesItemModelProvider extends ModelProvider {

    /** Held like a tool, not like a loaf of bread. */
    private static final Set<Item> HANDHELD = Set.of(SeaCreaturesItems.NARWHAL_SWORD.get(), SeaCreaturesItems.SHELL_SHOVEL.get());

    public SeaCreaturesItemModelProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return Constants.MOD_NAME + " item models";
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        SeaCreaturesItems.ITEMS.getEntries().stream().map(Supplier::get)
                .forEach(item -> itemModels.generateFlatItem(item, HANDHELD.contains(item) ? ModelTemplates.FLAT_HANDHELD_ITEM : ModelTemplates.FLAT_ITEM));
    }
}
