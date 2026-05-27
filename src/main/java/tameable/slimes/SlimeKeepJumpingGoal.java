package tameable.slimes.ai;

import java.util.EnumSet;

import tameable.slimes.entity.TameableSlime;
import tameable.slimes.ai.SlimeMoveControl;

import net.minecraft.world.entity.ai.goal.Goal;

public class SlimeKeepJumpingGoal extends Goal {
	private final TameableSlime slime;

    public SlimeKeepJumpingGoal(TameableSlime slime) {
        this.slime = slime;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    public boolean canUse() {
        return !this.slime.isPassenger() && this.slime.disabledTicks <= 0;
    }

    public void tick() {
        if (this.slime.getMoveControl() instanceof SlimeMoveControl moveControl)
        	moveControl.setWantedMovement(1);
    }
}
