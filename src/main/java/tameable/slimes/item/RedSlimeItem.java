
package tameable.slimes.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class RedSlimeItem extends Item {
	public RedSlimeItem() {
		super(new Item.Properties().stacksTo(0).rarity(Rarity.COMMON));
	}
}
