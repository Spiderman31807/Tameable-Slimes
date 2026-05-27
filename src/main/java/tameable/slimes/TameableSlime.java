package tameable.slimes.entity;

import tameable.slimes.ai.SlimeMoveControl;
import tameable.slimes.ai.SlimeKeepJumpingGoal;
import tameable.slimes.ai.SlimeFloatGoal;
import tameable.slimes.ai.SlimeAttackGoal;
import tameable.slimes.ai.SlimeRandomDirectionGoal;
import tameable.slimes.ai.NonTameRandomTargetGoal;
import tameable.slimes.ai.FollowOwnerGoal;
import tameable.slimes.ai.OwnerHurtByGoal;
import tameable.slimes.ai.OwnerHurtGoal;
import tameable.slimes.ai.StayGoal;
import tameable.slimes.ai.MergeGoal;
import tameable.slimes.init.TameableSlimesModItems;
import tameable.slimes.init.TameableSlimesModBlocks;
import tameable.slimes.init.SlimeRules;
import tameable.slimes.SplitData;
import tameable.slimes.Color;

import java.util.List;
import java.util.ArrayList;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.client.particle.Particle;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.scores.Team;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public abstract class TameableSlime extends Slime implements OwnableEntity {
   	private static final EntityDataAccessor<Boolean> Tamed = SynchedEntityData.defineId(TameableSlime.class, EntityDataSerializers.BOOLEAN);
   	private static final EntityDataAccessor<Optional<UUID>> Owner = SynchedEntityData.defineId(TameableSlime.class, EntityDataSerializers.OPTIONAL_UUID);
   	private static final EntityDataAccessor<Integer> Color = SynchedEntityData.defineId(TameableSlime.class, EntityDataSerializers.INT);
   	private static final EntityDataAccessor<Integer> HarvestCD = SynchedEntityData.defineId(TameableSlime.class, EntityDataSerializers.INT);
   	public MergeGoal mergeGoal = new MergeGoal(this, 1.2, 2, 10);
   	public ArrayList<Integer> killedAtSize = new ArrayList();
   	public ArrayList<UUID> siblings = new ArrayList();
   	public SplitData splitData = new SplitData(this);
   	public boolean followOwner = false;
   	public BlockPos stayPos = null;
   	public int disabledTicks = 0;
   	public float yaw = 0;
   
	public TameableSlime(EntityType<? extends TameableSlime> type, Level world) {
		super(type, world);
		this.moveControl = new SlimeMoveControl(this);
	}

    public ResourceLocation getTexture(int type) {
    	String Texture = "tameable_slimes:textures/entities/slime";
		if(type == 1)
			Texture += "_outer";
		if(type == 2)
			Texture += "_inner";
		
		return new ResourceLocation(Texture + ".png");
	}

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return this.toBlock();
    }

	@Override
    protected ResourceLocation getDefaultLootTable() {
        return EntityType.SLIME.getDefaultLootTable();
    }

    public boolean popItem(ItemStack stack, int pickupDelay, boolean soundEffect) {
    	if(this.level() instanceof ServerLevel server) {
    		ItemEntity item = new ItemEntity(server, this.getX(), this.getY(), this.getZ(), stack);
			item.setPickUpDelay(pickupDelay);
			server.addFreshEntity(item);

			if(soundEffect)
				this.level().playSound(null, this.blockPosition(), SoundEvents.CHICKEN_EGG, SoundSource.NEUTRAL, 1, 1);
			return true;
    	}
    	return false;
    }

	@Override
	protected void registerGoals() {
      	this.goalSelector.addGoal(1, new SlimeFloatGoal(this));
      	this.goalSelector.addGoal(2, new SlimeAttackGoal(this));
      	this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1, 10, 2));
      	this.goalSelector.addGoal(4, new StayGoal(this, 1, 10, 2));
      	this.goalSelector.addGoal(6, new SlimeRandomDirectionGoal(this));
      	this.goalSelector.addGoal(7, new SlimeKeepJumpingGoal(this));

		this.targetSelector.addGoal(1, new OwnerHurtByGoal(this));
		this.targetSelector.addGoal(2, new OwnerHurtGoal(this));
      	this.targetSelector.addGoal(3, new NonTameRandomTargetGoal<>(this, Player.class, true, (player) -> { return Math.abs(player.getY() - this.getY()) <= 4.0D; }));
      	this.targetSelector.addGoal(4, new NonTameRandomTargetGoal<>(this, IronGolem.class, true));
   	}

   	public void aiStep() {
   		if(this.disabledTicks > 0)
   			this.disabledTicks--;
   		if(this.getHarvestCD() > 0)
   			this.setHarvestCD(this.getHarvestCD() - 1);
   		super.aiStep();
   	}

	@Override
   	public float getYRot() {
   		if(disabledTicks > 0)
   			return this.yaw;
      	return super.getYRot();
   	}

	@Override
	protected void defineSynchedData() {
      	super.defineSynchedData();
      	this.entityData.define(Tamed, false);
      	this.entityData.define(Owner, Optional.empty());
      	this.entityData.define(Color, 7586402);
      	this.entityData.define(HarvestCD, 6000);
   	}

	@Override
   	public void addAdditionalSaveData(CompoundTag compound) {
      	super.addAdditionalSaveData(compound);

      	compound.putBoolean("Following", this.followOwner);
      	compound.put("SplitData", this.splitData.toTag());
      	if(!this.siblings.isEmpty()) {
  			CompoundTag siblingsTag = new CompoundTag();
  			for(int idx = 0; idx < this.siblings.size(); idx++) {
  				siblingsTag.putUUID("Slime" + idx, this.siblings.get(idx));
  			}
  			compound.put("Siblings", siblingsTag);
      	}
      	
      	if(!this.killedAtSize.isEmpty()) {
  			CompoundTag killedSizesTag = new CompoundTag();
  			for(int idx = 0; idx < this.killedAtSize.size(); idx++) {
  				killedSizesTag.putInt("Index" + idx, this.killedAtSize.get(idx));
  			}
  			compound.put("KilledAtSizes", killedSizesTag);
      	}

      	if(this.isDyed())
      		compound.putInt("Color", this.getColor().getLong());
      	if(this.isTame()) {
      		compound.putBoolean("Tamed", true);
      		compound.putUUID("Owner", this.getOwnerUUID());
      	}
      	
      	if(this.stayPos != null)
      		compound.put("StayPos", NbtUtils.writeBlockPos(this.stayPos));
   	}

	@Override
  	public void readAdditionalSaveData(CompoundTag compound) {
  		super.readAdditionalSaveData(compound);
  		this.splitData.parent = this.getUUID();
  		
  		if(compound.contains("Color"))
  			this.setColor(new Color(compound.getInt("Color")));
  		if(compound.contains("Tamed"))
  			this.setTame(compound.getBoolean("Tamed"), false);
  		if(compound.contains("Following"))
  			this.followOwner = compound.getBoolean("Following");
  		if(compound.contains("Owner"))
  			this.setOwnerUUID(compound.getUUID("Owner"));
  		if(compound.contains("SplitData")) {
  			this.splitData = new SplitData(compound.getCompound("SplitData"));
  			this.loadSiblings(this.splitData.children);
  		}
  		
  		if(compound.contains("Siblings")) {
  			CompoundTag siblingsTag = compound.getCompound("Siblings");
  			for(int idx = 0; idx < siblingsTag.size(); idx++) {
  				this.siblings.add(siblingsTag.getUUID("Slime" + idx));
  			}
  		}
  		
  		if(compound.contains("KilledAtSizes")) {
  			CompoundTag killedSizesTag = compound.getCompound("KilledAtSizes");
  			for(int idx = 0; idx < killedSizesTag.size(); idx++) {
  				this.killedAtSize.add(killedSizesTag.getInt("Index" + idx));
  			}
  		}

  		if(compound.contains("StayPos"))
  			this.stayPos = NbtUtils.readBlockPos(compound.getCompound("StayPos"));
   	}

	@Override
  	protected void dealDamage(LivingEntity entity) {
      	if(entity.isAlliedTo(this) || this.isAlliedTo(entity))
      		return;
      	if(this.wantsToAttack(entity, this.getOwner()));
      		super.dealDamage(entity);
   	}

	@Override
   	public void push(Entity entity) {
      	super.push(entity);
      	if(entity instanceof IronGolem)
      		return;
      	if ((this.getTarget() == entity || (entity instanceof Mob mob && mob.getTarget() == this)) && this.isDealsDamage())
         	this.dealDamage((LivingEntity)entity);
   	}

	@Override
   	public void remove(Entity.RemovalReason reason) {
		this.detachSiblings();
      	int size = this.getSize();
      	if (!this.level().isClientSide && size > 1 && this.isDeadOrDying()) {
         	Component Name = this.getCustomName();
         	boolean AI = this.isNoAi();
         	float offset = (float)size / 4.0F;
         	int childSize = size / 2;
         	int amount = 2 + this.random.nextInt(3);

			ArrayList<Slime> children = new ArrayList();
	        for(int idx = 0; idx < amount; ++idx) {
            	float offsetX = ((float)(idx % 2) - 0.5F) * offset;
            	float offsetZ = ((float)(idx / 2) - 0.5F) * offset;
            	Slime slime = this.getType().create(this.level());
            	if (slime == null)
            		continue;
            		
               	if (this.isPersistenceRequired())
                  	slime.setPersistenceRequired();

               	slime.setCustomName(Name);
               	slime.setNoAi(AI);
               	slime.setInvulnerable(this.isInvulnerable());
               	slime.setSize(childSize, true);
               	slime.moveTo(this.getX() + (double)offsetX, this.getY() + 0.5D, this.getZ() + (double)offsetZ, this.random.nextFloat() * 360.0F, 0.0F);
               	children.add(slime);
         	}

			this.splitData = new SplitData(this, size, children);
         	for(int idx = 0; idx < children.size(); idx++) {
         		this.finalizeSplit(children, children.get(idx), idx);
         	}
      	}

		this.setSize(0, false);
      	super.remove(reason);
   	}

   	public void finalizeSplit(ArrayList<Slime> children, Slime slime, int index) {
		ArrayList<Slime> siblings = new ArrayList(children);
		for(Slime cousin : this.getSiblings()) {
			if(cousin instanceof TameableSlime tameable)
				tameable.addSibling(slime);
			siblings.add(cousin);
		}
   		
   		if(slime instanceof TameableSlime tameable) {
			tameable.setSiblings(siblings);
			tameable.splitData = this.splitData;
			tameable.killedAtSize = this.killedAtSize;
      		tameable.goalSelector.addGoal(5, tameable.mergeGoal);
   			
   			if(this.isTame()) {
   				tameable.setTame(true, true);
   				tameable.setOwnerUUID(this.getOwnerUUID());
   			}

   			if(this.isDyed())
   				tameable.setColor(this.getColor());
   		}

   		this.level().addFreshEntity(slime);
   	}

   	protected void dropFromLootTable(DamageSource source, boolean murdered) {
      	ResourceLocation resourcelocation = this.getLootTable();
      	LootTable loottable = this.level().getServer().getLootData().getLootTable(resourcelocation);
      	LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel)this.level())).withParameter(LootContextParams.THIS_ENTITY, this).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.DAMAGE_SOURCE, source).withOptionalParameter(LootContextParams.KILLER_ENTITY, source.getEntity()).withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, source.getDirectEntity());
      	if (murdered && this.lastHurtByPlayer != null)
         	lootparams$builder = lootparams$builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer).withLuck(this.lastHurtByPlayer.getLuck());
      	LootParams lootparams = lootparams$builder.create(LootContextParamSets.ENTITY);
      	ObjectArrayList<ItemStack> items = loottable.getRandomItems(lootparams, this.getLootTableSeed());

      	int LivingDropRate = this.level().getLevelData().getGameRules().getInt(SlimeRules.LIVING_DROP_RATE);
      	for(ItemStack stack : items) {
      		Color stackColor = new Color(stack);
      		stackColor.setDefault(7586402);
			Item colorItem = TameableSlimesModItems.DYEABLE_SLIME_BALL.get();
      		
      		ItemStack spawnStack = stack;
			int LivingRoll = Mth.nextInt(this.random, 1, 100);
			if(LivingRoll <= LivingDropRate) {
				spawnStack = new ItemStack(TameableSlimesModItems.LIVING_SLIME.get());
				spawnStack.setTag(stack.getOrCreateTag());
				spawnStack.setCount(stack.getCount());
				colorItem = TameableSlimesModItems.DYED_LIVING_SLIME.get();
			}
      		
      		if(stackColor.getLong() != this.getColor().getLong()) {
				spawnStack = new ItemStack(colorItem);
				spawnStack.setTag(stack.getOrCreateTag());
				spawnStack.setCount(stack.getCount());
				this.getColor().applyColor(spawnStack);
      		}

      		this.spawnAtLocation(spawnStack);
      	}
   }

   	public boolean shouldDropExperience() {
      	if(this.killedAtSize.contains(this.getSize()))
      		return false;
      	this.killedAtSize.add(this.getSize());
      	return true;
   	}

   	public void detachSiblings() {
   		for(Slime slime : this.getSiblings()) {
			if(slime instanceof TameableSlime tameable) {
				tameable.removeSibling(this);
			}
		}
		this.setSiblings(new ArrayList());
   	}

   	public void setSiblings(ArrayList<Slime> slimes) {
		if(slimes.contains(this))
			slimes.remove(this);
   		
   		this.mergeGoal.setSiblings(slimes);
   		this.siblings = new ArrayList();
   		ArrayList<UUID> siblings = new ArrayList();
   		for(Slime slime : slimes) {
   			this.siblings.add(slime.getUUID());
   		}
   	}

   	public void loadSiblings(ArrayList<UUID> uuids) {
   		if(this.level() instanceof ServerLevel server) {
			if(uuids.contains(this.getUUID()))
				uuids.remove(this.getUUID());
   		
   			this.siblings = new ArrayList();
   			ArrayList<Slime> slimes = new ArrayList();
   			for(UUID uuid : uuids) {
   				Entity entity = server.getEntity(uuid);
   				if(entity instanceof TameableSlime slime) {
   					slimes.add(slime);
   					this.siblings.add(entity.getUUID());
   					slime.addSibling(this);
   				}
   			}
   		
   			this.mergeGoal.setSiblings(slimes);
   		}
   	}

   	public void addSibling(Slime slime) {
		if(slime == this || this.siblings.contains(slime))
			return;
   		
   		this.siblings.add(slime.getUUID());
   		this.mergeGoal.addSibling(slime);
   	}

   	public void removeSibling(Slime slime) {
		if(!this.siblings.contains(slime.getUUID()))
			return;
   		
   		this.siblings.remove(slime.getUUID());
   		this.mergeGoal.removeSibling(slime);
   	}

   	public void mergeWith(Slime slime) {
   		if(slime instanceof TameableSlime tameable) {
			if(tameable.splitData == null || this.splitData == null)
				return;
				
   			boolean shouldSwap = this.getSize() < tameable.getSize() || this.splitData.getPriority(this, slime) != this || tameable.getUUID() == this.splitData.parent;
   			boolean shouldContinue = tameable.getSize() < this.getSize() || tameable.splitData.getPriority(slime, this) != slime || this.getUUID() == tameable.splitData.parent;
   			if(shouldSwap == true && shouldContinue == false) {
   				tameable.mergeWith((Slime) this);
   				return;
   			}

			tameable.detachSiblings();
   			tameable.remove(Entity.RemovalReason.DISCARDED);
			this.killedAtSize.addAll(tameable.killedAtSize);
   			this.splitData.slimePercentage += (100 / this.splitData.numberOfChildren) * (slime.getSize() / this.splitData.splitInto);
			if(this.splitData.slimePercentage + this.splitData.lostPercentage >= 100 && this.getSize() < this.splitData.splitFrom) {
				this.setSize(this.splitData.splitFrom, true);
				this.setUUID(this.splitData.parent);
				this.splitData = this.splitData.getParentData(this);
				this.loadSiblings(this.splitData.children);
			}
			
   			this.setHealth(this.getHealth() + tameable.getHealth());
   			this.setCustomNameVisible(this.isCustomNameVisible() || tameable.isCustomNameVisible());
   			if(!this.hasCustomName() || tameable.hasCustomName())
   				this.setCustomName(tameable.getCustomName());

			double x = (this.getX() + tameable.getX()) / 2;
			double y = (this.getY() + tameable.getY()) / 2;
			double z = (this.getZ() + tameable.getZ()) / 2;
			this.setPosRaw(x, y + 0.2, z);
   		}
   	}

   	public boolean isSplit() {
   		if(this.splitData == null)
   			return false;
   		return this.splitData.parent != this.getUUID();
   	}

   	public Slime getParent() {
   		if(this.splitData == null || this.splitData.parent == this.getUUID())
   			return null;
   		if(this.level() instanceof ServerLevel server && server.getEntity(this.splitData.parent) instanceof Slime slime)
   			return slime;
   		return null;
   	}

   	public boolean parentAlive() {
   		return this.getParent() != null;
   	}

   	public ArrayList<Slime> getSiblings() {   		
   		if(this.splitData == null || this.splitData.parent == this.getUUID() || this.siblings == null || this.siblings.size() == 0)
   			return new ArrayList();
   		if(this.level() instanceof ServerLevel server) {
   			ArrayList<Slime> siblings = new ArrayList();
   			for(UUID slimeUUID : this.siblings) {
   				if(server.getEntity(slimeUUID) instanceof Slime slime)
   					siblings.add(slime);
   			}

   			return siblings;
   		}

   		return new ArrayList();
   	}

	@Override
   	protected boolean shouldDespawnInPeaceful() {
		return this.getSize() > 0 && !this.isTame();
   	}

    @Override
    public boolean removeWhenFarAway(double disatnce) {
        return !this.isTame();
    }

    protected ParticleOptions getParticleType() {
    	if(!this.isDyed())
    		return super.getParticleType();
    	return new ItemParticleOption(ParticleTypes.ITEM, this.getColor().getSlimeType());
    }

    protected CompoundTag saveLivingData() {
    	CompoundTag compound = this.saveWithoutId(new CompoundTag());
    	CompoundTag livingData = new CompoundTag();

   		if(compound.contains("UUID"))
   			livingData.putUUID("UUID", this.splitData.parent);
   		if(compound.contains("CustomName"))
   			livingData.putString("CustomName", compound.getString("CustomName"));
   		if(compound.contains("CustomNameVisible"))
   			livingData.putBoolean("CustomNameVisible", compound.getBoolean("CustomNameVisible"));
   		if(compound.contains("Tags"))
   			livingData.put("Tags", compound.getCompound("Tags"));
   		if(compound.contains("ForgeCaps"))
   			livingData.put("ForgeCaps", compound.getCompound("ForgeCaps"));
   		if(compound.contains("ForgeData"))
   			livingData.put("ForgeData", compound.getCompound("ForgeData"));

   		return livingData;
    }

   	protected ItemStack toItem() {
   		ItemStack stack = new ItemStack(this.isDyed() ? TameableSlimesModItems.DYED_LIVING_SLIME.get() : TameableSlimesModItems.LIVING_SLIME.get());
   		if(stack.getItem() == TameableSlimesModItems.DYED_LIVING_SLIME.get())
   			this.getColor().applyColor(stack);

		CompoundTag compound = this.saveLivingData();
		CompoundTag tag = stack.getOrCreateTag();
		tag.put("Slime", compound);
   		return stack;
   	}

   	protected ItemStack toBlock() {
   		ItemStack stack = new ItemStack(this.isDyed() ? TameableSlimesModBlocks.DYED_ALIVE_SLIME.get() : TameableSlimesModBlocks.ALIVE_SLIME.get());
   		if(stack.getItem() == TameableSlimesModBlocks.DYED_ALIVE_SLIME.get().asItem())
   			this.getColor().applyColor(stack);

		CompoundTag compound = this.saveLivingData();
		CompoundTag tag = stack.getOrCreateTag();
		tag.put("Slime", compound);
   		return stack;
   	}

   	protected ItemStack harvestBottle() {
   		ItemStack stack = new ItemStack(this.isDyed() ? TameableSlimesModItems.DYED_LIVING_SLIME_BOTTLE.get() : TameableSlimesModItems.LIVING_SLIME_BOTTLE.get());
   		if(stack.getItem() == TameableSlimesModItems.DYED_LIVING_SLIME_BOTTLE.get())
   			this.getColor().applyColor(stack);

   		return stack;
   	}

    @Override
    public float getSoundVolume() {
        return super.getSoundVolume();
    }

    @Override
    public boolean doPlayJumpSound() {
        return super.doPlayJumpSound();
    }

    @Override
    public int getJumpDelay() {
        return super.getJumpDelay();
    }

    public float getSoundPitch() {
        float f = this.isTiny() ? 1.4F : 0.8F;
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
    }

    @Override
    public boolean isDealsDamage() {
        return super.isDealsDamage();
    }

    @Override
    public SoundEvent getJumpSound() {
        return super.getJumpSound();
    }

    public boolean isTame() {
    	return this.entityData.get(Tamed);
    }

    public void setTame(boolean tamed, boolean sideAffect) {
        this.entityData.set(Tamed, tamed);
		if (sideAffect)
            this.applyTamingSideEffects();
    }

    protected void applyTamingSideEffects() {
    }

   	public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
    	if (target instanceof Creeper || target instanceof Ghast)
    		return false;
        if (target instanceof OwnableEntity ownable)
            return ownable.getOwner() != owner;
        if (target instanceof Player targetPlayer && owner instanceof Player ownerPlayer && !ownerPlayer.canHarmPlayer(targetPlayer))
            return false;
        return true;
   	}

   	public Team getTeam() {
      	if (this.isTame() && this.getOwner() != null)
         	return this.getOwner().getTeam();
      	return super.getTeam();
   	}

   	public boolean isAlliedTo(Entity entity) {
      	if (this.isTame() && this.getOwner() != null)
      		return (this.getOwner() == entity || this.getOwner().isAlliedTo(entity));
      	return super.isAlliedTo(entity);
   	}

    public boolean isOwnedBy(LivingEntity entity) {
      	return entity == this.getOwner();
   	}

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(Owner).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(Owner, Optional.ofNullable(uuid));
    }

    public void tame(Player owner) {
        this.setTame(true, true);
        this.setOwnerUUID(owner.getUUID());
    }

    public boolean isDyed() {
    	Color defaultColor = new Color(7586402);
    	Color currentColor = new Color(this.entityData.get(Color));
        return currentColor.getLong() != defaultColor.getLong();
    }

    public void randomiseColor() {
    	this.setColor(new Color(DyeColor.byId((int) Mth.randomBetween(this.random, 0, 15))));
    }

    public void setColor(Color color) {
        this.entityData.set(Color, color.getLong());
    }

    public Color getColor() {
        return new Color(this.entityData.get(Color));
    }

    public void setFollowing(boolean follow) {
    	this.followOwner = follow;
    	if(!follow)
    		this.stayPos = this.blockPosition();
    }

    public boolean isFollowing() {
    	return this.followOwner;
    }

    public BlockPos getStayPos() {
    	if(this.stayPos == null)
    		return this.blockPosition();
    	return this.stayPos;
    }

    public void setHarvestCD(int cooldown) {
    	this.entityData.set(HarvestCD, cooldown);
    }

    public int getHarvestCD() {
    	return this.entityData.get(HarvestCD);
    }

    protected void usePlayerItem(Player player, InteractionHand hand, ItemStack itemstack) {
    	if (!player.getAbilities().instabuild)
        	itemstack.shrink(1);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

        if (!this.level().isClientSide) {
            if (this.isTame()) {
                if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    this.heal((float)itemstack.getFoodProperties(this).getNutrition());
            		this.usePlayerItem(player, hand, itemstack);
            		this.gameEvent(GameEvent.EAT, this);
            		return InteractionResult.SUCCESS;
                } else {
                    if (item instanceof DyeItem dyeitem && this.isOwnedBy(player)) {
                        Color color = new Color(dyeitem);
                        if (color.getLong() != this.getColor().getLong()) {
							if(this.isDyed() && this.level().getLevelData().getGameRules().getBoolean(SlimeRules.MERGE_COLORS))
								color = this.getColor().mix(color);
                            this.setColor(color);
                            this.usePlayerItem(player, hand, itemstack);
                            return InteractionResult.SUCCESS;
                        }
						
                        return super.mobInteract(player, hand);
                    }
                    
                    if (item == Items.GLASS_BOTTLE && this.getHarvestCD() <= 0) {
                    	ItemStack slimeBottle = this.harvestBottle();
                        if(itemstack.getCount() == 1)
                        	player.setItemInHand(hand, slimeBottle);
                        if(itemstack.getCount() > 1)
                        	ItemUtils.createFilledResult(itemstack, player, slimeBottle);

						this.level().playSound(null, this.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
						this.setHarvestCD(6000);
                        return InteractionResult.SUCCESS;
                    }
                    
                    if (this.isDyed() && item == Items.POTION && PotionUtils.getPotion(itemstack) == Potions.WATER) {
                    	this.setColor(new Color(7586402));
                    	player.setItemInHand(hand, ItemUtils.createFilledResult(itemstack, player, new ItemStack(Items.GLASS_BOTTLE)));
						this.level().playSound(null, this.blockPosition(), SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }

                    
                    InteractionResult interactionresult = super.mobInteract(player, hand);
            		if ((!interactionresult.consumesAction() || this.isBaby()) && this.isOwnedBy(player)) {
               			this.setFollowing(!this.isFollowing());
               			return InteractionResult.SUCCESS;
					}
					
                    return interactionresult;
                }
            } else if (this.isFood(itemstack) && this.getTarget() == null) {
                this.usePlayerItem(player, hand, itemstack);
                this.tryToTame(player);
                return InteractionResult.SUCCESS;
            }
			
            return super.mobInteract(player, hand);
        }

        boolean flag = this.isOwnedBy(player) || this.isTame() || this.isFood(itemstack) && !this.isTame() && this.getTarget() == null;
        return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

	@Override
    public void handleEntityEvent(byte Event) {
      	if (Event == 7) {
         	this.spawnTamingParticles(true);
      	} else if (Event == 6) {
         	this.spawnTamingParticles(false);
      	} else {
         	super.handleEntityEvent(Event);
      	}

   	}

   	protected void spawnTamingParticles(boolean success) {
      	ParticleOptions particleoptions = success ? ParticleTypes.HEART : ParticleTypes.SMOKE;
      	for(int i = 0; i < 7; ++i) {
         	double d0 = this.random.nextGaussian() * 0.02D;
         	double d1 = this.random.nextGaussian() * 0.02D;
         	double d2 = this.random.nextGaussian() * 0.02D;
         	this.level().addParticle(particleoptions, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
      	}
   	}

    private void tryToTame(Player player) {
        if (this.random.nextInt(3) == 0) {
            this.tame(player);;
            this.level().broadcastEntityEvent(this, (byte)7);
        } else {
            this.level().broadcastEntityEvent(this, (byte)6);
        }
    }
    
    public boolean isFood(ItemStack itemstack) {
        return itemstack.is(ItemTags.create(new ResourceLocation("tameable_slimes:food")));
    }

    public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes();
	}
}
