package net.phantompig.soy.client.texture.variable;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariable;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariableSerializer;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.util.context.DataContext;

public class TitanNamespaceTextureVariable implements ITextureVariable {
    @Override
    public Object get(DataContext context) {
        return SoyProperties.TITAN.get(context.getEntity()).getNamespace();
    }

    public static class Serializer implements ITextureVariableSerializer {

        @Override
        public ITextureVariable parse(JsonObject json) {
            return new TitanNamespaceTextureVariable();
        }

        @Override
        public void addDocumentationFields(JsonDocumentationBuilder builder) {
            builder.setTitle("Titan Namespace");
        }

        @Override
        public String getDocumentationDescription() {
            return "Returns the namespace of the entity's titan (the bit before the semicolon)";
        }

        @Override
        public ResourceLocation getId() {
            return SubjectsOfYmir.rsrc("titan_namespace");
        }
    }
}
