import com.zeitheron.hammercore.asm.HammerCoreTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Destructor
{
	public static void main(String[] args)
	{
		File pth = new File("asm").getAbsoluteFile();
		System.out.println(pth);
		
		for(File file : pth.listFiles(f -> f.getName().endsWith(".class")))
		{
			try
			{
				ClassReader cr = new ClassReader(Files.readAllBytes(file.toPath()));
				ClassNode node = new ClassNode();
				cr.accept(node, ClassReader.EXPAND_FRAMES);
				
				for(MethodNode method : node.methods)
				{
					HammerCoreTransformer.toString(node, method);
				}
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}