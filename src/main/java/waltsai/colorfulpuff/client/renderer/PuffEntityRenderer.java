package waltsai.colorfulpuff.client.renderer;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import waltsai.colorfulpuff.ModProvider;
import waltsai.colorfulpuff.Utils;
import waltsai.colorfulpuff.client.model.PuffEntityModel;
import waltsai.colorfulpuff.server.entity.AbstractPuffEntity;
import waltsai.colorfulpuff.server.entity.PuffEntity;

public class PuffEntityRenderer extends AbstractPuffEntityRenderer<PuffEntity> {
    public PuffEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PuffEntityModel<>(ctx.getPart(PuffEntityModel.PUFF)), 0.5F);
        this.addFeature(new ArmorFeatureRenderer<>(this, this.getModel(ctx.getPart(PuffEntityModel.PUFF_BASE_INNER_ARMOR)), this.getModel(ctx.getPart(PuffEntityModel.PUFF_BASE_OUTER_ARMOR))));
        this.addFeature(new PuffEyeFeatureRenderer<>(this) {
            @Override
            public Identifier getEyeTexture(PuffEntity entity) {
                return Utils.identifier( "textures/entity/original/eyes/" + entity.getEyeType().name().toLowerCase() + ".png");
            }
        });
    }

    @Override
    protected void scale(PuffEntity entity, MatrixStack matrices, float amount) {
        super.scale(entity, matrices, amount);
        if(entity.getClothType() == PuffEntity.ClothType.HONEY) {
            matrices.scale(0.909F, 0.909F, 0.909F);
        }
    }

    @Override
    public String getTextureLocation(PuffEntity entity) {
        return "textures/entity/original/cloth/" + entity.getClothType().name().toLowerCase() + ".png";
    }

    private <T extends AbstractPuffEntity> BipedEntityModel<T> getModel(ModelPart part) {
        return new BipedEntityModel<T>(part);
    }
}
