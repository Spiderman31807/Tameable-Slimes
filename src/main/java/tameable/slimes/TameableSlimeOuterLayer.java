package tameable.slimes.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import tameable.slimes.entity.TameableSlime;
import tameable.slimes.Color;

@OnlyIn(Dist.CLIENT)
public class TameableSlimeOuterLayer<T extends LivingEntity> extends SlimeOuterLayer<T> {
    private final EntityModel<T> model;

    public TameableSlimeOuterLayer(RenderLayerParent<T, SlimeModel<T>> RenderLayer, EntityModelSet models) {
        super(RenderLayer, models);
        this.model = new SlimeModel<>(models.bakeLayer(ModelLayers.SLIME_OUTER));
    }

    public void render(PoseStack pose, MultiBufferSource buffer, int value, T entity, float limbSwing, float limbSwingAmount, float value2, float ageInTicks, float netHeadYaw, float headPitch) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean glow = minecraft.shouldEntityAppearGlowing(entity) && entity.isInvisible();

		float[] afloat = new float[] {1f, 1f, 1f};
        if(entity instanceof TameableSlime slime) {
			if(slime.isDyed()) {
				Color color = slime.getColor();
				int colorLong = color.invaildInput ? Color.slime : color.getLong();
				Color colorValues = new Color(Color.minLight(new Color(colorLong)));
				afloat = Color.toFloat(colorValues, 0.00392157f);
			}
        }

        if (!entity.isInvisible() || glow) {
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.outline(this.getTextureLocation(entity)));
            if(!glow)
                vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
			
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, value2);
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.model.renderToBuffer(pose, vertexconsumer, value, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), afloat[0], afloat[1], afloat[2], 1.0F);
        }
    }

    protected ResourceLocation getTextureLocation(T entity) {
    	if(entity instanceof TameableSlime slime && slime.isDyed())
    		return new ResourceLocation("tameable_slimes:textures/entities/slime_outer.png");
    	return new ResourceLocation("textures/entity/slime/slime.png");
    }
}