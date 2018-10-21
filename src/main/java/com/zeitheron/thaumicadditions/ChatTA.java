package com.zeitheron.thaumicadditions;

import com.zeitheron.hammercore.internal.Chat;
import com.zeitheron.hammercore.internal.Chat.ChatFingerprint;
import com.zeitheron.hammercore.utils.WorldUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;

public class ChatTA
{
	public static final ChatFingerprint THAUMIC_FINGERRINT = new ChatFingerprint(0x01_02_69_80_57_25_53_53l);
	
	public static void sendMessage(EntityPlayer player, ITextComponent text)
	{
		if(player == null)
			return;
		
		EntityPlayerMP mp = WorldUtil.cast(player, EntityPlayerMP.class);
		
		if(mp == null && !player.world.isRemote && player.getServer() != null)
			mp = player.getServer().getPlayerList().getPlayerByUUID(player.getGameProfile().getId());
		
		if(mp != null)
			Chat.editMessageFor(mp, text, THAUMIC_FINGERRINT);
	}
}