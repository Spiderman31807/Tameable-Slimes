
package tameable.slimes.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class LimeSlimeItem extends Item {
	public LimeSlimeItem() {
		super(new Item.Properties().stacksTo(0).rarity(Rarity.COMMON));
	}
}
