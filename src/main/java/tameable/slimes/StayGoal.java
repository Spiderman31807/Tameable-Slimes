package tameable.slimes.ai;

import java.util.EnumSet;

import tameable.slimes.entity.TameableSlime;
import tameable.slimes.ai.SlimeMoveControl;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.core.BlockPos;

public class StayGoal extends Goal{
	private final TameableSlime slime;
   	private final double speedModifier;
   	private final float startDistance;
   	private final float stopDistance;
   	private BlockPos stayAt;

    public StayGoal(TameableSlime slime, double speed, float start, float stop) {
        this.slime = slime;
        this.speedModifier = speed;
        this.startDistance = start;
        this.stopDistance = stop;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

	public boolean canUse() {
		if(this.slime.disabledTicks > 0)
			return false;
   	
      	if (this.slime.isFollowing())
         	return false;
      	if (this.unableToMove())
         	return false;

		this.stayAt = this.slime.getStayPos();
        float startAt = (this.startDistance * this.startDistance) * this.slime.getSize();
      	if (this.slime.distanceToSqr(this.stayAt.getCenter()) < (double)startAt)
         	return false;
   		return true;
   	}

	public boolean canContinueToUse() {
      	if (this.unableToMove())
         	return false;

        float stopAt = (this.stopDistance * this.stopDistance) * this.slime.getSize();
     	return !(this.slime.distanceToSqr(this.stayAt.getCenter()) <= (double)stopAt);
   	}

   	private boolean unableToMove() {
      	return this.slime.isPassenger() || this.slime.isLeashed();
   	}
   	
   	public void tick() {
        this.slime.lookAt(EntityAnchorArgument.Anchor.EYES, this.stayAt.getCenter());
        if (this.slime.getMoveControl() instanceof SlimeMoveControl moveControl) {
        	moveControl.setWantedMovement(this.speedModifier);
        	moveControl.setDirection(this.slime.getYRot(), false);
        }
   	}
}
