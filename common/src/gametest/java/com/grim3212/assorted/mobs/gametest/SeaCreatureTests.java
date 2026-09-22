package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import net.minecraft.world.item.Item;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.Registries;
import com.grim3212.assorted.mobs.common.entity.Narwhal;
import com.grim3212.assorted.mobs.common.entity.Seal;
import com.grim3212.assorted.mobs.common.entity.Walrus;
import com.grim3212.assorted.mobs.common.item.MobsItems;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.mobs.gametest.MobsTestSupport.*;

/** The sea creatures: the herd that stands together, what each eats and leaves, and the armour made of shells. */
final class SeaCreatureTests {

    private static final TagKey<Item> RAW_FISH = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "foods/raw_fish"));

    private SeaCreatureTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("hitting_one_walrus_angers_the_herd", SeaCreatureTests::hittingOneWalrusAngersTheHerd);
        out.accept("walrus_left_alone_leaves_players_alone", SeaCreatureTests::walrusLeftAloneLeavesPlayersAlone);
        out.accept("sea_creatures_eat_what_they_should", SeaCreatureTests::seaCreaturesEatWhatTheyShould);
        out.accept("narwhal_leaves_its_horn", SeaCreatureTests::narwhalLeavesItsHorn);
        out.accept("sea_creatures_are_full_of_fish", SeaCreatureTests::seaCreaturesAreFullOfFish);
        out.accept("shell_armor_is_as_good_as_chainmail_and_has_its_textures", SeaCreatureTests::shellArmorIsAsGoodAsChainmailAndHasItsTextures);
    }

    private static void hittingOneWalrusAngersTheHerd(GameTestHelper helper) {
        ServerPlayer player = standingPlayer(helper, CENTRE.north(3));
        Walrus struck = helper.spawn(MobsEntities.WALRUS.get(), CENTRE.west(2));
        Walrus other = helper.spawn(MobsEntities.WALRUS.get(), CENTRE.east(2));

        // Not on its first tick: a blow then carries the same timestamp as no blow at all.
        helper.startSequence()
                .thenIdle(5)
                .thenExecute(() -> struck.hurtServer(helper.getLevel(), struck.damageSources().playerAttack(player), 1.0F))
                .thenWaitUntil(() -> {
                    helper.assertTrue(struck.getTarget() == player, "the walrus that was hit is after " + struck.getTarget());
                    helper.assertTrue(other.getTarget() == player, "the walrus beside it is after " + other.getTarget());
                })
                .thenExecute(() -> {
                    // Not left to see it through.
                    struck.discard();
                    other.discard();
                })
                .thenSucceed();
    }

    private static void walrusLeftAloneLeavesPlayersAlone(GameTestHelper helper) {
        ServerPlayer player = standingPlayer(helper, CENTRE.north(3));
        Walrus walrus = helper.spawn(MobsEntities.WALRUS.get(), CENTRE);

        helper.runAfterDelay(60, () -> {
            helper.assertTrue(walrus.getTarget() == null, "a walrus nobody hit went after " + walrus.getTarget());
            helper.assertValueEqual(player.getHealth(), player.getMaxHealth(), "health of a player standing by a walrus");
            helper.succeed();
        });
    }

    private static void seaCreaturesEatWhatTheyShould(GameTestHelper helper) {
        Seal seal = helper.spawnWithNoFreeWill(MobsEntities.SEAL.get(), CENTRE.west(2));
        Walrus walrus = helper.spawnWithNoFreeWill(MobsEntities.WALRUS.get(), CENTRE);

        helper.assertTrue(seal.isFood(new ItemStack(Items.COD)) && seal.isFood(new ItemStack(Items.SALMON)), "a seal turns down fish");
        helper.assertTrue(walrus.isFood(new ItemStack(Items.COD)), "a walrus turns down fish");
        helper.assertFalse(seal.isFood(new ItemStack(Items.WHEAT)), "a seal eats wheat");
        helper.assertTrue(seal.getBreedOffspring(helper.getLevel(), seal) instanceof Seal, "two seals do not make a seal");
        helper.succeed();
    }

    private static void narwhalLeavesItsHorn(GameTestHelper helper) {
        Narwhal narwhal = helper.spawnWithNoFreeWill(MobsEntities.NARWHAL.get(), CENTRE);
        narwhal.kill(helper.getLevel());
        helper.succeedWhen(() -> helper.assertItemEntityPresent(MobsItems.NARWHAL_HORN.get(), CENTRE, 3.0D));
    }

    /**
     * All four of them drop from the common raw fish tag, which is every raw fish there is and none of the cooked, and what two
     * of them were already for is still there.
     */
    private static void seaCreaturesAreFullOfFish(GameTestHelper helper) {
        for (String creature : new String[]{"seal", "walrus", "sea_otter", "narwhal"}) {
            String table = readJson(helper, "/data/assortedmobs/loot_table/entities/" + creature + ".json").toString();
            helper.assertTrue(table.contains("\"c:foods/raw_fish\""), "a " + creature + " does not drop from the common raw fish tag");
        }
        for (Item fish : new Item[]{Items.COD, Items.SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH}) {
            helper.assertTrue(new ItemStack(fish).is(RAW_FISH), fish + " is not a fish a sea creature drops");
        }
        helper.assertFalse(new ItemStack(Items.COOKED_COD).is(RAW_FISH), "a sea creature drops cooked cod without being set alight");
        helper.assertTrue(readJson(helper, "/data/assortedmobs/loot_table/entities/sea_otter.json").toString().contains("assortedmobs:sea_shell"), "a sea otter no longer drops sea shells");
        helper.assertTrue(readJson(helper, "/data/assortedmobs/loot_table/entities/narwhal.json").toString().contains("assortedmobs:narwhal_horn"), "a narwhal no longer drops its horn");
        helper.succeed();
    }

    /** Chainmail is 12: 2, 5, 4 and 1. Worn armour with no equipment asset, or no texture behind it, is drawn as nothing. */
    private static void shellArmorIsAsGoodAsChainmailAndHasItsTextures(GameTestHelper helper) {
        // On a husk: a test's player is never ticked, so what it wears never reaches its attributes.
        Husk wearer = helper.spawnWithNoFreeWill(EntityTypes.HUSK, CENTRE);
        double bare = wearer.getAttributeValue(Attributes.ARMOR);
        wearer.setItemSlot(EquipmentSlot.HEAD, new ItemStack(MobsItems.SHELL_HELMET.get()));
        wearer.setItemSlot(EquipmentSlot.CHEST, new ItemStack(MobsItems.SHELL_CHESTPLATE.get()));
        wearer.setItemSlot(EquipmentSlot.LEGS, new ItemStack(MobsItems.SHELL_LEGGINGS.get()));
        wearer.setItemSlot(EquipmentSlot.FEET, new ItemStack(MobsItems.SHELL_BOOTS.get()));

        for (String asset : new String[]{"equipment/shell.json", "textures/entity/equipment/humanoid/shell.png", "textures/entity/equipment/humanoid_leggings/shell.png"}) {
            helper.assertTrue(resourceExists("/assets/assortedmobs/" + asset), "shell armour is missing " + asset);
        }
        helper.assertTrue(new ItemStack(MobsItems.SHELL_HELMET.get()).isValidRepairItem(new ItemStack(MobsItems.SEA_SHELL.get())), "a sea shell does not mend shell armour");

        helper.startSequence()
                .thenIdle(2)
                .thenExecute(() -> helper.assertValueEqual(wearer.getAttributeValue(Attributes.ARMOR) - bare, 12.0D, "armour a full set of shell adds"))
                .thenSucceed();
    }
}
