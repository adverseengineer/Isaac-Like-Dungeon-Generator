import jcurses.system.CharColor;
import jcurses.system.Toolkit;

public class DungeonPainter
{
	private final static CharColor TRANSPARENT 		= new CharColor(CharColor.NORMAL,CharColor.NORMAL);
	private final static CharColor DEFAULT 			= new CharColor(CharColor.NORMAL,CharColor.WHITE);
	private final static CharColor ROOM_SINGLE 		= new CharColor(CharColor.NORMAL,CharColor.CYAN);
	private final static CharColor ROOM_HORIZONTAL 	= new CharColor(CharColor.NORMAL,CharColor.MAGENTA);
	private final static CharColor ROOM_VERTICAL 	= new CharColor(CharColor.NORMAL,CharColor.YELLOW);
	private final static CharColor ROOM_QUAD 		= new CharColor(CharColor.NORMAL,CharColor.GREEN);

	public static void paint(DungeonLayout dungeon)
	{
		paint(dungeon,5,3);
	}

	public static void paint(DungeonLayout dungeon, int roomWidth, int roomHeight)
	{
		for(int y = 0; y < dungeon.getHeight(); y++)
			for(int x = 0; x < dungeon.getWidth(); x++)
				//if a room exists here
				if(dungeon.getRoom(x,y) > 0)
					//draw every room, whether it's joined or not
					Toolkit.drawBorder(x*(roomWidth+1),y*(roomHeight+1),roomWidth,roomHeight,ROOM_SINGLE);

		//drawing the double rooms needs to be done in a spearate loop because they break if the original room borders aren't done being drawn
		for(int y = 0; y < dungeon.getHeight(); y++)
			for(int x = 0; x < dungeon.getWidth(); x++)
				//if a room exists here
				if(dungeon.getRoom(x,y) > 0)
				{
					//if this room is part of a quad room
					if(dungeon.getWall(x,y,x+1,y) == DungeonLayout.WALL_JOINED_QUAD &&
						dungeon.getWall(x+1,y,x+1,y+1) == DungeonLayout.WALL_JOINED_QUAD &&
						dungeon.getWall(x+1,y+1,x,y+1) == DungeonLayout.WALL_JOINED_QUAD &&
						dungeon.getWall(x,y+1,x,y) == DungeonLayout.WALL_JOINED_QUAD)
					{
						//draw a rectangle to erase the old room borders
						Toolkit.drawRectangle(x*(roomWidth+1),y*(roomHeight+1),roomWidth*2+1,roomHeight*2+1,TRANSPARENT);
						//draw the quad room border
						Toolkit.drawBorder(x*(roomWidth+1),y*(roomHeight+1),roomWidth*2+1,roomHeight*2+1,ROOM_QUAD);
					}


					//if this room is part of a horizontal double room
					if(dungeon.getWall(x,y,x+1,y) == DungeonLayout.WALL_JOINED_HORIZONTAL)
					{
						//draw a rectangle to erase the old room borders
						Toolkit.drawRectangle(x*(roomWidth+1),y*(roomHeight+1),roomWidth*2+1,roomHeight,TRANSPARENT);
						//draw the double room border
						Toolkit.drawBorder(x*(roomWidth+1),y*(roomHeight+1),roomWidth*2+1,roomHeight,ROOM_HORIZONTAL);
					}
					//if this room and the room to the right are connected by a door
					else if(dungeon.getWall(x,y,x+1,y) >= DungeonLayout.WALL_DOOR_UNLOCKED)
						Toolkit.drawHorizontalThickLine(x*(roomWidth+1)+roomWidth,y*(roomHeight+1)+roomHeight/2,x*(roomWidth+1)+roomWidth,DEFAULT);						

					//if this room is part of a vertical double room
					if(dungeon.getWall(x,y,x,y+1) == DungeonLayout.WALL_JOINED_VERTICAL)
					{
						//draw a rectangle to erase the old room border
						Toolkit.drawRectangle(x*(roomWidth+1),y*(roomHeight+1),roomWidth,roomHeight*2+1,TRANSPARENT);
						//draw the double room border
						Toolkit.drawBorder(x*(roomWidth+1),y*(roomHeight+1),roomWidth,roomHeight*2+1,ROOM_VERTICAL);
					}
					//if this room and the room to the right are connected by a door
					else if(dungeon.getWall(x,y,x,y+1) >= DungeonLayout.WALL_DOOR_UNLOCKED)
						Toolkit.drawVerticalThickLine(x*(roomWidth+1)+roomWidth/2,y*(roomHeight+1)+roomHeight,y*(roomHeight+1)+roomHeight,DEFAULT);

					//draw the number of this room
					Toolkit.printString(dungeon.getRoom(x,y)+"",x*(roomWidth+1),y*(roomHeight+1),DEFAULT);
				}
	}
}