package com.bioxx.tfc.Blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import com.bioxx.tfc.Reference;
import com.bioxx.tfc.TFCItems;
import com.bioxx.tfc.TerraFirmaCraft;
import com.bioxx.tfc.Core.TFC_Time;
import com.bioxx.tfc.TileEntities.TELogPile;
import com.bioxx.tfc.api.TFCOptions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLogPile extends BlockTerraContainer
{
	IIcon[] icons = new IIcon[3];

	public BlockLogPile()
	{
		super(Material.wood);
		this.setTickRandomly(true);
	}

	public static int getDirectionFromMetadata(int i)
	{
		return i & 3;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
	{
		if (world.isRemote)
		{
			return true;
		}
		else
		{
			if((TELogPile)world.getTileEntity(i, j, k)!=null)
			{
				TELogPile te;
				te = (TELogPile)world.getTileEntity(i, j, k);
				ItemStack is = entityplayer.getCurrentEquippedItem();

				if(is != null && is.getItem() == TFCItems.Logs)
				{
					return false;
				}
				else
				{
					entityplayer.openGui(TerraFirmaCraft.instance, 0, world, i, j, k);
				}
				return true;
			} else {
				return false;
			}

		}

	}

	@Override
	public IIcon getIcon(int i, int j)
	{
		if(j == 0 || j == 2)//+z
		{
			if(i == 0) {
				return icons[1];
			} else if(i == 1) {
				return icons[1];
			} else if(i == 2) {
				return icons[2];
			} else if(i == 3) {
				return icons[2];
			} else if(i == 4) {
				return icons[0];
			} else if(i == 5) {
				return icons[0];
			}
		}
		else if(j == 1 || j == 3)//-x
		{
			if(i == 0) {
				return icons[0];
			} else if(i == 1) {
				return icons[0];
			} else if(i == 2) {
				return icons[0];
			} else if(i == 3) {
				return icons[0];
			} else if(i == 4) {
				return icons[2];
			} else if(i == 5) {
				return icons[2];
			}
		}

		return icons[0];

	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegisterer)
	{
		icons[0] = iconRegisterer.registerIcon(Reference.ModID + ":" + "devices/Log Pile Side 0");
		icons[1] = iconRegisterer.registerIcon(Reference.ModID + ":" + "devices/Log Pile Side 1");
		icons[2] = iconRegisterer.registerIcon(Reference.ModID + ":" + "devices/Log Pile End");
	}

	public void Eject(World world, int x, int y, int z)
	{
		if(!world.isRemote && (TELogPile)world.getTileEntity(x, y, z) != null)
		{
			TELogPile TELogPile;
			TELogPile = (TELogPile)world.getTileEntity(x, y, z);
			TELogPile.ejectContents();
			world.removeTileEntity(x, y, z);
		}
	}

	@Override
	public Item getItemDropped(int par1, Random random, int par3)
	{
		return Item.getItemById(0);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer entityplayer, int i, int j, int k, int l)
	{
		Eject(world, i, j, k);
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion ex)
	{
		Eject(world, x, y, z);
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int i)
	{
		Eject(world, x, y, z);
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z)
	{
		Eject(world, x, y, z);
		return super.removedByPlayer(world, player, x, y, z);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TELogPile();
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		if(!world.isRemote)
		{
			TELogPile teLogPile = (TELogPile)world.getTileEntity(x, y, z);
			if(teLogPile != null)
				teLogPile.neighborChanged();
		}
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		TELogPile te = (TELogPile)world.getTileEntity(x, y, z);
		if(te.isOnFire && te.fireTimer+TFCOptions.charcoalPitBurnTime < TFC_Time.getTotalHours())
		{
			te.createCharcoal(x, y, z);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand)
	{
		if(((TELogPile)world.getTileEntity(x, y, z)).isOnFire)
		{
			double centerX = (double)((float)x + 0.5F);
			double centerY = (double)((float)y + 2F);
			double centerZ = (double)((float)z + 0.5F);
			double d3 = 0.2199999988079071D;
			double d4 = 0.27000001072883606D;
			world.spawnParticle("smoke", centerX+(rand.nextDouble()-0.5), centerY, centerZ+(rand.nextDouble()-0.5), 0.0D, 0.1D, 0.0D);
			world.spawnParticle("smoke", centerX+(rand.nextDouble()-0.5), centerY, centerZ+(rand.nextDouble()-0.5), 0.0D, 0.15D, 0.0D);
			world.spawnParticle("smoke", centerX+(rand.nextDouble()-0.5), centerY-1, centerZ+(rand.nextDouble()-0.5), 0.0D, 0.1D, 0.0D);
			world.spawnParticle("smoke", centerX+(rand.nextDouble()-0.5), centerY-1, centerZ+(rand.nextDouble()-0.5), 0.0D, 0.15D, 0.0D);
		}
	}
}
