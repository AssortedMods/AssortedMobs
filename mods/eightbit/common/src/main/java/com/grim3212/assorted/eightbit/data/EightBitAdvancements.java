package com.grim3212.assorted.eightbit.data;

import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.eightbit.Family;
import com.grim3212.assorted.eightbit.common.entity.EightBitEntities;
import com.grim3212.assorted.eightbit.common.item.EightBitItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.triggers.KilledTrigger;
import net.minecraft.advancements.triggers.TameAnimalTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This part's advancements, under the Assorted Mobs root that every part with advancements writes the same. The root
 * names only tags each part fills, so it loads whichever parts are installed and opens on meeting any of their creatures.
 */
public class EightBitAdvancements implements AdvancementSubProvider {

    public static final TagKey<EntityType<?>> OPENS_WHEN_KILLED = TagKey.create(Registries.ENTITY_TYPE, family("opens_advancements_when_killed"));
    public static final TagKey<EntityType<?>> OPENS_WHEN_TAMED = TagKey.create(Registries.ENTITY_TYPE, family("opens_advancements_when_tamed"));
    public static final TagKey<Item> OPENS_WHEN_HELD = TagKey.create(Registries.ITEM, family("opens_advancements_when_held"));

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> out) {
        HolderGetter<EntityType<?>> entities = registries.lookupOrThrow(Registries.ENTITY_TYPE);

        AdvancementHolder root = Advancement.Builder.advancement()
                .display(Items.SNOWBALL, Component.translatable("advancements." + Family.ID + ".root.title"),
                        Component.translatable("advancements." + Family.ID + ".root.description"),
                        Identifier.withDefaultNamespace("block/snow"), AdvancementType.TASK, false, false, false)
                .addCriterion("killed", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entities, OPENS_WHEN_KILLED)))
                .addCriterion("tamed", TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().of(entities, OPENS_WHEN_TAMED)))
                .addCriterion("held", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(registries.lookupOrThrow(Registries.ITEM), OPENS_WHEN_HELD)))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(out, family("root").toString());

        task("shell_game", root, EightBitItems.PARABUZZY_SPAWN_EGG.get())
                .addCriterion("tamed_parabuzzy", TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().of(entities, EightBitEntities.PARABUZZY.get())))
                .save(out, id("shell_game"));

        task("bobomb", root, EightBitItems.BOBOMB.get())
                .addCriterion("has_bobomb", InventoryChangeTrigger.TriggerInstance.hasItems(EightBitItems.BOBOMB.get()))
                .save(out, id("bobomb"));
    }

    /** Every part with advancements writes all three of the root's tags, empty or not, so none is ever missing. */
    static TagAppender<EntityType<?>> openedWhenKilled(Function<TagKey<EntityType<?>>, TagAppender<EntityType<?>>> tagger) {
        tagger.apply(OPENS_WHEN_TAMED);
        return tagger.apply(OPENS_WHEN_KILLED);
    }

    static TagAppender<EntityType<?>> openedWhenTamed(Function<TagKey<EntityType<?>>, TagAppender<EntityType<?>>> tagger) {
        tagger.apply(OPENS_WHEN_KILLED);
        return tagger.apply(OPENS_WHEN_TAMED);
    }

    static TagAppender<Item> itemTags(Function<TagKey<Item>, TagAppender<Item>> appender) {
        return appender.apply(OPENS_WHEN_HELD);
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
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, name).toString();
    }

    private static Identifier family(String name) {
        return Identifier.fromNamespaceAndPath(Family.ID, name);
    }
}
