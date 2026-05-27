
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tameable.slimes.init;

import tameable.slimes.block.SlimeMixerBlock;
import tameable.slimes.block.DyedAliveSlimeBlock;
import tameable.slimes.block.AliveSlimeBlock;
import tameable.slimes.TameableSlimesMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.block.Block;

public class TameableSlimesModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, TameableSlimesMod.MODID);
	public static final RegistryObject<Block> ALIVE_SLIME = REGISTRY.register("alive_slime", () -> new AliveSlimeBlock());
	public static final RegistryObject<Block> DYED_ALIVE_SLIME = REGISTRY.register("dyed_alive_slime", () -> new DyedAliveSlimeBlock());
	public static final RegistryObject<Block> SLIME_MIXER = REGISTRY.register("slime_mixer", () -> new SlimeMixerBlock());

	// Start of user code block custom blocks
	// End of user code block custom blocks
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class BlocksClientSideHandler {
		@SubscribeEvent
		public static void blockColorLoad(RegisterColorHandlersEvent.Block event) {
			DyedAliveSlimeBlock.blockColorLoad(event);
		}

		@SubscribeEvent
		public static void itemColorLoad(RegisterColorHandlersEvent.Item event) {
			DyedAliveSlimeBlock.itemColorLoad(event);
		}
	}
}
