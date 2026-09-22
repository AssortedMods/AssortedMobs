package com.grim3212.assorted.mobs.client.data;

import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.common.item.MobsArmorMaterials;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Writes the {@code equipment} asset for shell armour. Without one it is worn untextured and nothing
 * warns. Vanilla's {@code EquipmentAssetProvider} hardcodes its own materials, so it cannot be extended.
 */
public class MobsEquipmentAssetProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;

    public MobsEquipmentAssetProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        // Not addHumanoidLayers: that names a humanoid_baby layer too, which has a 64x64 sheet of its own that
        // the 1.2.5 art has nothing for. With no layer a baby zombie in shell armour shows none; with a layer
        // and no texture it would show the missing texture.
        EquipmentClientInfo.Layer shell = new EquipmentClientInfo.Layer(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "shell"));
        Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> assets = Map.of(MobsArmorMaterials.SHELL_ASSET, EquipmentClientInfo.builder()
                .addLayers(EquipmentClientInfo.LayerType.HUMANOID, shell)
                .addLayers(EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS, shell)
                .build());
        return DataProvider.saveAll(cache, EquipmentClientInfo.CODEC, this.pathProvider::json, assets);
    }

    @Override
    public String getName() {
        return "Equipment Asset Definitions: " + Constants.MOD_ID;
    }
}
