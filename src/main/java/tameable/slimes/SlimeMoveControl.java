package tameable.slimes.ai;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

import tameable.slimes.entity.TameableSlime;

public class SlimeMoveControl extends MoveControl {
    private float yRot;
    private int jumpDelay;
    private final TameableSlime slime;
    private boolean isAggressive;
    private boolean jumping;

    public SlimeMoveControl(TameableSlime slime) {
    	super(slime);
        this.slime = slime;
        this.yRot = 180.0F * slime.getYRot() / (float)Math.PI;
        this.setJumping(true);
    }

    public void setDirection(float yaw, boolean attacking) {
        this.yRot = yaw;
        this.isAggressive = attacking;
    }
      
    public void setWantedMovement(double speed) {
        this.speedModifier = speed;
        this.operation = MoveControl.Operation.MOVE_TO;
    }

    public void setJumping(boolean jumping) {
    	this.jumping = jumping;
    }

	public void tick() {
	    this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
        this.mob.yHeadRot = this.mob.getYRot();
        this.mob.yBodyRot = this.mob.getYRot();
        if (this.operation != MoveControl.Operation.MOVE_TO) {
        	this.mob.setZza(0.0F);
        } else {
        	this.operation = MoveControl.Operation.WAIT;
            if (this.mob.onGround()) {
            	this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                if (this.jumpDelay-- <= 0 && this.jumping) {
                    this.jumpDelay = this.slime.getJumpDelay();
                    if (this.isAggressive) {
                    	this.jumpDelay /= 3;
                    }

                    this.slime.getJumpControl().jump();
                    if (this.slime.doPlayJumpSound()) {
                    	this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                    }
                } else {
                    this.slime.xxa = 0.0F;
                    this.slime.zza = 0.0F;
                	this.mob.setSpeed(0.0F);
                }
            } else {
            	this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
        	}
    	}
	}
}
