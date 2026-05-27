package tameable.slimes.entity;

import tameable.slimes.init.TameableSlimesModEntities;
import tameable.slimes.entity.TameableSlime;

import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;

public class SlimeEntity extends TameableSlime {
	public SlimeEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(TameableSlimesModEntities.SLIME.get(), world);
	}

	public SlimeEntity(EntityType<SlimeEntity> type, Level world) {
		super(type, world);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}