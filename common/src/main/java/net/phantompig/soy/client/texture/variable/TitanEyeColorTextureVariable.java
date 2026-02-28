package net.phantompig.soy.client.texture.variable;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariable;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariableSerializer;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.util.context.DataContext;

public class TitanEyeColorTextureVariable implements ITextureVariable {
    @Override
    public Object get(DataContext context) {
        return Integer.toHexString(SoyProperties.EYE_COLOR.get(context.getEntity()).getRGB()).substring(2); // chop off the pesky alpha
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
