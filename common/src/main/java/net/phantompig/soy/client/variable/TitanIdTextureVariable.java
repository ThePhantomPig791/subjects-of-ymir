package net.phantompig.soy.client.variable;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariable;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariableSerializer;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.util.context.DataContext;

public class TitanIdTextureVariable implements ITextureVariable {
    @Override
    public Object get(DataContext context) {
        return SoyProperties.TITAN.get(context.getEntity()).getPath();
    }

    public static class Serializer implements ITextureVariableSerializer {

        @Override
        public ITextureVariable parse(JsonObject json) {
            return new TitanIdTextureVariable();
        }

        @Override
        public void addDocumentationFields(JsonDocumentationBuilder builder) {
            builder.setTitle("Titan ID");
        }

        @Override
        public String getDocumentationDescription() {
            return "Returns the ID of the entity's titan (the path of its ResourceLocation; the bit after the semicolon)";
        }

        @Override
        public ResourceLocation getId() {
            return SubjectsOfYmir.rsrc("titan_id");
        }
    }
}
