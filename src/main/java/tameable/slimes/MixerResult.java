package tameable.slimes;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MixerResult {
	public ItemStack result = new ItemStack(Items.AIR);
	public ArrayList<ItemStack> ingredients = new ArrayList();
	public ItemStack dyeSlot = new ItemStack(Items.AIR);
	public ItemStack bottleSlot = new ItemStack(Items.AIR);
	public boolean usedDye = false;
	public boolean usedBottle = false;
	public int type = -1;
	public int recipe = -1;

	public MixerResult() {
	}
	
	public MixerResult(MixerResult copy) {
		this.result = copy.result;
		this.dyeSlot = copy.dyeSlot;
		this.bottleSlot = copy.bottleSlot;
		this.usedBottle = copy.usedBottle;
		this.usedDye = copy.usedDye;
		this.type = copy.type;
		this.recipe = copy.recipe;
		this.ingredients = copy.ingredients;
	}

	public MixerResult(List<ItemStack> items, ItemStack dye, ItemStack bottle, ItemStack output, int type, int recipe) {
		this.result = output;
		this.dyeSlot = dye;
		this.bottleSlot = bottle;
		this.type = type;
		this.recipe = recipe;
		this.ingredients = new ArrayList(items);
		if(type == 3 && recipe == -2)
			this.usedBottle = true;
	}
}
