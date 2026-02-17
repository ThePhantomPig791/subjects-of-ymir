package net.phantompig.soy.item;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorMaterial;
import net.threetag.palladium.entity.PalladiumAttributes;
import net.threetag.palladium.item.AddonArmorItem;

import java.util.UUID;

public class FallDamageResistantAddonArmorItem extends AddonArmorItem {
    private static final UUID FALL_RESISTANCE_UUID = UUID.fromString("cdaa0dab-e4bc-4c9b-b304-24732407e40c");

    private final double fallDamageResistance;

    public FallDamageResistantAddonArmorItem(ArmorMaterial material, Type type, Properties properties, double fallDamageResistance) {
        super(material, type, properties);
        this.fallDamageResistance = fallDamageResistance;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot != this.type.getSlot()) return super.getDefaultAttributeModifiers(slot);
        ImmutableListMultimap.Builder<Attribute, AttributeModifier> map = new ImmutableListMultimap.Builder<>();
        map.putAll(super.getDefaultAttributeModifiers(slot));
        map.put(PalladiumAttributes.FALL_RESISTANCE.get(), new AttributeModifier(FALL_RESISTANCE_UUID, "uniform boots fall resistance", fallDamageResistance, AttributeModifier.Operation.ADDITION));
        return map.build();
    }
}
