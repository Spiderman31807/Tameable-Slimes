package tameable.slimes.ai;

import java.util.EnumSet;

import tameable.slimes.entity.TameableSlime;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

public class OwnerHurtGoal extends TargetGoal {
   	private final TameableSlime tameable;
   	private LivingEntity ownerLastHurt;
   	private int timestamp;

   	public OwnerHurtGoal(TameableSlime slime) {
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

        this.ownerLastHurt = owner.getLastHurtMob();
        int i = owner.getLastHurtMobTimestamp();
    	return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) && this.tameable.wantsToAttack(this.ownerLastHurt, owner);
   	}

   	public void start() {
      	this.mob.setTarget(this.ownerLastHurt);
        LivingEntity owner = this.tameable.getOwner();
      	if (owner != null)
         	this.timestamp = owner.getLastHurtMobTimestamp();
     	super.start();
   	}
}