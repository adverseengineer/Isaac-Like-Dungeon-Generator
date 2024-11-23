import java.util.ArrayList;

public class DungeonLayout
{
	public int[][] roomData;
	public int[][] wallData;
	public int[][] keyData;

	public static final byte WALL_NORMAL			= 0;
	public static final byte WALL_BREAKABLE			= 1;
	public static final byte WALL_BROKEN			= 2;
	public static final byte WALL_JOINED_HORIZONTAL	= 3;
	public static final byte WALL_JOINED_VERTICAL	= 4;
	public static final byte WALL_JOINED_QUAD		= 5;
	public static final byte WALL_DOOR_UNLOCKED		= 6;
	public static final byte WALL_DOOR_LOCKED		= 7;
	//7+ = locked door; value - 6 = ID of key

	public DungeonLayout(int width, int height, int iterations, float quadRoomFrequency, float horizontalDoubleRoomFrequency, float verticalDoubleRoomFrequency)
	{
		roomData = new int[height][width];
		wallData = new int[height][width*2-1];

		//the center rooms value is equal to the number of iterations
		roomData[height/2-1][width/2-1] = iterations;
		generateRooms(width/2-1, height/2-1, iterations, iterations);

		//spice up the dungeon
		makeQuadRooms(quadRoomFrequency);
		makeHorizontalDoubleRooms(horizontalDoubleRoomFrequency);
		makeVerticalDoubleRooms(verticalDoubleRoomFrequency);
	}

	//takes parameters for
	//the x and y of the room that this iteration is using to generate more rooms
	//the total number of iterations that are being worked through
	//the number of the current iteration
	public void generateRooms(int chosenRoomX, int chosenRoomY, int numberOfIterations, int currentIteration)
	{
		//base case: if we are on the last iteration, stop
		if(currentIteration == 1)
			return;

		//for each of the adjacent rooms
		for(int[] adjacentRoom : getAdjacentCells(chosenRoomX, chosenRoomY))
		{
			//if the room passes the keep check and the space is not already taken (is zero)
			//NOTE: the chance to keep the room decreases gradually to zero, inversely proportional to the number of iterations that have passed 
			float chance = 1f * currentIteration / numberOfIterations;
			if(Util.randomFloat(0,1) < chance && roomData[adjacentRoom[1]][adjacentRoom[0]] == 0)
			{
				//add the room to the map
				setRoom(currentIteration - 1, adjacentRoom[0], adjacentRoom[1]);
				//add a door connecting the original room and the new room
				int[] doorPosition = getWallPosition(chosenRoomX,chosenRoomY,adjacentRoom[0],adjacentRoom[1]);
				wallData[doorPosition[1]][doorPosition[0]] = WALL_DOOR_UNLOCKED;

				//recurse with that room
				generateRooms(adjacentRoom[0], adjacentRoom[1], numberOfIterations, currentIteration - 1);
			}
		}
	}

	public void makeQuadRooms(float frequency)
	{
		for(int y = 0; y < getHeight(); y++)
			for(int x = 0; x < roomData[y].length; x++)
				//if a 2x2 group exists there
				if(getRoom(x,y) > 0 && getRoom(x+1,y) > 0 && getRoom(x,y+1) > 0 && getRoom(y+1,x+1) > 0)
					//if the group is connected by either regular walls or doors
					if((getWall(x,y,x+1,y) == WALL_NORMAL || getWall(x,y,x+1,y) >= WALL_DOOR_UNLOCKED) &&
						(getWall(x+1,y,x+1,y+1) == WALL_NORMAL || getWall(x+1,y,x+1,y+1) >= WALL_DOOR_UNLOCKED) &&
						(getWall(x+1,y+1,x,y+1) == WALL_NORMAL || getWall(x+1,y+1,x,y+1) >= WALL_DOOR_UNLOCKED) &&
						(getWall(x,y+1,x,y) == WALL_NORMAL || getWall(x,y+1,x,y) >= WALL_DOOR_UNLOCKED))
						//this huge block of conditions checks every wall connected to the candidate group and if ANY of them are joined, the group is discarded
						if(getWall(x,y,x,y-1) != WALL_JOINED_HORIZONTAL && getWall(x,y,x,y-1) != WALL_JOINED_VERTICAL && getWall(x,y,x,y-1) != WALL_JOINED_QUAD &&
							getWall(x+1,y,x+1,y-1) != WALL_JOINED_HORIZONTAL && getWall(x+1,y,x+1,y-1) != WALL_JOINED_VERTICAL && getWall(x+1,y,x+1,y-1) != WALL_JOINED_QUAD &&
							getWall(x+1,y,x+2,y) != WALL_JOINED_HORIZONTAL && getWall(x+1,y,x+2,y) != WALL_JOINED_VERTICAL && getWall(x+1,y,x+2,y) != WALL_JOINED_QUAD &&
							getWall(x+1,y+1,x+2,y+1) != WALL_JOINED_HORIZONTAL && getWall(x+1,y+1,x+2,y+1) != WALL_JOINED_VERTICAL && getWall(x+1,y+1,x+2,y+1) != WALL_JOINED_QUAD &&
							getWall(x+1,y+1,x+1,y+2) != WALL_JOINED_HORIZONTAL && getWall(x+1,y+1,x+1,y+2) != WALL_JOINED_VERTICAL && getWall(x+1,y+1,x+1,y+2) != WALL_JOINED_QUAD &&
							getWall(x,y+1,x,y+2) != WALL_JOINED_HORIZONTAL && getWall(x,y+1,x,y+2) != WALL_JOINED_VERTICAL && getWall(x,y+1,x,y+2) != WALL_JOINED_QUAD &&
							getWall(x,y+1,x-1,y+1) != WALL_JOINED_HORIZONTAL && getWall(x,y+1,x-1,y+1) != WALL_JOINED_VERTICAL && getWall(x,y+1,x-1,y+1) != WALL_JOINED_QUAD &&
							getWall(x,y,x-1,y) != WALL_JOINED_HORIZONTAL && getWall(x,y,x-1,y) != WALL_JOINED_VERTICAL && getWall(x,y,x-1,y) != WALL_JOINED_QUAD)
							//if rng passes the check
							if(Util.randomFloat(0,1) < frequency)
							{
								//set the walls between the 4 rooms to be joined
								setWall(WALL_JOINED_QUAD,x,y,x+1,y);
								setWall(WALL_JOINED_QUAD,x+1,y,x+1,y+1);
								setWall(WALL_JOINED_QUAD,x+1,y+1,x,y+1);
								setWall(WALL_JOINED_QUAD,x,y+1,x,y);
								Util.log("quad room @ {{" + x + "," + y + "},{" + (x+1) + "," + y + "},{" + x + "," + (y+1) + "},{" + (x+1) + "," + (y+1) + "}}");
							}
	}

	//loops over the entire map and finds horizontal pairs of rooms
	//every pair is given a <frequency>% chance to become joined into a double room
	public void makeHorizontalDoubleRooms(float frequency)	
	{
		for(int y = 0; y < getHeight(); y++)
			for(int x = 0; x < getWidth(); x++)
				//if a horizontal pair of rooms exists here connected by a door
				if(getRoom(x,y) > 0 && getRoom(x+1,y) > 0 && getWall(x,y,x+1,y) >= WALL_DOOR_UNLOCKED)
					//this huge block of conditions checks every wall connected to the candidate pair and if ANY of them are joined, the pair is discarded
					if(getWall(x,y,x-1,y) != WALL_JOINED_HORIZONTAL && getWall(x,y,x-1,y) != WALL_JOINED_VERTICAL && getWall(x,y,x-1,y) != WALL_JOINED_QUAD &&
						getWall(x+1,y,x+2,y) != WALL_JOINED_HORIZONTAL && getWall(x+1,y,x+2,y) != WALL_JOINED_VERTICAL && getWall(x+1,y,x+2,y) != WALL_JOINED_QUAD &&
						getWall(x,y,x,y-1) != WALL_JOINED_HORIZONTAL && getWall(x,y,x,y-1) != WALL_JOINED_VERTICAL && getWall(x,y,x,y-1) != WALL_JOINED_QUAD &&
						getWall(x,y,x,y+1) != WALL_JOINED_HORIZONTAL && getWall(x,y,x,y+1) != WALL_JOINED_VERTICAL && getWall(x,y,x,y+1) != WALL_JOINED_QUAD &&
						getWall(x+1,y,x+1,y-1) != WALL_JOINED_HORIZONTAL && getWall(x+1,y,x+1,y-1) != WALL_JOINED_VERTICAL && getWall(x+1,y,x+1,y-1) != WALL_JOINED_QUAD &&
						getWall(x+1,y,x+1,y+1) != WALL_JOINED_HORIZONTAL && getWall(x+1,y,x+1,y+1) != WALL_JOINED_VERTICAL && getWall(x+1,y,x+1,y+1) != WALL_JOINED_QUAD)
						//if rng passes the check
						if(Util.randomFloat(0,1) < frequency)
						{
							//set the wall between the pair to be joined
							setWall(WALL_JOINED_HORIZONTAL,x,y,x+1,y);
							Util.log("horizontal double room @ {{" + x + "," + y + "},{" + (x+1) + "," + y + "}}");
						}
	}

	//loops over the entire map and finds vertical pairs of rooms
	//every pair is given a <frequency>% chance to become joined into a double room
	public void makeVerticalDoubleRooms(float frequency)
	{
		for(int y = 0; y < getWidth(); y++)
			for(int x = 0; x < getWidth(); x++)
				//if a vertical pair of rooms exists here connected by a door
				if(getRoom(x,y) > 0 && getRoom(x,y+1) > 0 && getWall(x,y,x,y+1) >= WALL_DOOR_UNLOCKED)
					//this huge block of conditions checks every wall connected to the candidate pair and if ANY of them are joined, the pair is discarded
					if(getWall(x,y,x,y-1) != WALL_JOINED_HORIZONTAL && getWall(x,y,x,y-1) != WALL_JOINED_VERTICAL && getWall(x,y,x,y-1) != WALL_JOINED_QUAD &&
						getWall(x,y+1,x,y+2) != WALL_JOINED_HORIZONTAL && getWall(x,y+1,x,y+2) != WALL_JOINED_VERTICAL && getWall(x,y+1,x,y+2) != WALL_JOINED_QUAD &&
						getWall(x,y,x-1,y) != WALL_JOINED_HORIZONTAL && getWall(x,y,x-1,y) != WALL_JOINED_VERTICAL && getWall(x,y,x-1,y) != WALL_JOINED_QUAD &&
						getWall(x,y,x+1,y) != WALL_JOINED_HORIZONTAL && getWall(x,y,x+1,y) != WALL_JOINED_VERTICAL && getWall(x,y,x+1,y) != WALL_JOINED_QUAD && 
						getWall(x,y+1,x-1,y+1) != WALL_JOINED_HORIZONTAL && getWall(x,y+1,x-1,y+1) != WALL_JOINED_VERTICAL && getWall(x,y+1,x-1,y+1) != WALL_JOINED_QUAD && 
						getWall(x,y+1,x+1,y+1) != WALL_JOINED_HORIZONTAL && getWall(x,y+1,x+1,y+1) != WALL_JOINED_VERTICAL && getWall(x,y+1,x+1,y+1) != WALL_JOINED_QUAD)
						//if rng passes the check
						if(Util.randomFloat(0,1) < frequency)
						{
							//set the wall between the pair to be joined
							setWall(WALL_JOINED_VERTICAL,x,y,x,y+1);
							Util.log("vertical double room @ {{" + x + "," + y + "},{" + x + "," + (y+1) + "}}");
						}
	}

	public void makeBreakableWalls(float frequency)
	{
		//TODO: decide whether these are worth it
	}

	//sets the value of the room at the provided coords
	public void setRoom(int value, int roomX, int roomY)
	{
		if(roomY < getHeight() && roomX < getWidth())
			roomData[roomY][roomX] = value;
	}

	//sets the value at the provided coords
	public void setWall(int value, int wallX, int wallY)
	{
		if(wallY < wallData.length && wallX < wallData[0].length)
			wallData[wallY][wallX] = value;
	}

	//sets the value at the wall coords returned by the room coords
	public void setWall(int value, int room1X, int room1Y, int room2X, int room2Y)
	{
		int[] wallPosition = getWallPosition(room1X,room1Y,room2X,room2Y);
		setWall(value,wallPosition[0],wallPosition[1]);
	}

	//returns the value at the provided coords
	public int getRoom(int roomX, int roomY)
	{
		if(roomY < getHeight() && roomX < getWidth())
			return roomData[roomY][roomX];
		else
			return -1;
	}

	//returns the value at the provided coords
	public int getWall(int wallX, int wallY)
	{
		if(wallY >= 0 && wallY < wallData.length && wallX >= 0 && wallX < wallData[0].length)
			return wallData[wallY][wallX];
		else
			return -1;
	}

	//returns the value at the wall coords returned by the room coords
	public int getWall(int room1X, int room1Y, int room2X, int room2Y)
	{
		int[] wallPosition = getWallPosition(room1X, room1Y, room2X, room2Y);
		return getWall(wallPosition[0], wallPosition[1]);
	}

	//takes a pair of coordinates
	//returns a list of the coordinates of all adjacent cells
	public ArrayList<int[]> getAdjacentCells(int roomX, int roomY)
	{
		ArrayList<int[]> adjacentCells = new ArrayList<>(0);

		//these four lines are basically just hardcode to return the coordinates of the four adjacent cells
		//it does not return a pair of coordinates if they would be out of bounds 
		if(roomX >= 0 && roomX < getWidth() && roomY-1 >= 0 && roomY-1 < getHeight()) adjacentCells.add(new int[]{roomX,roomY-1});
		if(roomX+1 >= 0 && roomX+1 < getWidth() && roomY >= 0 && roomY < getHeight()) adjacentCells.add(new int[]{roomX+1,roomY});
		if(roomX >= 0 && roomX < getWidth() && roomY+1 >= 0 && roomY+1 < getHeight()) adjacentCells.add(new int[]{roomX,roomY+1});
		if(roomX-1 >= 0 && roomX-1 < getWidth() && roomY >= 0 && roomY < getHeight()) adjacentCells.add(new int[]{roomX-1,roomY});
		
		//this returns an array that structured like this: {{x1,y1},{x2,y2},{x3,y3},{x4,y4}}
		return adjacentCells;
	}

	//takes the coordinates of two adjacent rooms and returns the coords of the wall that lies between them
	public int[] getWallPosition(int room1X, int room1Y, int room2X, int room2Y)
	{
		//if the rooms are not adjacent, return null
		if(Math.abs(room1X-room2X) > 1 || Math.abs(room1Y-room2Y) > 1)
			return null;

		//if the coords refer to the same room twice, return null
		if(room1X == room2X && room1Y == room2Y)
			return null;

		//if the rooms are vertically adjacent (x1 == x2)
		//the y coord of the wall will always be the smaller of the two rooms' y coords
		if(room1X == room2X)
			return new int[]{room1X + getWidth() - 1,Math.min(room1Y,room2Y)};
		//if the rooms are horizontally adjacent (y1 == y2)
		//the x coord of the wall will always be the smaller of the two room's x coords
		else
			return new int[]{Math.min(room1X,room2X),room1Y};
	}

	//takes the coordinates of a wall in wallData and returns the coords of the rooms it lies between
	public int[] getRoomPositions(int wallX, int wallY)
	{
		//if the wall connects vertically
		if(wallX >= roomData[wallY].length - 1)
			return new int[]{wallX - getWidth() + 1,wallY,wallX - getWidth() + 1,wallY + 1};
		//if the wall connects horizontally
		else
			return new int[]{wallX,wallY,wallX + 1,wallY};
	}

	public int getWidth()
	{
		return roomData[0].length;
	}

	public int getHeight()
	{
		return roomData.length;
	}

	public int getNumberOfRooms()
	{
		int numberOfRooms = 0;

		for(int y = 0; y < getWidth(); y++)
			for(int x = 0; x < getWidth(); x++)
				if(getRoom(x,y) > 0)
					numberOfRooms++;

		return numberOfRooms;
	}
}