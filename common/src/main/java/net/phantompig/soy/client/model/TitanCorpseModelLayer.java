package net.phantompig.soy.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.*;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.entity.TitanCorpseEntity;

public class TitanCorpseModelLayer extends HumanoidModel<TitanCorpseEntity> {
    public static final ModelLayerLocation TITAN_CORPSE_MODEL_LAYER_LOCATION = new ModelLayerLocation(SubjectsOfYmir.rsrc("titan_corpse"), "main");

    public TitanCorpseModelLayer(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}
