import com.zeitheron.hammercore.utils.classes.ClassWrapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Scanner;

public class TestReflections
{
	public static void main(String[] args)
	{
		ClassWrapper cw = null;
		Scanner in = new Scanner(System.in);
		while(cw == null)
		{
			System.out.print("In Class >> ");
			cw = ClassWrapper.create(in.nextLine());
		}
		Field[] fs = cw.clazz.getDeclaredFields();
		Method[] ms = cw.clazz.getDeclaredMethods();
		while(true)
		{
			System.out.print("Get Index of >> ");
			String read = in.nextLine();
			for(int i = 0; i < fs.length; i++)
			{
				Field f = fs[i];
				if(f.getName().equalsIgnoreCase(read)) System.out.println("Fields[" + i + "] = " + f);
			}
			for(int i = 0; i < ms.length; i++)
			{
				Method m = ms[i];
				if(m.getName().equalsIgnoreCase(read)) System.out.println("Methods[" + i + "] = " + m);
			}
		}
	}
}