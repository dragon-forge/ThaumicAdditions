package com.zeitheron.thaumicadditions.client.util;

import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_HEIGHT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_INTERNAL_FORMAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WIDTH;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.glGetTexLevelParameteri;
import static org.lwjgl.opengl.GL11.glTexImage2D;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

public class GLDownloader
{
	public static BufferedImage toBufferedImage(ByteBuffer buffer, int width, int height, int channels)
	{
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for(int x = 0; x < width; ++x)
		{
			for(int y = 0; y < height; ++y)
			{
				int i = (x + y * width) * channels;
				
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				int a = 255;
				if(channels == 4)
					a = buffer.get(i + 3) & 0xFF;
				
				image.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
			}
		}
		return image;
	}
	
	public static void toGL4(ByteBuffer buffer, int glTex, int width, int height)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
	}
	
	public static void toGL3(ByteBuffer buffer, int glTex, int width, int height)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer);
	}
	
	public static ByteBuffer toByteBuffer(int glTex)
	{
		int format = getFormat(glTex);
		int width = getWidth(glTex);
		int height = getHeight(glTex);
		int channels = getChannels(format);
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * channels);
		glGetTexImage(GL_TEXTURE_2D, 0, format, GL_UNSIGNED_BYTE, buffer);
		return buffer;
	}
	
	public static int getChannels(int format)
	{
		int channels = 4;
		if(format == GL_RGB)
			channels = 3;
		return channels;
	}
	
	public static int getFormat(int glTex)
	{
		return glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_INTERNAL_FORMAT);
	}
	
	public static int getHeight(int glTex)
	{
		return glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);
	}
	
	public static int getWidth(int glTex)
	{
		return glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
	}
}