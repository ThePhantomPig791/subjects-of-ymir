package net.phantompig.soy.client.texture.variable;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.item.OdmAttachableAddonArmorItem;
import net.phantompig.soy.odm.component.OdmComponentItem;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariable;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariableSerializer;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.util.context.DataContext;

public class OdmBladesLeftTextureVariable implements ITextureVariable {
    @Override
    public Object get(DataContext context) {
        if (context.getEntity() instanceof LivingEntity living) {
            ItemStack stack = living.getItemBySlot(EquipmentSlot.LEGS);
            if (stack.getItem() instanceof OdmAttachableAddonArmorItem item) {
                ItemStack sheath = item.getLeftSheath(stack);
                if (sheath.getItem() instanceof OdmComponentItem componentItem) {
                    return componentItem.getBladeCount(sheath);
                }
            }
        }
        return 0;
    }

    public static class Serializer implements ITextureVariableSerializer {

        @Override
        public ITextureVariable parse(JsonObject json) {
            return new OdmBladesLeftTextureVariable();
        }

        @Override
        public void addDocumentationFields(JsonDocumentationBuilder builder) {
            builder.setTitle("ODM Blades Left");
        }

        @Override
        public String getDocumentationDescription() {
            return "Returns the amount of blades in the entity's left ODM sheath";
        }

        @Override
        public ResourceLocation getId() {
            return SubjectsOfYmir.rsrc("odm_blades_left");
        }
    }
}
