package com.grim3212.assorted.mobs.data;

import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.item.MobsItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.triggers.KilledTrigger;
import net.minecraft.advancements.triggers.TameAnimalTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

/**
 * The mod's advancement tab: Grim World's two creature achievements, now advancements, under a root
 * that opens on meeting any of them. Nothing is gated on a part; a switched-off part just leaves a
 * branch nobody can finish.
 */
public class MobsAdvancements implements AdvancementSubProvider {

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> out) {
        HolderGetter<EntityType<?>> entities = registries.lookupOrThrow(Registries.ENTITY_TYPE);

        AdvancementHolder root = Advancement.Builder.advancement()
                .display(MobsItems.PARABUZZY_SHELL.get(), title("root"), description("root"),
                        Identifier.withDefaultNamespace("block/snow"), AdvancementType.TASK, false, false, false)
                .addCriterion("killed_ice_pixie", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entities, MobsEntities.ICE_PIXIE.get())))
                .addCriterion("tamed_treasure_mob", TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().of(entities, MobsEntities.TREASURE_MOB.get())))
                .addCriterion("tamed_parabuzzy", TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().of(entities, MobsEntities.PARABUZZY.get())))
                .addCriterion("has_parabuzzy_shell", InventoryChangeTrigger.TriggerInstance.hasItems(MobsItems.PARABUZZY_SHELL.get()))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(out, id("root"));

        // Only fire hurts one, so killing one means having carried a torch into the snow for it.
        task("rare_encounter", root, MobsItems.ICE_PIXIE_SPAWN_EGG.get())
                .addCriterion("killed_ice_pixie", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entities, MobsEntities.ICE_PIXIE.get())))
                .save(out, id("rare_encounter"));

        task("dont_kill_him", root, MobsItems.TREASURE_MOB_SPAWN_EGG.get())
                .addCriterion("tamed_treasure_mob", TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().of(entities, MobsEntities.TREASURE_MOB.get())))
                .save(out, id("dont_kill_him"));

        task("shell_game", root, MobsItems.PARABUZZY_SPAWN_EGG.get())
                .addCriterion("tamed_parabuzzy", TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().of(entities, MobsEntities.PARABUZZY.get())))
                .save(out, id("shell_game"));

        task("bobomb", root, MobsItems.BOBOMB.get())
                .addCriterion("has_bobomb", InventoryChangeTrigger.TriggerInstance.hasItems(MobsItems.BOBOMB.get()))
                .save(out, id("bobomb"));
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
}
