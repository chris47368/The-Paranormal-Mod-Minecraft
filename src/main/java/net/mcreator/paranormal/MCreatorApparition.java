package net.mcreator.paranormal;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.block.state.IBlockState;

import java.util.Random;
import java.util.Iterator;
import java.util.ArrayList;

@Elementsparanormal.ModElement.Tag
public class MCreatorApparition extends Elementsparanormal.ModElement {
	public static final int ENTITYID = 1;
	public static final int ENTITYID_RANGED = 2;

	public MCreatorApparition(Elementsparanormal instance) {
		super(instance, 4);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("paranormal", "apparition"), ENTITYID).name("apparition").tracker(64, 1, true).build());
	}

	private Biome[] allbiomes(net.minecraft.util.registry.RegistryNamespaced<ResourceLocation, Biome> in) {
		Iterator<Biome> itr = in.iterator();
		ArrayList<Biome> ls = new ArrayList<Biome>();
		while (itr.hasNext())
			ls.add(itr.next());
		return ls.toArray(new Biome[ls.size()]);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
			RenderBiped customRender = new RenderBiped(renderManager, new ModelZombie(), 0.5f) {
				protected ResourceLocation getEntityTexture(Entity entity) {
					return new ResourceLocation("paranormal:textures/steve.png");
				}
			};
			customRender.addLayer(new net.minecraft.client.renderer.entity.layers.LayerBipedArmor(customRender) {
				protected void initArmor() {
					this.modelLeggings = new ModelZombie(0.5F, true);
					this.modelArmor = new ModelZombie(1.0F, true);
				}
			});
			return customRender;
		});
	}

	public static class EntityCustom extends EntityMob {
		public EntityCustom(World world) {
			super(world);
			setSize(0.6f, 1.95f);
			experienceValue = 5;
			this.isImmuneToFire = true;
			setNoAI(!true);
			this.tasks.addTask(1, new EntityAIBase() {
				{
					this.setMutexBits(1);
				}

				@Override
				public boolean shouldExecute() {
					EntityMoveHelper entitymovehelper = EntityCustom.this.getMoveHelper();
					if (!entitymovehelper.isUpdating()) {
						return true;
					} else {
						double dx = entitymovehelper.getX() - EntityCustom.this.posX;
						double dy = entitymovehelper.getY() - EntityCustom.this.posY;
						double dz = entitymovehelper.getZ() - EntityCustom.this.posZ;
						double d = dx * dx + dy * dy + dz * dz;
						return d < 1 || d > 3600;
					}
				}

				@Override
				public boolean shouldContinueExecuting() {
					return false;
				}

				@Override
				public void startExecuting() {
					Random random = EntityCustom.this.getRNG();
					double dir_x = EntityCustom.this.posX + ((random.nextFloat() * 2 - 1) * 16);
					double dir_y = EntityCustom.this.posY + ((random.nextFloat() * 2 - 1) * 16);
					double dir_z = EntityCustom.this.posZ + ((random.nextFloat() * 2 - 1) * 16);
					EntityCustom.this.getMoveHelper().setMoveTo(dir_x, dir_y, dir_z, 0.8);
				}
			});
			this.moveHelper = new EntityMoveHelper(this) {
				private int patchChangeTimer;

				@Override
				public void onUpdateMoveHelper() {
					if (this.action == EntityMoveHelper.Action.MOVE_TO) {
						double dx = this.posX - EntityCustom.this.posX;
						double dy = this.posY - EntityCustom.this.posY;
						double dz = this.posZ - EntityCustom.this.posZ;
						double d = dx * dx + dy * dy + dz * dz;
						if (this.patchChangeTimer-- <= 0) {
							this.patchChangeTimer += EntityCustom.this.getRNG().nextInt(5) + 2;
							d = (double) MathHelper.sqrt(d);
							if (this.isNotColliding(this.posX, this.posY, this.posZ, d)) {
								EntityCustom.this.motionX += dx / d * 0.1;
								EntityCustom.this.motionY += dy / d * 0.1;
								EntityCustom.this.motionZ += dz / d * 0.1;
							} else {
								this.action = EntityMoveHelper.Action.WAIT;
							}
						}
					}
				}

				private boolean isNotColliding(double x, double y, double z, double par) {
					double dx = (x - EntityCustom.this.posX) / par;
					double dy = (y - EntityCustom.this.posY) / par;
					double dz = (z - EntityCustom.this.posZ) / par;
					AxisAlignedBB axisalignedbb = EntityCustom.this.getEntityBoundingBox();
					for (int i = 1; (double) i < par; ++i) {
						axisalignedbb = axisalignedbb.offset(dx, dy, dz);
						if (!EntityCustom.this.world.getCollisionBoxes(EntityCustom.this, axisalignedbb).isEmpty())
							return false;
					}
					return true;
				}
			};
			this.tasks.addTask(2, new EntityAILookIdle(this));
			this.tasks.addTask(3, new EntityAIPanic(this, 1.2));
		}

		@Override
		public EnumCreatureAttribute getCreatureAttribute() {
			return EnumCreatureAttribute.UNDEAD;
		}

		@Override
		protected Item getDropItem() {
			return null;
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		protected float getSoundVolume() {
			return 1.0F;
		}

		@Override
		public void fall(float l, float d) {
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source == DamageSource.DROWN)
				return false;
			return super.attackEntityFrom(source, amount);
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			if (this.getEntityAttribute(SharedMonsterAttributes.ARMOR) != null)
				this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0D);
			if (this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null)
				this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
			if (this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH) != null)
				this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10D);
			if (this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null)
				this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3D);
		}

		@Override
		public void travel(float ti, float tj, float tk) {
			if (this.isInWater()) {
				this.moveRelative(ti, tj, tk, 0.02f);
				this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
				this.motionX *= 0.8;
				this.motionY *= 0.8;
				this.motionZ *= 0.8;
				return;
			}
			if (this.isInLava()) {
				this.moveRelative(ti, tj, tk, 0.02f);
				this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
				this.motionX *= 0.5;
				this.motionY *= 0.5;
				this.motionZ *= 0.5;
				return;
			}
			float f = 0.91F;
			if (this.onGround) {
				BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1,
						MathHelper.floor(this.posZ));
				IBlockState underState = this.world.getBlockState(underPos);
				f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.91F;
			}
			this.moveRelative(ti, tj, tk, this.onGround ? 0.1f * 0.16f / (f * f * f) : 0.02F);
			f = 0.91F;
			if (this.onGround) {
				BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1,
						MathHelper.floor(this.posZ));
				IBlockState underState = this.world.getBlockState(underPos);
				f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.91F;
			}
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double) f;
			this.motionY *= (double) f;
			this.motionZ *= (double) f;
			this.prevLimbSwingAmount = this.limbSwingAmount;
			double d1 = this.posX - this.prevPosX;
			double d0 = this.posZ - this.prevPosZ;
			float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
			if (f2 > 1)
				f2 = 1;
			this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
			this.limbSwing += this.limbSwingAmount;
		}

		@Override
		protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
		}
	}
}
