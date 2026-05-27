
package tameable.slimes.block;

import tameable.slimes.init.TameableSlimesModEntities;
import tameable.slimes.entity.TameableSlime;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;

public class AliveSlimeBlock extends SlimeBlock {
	public AliveSlimeBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.GRASS).friction(0.8F).sound(SoundType.SLIME_BLOCK).noOcclusion());
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
		CollisionGetter getter = (CollisionGetter)reader;
		EntityDimensions dimensions = EntityType.SLIME.getDimensions();
		AABB hitbox = dimensions.scale(0.610f).makeBoundingBox(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		return getter.noCollision(hitbox) && super.canSurvive(state, reader, pos);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		world.destroyBlock(pos, false, entity, 512);
		this.spawnSlime(world, pos, stack, entity);
   	}

	@Override
	public void appendHoverText(ItemStack stack, BlockGetter getter, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, getter, list, flag);
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

   	public void spawnSlime(Level world, BlockPos pos, ItemStack stack, LivingEntity entity) {
   		if(world instanceof ServerLevel server) {
   			Entity slime = TameableSlimesModEntities.SLIME.get().create(server);
   			slime.load(addData(world, pos, stack, entity));
   			if(slime instanceof TameableSlime tameable) {
   				tameable.yaw = this.getYaw(pos, entity);
   				tameable.disabledTicks = 20;
   			}
   			
   			world.addFreshEntity(slime);
   		}
   	}

   	public float getYaw(BlockPos pos, LivingEntity entity) {
        double dx = pos.getX() - entity.getX();
   		double dz = pos.getZ() - entity.getZ();
        float yaw = (float)Math.atan2(dx, dz);
       	if(yaw <= -1.8925469)
       		return 0;
       	if(yaw <= -0.9652518)
       		return 270;
       	if(yaw <= 0.1418969)
       		return 180;
       	if(yaw <= 3.2834896)
       		return 90;
       	return 0;
   	}

   	public CompoundTag addData(Level world, BlockPos pos, ItemStack stack, LivingEntity entity) {
   		CompoundTag compound = new CompoundTag();
   		compound.putInt("Size", 1);
   		compound.putBoolean("Tamed", true);
   		compound.putUUID("Owner", entity.getUUID());

   		compound.put("Pos", this.doubleList(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
   		compound.put("Rotation", this.floatList(this.getYaw(pos, entity), 0));

		CompoundTag tag = stack.getTag();
		if(tag == null || !tag.contains("Slime"))
			return compound;

   		tag = tag.getCompound("Slime");
   		if(tag.contains("UUID") && world instanceof ServerLevel server && server.getEntity(tag.getUUID("UUID")) == null)
   			compound.putUUID("UUID", tag.getUUID("UUID"));
   		if(tag.contains("CustomName"))
   			compound.putString("CustomName", tag.getString("CustomName"));
   		if(tag.contains("CustomNameVisible"))
   			compound.putBoolean("CustomNameVisible", tag.getBoolean("CustomNameVisible"));
   		if(tag.contains("Tags"))
   			compound.put("Tags", tag.getCompound("Tags"));
   		if(tag.contains("ForgeCaps"))
   			compound.put("ForgeCaps", tag.getCompound("ForgeCaps"));
   		if(tag.contains("ForgeData"))
   			compound.put("ForgeData", tag.getCompound("ForgeData"));
   			
   		return compound;
   	}

    public ListTag doubleList(double... values) {
        ListTag listtag = new ListTag();
        for (double f : values) {
            listtag.add(DoubleTag.valueOf(f));
        }

        return listtag;
    }

    public ListTag floatList(float... values) {
        ListTag listtag = new ListTag();
        for (float f : values) {
            listtag.add(FloatTag.valueOf(f));
        }

        return listtag;
    }
}