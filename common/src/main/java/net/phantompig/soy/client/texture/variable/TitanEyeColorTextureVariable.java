package net.phantompig.soy.client.texture.variable;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.power.ability.BerserkAbility;
import net.phantompig.soy.power.ability.SoyAbilities;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariable;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariableSerializer;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.AbilityUtil;
import net.threetag.palladium.util.context.DataContext;

public class TitanEyeColorTextureVariable implements ITextureVariable {
    @Override
    public Object get(DataContext context) {
        if (isInBerserk(context)) return "fd5800";
        return Integer.toHexString(SoyProperties.EYE_COLOR.get(context.getEntity()).getRGB()).substring(2); // chop off the pesky alpha
    }

    private boolean isInBerserk(DataContext context) {
        for (AbilityInstance ability : AbilityUtil.getEnabledInstances(context.getLivingEntity(), SoyAbilities.BERSERK.get())) {
            if (ability.getProperty(BerserkAbility.TIMER) > 0) return true;
        }
        return false;
    }

    public static class Serializer implements ITextureVariableSerializer {

        @Override
        public ITextureVariable parse(JsonObject json) {
            return new TitanEyeColorTextureVariable();
        }

        @Override
        public void addDocumentationFields(JsonDocumentationBuilder builder) {
            builder.setTitle("Titan Eye Color");
        }

        @Override
        public String getDocumentationDescription() {
            return "Returns the RGB representation of the titan's eye color";
        }

        @Override
        public ResourceLocation getId() {
            return SubjectsOfYmir.rsrc("titan_eye_color");
        }
    }
}
