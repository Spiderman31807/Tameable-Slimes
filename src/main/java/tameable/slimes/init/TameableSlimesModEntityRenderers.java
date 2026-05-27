
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tameable.slimes.init;

import tameable.slimes.client.renderer.SlimeRenderer;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TameableSlimesModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(TameableSlimesModEntities.SLIME.get(), SlimeRenderer::new);
	}
}
