package net.mcreator.paranormal;

import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Random;
import java.util.HashMap;

@Elementsparanormal.ModElement.Tag
public class MCreatorHolyCrossRightClickedInAir extends Elementsparanormal.ModElement {
	public MCreatorHolyCrossRightClickedInAir(Elementsparanormal instance) {
		super(instance, 6);
	}

	public static void executeProcedure(java.util.HashMap<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure MCreatorHolyCrossRightClickedInAir!");
			return;
		}
		if (dependencies.get("itemstack") == null) {
			System.err.println("Failed to load dependency itemstack for procedure MCreatorHolyCrossRightClickedInAir!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		ItemStack itemstack = (ItemStack) dependencies.get("itemstack");
		if (((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).capabilities.isCreativeMode : false)) {
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int) 1000, (int) 1, (false), (false)));
		} else {
			if ((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).experienceLevel : 0) >= 1)) {
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).addExperienceLevel(-((int) 1));
				if (itemstack.attemptDamageItem((int) 1, new Random(), null)) {
					itemstack.shrink(1);
					itemstack.setItemDamage(0);
				}
				if (entity instanceof EntityLivingBase)
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int) 1000, (int) 1, (false), (false)));
			}
		}
	}
}
