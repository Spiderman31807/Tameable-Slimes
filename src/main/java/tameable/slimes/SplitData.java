package tameable.slimes;

import java.util.UUID;
import java.util.ArrayList;

import net.minecraft.world.entity.monster.Slime;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.advancements.critereon.SlimePredicate;

public class SplitData {
	public UUID parent;
	public SplitData parentSplit;
	public int splitFrom = -1;
	public int splitInto = -1;
	public ArrayList<UUID> children;
	public int numberOfChildren = 0;
	public double slimePercentage = 100;
	public double lostPercentage = 0;

	public SplitData(Slime parent) {
		this.parent = parent.getUUID();
		this.children = new ArrayList();
	}

	public SplitData(Slime parent, int size, ArrayList<Slime> children) {
		this.parent = parent.getUUID();
		this.splitFrom = size;
		this.splitInto = size / 2;
		this.numberOfChildren = children.size();
		this.slimePercentage = 100 / numberOfChildren;
		this.lostPercentage = 100 - ((100 / numberOfChildren) * numberOfChildren);
		this.children = new ArrayList();
		for(Slime slime : children) {
			this.children.add(slime.getUUID());
		}
	}

	public SplitData getParentData(Slime slime) {
		if(parent == slime.getUUID())
			return this;
		return this.parentSplit;
	}

	public SplitData(CompoundTag compound) {
		this.slimePercentage = 0;
		
		if(compound.contains("Parent"))
			this.parent = compound.getUUID("Parent");
		if(compound.contains("FromSize"))
			this.splitFrom = compound.getInt("FromSize");
		if(compound.contains("IntoSize"))
			this.splitInto = compound.getInt("IntoSize");
		if(compound.contains("ChildrenAmount"))
			this.numberOfChildren = compound.getInt("ChildrenAmount");
		if(compound.contains("Percentage"))
			this.slimePercentage = compound.getDouble("Percentage");
		if(compound.contains("LostPercentage"))
			this.lostPercentage = compound.getDouble("LostPercentage");
		if(compound.contains("ChildrenUUID")) {
			CompoundTag childrenTag = compound.getCompound("ChildrenUUID");
			this.children = new ArrayList();
			for(int idx = 0; idx < childrenTag.size(); idx++) {
				this.children.add(childrenTag.getUUID("Child" + idx));
			}
		}
	}

	public CompoundTag toTag() {
		CompoundTag compound = new CompoundTag();
		if(this.parent != null)
			compound.putUUID("Parent", this.parent);
		if(this.splitFrom != -1)
			compound.putInt("FromSize", this.splitFrom);
		if(this.splitInto != -1)
			compound.putInt("IntoSize", this.splitInto);
		if(this.numberOfChildren != 0)
			compound.putInt("ChildrenAmount", this.numberOfChildren);
		if(this.slimePercentage > 0)
			compound.putDouble("Percentage", Math.min(this.slimePercentage, 100));
		if(this.lostPercentage > 0)
			compound.putDouble("LostPercentage", this.lostPercentage);

		if(this.children != null && this.children.size() > 0) {
			CompoundTag childrenTag = new CompoundTag();
			for(UUID uuid : this.children) {
				childrenTag.putUUID("Child" + this.children.indexOf(uuid), uuid);
			}
			compound.put("ChildrenUUID", childrenTag);
		}

		return compound;
	}

	public Slime getPriority(Slime slime1, Slime slime2) {
		if(this.children == null || this.children.size() < 0)
			return slime1;
		
		if(this.children.contains(slime1) && !this.children.contains(slime2))
			return slime1;
		if(this.children.contains(slime2) && !this.children.contains(slime1))
			return slime2;
		if(this.children.indexOf(slime1) < this.children.indexOf(slime2))
			return slime1;
		return slime2;
	}
}
