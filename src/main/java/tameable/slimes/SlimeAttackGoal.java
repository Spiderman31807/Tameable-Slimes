package tameable.slimes.ai;

import java.util.EnumSet;

import tameable.slimes.entity.TameableSlime;
import tameable.slimes.ai.SlimeMoveControl;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.LivingEntity;

public class SlimeAttackGoal extends Goal {
    private final TameableSlime slime;
    private int growTiredTimer;

    public SlimeAttackGoal(TameableSlime slime) {
        this.slime = slime;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    public boolean canUse() {
    	if(this.slime.disabledTicks > 0)
    		return false;
        LivingEntity livingentity = this.slime.getTarget();
        if (livingentity == null)
            return false;
        return !this.slime.canAttack(livingentity) ? false : this.slime.getMoveControl() instanceof SlimeMoveControl;
    }

    public void start() {
        this.growTiredTimer = reducedTickDelay(300);
        super.start();
    }

    public boolean canContinueToUse() {
        LivingEntity livingentity = this.slime.getTarget();
        if (livingentity == null) {
            return false;
        } else if (!this.slime.canAttack(livingentity)) {
            return false;
        } else {
            return --this.growTiredTimer > 0;
        }
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingentity = this.slime.getTarget();
        if (livingentity != null)
            this.slime.lookAt(livingentity, 10.0F, 10.0F);
        if (this.slime.getMoveControl() instanceof SlimeMoveControl moveControl)
            moveControl.setDirection(this.slime.getYRot(), this.slime.isDealsDamage());
    }
}
