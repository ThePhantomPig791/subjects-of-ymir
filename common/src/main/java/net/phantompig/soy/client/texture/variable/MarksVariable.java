package net.phantompig.soy.client.texture.variable;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.threetag.palladium.client.dynamictexture.variable.AbstractIntegerTextureVariable;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariable;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariableSerializer;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.util.context.DataContext;

import java.util.List;

public class MarksVariable extends AbstractIntegerTextureVariable {
    public MarksVariable(List<Pair<Operation, Integer>> operations) {
        super(operations);
    }

    @Override
    public int getNumber(DataContext context) {
        if (!(context.getEntity() instanceof SoyPlayerExtension ext)) return 0;
        int marks = ext.soy$getTitanInstance().getMarksTimer();
        return (int) (255f * marks / (marks + 2000));
    }

    public static class Serializer implements ITextureVariableSerializer {

        @Override
        public ITextureVariable parse(JsonObject json) {
            return new MarksVariable(AbstractIntegerTextureVariable.parseOperations(json));
        }

        @Override
        public void addDocumentationFields(JsonDocumentationBuilder builder) {
            builder.setTitle("Marks");
        }

        @Override
        public String getDocumentationDescription() {
            return "Returns the entity's marks alpha value, ranging from 0 to 255. 0 means the entity has no marks.";
        }

        @Override
        public ResourceLocation getId() {
            return SubjectsOfYmir.rsrc("marks");
        }
    }
}
