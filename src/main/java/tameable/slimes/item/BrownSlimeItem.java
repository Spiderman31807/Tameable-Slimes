
package tameable.slimes.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class BrownSlimeItem extends Item {
	public BrownSlimeItem() {
		super(new Item.Properties().stacksTo(0).rarity(Rarity.COMMON));
	}
}
