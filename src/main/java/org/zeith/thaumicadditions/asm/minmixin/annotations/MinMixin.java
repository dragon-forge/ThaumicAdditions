package org.zeith.thaumicadditions.asm.minmixin.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MinMixin
{
	String[] value();
}