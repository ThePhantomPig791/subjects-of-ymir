package net.phantompig.soy.client.texture.variable;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.titan.TitanInstance;
import net.threetag.palladium.client.dynamictexture.variable.AbstractIntegerTextureVariable;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariable;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariableSerializer;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.util.context.DataContext;

import java.util.List;

public class TitanDecayTextureVariable extends AbstractIntegerTextureVariable {
    public TitanDecayTextureVariable(List<Pair<Operation, Integer>> operations) {
        super(operations);
    }

    @Override
    public int getNumber(DataContext context) {
        return (int) (-255 * (SoyProperties.DECAY.get(context.getEntity()) - TitanInstance.MAX_CORPSE_DECAY) / (float) TitanInstance.MAX_CORPSE_DECAY); // https://www.desmos.com/calculator/ffcoddolmo
    }

    public static class Serializer implements ITextureVariableSerializer {

        @Override
        public ITextureVariable parse(JsonObject json) {
            return new TitanDecayTextureVariable(AbstractIntegerTextureVariable.parseOperations(json));
        }

        @Override
        public void addDocumentationFields(JsonDocumentationBuilder builder) {
            builder.setTitle("Titan Decay");
        }

        @Override
        public String getDocumentationDescription() {
            return "Returns the entity's titan decay, scaled to 255 to be used for alpha masks.";
        }

        @Override
        public ResourceLocation getId() {
            return SubjectsOfYmir.rsrc("titan_decay");
        }
    }
}
