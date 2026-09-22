package com.grim3212.assorted.mobs.common.sounds;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.mobs.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class MobsSounds {

    public static final RegistryProvider<SoundEvent> SOUNDS = RegistryProvider.create(Registries.SOUND_EVENT, Constants.MOD_ID);

    public static final IRegistryObject<SoundEvent> BOBOMB_AMBIENT = registerSound("entity.bobomb.ambient");
    public static final IRegistryObject<SoundEvent> PARABUZZY_AMBIENT = registerSound("entity.parabuzzy.ambient");
    public static final IRegistryObject<SoundEvent> PARABUZZY_HURT = registerSound("entity.parabuzzy.hurt");
    public static final IRegistryObject<SoundEvent> PARABUZZY_DEATH = registerSound("entity.parabuzzy.death");
    public static final IRegistryObject<SoundEvent> SEAL_AMBIENT = registerSound("entity.seal.ambient");
    public static final IRegistryObject<SoundEvent> SEAL_HURT = registerSound("entity.seal.hurt");
    public static final IRegistryObject<SoundEvent> WALRUS_AMBIENT = registerSound("entity.walrus.ambient");
    public static final IRegistryObject<SoundEvent> WALRUS_HURT = registerSound("entity.walrus.hurt");

    private static IRegistryObject<SoundEvent> registerSound(String name) {
        Identifier loc = Identifier.fromNamespaceAndPath(Constants.MOD_ID, name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(loc));
    }

    public static void init() {
    }
}
