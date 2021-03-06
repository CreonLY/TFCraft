package com.bioxx.tfc.Food;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Core.Player.FoodStatsTFC;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.Render.Item.FoodItemRenderer;
import com.bioxx.tfc.api.FoodRegistry;
import com.bioxx.tfc.api.TFC_ItemHeat;
import com.bioxx.tfc.api.Enums.EnumFoodGroup;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;
import com.bioxx.tfc.api.Interfaces.IFood;
import com.bioxx.tfc.api.Util.Helper;

public class ItemMeal extends ItemTerra implements IFood
{
	PotionEffect foodEffect;
	private boolean alwaysEdible = false;
	private String[] tasteArray = {"item.meal.terrible", "item.meal.poor", "item.meal.decent", "item.meal.good","item.meal.great","item.meal.fantastic"};

	public ItemMeal()
	{
		super();
		this.hasSubtypes = true;
		this.MetaNames = new String[]{"Meal0","Meal1","Meal2","Meal3","Meal4","Meal5","Meal6","Meal7","Meal8","Meal9","Meal10",};
		this.MetaIcons = new IIcon[11];
		this.setFolder("food/");
		this.stackable = false;
	}

	@Override
	public void registerIcons(IIconRegister registerer)
	{
		super.registerIcons(registerer);
		MinecraftForgeClient.registerItemRenderer(this, new FoodItemRenderer());
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tabs, List list)
	{
		// Removes meals from creative tab because without NBT data, they are useless
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return this.getUnlocalizedName();
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		ItemTerra.addSizeInformation(is, arraylist);
		if(!TFC_Core.showShiftInformation())
		{
			arraylist.add("");
			//arraylist.add("");
		}
		ItemFoodTFC.addHeatInformation(is, arraylist);

		if (is.hasTagCompound())
		{
			NBTTagCompound nbt = is.getTagCompound();

			if(TFC_Core.showShiftInformation())
			{
				addFGInformation(is, arraylist);
			}
			/*if(nbt.hasKey("satisfaction"))
			{
				float _sat = Helper.roundNumber(nbt.getFloat("satisfaction"),100);
				if(!isWarm(is))
					_sat *= 0.25f;
				int satIndex = Math.min(1+(int)(5 *_sat), 5);
				arraylist.add("Taste: " + StatCollector.translateToLocal(tasteArray[satIndex]) + EnumChatFormatting.DARK_GRAY + " (" + (_sat * 100) + "%)");
			}
			else
			{
				arraylist.add("Taste: " + StatCollector.translateToLocal(tasteArray[0]) + EnumChatFormatting.DARK_GRAY + " (0.0%)");
			}*/

			if(nbt.hasKey("foodWeight"))
			{
				float ounces = nbt.getFloat("foodWeight");
				if(ounces > 0)
					arraylist.add("Amount " + Helper.roundNumber(ounces, 10) + "oz");
				float decay = nbt.getFloat("foodDecay");
				if(decay > 0)
					arraylist.add(EnumChatFormatting.DARK_GRAY + "Decay " + Helper.roundNumber(decay / ounces * 100, 10) + "%");
			}
			else
			{
				arraylist.add(StatCollector.translateToLocal("gui.badnbt"));
			}

			if (TFC_Core.showCtrlInformation()) 
				ItemFoodTFC.addTasteInformation(is, player, arraylist);
			else
				arraylist.add(StatCollector.translateToLocal("gui.showtaste"));
		}
		else
		{
			arraylist.add(StatCollector.translateToLocal("gui.badnbt"));
		}
	}

	protected void addFGInformation(ItemStack is, List arraylist)
	{
		if (is.hasTagCompound())
		{
			NBTTagCompound nbt = is.getTagCompound();
			if(nbt.hasKey("FG"))
			{
				int[] fg = nbt.getIntArray("FG");
				for(int i = 0; i < fg.length; i++)
				{
					if(fg[i] != -1)
						arraylist.add(localize(fg[i]));
				}
			}
		}
	}

	protected String localize(int id)
	{
		return ItemFoodTFC.getFoodGroupColor(FoodRegistry.getInstance().getFoodGroup(id)) + 
				StatCollector.translateToLocal(FoodRegistry.getInstance().getFood(id).getUnlocalizedName() + ".name");
	}

	protected float[] getNutritionalWeights()
	{
		return new float[]{0.5f,0.2f,0.2f,0.1f};
	}

	/**
	 * @param fs
	 * @param amount This should be the amount that is actually consumed aka (weight - decay)
	 * @return The exact amount that should enter the stomach
	 */
	protected float getEatAmount(FoodStatsTFC fs, float amount)
	{
		float eatAmount = Math.min(amount, 5);
		float stomachDiff = fs.stomachLevel+eatAmount-fs.getMaxStomach(fs.player);
		if(stomachDiff > 0)
			eatAmount-=stomachDiff;
		return eatAmount;
	}

	protected float getFillingBoost()
	{
		return 1.0f;
	}

	@Override
	public ItemStack onEaten(ItemStack is, World world, EntityPlayer player)
	{
		world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);

		FoodStatsTFC foodstats = TFC_Core.getPlayerFoodStats(player);
		if(!world.isRemote)
		{
			ItemMeal item = (ItemMeal) is.getItem();
			float weight = item.getFoodWeight(is);
			float decay = Math.max(item.getFoodDecay(is), 0);
			float eatAmount = getEatAmount(foodstats, weight-decay);
			float tasteFactor = foodstats.getTasteFactor(is);
			//add the nutrition contents
			int[] fg = is.getTagCompound().getIntArray("FG");
			float[] weights = getNutritionalWeights();
			for(int i = 0; i < fg.length; i++)
			{
				if(fg[i] != -1)
					foodstats.addNutrition(FoodRegistry.getInstance().getFoodGroup(fg[i]), eatAmount*weights[i]*tasteFactor);
			}

			//fill the stomach
			foodstats.stomachLevel += eatAmount * getFillingBoost();
			foodstats.setSatisfaction(foodstats.getSatisfaction() + eatAmount * tasteFactor);
			//Now remove the eaten amount from the itemstack.
			if(FoodStatsTFC.reduceFood(is, eatAmount))
			{
				is.stackSize = 0;
			}
		}
		TFC_Core.setPlayerFoodStats(player, foodstats);
		return is;
	}

	public static boolean isWarm(ItemStack is)
	{
		if(TFC_ItemHeat.GetTemp(is) > TFC_ItemHeat.IsCookable(is) * 0.1)
			return true;
		else
			return false;
	}

	@Override
	public float getFoodWeight(ItemStack is)
	{
		if(is.hasTagCompound() && is.getTagCompound().hasKey("foodWeight"))
		{
			NBTTagCompound nbt = is.getTagCompound();
			return nbt.getFloat("foodWeight");
		}
		return 0f;
	}

	@Override
	public float getFoodDecay(ItemStack is)
	{
		if(is.hasTagCompound() && is.getTagCompound().hasKey("foodDecay"))
		{
			NBTTagCompound nbt = is.getTagCompound();
			return nbt.getFloat("foodDecay");
		}
		return 0f;
	}

	public float getSatisfaction(ItemStack is) 
	{
		if(is.hasTagCompound() && is.getTagCompound().hasKey("satisfaction"))
		{
			NBTTagCompound nbt = is.getTagCompound();
			return nbt.getFloat("satisfaction");
		}
		return 0f;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 32;
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.eat;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player)
	{
		FoodStatsTFC foodstats = TFC_Core.getPlayerFoodStats(player);

		//The player needs to be able to fit the food into his stomach
		if(foodstats.needFood())
			player.setItemInUse(is, this.getMaxItemUseDuration(is));

		return is;
	}

	@Override
	public EnumSize getSize(ItemStack is)
	{
		return EnumSize.SMALL;
	}

	@Override
	public EnumWeight getWeight(ItemStack is)
	{
		return EnumWeight.MEDIUM;
	}

	@Override
	public EnumFoodGroup getFoodGroup()
	{
		return null;
	}

	@Override
	public int getFoodID()
	{
		return 0;
	}

	@Override
	public float getDecayRate(ItemStack is)
	{
		return 0;
	}

	@Override
	public ItemStack onDecayed(ItemStack is, World world, int i, int j, int k)
	{
		return null;
	}

	@Override
	public boolean isEdible(ItemStack is)
	{
		return false;
	}

	@Override
	public boolean isUsable(ItemStack is)
	{
		return false;
	}

	@Override
	public int getTasteSweet(ItemStack is) {
		int base = 0;
		if(is != null && is.getTagCompound().hasKey("tasteSweet"))
			base = is.getTagCompound().getInteger("tasteSweet");
		return base + getTasteSweetMod(is);
	}

	public int getTasteSweetMod(ItemStack is) {
		int mod = 0;
		if(is != null && is.getTagCompound().hasKey("tasteSweetMod"))
			mod = is.getTagCompound().getInteger("tasteSweetMod");
		return mod;
	}

	@Override
	public int getTasteSour(ItemStack is) {
		int base = 0;
		if(is != null && is.getTagCompound().hasKey("tasteSour"))
			base = is.getTagCompound().getInteger("tasteSour");
		return base + getTasteSourMod(is);
	}

	public int getTasteSourMod(ItemStack is) {
		int mod = 0;
		if(is != null && is.getTagCompound().hasKey("tasteSourMod"))
			mod = is.getTagCompound().getInteger("tasteSourMod");
		return mod;
	}

	@Override
	public int getTasteSalty(ItemStack is) {
		int base = 0;
		if(is != null && is.getTagCompound().hasKey("tasteSalty"))
			base = is.getTagCompound().getInteger("tasteSalty");;
			return base + getTasteSaltyMod(is);
	}

	public int getTasteSaltyMod(ItemStack is) {
		int mod = 0;
		if(is != null && is.getTagCompound().hasKey("tasteSaltyMod"))
			mod = is.getTagCompound().getInteger("tasteSaltyMod");
		return mod;
	}

	@Override
	public int getTasteBitter(ItemStack is) {
		int base = 0;
		if(is != null && is.getTagCompound().hasKey("tasteBitter"))
			base = is.getTagCompound().getInteger("tasteBitter");
		return base + getTasteBitterMod(is);
	}

	public int getTasteBitterMod(ItemStack is) {
		int mod = 0;
		if(is != null && is.getTagCompound().hasKey("tasteBitterMod"))
			mod = is.getTagCompound().getInteger("tasteBitterMod");
		return mod;
	}

	@Override
	public int getTasteSavory(ItemStack is) {
		int base = 0;
		if(is != null && is.getTagCompound().hasKey("tasteUmami"))
			base = is.getTagCompound().getInteger("tasteUmami");
		return base + getTasteSavoryMod(is);
	}

	public int getTasteSavoryMod(ItemStack is) {
		int mod = 0;
		if(is != null && is.getTagCompound().hasKey("tasteUmamiMod"))
			mod = is.getTagCompound().getInteger("tasteUmamiMod");
		return mod;
	}

	@Override
	public float getFoodMaxWeight(ItemStack is) {
		return 20;
	}

	@Override
	public boolean renderDecay() {
		return true;
	}

	@Override
	public boolean renderWeight() {
		return true;
	}
}
