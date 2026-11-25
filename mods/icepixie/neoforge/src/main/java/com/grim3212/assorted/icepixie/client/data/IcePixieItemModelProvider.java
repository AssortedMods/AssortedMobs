package com.grim3212.assorted.icepixie.client.data;

import com.grim3212.assorted.icepixie.Constants;
import com.grim3212.assorted.icepixie.common.item.IcePixieItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

/** Every item here is a flat sprite named after it, spawn eggs included: their own textures are already coloured. */
public class IcePixieItemModelProvider extends ItemModelProvider {

    public IcePixieItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return Constants.MOD_NAME + " item models";
    }

    @Override
    protected void registerModels() {
        IcePixieItems.ITEMS.getEntries().stream().map(Supplier::get)
                .forEach(this::basicItem);
    }
}
