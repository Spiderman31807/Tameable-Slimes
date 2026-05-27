
package tameable.slimes.item;

import java.util.Optional;
import java.util.List;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;

public class LivingSlimeItem extends Item {
	public LivingSlimeItem() {
		super(new Item.Properties());
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		CompoundTag compound = stack.getTag();
		if(compound == null || !compound.contains("Slime"))
			return;
		compound = compound.getCompound("Slime");

		if (compound.contains("CustomName", 8)) {
        	String name = compound.getString("CustomName");
            try {
            	name = Component.Serializer.fromJson(name).getString();
            	Component displayName = Component.translatable("jigsaw_block.name");
            	displayName = Component.literal(displayName.getString() + " " + name).withStyle(ChatFormatting.GRAY);
            	list.add(displayName);
            } catch (Exception exception) {
            }
    	}
	}
}