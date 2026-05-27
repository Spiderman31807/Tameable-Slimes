
/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package tameable.slimes.init;

import tameable.slimes.item.WhiteSlimeItem;
import tameable.slimes.item.RedSlimeItem;
import tameable.slimes.item.PurpleSlimeItem;
import tameable.slimes.item.PinkSlimeItem;
import tameable.slimes.item.OrangeSlimeItem;
import tameable.slimes.item.MagentaSlimeItem;
import tameable.slimes.item.LivingSlimeItem;
import tameable.slimes.item.LivingSlimeBottleItem;
import tameable.slimes.item.LimeSlimeItem;
import tameable.slimes.item.LightGraySlimeItem;
import tameable.slimes.item.LightBlueSlimeItem;
import tameable.slimes.item.GreenSlimeItem;
import tameable.slimes.item.GraySlimeItem;
import tameable.slimes.item.DyedLivingSlimeItem;
import tameable.slimes.item.DyedLivingSlimeBottleItem;
import tameable.slimes.item.DyeableSlimeBallItem;
import tameable.slimes.item.CyanSlimeItem;
import tameable.slimes.item.BrownSlimeItem;
import tameable.slimes.item.BlueSlimeItem;
import tameable.slimes.item.BlackSlimeItem;
import tameable.slimes.TameableSlimesMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

public class TameableSlimesModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, TameableSlimesMod.MODID);
	public static final RegistryObject<Item> LIVING_SLIME = REGISTRY.register("living_slime", () -> new LivingSlimeItem());
	public static final RegistryObject<Item> DYED_LIVING_SLIME = REGISTRY.register("dyed_living_slime", () -> new DyedLivingSlimeItem());
	public static final RegistryObject<Item> LIVING_SLIME_BOTTLE = REGISTRY.register("living_slime_bottle", () -> new LivingSlimeBottleItem());
	public static final RegistryObject<Item> DYED_LIVING_SLIME_BOTTLE = REGISTRY.register("dyed_living_slime_bottle", () -> new DyedLivingSlimeBottleItem());
	public static final RegistryObject<Item> ALIVE_SLIME = block(TameableSlimesModBlocks.ALIVE_SLIME);
	public static final RegistryObject<Item> DYED_ALIVE_SLIME = block(TameableSlimesModBlocks.DYED_ALIVE_SLIME);
	public static final RegistryObject<Item> WHITE_SLIME = REGISTRY.register("white_slime", () -> new WhiteSlimeItem());
	public static final RegistryObject<Item> ORANGE_SLIME = REGISTRY.register("orange_slime", () -> new OrangeSlimeItem());
	public static final RegistryObject<Item> MAGENTA_SLIME = REGISTRY.register("magenta_slime", () -> new MagentaSlimeItem());
	public static final RegistryObject<Item> LIGHT_BLUE_SLIME = REGISTRY.register("light_blue_slime", () -> new LightBlueSlimeItem());
	public static final RegistryObject<Item> LIME_SLIME = REGISTRY.register("lime_slime", () -> new LimeSlimeItem());
	public static final RegistryObject<Item> PINK_SLIME = REGISTRY.register("pink_slime", () -> new PinkSlimeItem());
	public static final RegistryObject<Item> GRAY_SLIME = REGISTRY.register("gray_slime", () -> new GraySlimeItem());
	public static final RegistryObject<Item> LIGHT_GRAY_SLIME = REGISTRY.register("light_gray_slime", () -> new LightGraySlimeItem());
	public static final RegistryObject<Item> CYAN_SLIME = REGISTRY.register("cyan_slime", () -> new CyanSlimeItem());
	public static final RegistryObject<Item> PURPLE_SLIME = REGISTRY.register("purple_slime", () -> new PurpleSlimeItem());
	public static final RegistryObject<Item> BLUE_SLIME = REGISTRY.register("blue_slime", () -> new BlueSlimeItem());
	public static final RegistryObject<Item> BROWN_SLIME = REGISTRY.register("brown_slime", () -> new BrownSlimeItem());
	public static final RegistryObject<Item> GREEN_SLIME = REGISTRY.register("green_slime", () -> new GreenSlimeItem());
	public static final RegistryObject<Item> RED_SLIME = REGISTRY.register("red_slime", () -> new RedSlimeItem());
	public static final RegistryObject<Item> BLACK_SLIME = REGISTRY.register("black_slime", () -> new BlackSlimeItem());
	public static final RegistryObject<Item> SLIME_MIXER = block(TameableSlimesModBlocks.SLIME_MIXER);
	public static final RegistryObject<Item> DYEABLE_SLIME_BALL = REGISTRY.register("dyeable_slime_ball", () -> new DyeableSlimeBallItem());

	// Start of user code block custom items
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ItemClientSideHandler {
		@SubscribeEvent
		public static void itemColorLoad(RegisterColorHandlersEvent.Item event) {
			DyeableSlimeBallItem.itemColorLoad(event);
			DyedLivingSlimeItem.itemColorLoad(event);
			DyedLivingSlimeBottleItem.itemColorLoad(event);
		}
	}

	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
