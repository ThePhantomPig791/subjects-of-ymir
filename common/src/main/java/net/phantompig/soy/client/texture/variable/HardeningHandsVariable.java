package net.phantompig.soy.client.texture.variable;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.titan.hardening.HardeningSystemHolder;
import net.threetag.palladium.client.dynamictexture.variable.AbstractIntegerTextureVariable;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariable;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariableSerializer;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.util.context.DataContext;

import java.util.List;

public class HardeningHandsVariable extends AbstractIntegerTextureVariable {
    public HardeningHandsVariable(List<Pair<Operation, Integer>> operations) {
        super(operations);
    }

    @Override
    public int getNumber(DataContext context) {
        if (!(context.getEntity() instanceof HardeningSystemHolder hardh)) return 0;
        return (int) (hardh.soy$getHardeningSystem().getHands() * 255);
    }

    public static class Serializer implements ITextureVariableSerializer {

        @Override
        public ITextureVariable parse(JsonObject json) {
            return new HardeningHandsVariable(AbstractIntegerTextureVariable.parseOperations(json));
        }

        @Override
        public void addDocumentationFields(JsonDocumentationBuilder builder) {
            builder.setTitle("Hardening - Hands");
        }

        @Override
        public String getDocumentationDescription() {
            return "Returns the entity's hands hardening, on a scale of 0 to 255. 0 means there is no hardening present";
        }

        @Override
        public ResourceLocation getId() {
            return SubjectsOfYmir.rsrc("hardening_hands");
        }
    }
}
