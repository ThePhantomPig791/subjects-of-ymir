package net.phantompig.soy.client.variable;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariable;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariableSerializer;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.util.context.DataContext;

public class TitanVariantTextureVariable implements ITextureVariable {
    @Override
    public Object get(DataContext context) {
        return SoyProperties.VARIANT.get(context.getEntity());
    }

    public static class Serializer implements ITextureVariableSerializer {

        @Override
        public ITextureVariable parse(JsonObject json) {
            return new TitanVariantTextureVariable();
        }

        @Override
        public void addDocumentationFields(JsonDocumentationBuilder builder) {
            builder.setTitle("Titan Variant");
        }

        @Override
        public String getDocumentationDescription() {
            return "Returns the entity's titan variant";
        }

        @Override
        public ResourceLocation getId() {
            return SubjectsOfYmir.rsrc("titan_variant");
        }
    }
}
