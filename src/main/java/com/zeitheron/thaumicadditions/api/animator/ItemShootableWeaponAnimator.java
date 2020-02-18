package com.zeitheron.thaumicadditions.api.animator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemShootableWeaponAnimator
		extends ItemVanillaAnimator
{
	public static final BaseItemAnimator DEF_0125 = new ItemShootableWeaponAnimator(0.125F, 0F);
	public float recoil, offsetLeft;

	public ItemShootableWeaponAnimator(float recoil, float offsetLeft)
	{
		this.recoil = recoil;
		this.offsetLeft = offsetLeft;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean rendersHand(EntityPlayer player, EnumHand hand, EnumHandSide side)
	{
		EnumHandSide primSide = player.getPrimaryHand();
		EnumHandSide renderedSide = hand == EnumHand.MAIN_HAND ? primSide : primSide.opposite();
		return renderedSide == side.opposite();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderItemInFirstPerson(AbstractClientPlayer player, float partialTicks, float pitch, EnumHand hand, float swingProgress, ItemStack stack, float equipProgress)
	{
		float yOff = (float) Math.sin(swingProgress * Math.PI * 2) * recoil;
		float zOff = (float) Math.sin(swingProgress * Math.PI) * recoil;
		swingProgress = 0;

		boolean flag = hand == EnumHand.MAIN_HAND;
		EnumHandSide handSide = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
		GlStateManager.pushMatrix();

		if(stack.isEmpty())
		{
			if(flag && !player.isInvisible())
			{
				this.renderArmFirstPerson(equipProgress, swingProgress, handSide);
			}
		} else
		{
			boolean flag1 = handSide == EnumHandSide.RIGHT;

			GlStateManager.pushMatrix();
			GlStateManager.translate((flag1 ? -0.25F : 0.25F) - offsetLeft, yOff * 0.0625F, zOff);

			if(player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == hand)
				this.transformSideFirstPerson(handSide, equipProgress);
			else
			{
				float f = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
				float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float) Math.PI * 2F));
				float f2 = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
				int i = flag1 ? 1 : -1;
				GlStateManager.translate((float) i * f, f1, f2);
				this.transformSideFirstPerson(handSide, equipProgress);
				this.transformFirstPerson(handSide, swingProgress);
			}

			Minecraft.getMinecraft().getItemRenderer().renderItemSide(player, stack, flag1 ? TransformType.FIRST_PERSON_RIGHT_HAND : TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
			GlStateManager.popMatrix();

			GlStateManager.pushMatrix();
			GlStateManager.translate(flag1 ? -0.25F : 0.6F, yOff * 0.03125F, zOff * 0.25F);

			if(player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == hand)
				this.transformSideFirstPerson(handSide, equipProgress);
			else
			{
				float f = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
				float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float) Math.PI * 2F));
				float f2 = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
				int i = flag1 ? 1 : -1;
				GlStateManager.translate((float) i * f, f1, f2);
				this.transformSideFirstPerson(handSide, equipProgress);
				this.transformFirstPerson(handSide, swingProgress);
			}

			GlStateManager.translate(-0.17F, -1.18F, -0.14F);
			GlStateManager.rotate(40, 1, 0, 0);
			GlStateManager.rotate(flag1 ? -135 : 135, 0, 0, 1);
			GlStateManager.rotate(flag1 ? 40 : -40, 0, 1, 0);

			{
				Minecraft mc = Minecraft.getMinecraft();

				flag = !flag1;
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
				GlStateManager.rotate(flag1 ? 120 : -120, 0, 1, 0);
				RenderPlayer renderplayer = (RenderPlayer) mc.getRenderManager().<AbstractClientPlayer> getEntityRenderObject(abstractclientplayer);
				GlStateManager.disableCull();
				GlStateManager.scale(0.4F, 0.5F, 0.4F);
				if(flag) renderplayer.renderRightArm(abstractclientplayer);
				else renderplayer.renderLeftArm(abstractclientplayer);
				GlStateManager.enableCull();
			}
			GlStateManager.popMatrix();
		}

		GlStateManager.popMatrix();
	}
}