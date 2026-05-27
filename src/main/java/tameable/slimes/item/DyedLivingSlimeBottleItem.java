
package tameable.slimes.item;

import java.util.List;

import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import tameable.slimes.init.TameableSlimesModItems;
import tameable.slimes.item.LivingSlimeBottleItem;
import tameable.slimes.Color;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;

public class DyedLivingSlimeBottleItem extends LivingSlimeBottleItem {
	public DyedLivingSlimeBottleItem() {
		super();
	}

	@OnlyIn(Dist.CLIENT)
	public static void itemColorLoad(RegisterColorHandlersEvent.Item event) {
		event.getItemColors().register((stack, index) -> {
			if(index != 0)
				return Color.Values.get(0);
			if(stack == null)
				return Color.minLight(Color.slime);
			Color color = new Color(stack);
			color.setDefault(Color.slime);
			return Color.minLight(color);
		}, TameableSlimesModItems.DYED_LIVING_SLIME_BOTTLE.get());
	}
}