package com.grim3212.assorted.eightbit.data;

import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.eightbit.common.entity.EightBitEntities;
import com.grim3212.assorted.eightbit.common.item.EightBitItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * This part's advancements, under the Assorted Mobs root that every part with advancements writes the same. The root
 * names only tags each part fills, so it loads whichever parts are installed and opens on meeting any of their creatures.
 */
public class EightBitAdvancements implements AdvancementSubProvider {

    public static final TagKey<EntityType<?>> OPENS_WHEN_KILLED = TagKey.create(Registries.ENTITY_TYPE, family("opens_advancements_when_killed"));
    public static final TagKey<EntityType<?>> OPENS_WHEN_TAMED = TagKey.create(Registries.ENTITY_TYPE, family("opens_advancements_when_tamed"));
    public static final TagKey<Item> OPENS_WHEN_HELD = TagKey.create(Registries.ITEM, family("opens_advancements_when_held"));

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> output) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStack(Items.SNOWBALL), Component.translatable("advancements." + Constants.FAMILY_ID + ".root.title"),
                        Component.translatable("advancements." + Constants.FAMILY_ID + ".root.description"),
                        Optional.of(ResourceLocation.withDefaultNamespace("textures/block/snow.png")), AdvancementType.TASK, false, false, false))
                .addCriterion("killed", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(OPENS_WHEN_KILLED)))
                .addCriterion("tamed", TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().of(OPENS_WHEN_TAMED)))
                .addCriterion("held", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(OPENS_WHEN_HELD)))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(output, family("root").toString());

        task("shell_game", root, EightBitItems.PARABUZZY_SPAWN_EGG.get())
                .addCriterion("tamed_parabuzzy", TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().of(EightBitEntities.PARABUZZY.get())))
                .save(output, id("shell_game"));

        task("bobomb", root, EightBitItems.BOBOMB.get())
                .addCriterion("has_bobomb", InventoryChangeTrigger.TriggerInstance.hasItems(EightBitItems.BOBOMB.get()))
                .save(output, id("bobomb"));
    }

    /**
     * Every part with advancements writes all three of the root's tags, empty or not, so none is ever missing.
     * These declare the other entity tag through {@code declare} and return the one to fill.
     */
    static TagKey<EntityType<?>> openedWhenKilled(Consumer<TagKey<EntityType<?>>> declare) {
        declare.accept(OPENS_WHEN_TAMED);
        return OPENS_WHEN_KILLED;
    }

    static TagKey<EntityType<?>> openedWhenTamed(Consumer<TagKey<EntityType<?>>> declare) {
        declare.accept(OPENS_WHEN_KILLED);
        return OPENS_WHEN_TAMED;
    }

    static TagKey<Item> itemTag() {
        return OPENS_WHEN_HELD;
    }

    private static Advancement.Builder task(String name, AdvancementHolder parent, ItemLike icon) {
        return Advancement.Builder.advancement()
                .parent(parent)
                .display(icon, title(name), description(name), null, AdvancementType.TASK, true, true, false);
    }

    private static Component title(String name) {
        return Component.translatable("advancements." + Constants.MOD_ID + "." + name + ".title");
    }

    private static Component description(String name) {
        return Component.translatable("advancements." + Constants.MOD_ID + "." + name + ".description");
    }

    private static String id(String name) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name).toString();
    }

    private static ResourceLocation family(String name) {
        return ResourceLocation.fromNamespaceAndPath(Constants.FAMILY_ID, name);
    }
}
