package waltsai.colorfulpuff.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import waltsai.colorfulpuff.Utils;
import waltsai.colorfulpuff.server.entity.PuffDollEntity;

public class PuffDollEntityModel extends EntityModel<PuffDollEntity> {
    public static final EntityModelLayer PUFF_DOLL = new EntityModelLayer(Utils.identifier("puff_doll"), "main");

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    public final ModelPart eyes;

    public PuffDollEntityModel(ModelPart root) {
        this.head = root.getChild(EntityModelPartNames.HEAD);
        this.body = root.getChild(EntityModelPartNames.BODY);
        this.leftArm = root.getChild(EntityModelPartNames.LEFT_ARM);
        this.rightArm = root.getChild(EntityModelPartNames.RIGHT_ARM);
        this.leftLeg = root.getChild(EntityModelPartNames.LEFT_LEG);
        this.rightLeg = root.getChild(EntityModelPartNames.RIGHT_LEG);
        this.eyes = root.getChild("eyes");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, Dilation.NONE)
                .uv(32, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.5f)), ModelTransform.NONE);

        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, Dilation.NONE)
                .uv(16, 32).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.25f)), ModelTransform.NONE);

        modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, Dilation.NONE)
                .uv(48, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.25f)), ModelTransform.pivot(5.0F, 2.0F, 0.0F));

        modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(40, 16).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, Dilation.NONE)
                .uv(40, 32).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.25f)), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));

        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(16, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, Dilation.NONE)
                .uv(0, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.25f)), ModelTransform.pivot(2.0F, 12.0F, 0.0F));

        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, Dilation.NONE)
                .uv(0, 32).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.25f)), ModelTransform.pivot(-2.0F, 12.0F, 0.0F));

        modelPartData.addChild("eyes", ModelPartBuilder.create().uv(8, 12).cuboid(-3.0F, -3.0F, -4.01F, 1.0F, 2.0F, 1.0F, Dilation.NONE)
                .uv(12, 12).cuboid(2.0F, -3.0F, -4.01F, 1.0F, 2.0F, 1.0F, Dilation.NONE), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(PuffDollEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isSitting()) {
            this.rightArm.pitch = -0.62831855F;
            this.leftArm.pitch = -0.62831855F;
            this.rightLeg.pitch = -1.5707963F;
            this.rightLeg.yaw = 0.31415927F;
            this.rightLeg.roll = 0.07853982F;
            this.leftLeg.pitch = -1.5707963F;
            this.leftLeg.yaw = -0.31415927F;
            this.leftLeg.roll = -0.07853982F;
            this.rightArm.yaw = 0.0F;
            this.leftArm.yaw = 0.0F;
        } else {
            this.rightArm.pitch = 0.0F;
            this.leftArm.pitch = 0.0F;
            this.rightLeg.pitch = 0.0F;
            this.rightLeg.yaw = 0.0F;
            this.rightLeg.roll = 0.0F;
            this.leftLeg.pitch = 0.0F;
            this.leftLeg.yaw = 0.0F;
            this.leftLeg.roll = 0.0F;
            this.rightArm.yaw = 0.0F;
            this.leftArm.yaw = 0.0F;
        }

        this.body.pitch = 0.0F;
        this.rightLeg.pivotZ = 0.1F;
        this.leftLeg.pivotZ = 0.1F;
        this.rightLeg.pivotY = 12.0F;
        this.leftLeg.pivotY = 12.0F;
        this.head.pivotY = 0.0F;
        this.body.pivotY = 0.0F;
        this.leftArm.pivotY = 2.0F;
        this.rightArm.pivotY = 2.0F;


        this.eyes.pitch = this.head.pitch;
        this.eyes.yaw = this.head.yaw;
        this.eyes.roll = this.head.roll;
        this.eyes.pivotY = this.head.pivotY;
        this.eyes.visible = true;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(matrixStack, buffer, packedLight, packedOverlay);
        leftArm.render(matrixStack, buffer, packedLight, packedOverlay);
        rightArm.render(matrixStack, buffer, packedLight, packedOverlay);
        leftLeg.render(matrixStack, buffer, packedLight, packedOverlay);
        rightLeg.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void renderHead(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay) {
        head.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void renderEyes(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay) {
        eyes.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}
