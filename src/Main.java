import java.util.HashMap;
import jcurses.system.CharColor;
import jcurses.system.InputChar;
import jcurses.system.Toolkit;

public class Main
{
	public static void main(String[] args)
	{
		Toolkit.init();

		DungeonLayout dungeon;

		while(true)
		{

			Toolkit.clearScreen(new CharColor(CharColor.NORMAL,CharColor.NORMAL));

			dungeon = new DungeonLayout(11,11,7,0.1f,0.2f,0.2f);

			DungeonPainter.paint(dungeon);

			InputChar ic = Toolkit.readCharacter();
			if(!ic.isSpecialCode() && ic.getCode() == '\t')
			{
				Toolkit.shutdown();
				break;
			}
		}
	}

	// public static void printInfoPane(String info,int x,int y)
	// {
	// 	String[] lines = info.split("\n");

	// 	int maxLength = -1;

	// 	for(String line : lines)
	// 		if(line.length() > maxLength)
	// 			maxLength = line.length();

	// 	Toolkit.drawBorder(x,y,maxLength+2,lines.length+2,new CharColor(CharColor.NORMAL,CharColor.RED));
	// 	for(int i = 0; i < lines.length; i++)
	// 		Toolkit.printString(String.format("%-" + maxLength + "s",lines[i]),x+1,y+i+1,new CharColor(CharColor.NORMAL,CharColor.WHITE));
	// }
}