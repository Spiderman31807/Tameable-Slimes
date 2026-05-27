package tameable.slimes.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import tameable.slimes.entity.TameableSlime;
import tameable.slimes.renderer.layers.TameableSlimeOuterLayer;

@OnlyIn(Dist.CLIENT)
public class TameableSlimeRenderer extends SlimeRenderer {

    public TameableSlimeRenderer(EntityRendererProvider.Context conext) {
        super(conext);
        this.addLayer(new TameableSlimeOuterLayer<>(this, conext.getModelSet()));
    }

    protected void scale(TameableSlime slime, PoseStack pose, float value) {
        float f = 0.999F;
        pose.scale(0.999F, 0.999F, 0.999F);
        pose.translate(0.0F, 0.001F, 0.0F);
        float f1 = (float)slime.getSize();
        float f2 = Mth.lerp(value, slime.oSquish, slime.squish) / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        pose.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
    }

    protected float getShadowRadius(TameableSlime slime) {
        return 0.25F * (float)slime.getSize();
    }

    public ResourceLocation getTextureLocation(TameableSlime slime) {
        return getTextureLocation((Slime) slime);
    }

	@Override
    public ResourceLocation getTextureLocation(Slime slime) {
        return new ResourceLocation("tameable_slimes:textures/entities/slime_inner.png");
    }
}