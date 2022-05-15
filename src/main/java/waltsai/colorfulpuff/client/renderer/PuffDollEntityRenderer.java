package waltsai.colorfulpuff.client.renderer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import waltsai.colorfulpuff.Utils;
import waltsai.colorfulpuff.client.model.PuffDollEntityModel;
import waltsai.colorfulpuff.server.entity.PuffDollEntity;

public class PuffDollEntityRenderer extends EntityRenderer<PuffDollEntity> {
    private final PuffDollEntityModel model;

    public PuffDollEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.15f;
        this.shadowOpacity = 0.75f;
        this.model = new PuffDollEntityModel(ctx.getPart(PuffDollEntityModel.PUFF_DOLL));
    }

    @Override
    public void render(PuffDollEntity dollEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(-0.45f, -0.45f, 0.45f);
        float h = MathHelper.lerpAngle(dollEntity.prevYaw, dollEntity.getYaw(), g);
        float j = MathHelper.lerp(g, dollEntity.prevPitch, dollEntity.getPitch());
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(this.getTexture(dollEntity)));
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(h - 180.0f));
        matrixStack.translate(0, -1.5f, 0);
        if (dollEntity.isSitting()) {
            matrixStack.translate(0, 0.7f, 0);
        }
        this.model.setAngles(dollEntity, 0.0f, 0.0f, g, h, j);
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(j));
        this.model.renderHead(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(this.getEyeTexture(dollEntity)));
        this.model.renderEyes(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(dollEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public final Identifier getTexture(PuffDollEntity mobEntity) {
        return Utils.identifier("textures/entity/puff_doll/cloth/" + mobEntity.getClothType().getName().toLowerCase() + ".png");
    }

    public final Identifier getEyeTexture(PuffDollEntity mobEntity) {
        return Utils.identifier("textures/entity/puff_doll/eyes/" + mobEntity.getEyeType().getName().toLowerCase() + ".png");
    }
}
