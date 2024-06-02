package org.zeith.thaumicadditions.asm.minmixin;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MinMixin
{
	String[] value();
}