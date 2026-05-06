package net.phantompig.soy.client.texture.variable;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.item.OdmAttachableAddonArmorItem;
import net.phantompig.soy.odm.component.OdmComponentItem;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariable;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariableSerializer;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.util.context.DataContext;

public class OdmSheathRightTextureVariable implements ITextureVariable {
    @Override
    public Object get(DataContext context) {
        if (context.getEntity() instanceof Player player) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.LEGS);
            if (stack.getItem() instanceof OdmAttachableAddonArmorItem item) {
                if (item.getRightSheath(stack).getItem() instanceof OdmComponentItem componentItem) {
                    return componentItem.odmComponent.name();
                }
            }
        }
        return "none";
    }

    public static class Serializer implements ITextureVariableSerializer {

        @Override
        public ITextureVariable parse(JsonObject json) {
            return new OdmSheathRightTextureVariable();
        }

        @Override
        public void addDocumentationFields(JsonDocumentationBuilder builder) {
            builder.setTitle("ODM Sheath Right");
        }

        @Override
        public String getDocumentationDescription() {
            return "Returns the name of the ODM component attached in the entity's legging's right sheath ODM slot";
        }

        @Override
        public ResourceLocation getId() {
            return SubjectsOfYmir.rsrc("odm_sheath_right");
        }
    }
}
