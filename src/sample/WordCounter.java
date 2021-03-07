package sample;
import java.io.*;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.stream.Collectors;


public class WordCounter{
	
	private Map<String, Integer> wordCounts;
	
	public WordCounter(){
		wordCounts = new TreeMap<>();
	}

	// Written code to individually parse seperate directories within one folder, and generate different output.txt's per directory
	public void parseDir(String pathToDir) throws IOException{

		List<Path> result;
		// Tells the function to go through the whole path in the directory
		try (Stream<Path> paths = Files.walk(Paths.get(pathToDir), 0)) {

			result = paths.filter(Files::isDirectory).collect(Collectors.toList());

			// Testing variable (not used)
			int x = 0;

			for(Path path : result){
				System.out.println("value of x is " + x);
				System.out.println("Current File Path is " + path.toString());
				File currentDir = new File(path.toString());
				String[] dirNames = currentDir.list();

				// Checks for if directories are directories, and if true, does some stuff to them
				// Currently parses them if they are directories and outputs their words + occurences in their own output.txt
				// The summation of the words is still buggy (it adds all the words over the WHOLE directory still
				for(String dirs : dirNames){
					if(new File(pathToDir + dirs).isDirectory()){

						// Edit this section if you have your own ideas of what you want to do with the sub-directions!!!
						// --------------------- IMPORTANT -------------------

						File current = new File(pathToDir+dirs);
						System.out.println("The Current path is " + pathToDir + dirs);
						parseFile(current);
						File outDir = new File (pathToDir+dirs+".txt");
						System.out.println("Output file is " + pathToDir+dirs+".txt");
						outputWordCount(1, outDir);
					}
				}


				x++;
			}


		}

	}


	public void parseFile(File file) throws IOException{
		//System.out.println("Starting parsing the file:" + file.getAbsolutePath());
		if(file.isDirectory()){
			//parse each file inside the directory
			File[] content = file.listFiles();
			for(File current: content){
				parseFile(current);
			}
		}else{
			Scanner scanner = new Scanner(file);
			// scanning token by token
			Set<String> keys = new HashSet<>();
			while (scanner.hasNext()){
				String token = scanner.next();
				if (isValidWord(token)){
					keys.add(token);
				}
			}
			countWord(keys);
		}
		
	}
	
	private boolean isValidWord(String word){
		String allLetters = "^[a-zA-Z]+$";
		// returns true if the word is composed by only letters otherwise returns false;
		return word.matches(allLetters);
			
	}
	
	private void countWord(Set keys){
		Iterator<String> keyIterator = keys.iterator();
		int sum;
		while(keyIterator.hasNext()){
			String key = keyIterator.next();
			if(wordCounts.containsKey(key)){
				int previous = wordCounts.get(key);
				wordCounts.put(key, previous+1);
			}else{
				wordCounts.put(key, 1);

			}
		}

	}
	
	public void outputWordCount(int minCount, File output) throws IOException{
		System.out.println("Saving word counts to file:" + output.getAbsolutePath());
		System.out.println("Total words:" + wordCounts.keySet().size());
		
		if (!output.exists()){
			output.createNewFile();
			if (output.canWrite()){
				PrintWriter fileOutput = new PrintWriter(output);

				Set<String> keys = wordCounts.keySet();
				Iterator<String> keyIterator = keys.iterator();
				while(keyIterator.hasNext()){
					String key = keyIterator.next();
					int count = wordCounts.get(key);
					// testing minimum number of occurances
					if(count>=minCount){					
						fileOutput.println(key + ": " + count);
					}
				}

				fileOutput.close();
			}
		}else{
			System.out.println("Error: the output file already exists: " + output.getAbsolutePath());
		}
		
	}
	
	//main method
	public static void main(String[] args) {
		
		if(args.length < 2){
			System.err.println("Usage: java WordCounter <inputDir> <outfile>");
			System.exit(0);
		}

		String pathDir = args[0];
		File dataDir = new File(args[0]);
		//File dataDir2 = new File(args[1]);
		//File dataDir3 = new File(args[2]);
		File outFile = new File(args[1]);
		
		WordCounter wordCounter = new WordCounter();
		System.out.println("Hello");
		try{
			wordCounter.parseDir(pathDir);
			//wordCounter.parseFile(dataDir);
			//wordCounter.outputWordCount(1, outFile);
		}catch(FileNotFoundException e){
			System.err.println("Invalid input dir: " + dataDir.getAbsolutePath());
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
	}
	
}