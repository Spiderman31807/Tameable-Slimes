package tameable.slimes.ai;

import java.util.EnumSet;

import tameable.slimes.entity.TameableSlime;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

public class OwnerHurtByGoal extends TargetGoal {
   	private final TameableSlime tameable;
   	private LivingEntity ownerLastHurtBy;
   	private int timestamp;

   	public OwnerHurtByGoal(TameableSlime slime) {
      	super(slime, false);
      	this.tameable = slime;
      	this.setFlags(EnumSet.of(Goal.Flag.TARGET));
   	}

   	public boolean canUse() {
   		if(this.tameable.disabledTicks > 0)
   			return false;
   		
        LivingEntity owner = this.tameable.getOwner();
      	if (!this.tameable.isTame() || owner == null)
      		return false;

        this.ownerLastHurtBy = owner.getLastHurtMob();
        int i = owner.getLastHurtByMobTimestamp();
    	return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.tameable.wantsToAttack(this.ownerLastHurtBy, owner);
   	}

   	public void start() {
      	this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity owner = this.tameable.getOwner();
      	if (owner != null)
         	this.timestamp = owner.getLastHurtByMobTimestamp();
     	super.start();
   	}
}