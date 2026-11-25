package com.grim3212.assorted.seacreatures.client.data;

import com.grim3212.assorted.seacreatures.Constants;
import com.grim3212.assorted.seacreatures.common.item.SeaCreaturesItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Set;
import java.util.function.Supplier;

/** Every item here is a flat sprite named after it, spawn eggs included. */
public class SeaCreaturesItemModelProvider extends ItemModelProvider {

    public SeaCreaturesItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return Constants.MOD_NAME + " item models";
    }

    @Override
    protected void registerModels() {
        // Held like a tool, not like a loaf of bread.
        Set<Item> handheld = Set.of(SeaCreaturesItems.NARWHAL_SWORD.get(), SeaCreaturesItems.SHELL_SHOVEL.get());
        SeaCreaturesItems.ITEMS.getEntries().stream().map(Supplier::get).forEach(item -> {
            if (handheld.contains(item)) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
                this.withExistingParent(id.toString(), ResourceLocation.withDefaultNamespace("item/handheld"))
                        .texture("layer0", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath()));
            } else {
                this.basicItem(item);
            }
        });
    }
}
