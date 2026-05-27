
package tameable.slimes.item;

import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import tameable.slimes.init.TameableSlimesModItems;
import tameable.slimes.Color;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.PowderSnowCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;

public class DyeableSlimeBallItem extends Item {
	public DyeableSlimeBallItem() {
		super(new Item.Properties());
	}
	
   	public InteractionResult useOn(UseOnContext context) {
     	Level world = context.getLevel();
     	BlockPos pos = context.getClickedPos();
     	InteractionHand hand = context.getHand();
  		Player player = context.getPlayer();
  		ItemStack stack = player.getItemInHand(hand);
     	BlockState hitBlock = world.getBlockState(pos);
     	if(hitBlock.getBlock() instanceof LayeredCauldronBlock cauldron) {
     		if(cauldron instanceof PowderSnowCauldronBlock)
     			return super.useOn(context);
     		cauldron.lowerFillLevel(hitBlock, world, pos);
     		ItemUtils.createFilledResult(stack, player, new ItemStack(Items.SLIME_BALL));
     		return InteractionResult.SUCCESS;
     	}
     	
     	return super.useOn(context);
   	}

	@OnlyIn(Dist.CLIENT)
	public static void itemColorLoad(RegisterColorHandlersEvent.Item event) {
		event.getItemColors().register((stack, index) -> {
			if(stack == null)
				return Color.minLight(Color.slime);
			Color color = new Color(stack);
			color.setDefault(Color.slime);
			return Color.minLight(color);
		}, TameableSlimesModItems.DYEABLE_SLIME_BALL.get());
	}
}
