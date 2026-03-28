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

public class StaminaVariable extends AbstractIntegerTextureVariable {
    public StaminaVariable(List<Pair<Operation, Integer>> operations) {
        super(operations);
    }

    @Override
    public int getNumber(DataContext context) {
        if (!(context.getEntity() instanceof SoyPlayerExtension ext)) return 0;
        return (int) ((float) ext.getTitanInstance().getStamina() / ext.getTitanInstance().getMaxStamina() * 255);
    }

    public static class Serializer implements ITextureVariableSerializer {

        @Override
        public ITextureVariable parse(JsonObject json) {
            return new StaminaVariable(AbstractIntegerTextureVariable.parseOperations(json));
        }

        @Override
        public void addDocumentationFields(JsonDocumentationBuilder builder) {
            builder.setTitle("Stamina");
        }

        @Override
        public String getDocumentationDescription() {
            return "Returns the entity's stamina out of their max stamina, ranging from 0 to 255. 0 means the entity is at zero stamina.";
        }

        @Override
        public ResourceLocation getId() {
            return SubjectsOfYmir.rsrc("stamina");
        }
    }
}
