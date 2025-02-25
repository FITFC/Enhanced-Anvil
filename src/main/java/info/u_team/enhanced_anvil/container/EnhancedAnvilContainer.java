package info.u_team.enhanced_anvil.container;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import info.u_team.enhanced_anvil.block.EnhancedAnvilBlock;
import info.u_team.enhanced_anvil.init.EnhancedAnvilContainerTypes;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants.WorldEvents;

public class EnhancedAnvilContainer extends RepairContainer {
	
	// Client
	public EnhancedAnvilContainer(int id, PlayerInventory playerInventory, PacketBuffer buffer) {
		this(id, playerInventory, IWorldPosCallable.DUMMY);
	}
	
	// Server
	public EnhancedAnvilContainer(int id, PlayerInventory playerInventory, IWorldPosCallable posCallable) {
		super(id, playerInventory, posCallable);
		containerType = EnhancedAnvilContainerTypes.ENHANCED_ANVIL.get();
	}
	
	@Override
	protected ItemStack func_230301_a_(PlayerEntity player, ItemStack output) {
		if (!player.abilities.isCreativeMode) {
			player.addExperienceLevel(-maximumCost.get());
		}
		
		final float breakChance = ForgeHooks.onAnvilRepair(player, output, field_234643_d_.getStackInSlot(0), field_234643_d_.getStackInSlot(1));
		
		field_234643_d_.setInventorySlotContents(0, ItemStack.EMPTY);
		if (materialCost > 0) {
			final ItemStack itemstack = this.field_234643_d_.getStackInSlot(1);
			if (!itemstack.isEmpty() && itemstack.getCount() > this.materialCost) {
				itemstack.shrink(this.materialCost);
				field_234643_d_.setInventorySlotContents(1, itemstack);
			} else {
				field_234643_d_.setInventorySlotContents(1, ItemStack.EMPTY);
			}
		} else {
			field_234643_d_.setInventorySlotContents(1, ItemStack.EMPTY);
		}
		
		maximumCost.set(0);
		field_234644_e_.consume((world, pos) -> {
			final BlockState oldState = world.getBlockState(pos);
			if (!player.abilities.isCreativeMode && oldState.isIn(BlockTags.ANVIL) && player.getRNG().nextFloat() < breakChance) {
				if (oldState.getBlock() instanceof EnhancedAnvilBlock) {
					final BlockState newState = (((EnhancedAnvilBlock) oldState.getBlock()).damageAnvil(oldState));
					if (newState == null) {
						world.removeBlock(pos, false);
						world.playEvent(WorldEvents.ANVIL_DESTROYED_SOUND, pos, 0);
					} else {
						world.setBlockState(pos, newState, 2);
						world.playEvent(WorldEvents.ANVIL_USE_SOUND, pos, 0);
					}
				}
			} else {
				world.playEvent(1030, pos, 0);
			}
			
		});
		return output;
	}
	
	@Override
	public void updateRepairOutput() {
		final ItemStack itemstack = this.field_234643_d_.getStackInSlot(0);
		this.maximumCost.set(1);
		int i = 0;
		int j = 0;
		int k = 0;
		if (itemstack.isEmpty()) {
			this.field_234642_c_.setInventorySlotContents(0, ItemStack.EMPTY);
			this.maximumCost.set(0);
		} else {
			ItemStack itemstack1 = itemstack.copy();
			final ItemStack itemstack2 = this.field_234643_d_.getStackInSlot(1);
			final Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
			j = j + itemstack.getRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getRepairCost());
			this.materialCost = 0;
			boolean flag = false;
			
			if (!itemstack2.isEmpty()) {
				if (!net.minecraftforge.common.ForgeHooks.onAnvilChange(this, itemstack, itemstack2, field_234642_c_, repairedItemName, j, this.field_234645_f_))
					return;
				flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(itemstack2).isEmpty();
				if (itemstack1.isDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2)) {
					int l2 = Math.min(itemstack1.getDamage(), itemstack1.getMaxDamage() / 4);
					if (l2 <= 0) {
						this.field_234642_c_.setInventorySlotContents(0, ItemStack.EMPTY);
						this.maximumCost.set(0);
						return;
					}
					
					int i3;
					for (i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
						final int j3 = itemstack1.getDamage() - l2;
						itemstack1.setDamage(j3);
						++i;
						l2 = Math.min(itemstack1.getDamage(), itemstack1.getMaxDamage() / 4);
					}
					
					this.materialCost = i3;
				} else {
					if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isDamageable())) {
						this.field_234642_c_.setInventorySlotContents(0, ItemStack.EMPTY);
						this.maximumCost.set(0);
						return;
					}
					
					if (itemstack1.isDamageable() && !flag) {
						final int l = itemstack.getMaxDamage() - itemstack.getDamage();
						final int i1 = itemstack2.getMaxDamage() - itemstack2.getDamage();
						final int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
						final int k1 = l + j1;
						int l1 = itemstack1.getMaxDamage() - k1;
						if (l1 < 0) {
							l1 = 0;
						}
						
						if (l1 < itemstack1.getDamage()) {
							itemstack1.setDamage(l1);
							i += 2;
						}
					}
					
					final Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
					boolean flag2 = false;
					boolean flag3 = false;
					
					for (final Enchantment enchantment1 : map1.keySet()) {
						if (enchantment1 != null) {
							final int i2 = map.getOrDefault(enchantment1, 0);
							int j2 = map1.get(enchantment1);
							j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
							boolean flag1 = enchantment1.canApply(itemstack);
							if (this.field_234645_f_.abilities.isCreativeMode || itemstack.getItem() == Items.ENCHANTED_BOOK) {
								flag1 = true;
							}
							
							for (final Enchantment enchantment : map.keySet()) {
								if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
									flag1 = false;
									++i;
								}
							}
							
							if (!flag1) {
								flag3 = true;
							} else {
								flag2 = true;
								if (j2 > enchantment1.getMaxLevel()) {
									j2 = enchantment1.getMaxLevel();
								}
								
								map.put(enchantment1, j2);
								int k3 = 0;
								switch (enchantment1.getRarity()) {
								case COMMON:
									k3 = 1;
									break;
								case UNCOMMON:
									k3 = 2;
									break;
								case RARE:
									k3 = 4;
									break;
								case VERY_RARE:
									k3 = 8;
								}
								
								if (flag) {
									k3 = Math.max(1, k3 / 2);
								}
								
								i += k3 * j2;
								if (itemstack.getCount() > 1) {
									i = 40;
								}
							}
						}
					}
					
					if (flag3 && !flag2) {
						this.field_234642_c_.setInventorySlotContents(0, ItemStack.EMPTY);
						this.maximumCost.set(0);
						return;
					}
				}
			}
			
			if (StringUtils.isBlank(this.repairedItemName)) {
				if (itemstack.hasDisplayName()) {
					k = 1;
					i += k;
					itemstack1.clearCustomName();
				}
			} else if (!this.repairedItemName.equals(itemstack.getDisplayName().getString())) {
				k = 1;
				i += k;
				itemstack1.setDisplayName(new StringTextComponent(this.repairedItemName));
			}
			if (flag && !itemstack1.isBookEnchantable(itemstack2))
				itemstack1 = ItemStack.EMPTY;
			
			this.maximumCost.set(j + i);
			if (i <= 0) {
				itemstack1 = ItemStack.EMPTY;
			}
			
			if (k == i && k > 0 && this.maximumCost.get() >= 40) {
				this.maximumCost.set(39);
			}
			
			if (!itemstack1.isEmpty()) {
				int k2 = itemstack1.getRepairCost();
				if (!itemstack2.isEmpty() && k2 < itemstack2.getRepairCost()) {
					k2 = itemstack2.getRepairCost();
				}
				
				if (k != i || k == 0) {
					k2 = getNewRepairCost(k2);
				}
				
				itemstack1.setRepairCost(k2);
				EnchantmentHelper.setEnchantments(map, itemstack1);
			}
			
			this.field_234642_c_.setInventorySlotContents(0, itemstack1);
			this.detectAndSendChanges();
		}
	}
}
