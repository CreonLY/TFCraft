package com.bioxx.tfc.Food;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import com.bioxx.tfc.Core.Player.FoodStatsTFC;

public class ItemSandwich extends ItemMeal
{

	public ItemSandwich()
	{
		super();
		this.hasSubtypes = true;
		this.MetaNames = new String[]{"Sandwich Wheat","Sandwich Oat","Sandwich Barley","Sandwich Rye","Sandwich Corn","Sandwich Rice"};
		this.MetaIcons = new IIcon[6];
		this.setFolder("food/");
	}

	@Override
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
					if(i == 5 && fg[5] == fg[0])
						return;
					if(fg[i] != -1)
						arraylist.add(localize(fg[i]));
				}
			}
		}
	}

	@Override
	protected float getEatAmount(FoodStatsTFC fs, float amount)
	{
		float eatAmount = Math.min(amount, 9);
		float stomachDiff = fs.stomachLevel+eatAmount-fs.getMaxStomach(fs.player);
		if(stomachDiff > 0)
			eatAmount-=stomachDiff;
		return eatAmount;
	}

	@Override
	protected float getFillingBoost()
	{
		return 1.25f;
	}

	@Override
	protected float[] getNutritionalWeights()
	{
		//These numbers are 5% of the oz value for each slot 1/3/2/1/1/1
		return new float[]{0.05f,0.15f,0.1f,0.05f,0.05f,0.05f};
	}

	@Override
	public float getFoodMaxWeight(ItemStack is) {
		return 9;
	}

	@Override
	public boolean renderDecay() {
		return true;
	}

	@Override
	public boolean renderWeight() {
		return false;
	}
}
