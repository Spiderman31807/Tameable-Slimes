package tameable.slimes.ai;

import java.util.EnumSet;

import tameable.slimes.entity.TameableSlime;
import tameable.slimes.ai.SlimeMoveControl;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.effect.MobEffects;

public class SlimeRandomDirectionGoal extends Goal {
    private final TameableSlime slime;
    private float chosenDegrees;
    private int nextRandomizeTime;

    public SlimeRandomDirectionGoal(TameableSlime slime) {
        this.slime = slime;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    public boolean canUse() {
        return this.slime.getTarget() == null && this.slime.disabledTicks <= 0 && (this.slime.onGround() || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION)) && this.slime.getMoveControl() instanceof SlimeMoveControl;
    }

    public void tick() {
        if (--this.nextRandomizeTime <= 0) {
            this.nextRandomizeTime = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
            this.chosenDegrees = (float)this.slime.getRandom().nextInt(360);
        }
        
        if (this.slime.getMoveControl() instanceof SlimeMoveControl moveControl)
        	moveControl.setDirection(this.chosenDegrees, false);

    }
}
