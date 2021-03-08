package sample;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Set;
import java.util.Map;
import java.util.TreeMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.stream.Collectors;


public class WordCounter{
	
	private Map<String, Integer> wordCounts;
	TreeMap<String, Integer> hamCounts = new TreeMap<String, Integer>();
	TreeMap<String, Integer> ham2Counts = new TreeMap<String, Integer>();
	TreeMap<String, Integer> spamCounts = new TreeMap<String, Integer>();


	public WordCounter(){
		wordCounts = new TreeMap<>();
	}

	private TreeMap<String, Integer> merge(TreeMap<String, Integer> ham, TreeMap<String, Integer> ham2) {
		TreeMap<String, Integer> mergedMap = new TreeMap<String, Integer>();
		Set<String> keysHam2 = ham2.keySet();
		Iterator<String> Ham2Iterator = keysHam2.iterator();
		while (Ham2Iterator.hasNext()) {
			String key = Ham2Iterator.next();
			int count2 = ham2.get(key);
			if (ham.containsKey(key)) {
				int count1 = ham.get(key);
				mergedMap.put(key, count1 + count2);
			} else {
				mergedMap.put(key, 1);
			}
		}

		return mergedMap;
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

						if(x == 0) {
							File current = new File(pathToDir + dirs);
							System.out.println("The Current path is " + pathToDir + dirs);
							wordCounts.clear();
							parseFile(current);
							File outDir = new File(pathToDir + dirs + ".txt");
							System.out.println("Output file is " + pathToDir + dirs + ".txt");
							outputWordCount(1, outDir);
							//System.out.println("Size of wordCounts is " + wordCounts.size());
							hamCounts.putAll(wordCounts);
							//System.out.println("Size of hamCounts is" + hamCounts.size());
						}
						else if(x == 1){
							File current = new File(pathToDir+dirs);
							System.out.println("The Current path is " + pathToDir + dirs);
							wordCounts.clear();
							parseFile(current);
							File outDir = new File (pathToDir+dirs+".txt");
							System.out.println("Output file is " + pathToDir+dirs+".txt");
							outputWordCount(1, outDir);
							//System.out.println("Size of wordCounts is " + wordCounts.size());
							ham2Counts.putAll(wordCounts);
							//System.out.println("Size of ham2Counts is " + ham2Counts.size());

						}
						else{
							File current = new File(pathToDir+dirs);
							System.out.println("The Current path is " + pathToDir + dirs);
							wordCounts.clear();
							parseFile(current);
							File outDir = new File (pathToDir+dirs+".txt");
							System.out.println("Output file is " + pathToDir+dirs+".txt");
							outputWordCount(1, outDir);
							//System.out.println("Size of wordCounts is " + wordCounts.size());
							spamCounts.putAll(wordCounts);
							//System.out.println("Size of spamCounts is " + spamCounts.size());
						}
						x++;
					}
				}



			}


		}

		TreeMap<String, Integer> mergedHam = merge(hamCounts,ham2Counts);
		int totalHamCount = hamCounts.size()+ham2Counts.size();
		TreeMap<String, Double> mergedHamProb = new TreeMap<String, Double>();

		System.out.println("TotalHamCount is" + totalHamCount);

		Set<String> mergedHamSet = mergedHam.keySet();
		Iterator<String> mergedHamIterator = mergedHamSet.iterator();
		while (mergedHamIterator.hasNext()) {
			String key = mergedHamIterator.next();
			if(mergedHam.containsKey(key)){
				int previous = mergedHam.get(key);
				//System.out.println("Previous value is " + previous);
				//System.out.println("TOTAL HAM COUNT IS " + totalHamCount);
				DecimalFormat df = new DecimalFormat("0.00000");
				double prob = (double) previous/totalHamCount;
				//System.out.println("Merged HAM Probability is " + df.format(prob));
				mergedHamProb.put(key, prob);
			}else{
				mergedHamProb.put(key, 1.0);
			}

		}

		TreeMap<String, Double> spamProb = new TreeMap<String, Double>();

		Set<String> spamSet = spamCounts.keySet();
		Iterator<String> spamIterator = spamSet.iterator();
		while (spamIterator.hasNext()) {
			String key = spamIterator.next();
			if(spamCounts.containsKey(key)){
				int previous = spamCounts.get(key);
				//System.out.println("Previous value is " + previous);
				//System.out.println("TOTAL HAM COUNT IS " + totalHamCount);
				DecimalFormat df = new DecimalFormat("0.00000");
				double prob = (double) previous/spamCounts.size();
				//System.out.println("SPAM Probability is " + df.format(prob));
				spamProb.put(key, prob);
			}else{
				spamProb.put(key, 1.0);
			}

		}


		/*
		TreeMap<String, Double> probWisSpam = new TreeMap<String, Double>();
		probWisSpam

		while (mergedHamIterator.hasNext()) {
			String key = mergedHamIterator.next();
			if(mergedHam.containsKey(key)){
				int previous = mergedHam.get(key);
				//System.out.println("Previous value is " + previous);
				//System.out.println("TOTAL HAM COUNT IS " + totalHamCount);
				DecimalFormat df = new DecimalFormat("0.00000");
				double prob = (double) previous/totalHamCount;
				//System.out.println("Merged HAM Probability is " + df.format(prob));
				mergedHamProb.put(key, prob);
			}else{
				mergedHamProb.put(key, 1.0);
			}

		}


		private TreeMap<String, Integer> merge(TreeMap<String, Integer> ham, TreeMap<String, Integer> ham2) {
			TreeMap<String, Integer> mergedMap = new TreeMap<String, Integer>();
			Set<String> keysHam2 = ham2.keySet();
			Iterator<String> Ham2Iterator = keysHam2.iterator();
			while (Ham2Iterator.hasNext()) {
				String key = Ham2Iterator.next();
				int count2 = ham2.get(key);
				if (ham.containsKey(key)) {
					int count1 = ham.get(key);
					mergedMap.put(key, count1 + count2);
				} else {
					mergedMap.put(key, 1);
				}
			}


		*/





/*
		for (Map.Entry<String, Double> word : mergedHamProb.entrySet()) {
			String key = word.getKey();
			Double value = word.getValue();
			System.out.println("The key is " + key + " and the occurence is " + value);
		}
*/

	}
/*
	public void getProb(Set<String> probMap){
		double n = 0;
		for(int i = 0; i < probMap.size(); i++){
			n += (Math.log((1-prSW)) - Math.log(prSW))
		}
	}
*/


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
				System.out.println("Currently writing file for " + output.toString() + "The wordCount for this is " + wordCounts.size());
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
		File outFile = new File(args[1]);
		
		WordCounter wordCounter = new WordCounter();
		System.out.println("Hello");
		try{
			wordCounter.parseDir(pathDir);
		}catch(FileNotFoundException e){
			System.err.println("Invalid input dir: " + dataDir.getAbsolutePath());
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
	}
	
}