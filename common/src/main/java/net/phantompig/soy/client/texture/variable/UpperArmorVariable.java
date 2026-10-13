package net.phantompig.soy.client.texture.variable;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.titan.armor.ArmorSystemHolder;
import net.threetag.palladium.client.dynamictexture.variable.AbstractIntegerTextureVariable;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariable;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariableSerializer;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.util.context.DataContext;

import java.util.List;

public class UpperArmorVariable extends AbstractIntegerTextureVariable {
    public UpperArmorVariable(List<Pair<Operation, Integer>> operations) {
        super(operations);
    }

    @Override
    public int getNumber(DataContext context) {
        if (!(context.getEntity() instanceof ArmorSystemHolder arh)) return 0;
        return arh.soy$getArmorSystem().getUpper();
    }

    public static class Serializer implements ITextureVariableSerializer {

        @Override
        public ITextureVariable parse(JsonObject json) {
            return new UpperArmorVariable(AbstractIntegerTextureVariable.parseOperations(json));
        }

        @Override
        public void addDocumentationFields(JsonDocumentationBuilder builder) {
            builder.setTitle("Upper Armor Stage");
        }

        @Override
        public String getDocumentationDescription() {
            return "Returns the entity's upper armor stage on [0, 2]";
        }

        @Override
        public ResourceLocation getId() {
            return SubjectsOfYmir.rsrc("upper_armor");
        }
    }
}
