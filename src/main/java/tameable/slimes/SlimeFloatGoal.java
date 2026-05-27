package tameable.slimes.ai;

import java.util.EnumSet;

import tameable.slimes.entity.TameableSlime;
import tameable.slimes.ai.SlimeMoveControl;

import net.minecraft.world.entity.ai.goal.Goal;

public class SlimeFloatGoal extends Goal {
    private final TameableSlime slime;

    public SlimeFloatGoal(TameableSlime slime) {
        this.slime = slime;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        slime.getNavigation().setCanFloat(true);
    }

    public boolean canUse() {
        return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.disabledTicks <= 0 && this.slime.getMoveControl() instanceof SlimeMoveControl;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        if (this.slime.getRandom().nextFloat() < 0.8F)
            this.slime.getJumpControl().jump();
        if (this.slime.getMoveControl() instanceof SlimeMoveControl moveControl)
            moveControl.setWantedMovement(1.2);
    }
}
