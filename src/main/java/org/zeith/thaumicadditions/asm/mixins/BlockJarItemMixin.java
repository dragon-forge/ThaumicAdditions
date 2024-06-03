package org.zeith.thaumicadditions.asm.mixins;

import org.zeith.thaumicadditions.asm.minmixin.base.CopyingMixin;
import org.zeith.thaumicadditions.asm.minmixin.annotations.MinMixin;
import org.zeith.thaumicadditions.asm.mixins.inner.ItemJarInner;

@MinMixin({
		"thaumcraft.common.blocks.essentia.BlockJarItem",
		"org.zeith.thaumicadditions.asm.mixins.inner.ItemJarInner"
})
public class BlockJarItemMixin
		extends CopyingMixin
{
	@Override
	protected void initInner()
	{
		ItemJarInner.init();
	}
}