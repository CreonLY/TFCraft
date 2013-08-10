package TFC.Render.Models;

import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import TFC.Entities.Mobs.EntitySheepTFC;
public class ModelSheep2TFC extends ModelQuadruped
{
	private float field_44017_o;

	public ModelSheep2TFC()
	{
		super(12, 0.0F);
		this.head = new ModelRenderer(this, 0, 0);
		this.head.addBox(-3.0F, -4.0F, -6.0F, 6, 6, 8, 0.0F);
		this.head.setRotationPoint(0.0F, 6.0F, -8.0F);
		this.body = new ModelRenderer(this, 28, 8);
		this.body.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 0.0F);
		this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
	}

	/**
	 * Used for easily adding entity-dependent animations. The second and third float params here are the same second
	 * and third as in the setRotationAngles method.
	 */
	public void setLivingAnimations(EntityLiving par1EntityLiving, float par2, float par3, float par4)
	{
		super.setLivingAnimations(par1EntityLiving, par2, par3, par4);
		this.head.rotationPointY = 6.0F + ((EntitySheepTFC)par1EntityLiving).func_44003_c(par4) * 9.0F;
		this.field_44017_o = ((EntitySheepTFC)par1EntityLiving).func_44002_d(par4);
	}

	/**
	 * Sets the models various rotation angles.
	 */
	@Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity e)
	{
		super.setRotationAngles(par1, par2, par3, par4, par5, par6, e);
		this.head.rotateAngleX = this.field_44017_o;
	}
}
