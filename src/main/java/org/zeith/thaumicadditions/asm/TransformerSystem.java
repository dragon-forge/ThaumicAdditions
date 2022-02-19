package org.zeith.thaumicadditions.asm;

import com.zeitheron.hammercore.asm.HCASM;
import com.zeitheron.hammercore.lib.zlib.utils.TaskedThread;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class TransformerSystem
{
	private final List<IASMHook> hooks = new ArrayList<>();
	private final TaskedThread SAVE_THREAD = new TaskedThread();
	private final File CLASS_SAVE_DIR = new File("HammerCore", "asm_classes");
	String currentClass, transformedCurrentClass;
	private String indentstr = "";
	private int indents = 0;

	private void saveClass(byte[] original, byte[] modified, String clazz)
	{
		final byte[] o = original.clone();
		final byte[] m = modified.clone();

		SAVE_THREAD.addTask(() ->
		{
			if(!CLASS_SAVE_DIR.isDirectory())
				CLASS_SAVE_DIR.mkdirs();
			try
			{
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File(CLASS_SAVE_DIR, clazz.replaceAll("/", "_").replaceAll("[.]", "_") + ".zip")));

				zos.putNextEntry(new ZipEntry("origin.class"));
				zos.write(original);
				zos.closeEntry();

				if(!Arrays.equals(modified, original))
				{
					zos.putNextEntry(new ZipEntry("modified.class"));
					zos.write(modified);
					zos.closeEntry();
				}

				zos.putNextEntry(new ZipEntry("methods.txt"));
				{
					ClassNode cn = ObjectWebUtils.loadClass(m);
					for(MethodNode mn : cn.methods)
						zos.write((mn.name + " " + mn.desc + "\n").getBytes());
				}
				zos.closeEntry();

				zos.close();
			} catch(IOException e)
			{
				info("Failed to save ASM class for " + clazz);
				e.printStackTrace();
			}
		});
	}

	public void push()
	{
		if(indents < 15)
			++indents;
		indentstr = "";
		for(int i = 0; i < indents; ++i)
			indentstr += "  ";
	}

	public void pop()
	{
		if(indents > 0)
			--indents;
		indentstr = "";
		for(int i = 0; i < indents; ++i)
			indentstr += "  ";
	}

	public void info(String text)
	{
		if(indents == 0)
			HCASM.ASM_LOG.info(text);
		else
			HCASM.ASM_LOG.info(indentstr + "-" + text);
	}

	public void addHook(IASMHook hook)
	{
		hooks.add(hook);
	}

	public String getCurrentClass()
	{
		return currentClass;
	}

	public String getTransformedCurrentClass()
	{
		return transformedCurrentClass;
	}

	public byte[] transform(String name, String transformedName, byte[] data)
	{
		currentClass = name;
		transformedCurrentClass = transformedName;

		byte[] origin = data;
		boolean l = false;
		for(IASMHook h : hooks)
			if(h.accepts(transformedName) || h.accepts(name))
			{
				if(!l)
				{
					l = true;
					info("Transforming " + transformedName + " (" + name + ")...");
				}

				push();

				if(h.opName() != null)
					info(h.opName());

				boolean obf = !name.equals(transformedName);
				ClassNode node = ObjectWebUtils.loadClass(data);

				currentClass = name;
				transformedCurrentClass = transformedName;

				push();
				h.transform(node, obf);
				pop();

				data = ObjectWebUtils.writeClassToByteArray(node);

				pop();
			}

		// Not interesting
		/** Save classes that we are interested in. */
		// if(l || HammerCoreTransformer.CLASS_MAPPINGS.containsKey("L" +
		// transformedName.replaceAll("[.]", "/") + ";"))
		// saveClass(origin, data, transformedName);

		currentClass = null;
		transformedCurrentClass = null;

		return data;
	}

	interface IASMHook
	{
		boolean accepts(String name);

		String opName();

		void transform(ClassNode node, boolean obf);
	}

	static class ObjectWebUtils
	{
		public static ClassNode loadClass(byte[] data)
		{
			ClassReader reader = new ClassReader(data);
			ClassNode node = new ClassNode();
			reader.accept(node, 0);
			return node;
		}

		public static ClassNode loadClass(InputStream stream) throws IOException
		{
			ClassReader reader = new ClassReader(stream);
			ClassNode node = new ClassNode();
			reader.accept(node, 0);
			return node;
		}

		public static ClassNode loadClass(File file) throws IOException
		{
			FileInputStream stream = new FileInputStream(file);
			ClassNode node = loadClass(stream);
			stream.close();
			return node;
		}

		public static ClassNode loadClass(URL url) throws IOException
		{
			URLConnection conn = url.openConnection();
			try
			{
				conn.setDoInput(true);
				conn.connect();
			} catch(Throwable er)
			{
			}
			InputStream stream = conn.getInputStream();
			ClassNode node = loadClass(stream);
			stream.close();
			return node;
		}

		public static byte[] writeClassToByteArray(ClassNode node)
		{
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			node.accept(writer);
			return writer.toByteArray();
		}

		public static void writeClassToOutputStream(ClassNode node, OutputStream stream) throws IOException
		{
			stream.write(writeClassToByteArray(node));
		}

		public static void writeClassToFile(ClassNode node, File file) throws IOException
		{
			FileOutputStream os = new FileOutputStream(file);
			writeClassToOutputStream(node, os);
			os.close();
		}

		public static void writeClassToURL(ClassNode node, URL url) throws IOException
		{
			URLConnection conn = url.openConnection();
			try
			{
				conn.setDoOutput(true);
				conn.connect();
			} catch(Throwable er)
			{
			}
			OutputStream os = conn.getOutputStream();
			writeClassToOutputStream(node, os);
			os.close();
		}
	}
}