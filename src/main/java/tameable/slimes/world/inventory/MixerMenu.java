package tameable.slimes.world.inventory;

import tameable.slimes.init.TameableSlimesModMenus;
import tameable.slimes.init.TameableSlimesModBlocks;
import tameable.slimes.init.TameableSlimesModItems;
import tameable.slimes.MixerResult;
import tameable.slimes.Color;

import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;

import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class MixerMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
	public final static HashMap<String, Object> guistate = new HashMap<>();
	public final Level world;
	public final Player entity;
	public int x, y, z;
	private ContainerLevelAccess access = ContainerLevelAccess.NULL;
	private IItemHandler internal;
	private final Map<Integer, Slot> customSlots = new HashMap<>();
	private boolean bound = false;
	private Supplier<Boolean> boundItemMatcher = null;
	private Entity boundEntity = null;
	private BlockEntity boundBlockEntity = null;
	protected MixerResult lastResult = new MixerResult();

	public MixerMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(TameableSlimesModMenus.MIXER.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
		this.internal = new ItemStackHandler(12);
		BlockPos pos = null;
		if (extraData != null) {
			pos = extraData.readBlockPos();
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			access = ContainerLevelAccess.create(world, pos);
		}
		if (pos != null) {
			if (extraData.readableBytes() == 1) { // bound to item
				byte hand = extraData.readByte();
				ItemStack itemstack = hand == 0 ? this.entity.getMainHandItem() : this.entity.getOffhandItem();
				this.boundItemMatcher = () -> itemstack == (hand == 0 ? this.entity.getMainHandItem() : this.entity.getOffhandItem());
				itemstack.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
					this.internal = capability;
					this.bound = true;
				});
			} else if (extraData.readableBytes() > 1) { // bound to entity
				extraData.readByte(); // drop padding
				boundEntity = world.getEntity(extraData.readVarInt());
				if (boundEntity != null)
					boundEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
						this.internal = capability;
						this.bound = true;
					});
			} else { // might be bound to block
				boundBlockEntity = this.world.getBlockEntity(pos);
				if (boundBlockEntity != null)
					boundBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
						this.internal = capability;
						this.bound = true;
					});
			}
		}
		this.customSlots.put(0, this.addSlot(new SlotItemHandler(internal, 0, 44, 21) {
			private final int slot = 0;

			@Override
			public boolean mayPlace(ItemStack stack) {
				if(stack.is(ItemTags.create(new ResourceLocation("tameable_slimes:vaild_mixer"))))
					return super.mayPlace(stack);
				return false;
			}

			@Override
			public void setChanged() {
				super.setChanged();
				updateMixing();
			}

			@Override
			public void onTake(Player entity, ItemStack stack) {
				super.onTake(entity, stack);
				updateMixing();
			}

			@Override
			public void onQuickCraft(ItemStack a, ItemStack b) {
				super.onQuickCraft(a, b);
				updateMixing();
			}
		}));
		this.customSlots.put(1, this.addSlot(new SlotItemHandler(internal, 1, 62, 21) {
			private final int slot = 1;

			@Override
			public boolean mayPlace(ItemStack stack) {
				if(stack.is(ItemTags.create(new ResourceLocation("tameable_slimes:vaild_mixer"))))
					return super.mayPlace(stack);
				return false;
			}

			@Override
			public void setChanged() {
				super.setChanged();
				updateMixing();
			}

			@Override
			public void onTake(Player entity, ItemStack stack) {
				super.onTake(entity, stack);
				updateMixing();
			}

			@Override
			public void onQuickCraft(ItemStack a, ItemStack b) {
				super.onQuickCraft(a, b);
				updateMixing();
			}
		}));
		this.customSlots.put(2, this.addSlot(new SlotItemHandler(internal, 2, 80, 21) {
			private final int slot = 2;

			@Override
			public boolean mayPlace(ItemStack stack) {
				if(stack.is(ItemTags.create(new ResourceLocation("tameable_slimes:vaild_mixer"))))
					return super.mayPlace(stack);
				return false;
			}

			@Override
			public void setChanged() {
				super.setChanged();
				updateMixing();
			}

			@Override
			public void onTake(Player entity, ItemStack stack) {
				super.onTake(entity, stack);
				updateMixing();
			}

			@Override
			public void onQuickCraft(ItemStack a, ItemStack b) {
				super.onQuickCraft(a, b);
				updateMixing();
			}
		}));
		this.customSlots.put(3, this.addSlot(new SlotItemHandler(internal, 3, 44, 39) {
			private final int slot = 3;

			@Override
			public boolean mayPlace(ItemStack stack) {
				if(stack.is(ItemTags.create(new ResourceLocation("tameable_slimes:vaild_mixer"))))
					return super.mayPlace(stack);
				return false;
			}

			@Override
			public void setChanged() {
				super.setChanged();
				updateMixing();
			}

			@Override
			public void onTake(Player entity, ItemStack stack) {
				super.onTake(entity, stack);
				updateMixing();
			}

			@Override
			public void onQuickCraft(ItemStack a, ItemStack b) {
				super.onQuickCraft(a, b);
				updateMixing();
			}
		}));
		this.customSlots.put(4, this.addSlot(new SlotItemHandler(internal, 4, 62, 39) {
			private final int slot = 4;

			@Override
			public boolean mayPlace(ItemStack stack) {
				if(stack.is(ItemTags.create(new ResourceLocation("tameable_slimes:vaild_mixer"))))
					return super.mayPlace(stack);
				return false;
			}

			@Override
			public void setChanged() {
				super.setChanged();
				updateMixing();
			}

			@Override
			public void onTake(Player entity, ItemStack stack) {
				super.onTake(entity, stack);
				updateMixing();
			}

			@Override
			public void onQuickCraft(ItemStack a, ItemStack b) {
				super.onQuickCraft(a, b);
				updateMixing();
			}
		}));
		this.customSlots.put(5, this.addSlot(new SlotItemHandler(internal, 5, 80, 39) {
			private final int slot = 5;

			@Override
			public boolean mayPlace(ItemStack stack) {
				if(stack.is(ItemTags.create(new ResourceLocation("tameable_slimes:vaild_mixer"))))
					return super.mayPlace(stack);
				return false;
			}

			@Override
			public void setChanged() {
				super.setChanged();
				updateMixing();
			}

			@Override
			public void onTake(Player entity, ItemStack stack) {
				super.onTake(entity, stack);
				updateMixing();
			}

			@Override
			public void onQuickCraft(ItemStack a, ItemStack b) {
				super.onQuickCraft(a, b);
				updateMixing();
			}
		}));
		this.customSlots.put(6, this.addSlot(new SlotItemHandler(internal, 6, 44, 57) {
			private final int slot = 6;

			@Override
			public boolean mayPlace(ItemStack stack) {
				if(stack.is(ItemTags.create(new ResourceLocation("tameable_slimes:vaild_mixer"))))
					return super.mayPlace(stack);
				return false;
			}

			@Override
			public void setChanged() {
				super.setChanged();
				updateMixing();
			}

			@Override
			public void onTake(Player entity, ItemStack stack) {
				super.onTake(entity, stack);
				updateMixing();
			}

			@Override
			public void onQuickCraft(ItemStack a, ItemStack b) {
				super.onQuickCraft(a, b);
				updateMixing();
			}
		}));
		this.customSlots.put(7, this.addSlot(new SlotItemHandler(internal, 7, 62, 57) {
			private final int slot = 7;

			@Override
			public boolean mayPlace(ItemStack stack) {
				if(stack.is(ItemTags.create(new ResourceLocation("tameable_slimes:vaild_mixer"))))
					return super.mayPlace(stack);
				return false;
			}

			@Override
			public void setChanged() {
				super.setChanged();
				updateMixing();
			}

			@Override
			public void onTake(Player entity, ItemStack stack) {
				super.onTake(entity, stack);
				updateMixing();
			}

			@Override
			public void onQuickCraft(ItemStack a, ItemStack b) {
				super.onQuickCraft(a, b);
				updateMixing();
			}
		}));
		this.customSlots.put(8, this.addSlot(new SlotItemHandler(internal, 8, 80, 57) {
			private final int slot = 8;

			@Override
			public boolean mayPlace(ItemStack stack) {
				if(stack.is(ItemTags.create(new ResourceLocation("tameable_slimes:vaild_mixer"))))
					return super.mayPlace(stack);
				return false;
			}

			@Override
			public void setChanged() {
				super.setChanged();
				updateMixing();
			}

			@Override
			public void onTake(Player entity, ItemStack stack) {
				super.onTake(entity, stack);
				updateMixing();
			}

			@Override
			public void onQuickCraft(ItemStack a, ItemStack b) {
				super.onQuickCraft(a, b);
				updateMixing();
			}
		}));
		this.customSlots.put(9, this.addSlot(new SlotItemHandler(internal, 9, 134, 39) {
			private final int slot = 9;

			@Override
			public void onTake(Player entity, ItemStack stack) {
				super.onTake(entity, stack);
				craftResult();
			}

			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}
		}));
		this.customSlots.put(10, this.addSlot(new SlotItemHandler(internal, 10, 17, 30) {
			private final int slot = 10;

			@Override
			public boolean mayPlace(ItemStack stack) {
				if(stack.getItem() instanceof DyeItem)
					return super.mayPlace(stack);
				return false;
			}

			@Override
			public void setChanged() {
				super.setChanged();
				updateMixing();
			}

			@Override
			public void onTake(Player entity, ItemStack stack) {
				super.onTake(entity, stack);
				updateMixing();
			}

			@Override
			public void onQuickCraft(ItemStack a, ItemStack b) {
				super.onQuickCraft(a, b);
				updateMixing();
			}
		}));
		this.customSlots.put(11, this.addSlot(new SlotItemHandler(internal, 11, 17, 48) {
			private final int slot = 11;

			@Override
			public boolean mayPlace(ItemStack stack) {
				Item item = stack.getItem();
				if(item == Items.GLASS_BOTTLE)	
					return super.mayPlace(stack);
				if(item == Items.POTION && PotionUtils.getPotion(stack) == Potions.WATER)	
					return super.mayPlace(stack);
				return false;
			}

			@Override
			public void setChanged() {
				super.setChanged();
				updateMixing();
			}

			@Override
			public void onTake(Player entity, ItemStack stack) {
				super.onTake(entity, stack);
				updateMixing();
			}

			@Override
			public void onQuickCraft(ItemStack a, ItemStack b) {
				super.onQuickCraft(a, b);
				updateMixing();
			}
		}));
		for (int si = 0; si < 3; ++si)
			for (int sj = 0; sj < 9; ++sj)
				this.addSlot(new Slot(inv, sj + (si + 1) * 9, 0 + 8 + sj * 18, 0 + 84 + si * 18));
		for (int si = 0; si < 9; ++si)
			this.addSlot(new Slot(inv, si, 0 + 8 + si * 18, 0 + 142));
	}

	public void craftResult() {
		MixerResult craftInfo = new MixerResult(this.lastResult);
		NonNullList<ItemStack> items = this.getItems();
		items.subList(items.size() - 36, items.size()).clear();
		
		for(int idx = 0; idx < craftInfo.ingredients.size(); idx++) {
			if(craftInfo.ingredients.get(idx).isEmpty())
				continue;
			items.get(idx).shrink(1);
		}

		if(craftInfo.usedDye == true)
			items.get(10).shrink(1);
		if(craftInfo.usedBottle == true) {
			ItemStack bottleStack = items.get(11);
			if(bottleStack.getItem() == Items.POTION) {
				this.getSlot(11).set(new ItemStack(Items.GLASS_BOTTLE));
			} else {
				bottleStack.shrink(1);
			}
		}
		
		this.lastResult = new MixerResult();
		this.updateMixing();
	}

	public void updateMixing() {
		Slot outputSlot = this.getSlot(9);
		outputSlot.set(new ItemStack(Items.AIR));
		
		NonNullList<ItemStack> items = this.getItems();
		items.subList(items.size() - 36, items.size()).clear();
		int craftBlock = this.canCraftBlock(items);
		int craftBall = this.canCraftBall(items);
		boolean craftBottle = this.isOnly(items.subList(0, 9), TameableSlimesModItems.LIVING_SLIME_BOTTLE.get());
		boolean colorEdit = this.canEditColor(items);

		if(craftBlock > 0 && craftBall == 0)
			craftBall = -1;
		if(craftBall > 0 && craftBlock == 0)
			craftBlock = -1;
		if(craftBall > 0 && craftBottle)
			craftBottle = false;
		
		if(craftBlock != -1 && (craftBottle || craftBall != -1))
			return;
		if(craftBall != -1 && (craftBottle || craftBlock != -1))
			return;
		if(craftBottle && (craftBlock != -1 || craftBall != -1))
			return;

		int craftType = colorEdit ? 0 : (craftBottle ? 1 : (craftBlock != -1 ? 2 : 3));
		int recipeType = craftType <= 1 ? 0 : (craftType == 2 ? craftBlock : craftBall);
		this.craftOption(items, craftType, recipeType);
	}

	public void craftOption(NonNullList<ItemStack> items, int type, int recipe) {
		ItemStack option = new ItemStack(Items.AIR);
		ItemStack bottle = items.get(11);
		ItemStack dye = items.get(10);
	
		boolean hasWater = bottle.isEmpty() ? false : PotionUtils.getPotion(bottle) == Potions.WATER;
		boolean hasDye = hasWater ? false : !dye.isEmpty();
		
		MixerResult result = new MixerResult(items.subList(0, 9), dye, bottle, option, type, recipe);
		if(recipe == -2) {
			ArrayList<ItemStack> slime = this.getAll(items.subList(0, 9));
			option = this.splitSlime(slime.get(0));
		} else if(recipe == 0) {
			if(type == 0) {
				ArrayList<ItemStack> slime = this.getAll(items.subList(0, 9));
				ItemStack stack = slime.get(0);
				option = new ItemStack(stack.getItem());
				option.setTag(stack.getOrCreateTag().copy());
			} else if(type == 1) {
				ArrayList<ItemStack> slime = this.getSlotsOf(items.subList(0, 9), TameableSlimesModItems.LIVING_SLIME_BOTTLE.get());
				if(slime.size() > 1 && canMix(slime, Items.AIR, false))
					option = mixSlime(slime, Items.AIR, false);
			} else if(type == 3) {
				ArrayList<ItemStack> slime = this.getSlotsOf(items.subList(0, 9), TameableSlimesModItems.LIVING_SLIME.get());
				if(slime.size() > 1 && canMix(slime, Items.AIR, true))
					option = mixSlime(slime, Items.AIR, true);
			} else if(type == 2) {
				ArrayList<ItemStack> slime = this.getSlotsOf(items.subList(0, 9), TameableSlimesModBlocks.ALIVE_SLIME.get().asItem());
				if(slime.size() > 1 && canMix(slime, Items.AIR, true))
					option = mixSlime(slime, Items.AIR, true);
			}
		} else if(recipe == 1) {
			if(type == 3) {
				ArrayList<ItemStack> slime = this.getSlotsOf(items.subList(0, 9), TameableSlimesModItems.LIVING_SLIME_BOTTLE.get());
				option = mixSlime(slime, TameableSlimesModItems.LIVING_SLIME.get(), false);
			} else if(type == 2) {
				ArrayList<ItemStack> slime = this.getSlotsOf(items.subList(0, 9), TameableSlimesModItems.LIVING_SLIME.get());
				option = mixSlime(slime, TameableSlimesModBlocks.ALIVE_SLIME.get().asItem(), true);
			}
		} else if(recipe >= 2) {
			if(type == 3) {
				ArrayList<ItemStack> slime = this.getSlotsOf(items.subList(0, 9), TameableSlimesModItems.LIVING_SLIME_BOTTLE.get());
				option = mixSlime(slime, TameableSlimesModItems.LIVING_SLIME.get(), false);
			}
		}

		Color optionColor = new Color(option);
		optionColor.setDefault(Color.slime);
		boolean hasColor = optionColor.getLong() != Color.slime;
	
		if(hasWater && this.isDyeable(option)) {
			option = this.toDefault(option);
			result.usedBottle = true;
		}
			
		if(hasDye && dye.getItem() instanceof DyeItem dyeItem) {
			option = this.toDyeable(option);
			Color mixColor = new Color(dyeItem);
			mixColor.setDefault(Color.slime);
			if(hasColor) {
				if(type == 0 && recipe == 0 && optionColor.getLong() == mixColor.getLong()) {
					option = new ItemStack(Items.AIR);
				} else {
					optionColor.mix(mixColor).applyColor(option);
					result.usedDye = true;
				}
			} else {
				mixColor.applyColor(option);
				result.usedDye = true;
			}
		}

		this.lastResult = result;
		Slot outputSlot = this.getSlot(9);
		if(outputSlot.getItem() != option)
			outputSlot.set(option);
	}

	public ItemStack toDefault(ItemStack stack) {
		Item item = stack.getItem();
		if(item == TameableSlimesModItems.DYED_LIVING_SLIME.get())
			item = TameableSlimesModItems.LIVING_SLIME.get();
		if(item == TameableSlimesModItems.DYED_LIVING_SLIME_BOTTLE.get())
			item = TameableSlimesModItems.LIVING_SLIME_BOTTLE.get();
		if(item == TameableSlimesModBlocks.DYED_ALIVE_SLIME.get().asItem())
			item = TameableSlimesModBlocks.ALIVE_SLIME.get().asItem();
		if(item == stack.getItem())
			return stack;

		ItemStack itemstack = new ItemStack(item);
		CompoundTag tag = stack.getOrCreateTag().copy();
		if(tag.contains("display"))
			tag.remove("display");
			
		itemstack.setTag(tag);
		itemstack.setCount(stack.getCount());
		return itemstack;
	}

	public ItemStack toDyeable(ItemStack stack) {
		Item item = stack.getItem();
		if(item == TameableSlimesModItems.LIVING_SLIME.get())
			item = TameableSlimesModItems.DYED_LIVING_SLIME.get();
		if(item == TameableSlimesModItems.LIVING_SLIME_BOTTLE.get())
			item = TameableSlimesModItems.DYED_LIVING_SLIME_BOTTLE.get();
		if(item == TameableSlimesModBlocks.ALIVE_SLIME.get().asItem())
			item = TameableSlimesModBlocks.DYED_ALIVE_SLIME.get().asItem();
		if(item == stack.getItem())
			return stack;

		ItemStack itemstack = new ItemStack(item);
		itemstack.setTag(stack.getOrCreateTag().copy());
		itemstack.setCount(stack.getCount());
		return itemstack;
	}

	public boolean isDyeable(ItemStack stack) {
		if(stack.getItem() == TameableSlimesModItems.DYED_LIVING_SLIME.get())
			return true;
		if(stack.getItem() == TameableSlimesModItems.DYED_LIVING_SLIME_BOTTLE.get())
			return true;
		if(stack.getItem() == TameableSlimesModBlocks.DYED_ALIVE_SLIME.get().asItem())
			return true;
		return false;
	}

	public boolean canMix(ArrayList<ItemStack> slime, Item convertInto, boolean NBT) {
		ItemStack lastStack = new ItemStack(Items.AIR);
		boolean sameStack = true;
		boolean sameItem = true;
		for(ItemStack stack : slime) {
			if(lastStack.getItem() != Items.AIR) {
				if(sameItem)
					sameItem = this.isSameItem(stack, lastStack);
				if(sameItem) {
					if(sameStack)
						sameStack = this.isSameTags(stack, lastStack);
				} else {
					sameStack = false;
					break;
				}
			}
			
			lastStack = stack;
			if(!sameStack && !sameItem)
				break;
		}

		Item stackItem = convertInto != Items.AIR ? convertInto : (sameItem ? lastStack.getItem() : Items.AIR);
		if(stackItem != lastStack.getItem())
			return true;
		return !sameStack;
	}

	public CompoundTag mixData(CompoundTag compound1, CompoundTag compound2) {
		CompoundTag OringalTag = compound1.copy();
		if(compound1 == null && compound2 == null)
			return new CompoundTag();
		if(compound1 == null || compound2 == null)
			return compound1 == null ? compound2 : compound1;
			
		if(!compound1.contains("Slime") && !compound2.contains("Slime"))
			return new CompoundTag();
		if(!compound1.contains("Slime") || !compound2.contains("Slime"))
			return !compound1.contains("Slime") ? compound2 : compound1;
		compound1 = compound1.getCompound("Slime");
		compound2 = compound1.getCompound("Slime");

		if(!compound1.contains("UUID") && compound2.contains("UUID"))
			compound1.putUUID("UUID", compound2.getUUID("UUID"));
		if(!compound1.contains("CustomName") && compound2.contains("CustomName"))
			compound1.putString("CustomName", compound2.getString("CustomName"));
		if(!compound1.contains("CustomNameVisible") && compound2.contains("CustomNameVisible"))
			compound1.putBoolean("CustomNameVisible", compound2.getBoolean("CustomNameVisible"));
			
		if(!compound1.contains("Tags") && compound2.contains("Tags")) {
			compound1.put("Tags", compound2.getList("Tags", 8));
		} else if(compound1.contains("Tags") && compound2.contains("Tags")) {
			ListTag tags1 = compound1.getList("Tags", 8);
			ListTag tags2 = compound2.getList("Tags", 8);
			for(Tag value : tags2) {
				tags1.add(value);
			}
			compound1.put("Tags", tags1);
		}
		
		if(!compound1.contains("ForgeCaps") && compound2.contains("ForgeCaps")) {
			compound1.put("ForgeCaps", compound2.getCompound("ForgeCaps"));
		} else if(compound1.contains("ForgeCaps") && compound2.contains("ForgeCaps")) {
			CompoundTag caps1 = compound1.getCompound("ForgeCaps");
			CompoundTag caps2 = compound2.getCompound("ForgeCaps");
			compound1.put("ForgeCaps", caps1.merge(caps2));
		}
		
		if(!compound1.contains("ForgeData") && compound2.contains("ForgeData")) {
			compound1.put("ForgeData", compound2.getCompound("ForgeData"));
		} else if(compound1.contains("ForgeData") && compound2.contains("ForgeData")) {
			CompoundTag caps1 = compound1.getCompound("ForgeData");
			CompoundTag caps2 = compound2.getCompound("ForgeData");
			compound1.put("ForgeData", caps1.merge(caps2));
		}

		OringalTag.put("Slime", compound1);
		return OringalTag;
	}

	public ItemStack mixSlime(ArrayList<ItemStack> slime, Item convertInto, boolean NBT) {
		ItemStack lastStack = new ItemStack(Items.AIR);
		boolean sameItem = true;
		
		CompoundTag compound = new CompoundTag();
		ArrayList<Color> colors = new ArrayList();
		for(ItemStack stack : slime) {
			Color color = new Color(stack);
			color.setDefault(7586402);
			if(color.getLong() != 7586402)
				colors.add(color);

			compound = this.mixData(compound.copy(), stack.getOrCreateTag().copy());
			if(lastStack.getItem() != Items.AIR && !this.isSameItem(stack, lastStack))
				sameItem = false;
			lastStack = stack;
		}

		Item stackItem = convertInto != Items.AIR ? convertInto : (sameItem ? lastStack.getItem() : Items.AIR);
		if(stackItem == Items.AIR)
			return new ItemStack(Items.AIR);
		if(colors.size() == 0) {
			ItemStack returnStack =new ItemStack(stackItem);
			//if(NBT)
				returnStack.setTag(compound);
			return returnStack;
		}

		if(stackItem == TameableSlimesModItems.LIVING_SLIME.get())
			stackItem = TameableSlimesModItems.DYED_LIVING_SLIME.get();
		if(stackItem == TameableSlimesModItems.LIVING_SLIME_BOTTLE.get())
			stackItem = TameableSlimesModItems.DYED_LIVING_SLIME_BOTTLE.get();
		if(stackItem == TameableSlimesModBlocks.ALIVE_SLIME.get().asItem())
			stackItem = TameableSlimesModBlocks.DYED_ALIVE_SLIME.get().asItem();
			
		Color blendedColors = Color.blend(colors);
		ItemStack mixedStack = new ItemStack(stackItem);
		if(NBT)
			mixedStack.setTag(compound);
		blendedColors.applyColor(mixedStack);
		return mixedStack;
	}

	public ItemStack splitSlime(ItemStack stack) {
		boolean dyeable = this.isDyeable(stack);
		ItemStack splitStack = new ItemStack(Items.AIR);
		if(this.isSlimeBlock(stack)) {
			splitStack = new ItemStack(TameableSlimesModItems.LIVING_SLIME.get());
			splitStack.setTag(stack.getOrCreateTag().copy());
			splitStack.setCount(9);
		} else if(this.isSlimeBall(stack)) {
			splitStack = new ItemStack(TameableSlimesModItems.LIVING_SLIME_BOTTLE.get());
			splitStack.setTag(stack.getOrCreateTag().copy());
			splitStack.setCount(1);
		}

		if(dyeable && !this.isDyeable(splitStack))
			splitStack = this.toDyeable(splitStack);
		return splitStack;
	}

	public int canCraftBlock(NonNullList<ItemStack> items) {
		if(this.isOnly(items.subList(0, 9), TameableSlimesModBlocks.ALIVE_SLIME.get().asItem()))
			return this.getSlotsOf(items.subList(0, 9), TameableSlimesModBlocks.ALIVE_SLIME.get().asItem()).size() == 1 ? -2 : 0;
		if(this.checkRecipe(items.subList(0, 9), new ArrayList(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8)), TameableSlimesModItems.LIVING_SLIME.get()))
			return 1;
		return -1;
	}

	public int canCraftBall(NonNullList<ItemStack> items) {
		if(this.isOnly(items.subList(0, 9), TameableSlimesModItems.LIVING_SLIME.get())) {
			if(this.getSlotsOf(items.subList(0, 9), TameableSlimesModItems.LIVING_SLIME.get()).size() != 1)
				return 0;
			return items.get(11).getItem() == Items.GLASS_BOTTLE ? -2 : -1;
		}
		
		if(this.checkRecipe(items.subList(0, 9), new ArrayList(List.of(0, 1, 3, 4)), TameableSlimesModItems.LIVING_SLIME_BOTTLE.get()))
			return 1;
		if(this.checkRecipe(items.subList(0, 9), new ArrayList(List.of(1, 2, 4, 5)), TameableSlimesModItems.LIVING_SLIME_BOTTLE.get()))
			return 2;
		if(this.checkRecipe(items.subList(0, 9), new ArrayList(List.of(3, 4, 6, 7)), TameableSlimesModItems.LIVING_SLIME_BOTTLE.get()))
			return 3;
		if(this.checkRecipe(items.subList(0, 9), new ArrayList(List.of(4, 5, 7, 8)), TameableSlimesModItems.LIVING_SLIME_BOTTLE.get()))
			return 4;
		return -1;
	}

	public boolean checkRecipe(List<ItemStack> stacks, ArrayList<Integer> indices, Item item) {
		ArrayList<Item> items = new ArrayList();
		for(int idx = 0; idx < indices.size(); idx++) {
			items.add(item);
		}
		return this.checkRecipe(stacks, indices, items);
	}

	public boolean checkRecipe(List<ItemStack> stacks, ArrayList<Integer> indices, ArrayList<Item> items) {
		for(int idx = 0; idx < stacks.size(); idx++) {
			ItemStack stack = stacks.get(idx);
			if(indices.contains(idx) ? this.isSameItem(stack, new ItemStack(items.get(indices.indexOf(idx)))) : stack.isEmpty())
				continue;
			return false;
		}
		return true;
	}

	public boolean canEditColor(NonNullList<ItemStack> items) {
		boolean hasWater = items.get(11).isEmpty() ? false : PotionUtils.getPotion(items.get(11)) == Potions.WATER;
		if(items.get(10).isEmpty() && !hasWater)
			return false;

		boolean isSingle = false;
		if(this.isOnly(items.subList(0, 9), TameableSlimesModItems.LIVING_SLIME.get()))
			isSingle = this.getSlotsOf(items.subList(0, 9), TameableSlimesModItems.LIVING_SLIME.get()).size() == 1;
		if(this.isOnly(items.subList(0, 9), TameableSlimesModItems.LIVING_SLIME_BOTTLE.get()))
			isSingle = this.getSlotsOf(items.subList(0, 9), TameableSlimesModItems.LIVING_SLIME_BOTTLE.get()).size() == 1;
		if(this.isOnly(items.subList(0, 9), TameableSlimesModBlocks.ALIVE_SLIME.get().asItem()))
			isSingle = this.getSlotsOf(items.subList(0, 9), TameableSlimesModBlocks.ALIVE_SLIME.get().asItem()).size() == 1;
		if(!isSingle)
			return false;

		if(!hasWater) {
			return true;
		} else if(items.get(10).isEmpty()) {
			ArrayList<ItemStack> slime = this.getAll(items.subList(0, 9));
			return this.isDyeable(slime.get(0));
		}

		return false;
	}

	public ArrayList<ItemStack> getAll(List<ItemStack> items) {
		ArrayList<ItemStack> stacks = new ArrayList();
		for(ItemStack stack : items) {
			boolean isVaild = false;
			if(!stack.isEmpty())
				stacks.add(stack);
		}
		return stacks;
	}

	public ArrayList<ItemStack> getSlotsOf(List<ItemStack> items, Item type) {
		ArrayList<ItemStack> stacks = new ArrayList();
		for(ItemStack stack : items) {
			boolean isVaild = false;
			if(stack.isEmpty() && type == Items.AIR)
				isVaild = true;
			if(stack.getItem() == type)
				isVaild = true;
			if(this.isSlimeBlock(stack) && type == TameableSlimesModBlocks.ALIVE_SLIME.get().asItem())
				isVaild = true;
			if(this.isSlimeBall(stack) && type == TameableSlimesModItems.LIVING_SLIME.get())
				isVaild = true;
			if(this.isSlimeBottle(stack) && type == TameableSlimesModItems.LIVING_SLIME_BOTTLE.get())
				isVaild = true;
				
			if(isVaild == true)
				stacks.add(stack);
		}
		return stacks;
	}

	public boolean isOnly(List<ItemStack> items, Item type) {
		for(ItemStack stack : items) {
			if(stack.isEmpty())
				continue;
			if(this.isSlimeBlock(stack) && type == TameableSlimesModItems.ALIVE_SLIME.get().asItem())
				continue;
			if(this.isSlimeBall(stack) && type == TameableSlimesModItems.LIVING_SLIME.get())
				continue;
			if(this.isSlimeBottle(stack) && type == TameableSlimesModItems.LIVING_SLIME_BOTTLE.get())
				continue;
			return false;
		}
		return true;
	}

	public boolean isSameItem(ItemStack item1, ItemStack item2) {
		if(this.isSlimeBlock(item1) && this.isSlimeBlock(item2))
			return true;
		if(this.isSlimeBall(item1) && this.isSlimeBall(item2))
			return true;
		if(this.isSlimeBottle(item1) && this.isSlimeBottle(item2))
			return true;
		return item1.getItem() == item2.getItem();
	}

	public boolean isSameTags(ItemStack item1, ItemStack item2) {
		return item1.isEmpty() && item2.isEmpty() ? true : Objects.equals(item1.getOrCreateTag(), item2.getOrCreateTag()) && item1.areCapsCompatible(item2);
	}

	public boolean isSlimeBlock(ItemStack stack) {
		if(stack.getItem() == TameableSlimesModBlocks.ALIVE_SLIME.get().asItem())
			return true;
		if(stack.getItem() == TameableSlimesModBlocks.DYED_ALIVE_SLIME.get().asItem())
			return true;
		return false;
	}

	public boolean isSlimeBall(ItemStack stack) {
		if(stack.getItem() == TameableSlimesModItems.LIVING_SLIME.get())
			return true;
		if(stack.getItem() == TameableSlimesModItems.DYED_LIVING_SLIME.get())
			return true;
		return false;
	}

	public boolean isSlimeBottle(ItemStack stack) {
		if(stack.getItem() == TameableSlimesModItems.LIVING_SLIME_BOTTLE.get())
			return true;
		if(stack.getItem() == TameableSlimesModItems.DYED_LIVING_SLIME_BOTTLE.get())
			return true;
		return false;
	}

	@Override
	public boolean stillValid(Player player) {
		if (this.bound) {
			if (this.boundItemMatcher != null)
				return this.boundItemMatcher.get();
			else if (this.boundBlockEntity != null)
				return AbstractContainerMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
			else if (this.boundEntity != null)
				return this.boundEntity.isAlive();
		}
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot) this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < 12) {
				if (!this.moveItemStackTo(itemstack1, 12, this.slots.size(), true))
					return ItemStack.EMPTY;
				slot.onQuickCraft(itemstack1, itemstack);
			} else if (!this.moveItemStackTo(itemstack1, 0, 12, false)) {
				if (index < 12 + 27) {
					if (!this.moveItemStackTo(itemstack1, 12 + 27, this.slots.size(), true))
						return ItemStack.EMPTY;
				} else {
					if (!this.moveItemStackTo(itemstack1, 12, 12 + 27, false))
						return ItemStack.EMPTY;
				}
				return ItemStack.EMPTY;
			}
			if (itemstack1.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
			if (itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;
			slot.onTake(playerIn, itemstack1);
		}
		return itemstack;
	}

	@Override
	protected boolean moveItemStackTo(ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_) {
		boolean flag = false;
		int i = p_38905_;
		if (p_38907_) {
			i = p_38906_ - 1;
		}
		if (p_38904_.isStackable()) {
			while (!p_38904_.isEmpty()) {
				if (p_38907_) {
					if (i < p_38905_) {
						break;
					}
				} else if (i >= p_38906_) {
					break;
				}
				Slot slot = this.slots.get(i);
				ItemStack itemstack = slot.getItem();
				if (slot.mayPlace(itemstack) && !itemstack.isEmpty() && ItemStack.isSameItemSameTags(p_38904_, itemstack)) {
					int j = itemstack.getCount() + p_38904_.getCount();
					int maxSize = Math.min(slot.getMaxStackSize(), p_38904_.getMaxStackSize());
					if (j <= maxSize) {
						p_38904_.setCount(0);
						itemstack.setCount(j);
						slot.set(itemstack);
						flag = true;
					} else if (itemstack.getCount() < maxSize) {
						p_38904_.shrink(maxSize - itemstack.getCount());
						itemstack.setCount(maxSize);
						slot.set(itemstack);
						flag = true;
					}
				}
				if (p_38907_) {
					--i;
				} else {
					++i;
				}
			}
		}
		if (!p_38904_.isEmpty()) {
			if (p_38907_) {
				i = p_38906_ - 1;
			} else {
				i = p_38905_;
			}
			while (true) {
				if (p_38907_) {
					if (i < p_38905_) {
						break;
					}
				} else if (i >= p_38906_) {
					break;
				}
				Slot slot1 = this.slots.get(i);
				ItemStack itemstack1 = slot1.getItem();
				if (itemstack1.isEmpty() && slot1.mayPlace(p_38904_)) {
					if (p_38904_.getCount() > slot1.getMaxStackSize()) {
						slot1.setByPlayer(p_38904_.split(slot1.getMaxStackSize()));
					} else {
						slot1.setByPlayer(p_38904_.split(p_38904_.getCount()));
					}
					slot1.setChanged();
					flag = true;
					break;
				}
				if (p_38907_) {
					--i;
				} else {
					++i;
				}
			}
		}
		return flag;
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		if (!bound && playerIn instanceof ServerPlayer serverPlayer) {
			if (!serverPlayer.isAlive() || serverPlayer.hasDisconnected()) {
				for (int j = 0; j < internal.getSlots(); ++j) {
					if(j != 9)
						playerIn.drop(internal.extractItem(j, internal.getStackInSlot(j).getCount(), false), false);
				}
			} else {
				for (int i = 0; i < internal.getSlots(); ++i) {
					if(i != 9)
						playerIn.getInventory().placeItemBackInInventory(internal.extractItem(i, internal.getStackInSlot(i).getCount(), false));
				}
			}
		}
	}

	public Map<Integer, Slot> get() {
		return customSlots;
	}
}