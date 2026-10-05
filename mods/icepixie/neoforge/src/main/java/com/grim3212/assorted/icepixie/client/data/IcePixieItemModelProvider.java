package com.grim3212.assorted.icepixie.client.data;

import com.grim3212.assorted.icepixie.Constants;
import com.grim3212.assorted.icepixie.common.item.IcePixieItems;
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
public class IcePixieItemModelProvider extends ModelProvider {

    public IcePixieItemModelProvider(PackOutput output) {
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
        IcePixieItems.ITEMS.getEntries().stream().map(Supplier::get)
                .forEach(item -> itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM));
    }
}
