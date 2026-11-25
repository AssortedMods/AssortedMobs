package com.grim3212.assorted.eightbit.client.data;

import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.eightbit.common.item.EightBitItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

/** Every item here is a flat sprite named after it, spawn eggs included: their own textures are already coloured. */
public class EightBitItemModelProvider extends ItemModelProvider {

    public EightBitItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return Constants.MOD_NAME + " item models";
    }

    @Override
    protected void registerModels() {
        EightBitItems.ITEMS.getEntries().stream().map(Supplier::get)
                .forEach(this::basicItem);
    }
}
