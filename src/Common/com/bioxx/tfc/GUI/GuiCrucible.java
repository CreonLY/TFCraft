package com.bioxx.tfc.GUI;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc.Reference;
import com.bioxx.tfc.Containers.ContainerCrucible;
import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Core.Player.PlayerInventory;
import com.bioxx.tfc.TileEntities.TECrucible;
import com.bioxx.tfc.api.TFCOptions;


public class GuiCrucible extends GuiContainer
{
	private TECrucible te;


	public GuiCrucible(InventoryPlayer inventoryplayer, TECrucible tileEntity, World world, int x, int y, int z)
	{
		super(new ContainerCrucible(inventoryplayer,tileEntity, world, x, y, z) );
		te = tileEntity;
		this.xSize = 176;
		this.ySize = 113+PlayerInventory.invYSize;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		TFC_Core.bindTexture(new ResourceLocation(Reference.ModID, Reference.AssetPathGui + "gui_crucible.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
		int w = (width - xSize) / 2;
		int h = (height - ySize) / 2;
		drawTexturedModalRect(w, h, 0, 0, xSize, ySize);

		int scale = 0;

		scale = te.getTemperatureScaled(49);
		drawTexturedModalRect(w + 153, h + 80 - scale, 185, 0, 15, 6);

		scale = te.getOutCountScaled(100);
		drawTexturedModalRect(w + 129, h + 106 - scale, 177, 6, 8, scale);

		PlayerInventory.drawInventory(this, width, height, ySize-PlayerInventory.invYSize);

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j)
	{
		if(te.currentAlloy != null)
		{
			if(te.currentAlloy.outputType != null)
			{
				this.fontRendererObj.drawString(EnumChatFormatting.UNDERLINE+StatCollector.translateToLocal("gui.metal." + te.currentAlloy.outputType.Name.replace(" ", "")),7,7,0x000000);
			} else
			{
				this.fontRendererObj.drawString(EnumChatFormatting.UNDERLINE+StatCollector.translateToLocal("gui.metal.Unknown"),7,7,0x000000);
			}

			for(int c = 0; c < te.currentAlloy.AlloyIngred.size(); c++)
			{
				double m = te.currentAlloy.AlloyIngred.get(c).metal;
				m = Math.round(m * 100d)/100d;
				if(te.currentAlloy.AlloyIngred.get(c).metalType != null)
				{
					this.fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY+StatCollector.translateToLocal("gui.metal." + te.currentAlloy.AlloyIngred.get(c).metalType.Name.replace(" ", "")) + 
							": "+ EnumChatFormatting.DARK_GREEN + m + "%", 7,18 + 10 * (c),0x000000);
				}
			}
		}

		if(TFCOptions.enableDebugMode)
		{
			this.fontRendererObj.drawString("Temp: " + te.temperature ,178, 8, 0xffffff);
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		if (te.currentAlloy != null) {
			int w = (this.width - this.xSize) / 2;
			int h = (this.height - this.ySize) / 2;
			if (par1 >= 129 + w && par2 >= 6 + h&& par1 <= 137 + w && par2 <= 106 + h) {
				String[] text = { String.format("%2.0f", te.currentAlloy.outputAmount) };
				List temp = Arrays.asList(text);
				drawHoveringText(temp, par1, par2, fontRendererObj);
			}
		}
	}

	@Override
	public void drawCenteredString(FontRenderer fontrenderer, String s, int i, int j, int k)
	{
		fontrenderer.drawString(s, i - fontrenderer.getStringWidth(s) / 2, j, k);
	}
}
