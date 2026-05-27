
package tameable.slimes.item;

import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import tameable.slimes.init.TameableSlimesModItems;
import tameable.slimes.item.LivingSlimeItem;
import tameable.slimes.Color;

public class DyedLivingSlimeItem extends LivingSlimeItem {
	public DyedLivingSlimeItem() {
		super();
	}

	@OnlyIn(Dist.CLIENT)
	public static void itemColorLoad(RegisterColorHandlersEvent.Item event) {
		event.getItemColors().register((stack, index) -> {
			if(stack == null)
				return Color.minLight(Color.slime);
			Color color = new Color(stack);
			color.setDefault(Color.slime);
			return Color.minLight(color);
		}, TameableSlimesModItems.DYED_LIVING_SLIME.get());
	}
}