import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unchecked")

public class ArgumentParser
{
	public static HashMap<String,String> parseArgs(String[] args)
	{
		//TODO: edit the logic so that it supports flags (options without values)

		//declare a new hashmap to store the option/value pairs
		HashMap<String,String> options = new HashMap<>();

		//for every arg
		for(int i = 0; i < args.length; i++)
			//if it is a valid option
			if(args[i].startsWith("--"))
				//add the pair to the hashmap
				options.put(args[i].substring(2,args[i].length()),args[++i]);
			else
				//otherwise, yell at the user
				throw new IllegalArgumentException("Not a valid argument: " + args[i]);

		return options;
	}

	public static void main(String[] args)
	{
		HashMap options = ArgumentParser.parseArgs(args);
		options.forEach((k,v) -> System.out.println("Argument:\t" + k + "\nValue:\t\t" + v + "\n"));
	}



	// public static void main(String[] args) {
	// 	List<String> argsList = new ArrayList<>();  
	// 	HashMap<String,String> optsList = new HashMap<>();
	// 	List<String> doubleOptsList = new ArrayList<>();

	// 	for (int i = 0; i < args.length; i++) {
	// 		switch (args[i].charAt(0)) {
	// 		case '-':
	// 			if (args[i].length() < 2)
	// 				throw new IllegalArgumentException("Not a valid argument: "+args[i]);
	// 			if (args[i].charAt(1) == '-') {
	// 				if (args[i].length() < 3)
	// 					throw new IllegalArgumentException("Not a valid argument: "+args[i]);
	// 				// --opt
	// 				doubleOptsList.add(args[i].substring(2, args[i].length()));
	// 			} else {
	// 				if (args.length-1 == i)
	// 					throw new IllegalArgumentException("Expected arg after: "+args[i]);
	// 				// -opt
	// 				optsList.put(args[i],args[i+1]);
	// 				i++;
	// 			}
	// 			break;
	// 		default:
	// 			// arg
	// 			argsList.add(args[i]);
	// 			break;
	// 		}
	// 	}
	// 	// etc
	// }
}