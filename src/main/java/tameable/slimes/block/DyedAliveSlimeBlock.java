
package tameable.slimes.block;

import tameable.slimes.init.TameableSlimesModBlocks;
import tameable.slimes.block.AliveSlimeBlock;
import tameable.slimes.Color;

import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class DyedAliveSlimeBlock extends AliveSlimeBlock {
	public Color color = new Color(Color.slime);
	
	public DyedAliveSlimeBlock() {
		super();
	}

   	public CompoundTag addData(Level world, BlockPos pos, ItemStack stack, LivingEntity entity) {
   		CompoundTag compound = super.addData(world, pos, stack, entity);
   		compound.putInt("Color", new Color(stack).getDefault(Color.slime));

   		return compound;
   	}

	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		if(state.getBlock() instanceof DyedAliveSlimeBlock slimeBlock && Color.verifyTag(stack.getTag()))
			slimeBlock.color = new Color(stack);
		super.setPlacedBy(world, pos, state, entity, stack);
   	}

   	public static Color getColor(BlockState state) {
   		if(state.getBlock() instanceof DyedAliveSlimeBlock slimeBlock)
   			return slimeBlock.color;
   		return new Color(Color.slime);
   	}

	@OnlyIn(Dist.CLIENT)
	public static void blockColorLoad(RegisterColorHandlersEvent.Block event) {
		event.getBlockColors().register((state, world, pos, index) -> {
			if(state == null)
				return Color.slime;
			return getColor(state).getLong();
		}, TameableSlimesModBlocks.DYED_ALIVE_SLIME.get());
	}

	@OnlyIn(Dist.CLIENT)
	public static void itemColorLoad(RegisterColorHandlersEvent.Item event) {
		event.getItemColors().register((stack, index) -> {
			if(stack == null)
				return Color.minLight(Color.slime);
			Color color = new Color(stack);
			color.setDefault(Color.slime);
			return Color.minLight(color);
		}, TameableSlimesModBlocks.DYED_ALIVE_SLIME.get());
	}
}
