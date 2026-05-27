package tameable.slimes.ai;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import tameable.slimes.entity.TameableSlime;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;

public class MergeGoal extends Goal {
	private final TameableSlime slime;
   	private final double speedModifier;
   	private final double maxDistance;
   	private final int mergeTime;
	private ArrayList<Slime> siblings;
	private ArrayList<Integer> merge;
   	private boolean isActive = false;
	private int parentMerge;

    public MergeGoal(TameableSlime slime, double speed, double distance, int time) {
        this.slime = slime;
        this.speedModifier = speed;
        this.maxDistance = distance;
        this.mergeTime = time * 20;
        this.setSiblings(slime.getSiblings());
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    public boolean canUse() {
    	if(!this.slime.isSplit())
    		return false;
    	if(!this.canPath())
    		return false;
    	if(this.slime.disabledTicks > 0)
    		return false;
    	return true;
    }

    public void start() {
    	this.isActive = true;
    	this.parentMerge = mergeTime;
    	this.merge = new ArrayList();
    	for(Slime slime : this.siblings) {
    		this.merge.add(0);
    	}
    }

    public void stop() {
    	this.isActive = false;
    	if (this.slime.getMoveControl() instanceof SlimeMoveControl moveControl) {
			moveControl.setWantedMovement(1);
			moveControl.setJumping(true);
    	}
    }

    public boolean requiresUpdateEveryTick() {
    	return true;
    }

    public void tick() {
		double distance = this.targetDistance();
		if (this.slime.getMoveControl() instanceof SlimeMoveControl moveControl) {
        	if(distance > maxDistance * this.slime.getSize()) {
				moveControl.setWantedMovement(this.speedModifier);
				moveControl.setJumping(true);
        	} else {
				moveControl.setWantedMovement(this.speedModifier * ((distance * this.slime.getSize()) / 4));
				moveControl.setJumping(false);
				if(this.slime.parentAlive())
					this.parentMerge++;
				if(!this.slime.parentAlive())
					this.addMergeTime();
			}
    	
    		if(this.slime.parentAlive()) {
				if(this.parentMerge == this.mergeTime)
					this.mergeParent();
        		this.slime.lookAt(this.slime.getParent(), 10, 10);
        		moveControl.setDirection(this.slime.getYRot(), false);
    		} else {
    			if(this.merge.contains(this.mergeTime))
    				this.mergeSiblings();

    			if(this.getClosestSibling() != null) {
       	 			this.slime.lookAt(this.getClosestSibling(), 10, 10);
        			moveControl.setDirection(this.slime.getYRot(), false);
    			}
    		}
        }
    }

    public boolean containsSlime(ArrayList<Slime> array, Slime check) {
		if(array == null || array.size() == 0)
			return false;
    	
    	for(Slime slime : array) {
    		if(slime.getUUID() == check.getUUID())
    			return true;
    	}
    	return false;
    }

    public void setSiblings(ArrayList<Slime> slimes) {
    	this.siblings = slimes;
    	this.merge = new ArrayList();
    	for(Slime slime : slimes) {
    		this.merge.add(0);
    	}
    }

    public void addSibling(Slime slime) {
    	this.siblings.add(slime);
    	this.merge.add(0);
    }

    public void removeSibling(Slime slime) {
    	int index = this.siblings.indexOf(slime);
    	if(index != -1) {
    		this.siblings.remove(index);
    		this.merge.remove(index);
    	}
    }

    public double targetDistance() {
    	if(this.slime.parentAlive())
    		return this.slime.distanceToSqr(this.slime.getParent());
    	if(this.getClosestSibling() == null)
    		return Double.MAX_VALUE;
    	return this.slime.distanceToSqr(this.getClosestSibling());
    }

    public boolean canPath() {
    	if(this.slime.parentAlive())
    		return this.isSameDimension(this.slime, this.slime.getParent());
    	return this.canPathToSibling();
    }

    public boolean canPathToSibling() {
    	if(this.getClosestSibling() == null)
    		return false;
    	return this.isSameDimension(this.slime, this.getClosestSibling());
    }

	public boolean isSameDimension(TameableSlime slime1, Slime slime2) {
		if(slime1 == null || slime2 == null)
			return false;
		return slime1.level().dimension() == slime2.level().dimension();
	}

	public Slime getClosestSibling() {
		if(this.siblings == null || this.siblings.size() == 0)
			return null;
			
		Map<Slime, Double> DistanceMap = new HashMap<>();
		Map<Double, Slime> SlimeMap = new HashMap<>();
		for(Slime slime : this.siblings) {
			double distance = this.slime.distanceToSqr(slime);
			if(!isSameDimension(this.slime, slime))
				distance = Double.MAX_VALUE;
			DistanceMap.put(slime, distance);
			SlimeMap.put(distance, slime);
		}
		
		List<Double> distances = new ArrayList(DistanceMap.values());
		distances.sort(Double::compareTo);
		return SlimeMap.get(distances.get(0));
	}

	public void addMergeTime() {
		if(this.siblings == null || this.siblings.size() == 0)
			return;
		
		for(Slime slime : this.siblings) {
			if(this.slime.distanceToSqr(slime) > (maxDistance * this.slime.getSize()))
				continue;
			int idx = this.siblings.indexOf(slime);
			this.merge.set(idx, this.merge.get(idx) + 1); 
		}
	}

	public void mergeSiblings() {
		if(this.siblings == null || this.siblings.size() == 0)
			return;

		ArrayList<Slime> tempList = new ArrayList(this.siblings); 
		for(int idx = 0; idx < tempList.size(); idx++) {
			if(tempList.size() != this.siblings.size())
				return;

			Slime slime = tempList.get(idx);
			if(this.slime.distanceToSqr(slime) > (maxDistance * this.slime.getSize()))
				continue;
			if(this.merge.get(idx) >= this.mergeTime) {
				this.merge.set(idx, 0); 
				this.slime.mergeWith(slime);
				return;
			}
		}
	}

	public void mergeParent() {
		this.slime.mergeWith(this.slime.getParent());
	}
}