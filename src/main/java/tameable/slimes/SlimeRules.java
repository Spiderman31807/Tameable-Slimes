package tameable.slimes.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.level.GameRules;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SlimeRules {
	public static GameRules.Key<GameRules.IntegerValue> DYED_RATE = GameRules.register("TameableSlimes_DyedRate", GameRules.Category.MOBS, GameRules.IntegerValue.create(5));
	public static GameRules.Key<GameRules.IntegerValue> LIVING_DROP_RATE = GameRules.register("TameableSlimes_Living_DropRate", GameRules.Category.MOBS, GameRules.IntegerValue.create(15));
	public static GameRules.Key<GameRules.IntegerValue> SPAWN_RATE = GameRules.register("TameableSlimes_SpawnRate", GameRules.Category.MOBS, GameRules.IntegerValue.create(66));
	public static GameRules.Key<GameRules.BooleanValue> MERGE_COLORS = GameRules.register("TameableSlimes_MergeColors", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
}