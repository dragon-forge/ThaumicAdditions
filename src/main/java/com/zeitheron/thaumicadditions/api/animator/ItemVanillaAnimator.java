package com.zeitheron.thaumicadditions.api.animator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemVanillaAnimator
		extends BaseItemAnimator
{
	public static final ItemVanillaAnimator VANILLA = new ItemVanillaAnimator();

	@Override
	@SideOnly(Side.CLIENT)
	public boolean transformHand(RenderSpecificHandEvent e, float progress)
	{
		Minecraft mc = Minecraft.getMinecraft();
		AbstractClientPlayer player = mc.player;
		renderItemInFirstPerson(player, mc.getRenderPartialTicks(), e.getInterpolatedPitch(), e.getHand(), getSwingProgress(progress), e.getItemStack(), e.getEquipProgress());
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean transformHandItem(RenderSpecificHandEvent e, float progress)
	{
		return false;
	}

	public static final float PI_F = (float) Math.PI;

	@SideOnly(Side.CLIENT)
	public void renderItemInFirstPerson(AbstractClientPlayer player, float partialTicks, float pitch, EnumHand hand, float swingProgress, ItemStack stack, float equipProgress)
	{
		boolean flag = hand == EnumHand.MAIN_HAND;
		EnumHandSide enumhandside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
		GlStateManager.pushMatrix();

		if(stack.isEmpty())
		{
			if(flag && !player.isInvisible())
			{
				this.renderArmFirstPerson(equipProgress, swingProgress, enumhandside);
			}
		} else
		{
			boolean flag1 = enumhandside == EnumHandSide.RIGHT;

			if(player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == hand)
				this.transformSideFirstPerson(enumhandside, equipProgress);
			else
			{
				float f = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
				float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float) Math.PI * 2F));
				float f2 = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
				int i = flag1 ? 1 : -1;
				GlStateManager.translate((float) i * f, f1, f2);
				this.transformSideFirstPerson(enumhandside, equipProgress);
				this.transformFirstPerson(enumhandside, swingProgress);
			}

			Minecraft.getMinecraft().getItemRenderer().renderItemSide(player, stack, flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
		}

		GlStateManager.popMatrix();
	}

	@SideOnly(Side.CLIENT)
	protected void transformSideFirstPerson(EnumHandSide hand, float swingProgress)
	{
		int i = hand == EnumHandSide.RIGHT ? 1 : -1;
		GlStateManager.translate((float) i * 0.56F, -0.52F + swingProgress * -0.6F, -0.72F);
	}

	@SideOnly(Side.CLIENT)
	protected void transformFirstPerson(EnumHandSide hand, float swingProgress)
	{
		int i = hand == EnumHandSide.RIGHT ? 1 : -1;
		float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
		GlStateManager.rotate((float) i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
		float f1 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
		GlStateManager.rotate((float) i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((float) i * -45.0F, 0.0F, 1.0F, 0.0F);
	}

	@SideOnly(Side.CLIENT)
	protected void renderArmFirstPerson(float equipProgress, float swingProgress, EnumHandSide hand)
	{
		Minecraft mc = Minecraft.getMinecraft();

		boolean flag = hand != EnumHandSide.LEFT;
		float f = flag ? 1.0F : -1.0F;
		float f1 = MathHelper.sqrt(swingProgress);
		float f2 = -0.3F * MathHelper.sin(f1 * PI_F);
		float f3 = 0.4F * MathHelper.sin(f1 * (PI_F * 2F));
		float f4 = -0.4F * MathHelper.sin(swingProgress * PI_F);
		GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + equipProgress * -0.6F, f4 + -0.71999997F);
		GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);
		float f5 = MathHelper.sin(swingProgress * swingProgress * PI_F);
		float f6 = MathHelper.sin(f1 * PI_F);
		GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
		AbstractClientPlayer abstractclientplayer = mc.player;
		mc.getTextureManager().bindTexture(abstractclientplayer.getLocationSkin());
		GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
		GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);
		RenderPlayer renderplayer = (RenderPlayer) mc.getRenderManager().<AbstractClientPlayer> getEntityRenderObject(abstractclientplayer);
		GlStateManager.disableCull();
		if(flag) renderplayer.renderRightArm(abstractclientplayer);
		else renderplayer.renderLeftArm(abstractclientplayer);
		GlStateManager.enableCull();
	}

	protected float getSwingProgress(float progress)
	{
		return progress;
	}
}