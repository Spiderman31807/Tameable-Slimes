package tameable.slimes.ai;

import java.util.EnumSet;

import tameable.slimes.entity.TameableSlime;
import tameable.slimes.ai.SlimeMoveControl;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.entity.ai.goal.Goal;

public class FollowOwnerGoal extends Goal {
   public static final int TELEPORT_WHEN_DISTANCE_IS = 12;
   private static final int MIN_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 2;
   private static final int MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 3;
   private static final int MAX_VERTICAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 1;
   private final TameableSlime tameable;
   private LivingEntity owner;
   private final LevelReader level;
   private final double speedModifier;
   private final float stopDistance;
   private final float startDistance;

   	public FollowOwnerGoal(TameableSlime slime, double speed, float start, float stop) {
      	this.tameable = slime;
      	this.level = slime.level();
      	this.speedModifier = speed;
      	this.startDistance = start;
      	this.stopDistance = stop;
      	this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   	}

	public boolean canUse() {
		if(this.tameable.disabledTicks > 0)
			return false;
   	
      	LivingEntity livingentity = this.tameable.getOwner();
      	if (livingentity == null)
         	return false;
      	if (livingentity.isSpectator())
         	return false;
      	if (!this.tameable.isFollowing())
         	return false;
      	if (this.unableToMove())
         	return false;

        float startAt = (this.startDistance * this.startDistance) * this.tameable.getSize();
      	if (this.tameable.distanceToSqr(livingentity) < (double)startAt)
         	return false;
         
    	this.owner = livingentity;
   		return true;
   	}

	public boolean canContinueToUse() {
      	if (this.unableToMove())
         	return false;

        float stopAt = (this.stopDistance * this.stopDistance) * this.tameable.getSize();
     	return !(this.tameable.distanceToSqr(this.owner) <= (double)stopAt);
   	}

   	private boolean unableToMove() {
      	return this.tameable.isPassenger() || this.tameable.isLeashed();
   	}
   	
   	public void tick() {
    	if (this.tameable.distanceToSqr(this.owner) >= 144 * this.tameable.getSize())
            this.teleportToOwner();
            
        this.tameable.lookAt(this.owner, 10, 10);
        if (this.tameable.getMoveControl() instanceof SlimeMoveControl moveControl) {
        	moveControl.setWantedMovement(this.speedModifier);
        	moveControl.setDirection(this.tameable.getYRot(), false);
        }
   	}

   	private void teleportToOwner() {
      	BlockPos blockpos = this.owner.blockPosition();
      	for(int i = 0; i < 10; ++i) {
         	int j = this.randomIntInclusive(-3, 3);
         	int k = this.randomIntInclusive(-1, 1);
         	int l = this.randomIntInclusive(-3, 3);
         	boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
         	if (flag)
            	return;
      	}
   	}

   	private boolean maybeTeleportTo(int x, int y, int z) {
      	if (Math.abs((double)x - this.owner.getX()) < 2 && Math.abs((double)z - this.owner.getZ()) < 2)
         	return false;
      	if (!this.canTeleportTo(new BlockPos(x, y, z)))
         	return false;
         	
    	this.tameable.moveTo((double)x + 0.5, (double)y, (double)z + 0.5, this.tameable.getYRot(), this.tameable.getXRot());
        return true;
   	}

   	private boolean canTeleportTo(BlockPos pos) {
      	BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pos.mutable());
      	if (blockpathtypes != BlockPathTypes.WALKABLE)
         	return false;
         
      	BlockState blockstate = this.level.getBlockState(pos.below());
      	if (blockstate.getBlock() instanceof LeavesBlock)
      		return false;
            
      	BlockPos blockpos = pos.subtract(this.tameable.blockPosition());
      	return this.level.noCollision(this.tameable, this.tameable.getBoundingBox().move(blockpos));
   	}

   	private int randomIntInclusive(int min, int max) {
      	return this.tameable.getRandom().nextInt(max - min + 1) + min;
   	}
}