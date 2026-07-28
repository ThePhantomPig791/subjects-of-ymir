package net.phantompig.soy.client.texture.variable;

import net.threetag.palladium.client.dynamictexture.DynamicTextureManager;

public class SoyDynamicTextureVariables {
    public static void init() {
        DynamicTextureManager.registerVariable(new TitanIdTextureVariable.Serializer());
        DynamicTextureManager.registerVariable(new TitanNamespaceTextureVariable.Serializer());
        DynamicTextureManager.registerVariable(new TitanVariantTextureVariable.Serializer());
        DynamicTextureManager.registerVariable(new TitanProgressTextureVariable.Serializer());
        DynamicTextureManager.registerVariable(new TitanDecayTextureVariable.Serializer());
        DynamicTextureManager.registerVariable(new TitanEyeColorTextureVariable.Serializer());
        DynamicTextureManager.registerVariable(new HardeningAllVariable.Serializer());
        DynamicTextureManager.registerVariable(new HardeningKnucklesVariable.Serializer());
        DynamicTextureManager.registerVariable(new HardeningHandsVariable.Serializer());
        DynamicTextureManager.registerVariable(new MarksVariable.Serializer());
        DynamicTextureManager.registerVariable(new OdmTurbineTextureVariable.Serializer());
        DynamicTextureManager.registerVariable(new OdmSheathRightTextureVariable.Serializer());
        DynamicTextureManager.registerVariable(new OdmSheathLeftTextureVariable.Serializer());
        DynamicTextureManager.registerVariable(new OdmBladesLeftTextureVariable.Serializer());
        DynamicTextureManager.registerVariable(new OdmBladesRightTextureVariable.Serializer());
    }
}
