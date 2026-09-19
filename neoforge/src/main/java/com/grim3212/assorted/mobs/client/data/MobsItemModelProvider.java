package com.grim3212.assorted.mobs.client.data;

import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.common.item.MobsItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;
import java.util.stream.Stream;

/** Every item here is a flat sprite named after it, spawn eggs included, as vanilla's are now. */
public class MobsItemModelProvider extends ModelProvider {

    public MobsItemModelProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Mobs item models";
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        MobsItems.ITEMS.getEntries().stream().map(Supplier::get).forEach(item -> itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM));
    }
}
