
package tameable.slimes.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class BlackSlimeItem extends Item {
	public BlackSlimeItem() {
		super(new Item.Properties().stacksTo(0).rarity(Rarity.COMMON));
	}
}
