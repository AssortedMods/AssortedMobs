package com.grim3212.assorted.treasuremob.client.data;

import com.grim3212.assorted.treasuremob.Constants;
import com.grim3212.assorted.treasuremob.common.item.TreasureMobItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

/** Every item here is a flat sprite named after it, spawn eggs included. */
public class TreasureMobItemModelProvider extends ItemModelProvider {

    public TreasureMobItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return Constants.MOD_NAME + " item models";
    }

    @Override
    protected void registerModels() {
        TreasureMobItems.ITEMS.getEntries().stream().map(Supplier::get).forEach(this::basicItem);
    }
}
