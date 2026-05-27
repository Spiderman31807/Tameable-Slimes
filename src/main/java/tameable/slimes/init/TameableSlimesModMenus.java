
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package tameable.slimes.init;

import tameable.slimes.world.inventory.MixerMenu;
import tameable.slimes.TameableSlimesMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

public class TameableSlimesModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, TameableSlimesMod.MODID);
	public static final RegistryObject<MenuType<MixerMenu>> MIXER = REGISTRY.register("mixer", () -> IForgeMenuType.create(MixerMenu::new));
}
