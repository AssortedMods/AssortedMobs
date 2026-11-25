package com.grim3212.assorted.icepixie.gametest;

import com.google.gson.JsonObject;
import com.grim3212.assorted.icepixie.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.icepixie.gametest.IcePixieTestSupport.*;

/** What the mod ships: a model and a name for every item, a name for every creature and tag. */
final class AssetTests {

    private AssetTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("every_item_and_creature_has_a_model_and_name", AssetTests::everyItemAndCreatureHasAModelAndName);
        out.accept("every_item_tag_has_a_name", AssetTests::everyItemTagHasAName);
    }

    private static void everyItemAndCreatureHasAModelAndName(GameTestHelper helper) {
        JsonObject lang = readLang(helper);
        List<String> missing = new ArrayList<>();

        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            ResourceLocation id = entry.getKey().location();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            // 1.21.1 reads an item's model straight from models/item; the items/ definitions came with 1.21.4.
            if (!resourceExists("/assets/" + id.getNamespace() + "/models/item/" + id.getPath() + ".json")) {
                missing.add("models/item/" + id.getPath() + ".json");
            }
            // A block's item is drawn from its block model, which has no sprite of its own.
            if (!(entry.getValue() instanceof BlockItem) && !resourceExists("/assets/" + id.getNamespace() + "/textures/item/" + id.getPath() + ".png")) {
                missing.add("textures/item/" + id.getPath() + ".png");
            }
            if (!lang.has(entry.getValue().getDescriptionId())) {
                missing.add("lang key " + entry.getValue().getDescriptionId());
            }
        }

        for (Map.Entry<ResourceKey<EntityType<?>>, EntityType<?>> entry : BuiltInRegistries.ENTITY_TYPE.entrySet()) {
            if (Constants.MOD_ID.equals(entry.getKey().location().getNamespace()) && !lang.has(entry.getValue().getDescriptionId())) {
                missing.add("lang key " + entry.getValue().getDescriptionId());
            }
        }

        if (!lang.has("itemGroup." + Constants.FAMILY_ID)) {
            missing.add("lang key itemGroup." + Constants.FAMILY_ID);
        }

        helper.assertTrue(missing.isEmpty(), missing.size() + " missing assets: " + String.join("; ", missing));
        helper.succeed();
    }

    /**
     * Every non-vanilla item tag has a {@code tag.item.<namespace>.<path>} name, the check Fabric
     * API warns about at dev startup. Both loaders name the standard c: tags, so anything missing
     * is ours.
     */
    private static void everyItemTagHasAName(GameTestHelper helper) {
        Language language = Language.getInstance();
        List<String> missing = helper.getLevel().registryAccess().registryOrThrow(Registries.ITEM).getTagNames()
                .map(TagKey::location)
                .filter(id -> !"minecraft".equals(id.getNamespace()))
                .map(id -> "tag.item." + id.getNamespace() + "." + id.getPath().replace('/', '.'))
                .filter(key -> !language.has(key))
                .sorted()
                .toList();
        helper.assertTrue(missing.isEmpty(), "item tags with no name in any lang file: " + missing);
        helper.succeed();
    }
}
