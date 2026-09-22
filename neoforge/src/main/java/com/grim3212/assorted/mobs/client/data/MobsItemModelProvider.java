package com.grim3212.assorted.mobs.client.data;

import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.common.item.MobsItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/** Every item here is a flat sprite named after it, spawn eggs included, as vanilla's are now. */
public class MobsItemModelProvider extends ModelProvider {

    /** Held like a tool, not like a loaf of bread. */
    private static final Set<Item> HANDHELD = Set.of(MobsItems.NARWHAL_SWORD.get(), MobsItems.SHELL_SHOVEL.get());

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

    /** The blocks' items are {@link MobsBlockstateProvider}'s. */
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> !(holder.value() instanceof BlockItem));
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        MobsItems.ITEMS.getEntries().stream().map(Supplier::get).filter(item -> !(item instanceof BlockItem))
                .forEach(item -> itemModels.generateFlatItem(item, HANDHELD.contains(item) ? ModelTemplates.FLAT_HANDHELD_ITEM : ModelTemplates.FLAT_ITEM));
    }
}
