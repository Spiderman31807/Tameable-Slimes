package tameable.slimes;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

import net.minecraft.tags.TagKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

import tameable.slimes.init.SlimeRules;
import tameable.slimes.init.TameableSlimesModEntities;
import tameable.slimes.entity.TameableSlime;

@Mod.EventBusSubscriber
public class SpawnSlime {
	@SubscribeEvent
	public static void onEntitySpawned(EntityJoinLevelEvent event) {
		Entity entity = event.getEntity();
		Level world = event.getLevel();
		boolean replaceable = entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("tameable_slimes:can_replace")));
		if(entity == null || !replaceable || world.isClientSide() || event.loadedFromDisk())
			return;
			
		int ReplaceChance = Mth.nextInt(RandomSource.create(), 1, 100);
		int DyedChance = Mth.nextInt(RandomSource.create(), 1, 100);
		boolean IsDyed = DyedChance <= world.getLevelData().getGameRules().getInt(SlimeRules.DYED_RATE);
		if(ReplaceChance > world.getLevelData().getGameRules().getInt(SlimeRules.SPAWN_RATE))
			return;
			
		Entity slime = TameableSlimesModEntities.SLIME.get().create(world);
		CompoundTag data = entity.saveWithoutId(new CompoundTag());
		data.remove("UUID");
		entity.discard();
		slime.load(data);
		if(IsDyed && slime instanceof TameableSlime tameable)
			tameable.randomiseColor();
		world.addFreshEntity(slime);
	}
}