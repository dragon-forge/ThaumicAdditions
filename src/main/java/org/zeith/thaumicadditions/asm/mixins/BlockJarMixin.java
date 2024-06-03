package org.zeith.thaumicadditions.asm.mixins;

import org.zeith.thaumicadditions.asm.minmixin.base.CopyingMixin;
import org.zeith.thaumicadditions.asm.minmixin.annotations.MinMixin;
import org.zeith.thaumicadditions.asm.mixins.inner.BlockJarInner;

@MinMixin({
		"thaumcraft.common.blocks.essentia.BlockJar",
		"org.zeith.thaumicadditions.asm.mixins.inner.BlockJarInner"
})
public class BlockJarMixin
		extends CopyingMixin
{
	@Override
	protected void initInner()
	{
		BlockJarInner.init();
	}
}