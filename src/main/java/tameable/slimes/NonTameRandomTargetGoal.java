package tameable.slimes.ai;

import java.util.function.Predicate;
import javax.annotation.Nullable;

import tameable.slimes.entity.TameableSlime;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class NonTameRandomTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
	private final TameableSlime tameable;

    public NonTameRandomTargetGoal(TameableSlime slime, Class<T> targetClass, boolean mustSee, @Nullable Predicate<LivingEntity> conditions) {
        super(slime, targetClass, 10, mustSee, false, conditions);
    	this.tameable = slime;
    }

    public NonTameRandomTargetGoal(TameableSlime slime, Class<T> targetClass, boolean mustSee) {
        this(slime, targetClass, mustSee, (Predicate<LivingEntity>)null);
    }

    public boolean canUse() {
    	return !this.tameable.isTame() && this.tameable.disabledTicks <= 0 && super.canUse();
    }

    public boolean canContinueToUse() {
        return this.targetConditions != null ? this.targetConditions.test(this.mob, this.target) : super.canContinueToUse();
    }
}
