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
	TreeMap<String, Double> mergedHamProb = new TreeMap<String, Double>();
	TreeMap<String, Double> probWisSpam = new TreeMap<String, Double>();
	TreeMap<String, Double> spamProb = new TreeMap<String, Double>();
	DecimalFormat df = new DecimalFormat("0.00000");
	double prSF = 0;
	double n = 0;
	int hamSize = 0;
	int spamSize = 0;


	public WordCounter(){
		wordCounts = new TreeMap<>();
	}

	private TreeMap<String, Integer> merge(TreeMap<String, Integer> ham, TreeMap<String, Integer> ham2) {
		TreeMap<String, Integer> mergedMap = new TreeMap<String, Integer>();
		mergedMap.putAll(ham);
		Set<String> keysHam2 = ham2.keySet();
		Iterator<String> Ham2Iterator = keysHam2.iterator();
		while (Ham2Iterator.hasNext()) {
			String key = Ham2Iterator.next();
			int count2 = ham2.get(key);
			if (mergedMap.containsKey(key)) {
				int count1 = mergedMap.get(key);
				mergedMap.put(key, count1 + count2);
			} else {
				mergedMap.put(key, count2);
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

						if(x == 0 && dirs.contains("ham")) {
							File current = new File(pathToDir + dirs);
							hamSize += current.list().length;
							System.out.println("The Current path is " + pathToDir + dirs);
							wordCounts.clear();
							parseFile(current);
							File outDir = new File(pathToDir + dirs + ".txt");
							//System.out.println("Output file is " + pathToDir + dirs + ".txt");
							outputWordCount(1, outDir);
							//System.out.println("Size of wordCounts is " + wordCounts.size());
							hamCounts.putAll(wordCounts);
							//System.out.println("Size of hamCounts is" + hamCounts.size());
						}
						else if(x == 1 && dirs.contains("ham2")){
							File current = new File(pathToDir+dirs);
							hamSize += current.list().length;
							System.out.println("The Current path is " + pathToDir + dirs);
							wordCounts.clear();
							parseFile(current);
							File outDir = new File (pathToDir+dirs+".txt");
							//System.out.println("Output file is " + pathToDir+dirs+".txt");
							outputWordCount(1, outDir);
							//System.out.println("Size of wordCounts is " + wordCounts.size());
							ham2Counts.putAll(wordCounts);
							//System.out.println("Size of ham2Counts is " + ham2Counts.size());

						}
						else{
							File current = new File(pathToDir+dirs);
							spamSize += current.list().length;
							System.out.println("The Current path is " + pathToDir + dirs);
							wordCounts.clear();
							parseFile(current);
							File outDir = new File (pathToDir+dirs+".txt");
							//System.out.println("Output file is " + pathToDir+dirs+".txt");
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
		Set<String> mergedHamSet = mergedHam.keySet();
		Iterator<String> mergedHamIterator = mergedHamSet.iterator();
		while (mergedHamIterator.hasNext()) {
			String key = mergedHamIterator.next();
			if(mergedHam.containsKey(key)){
				int previous = mergedHam.get(key);
				double prob = (double) previous/hamSize;;
				mergedHamProb.put(key, prob);
			}else{
				mergedHamProb.put(key, 0.0);
			}

		}

/*
		for (Map.Entry<String, Double> word : mergedHamProb.entrySet()) {
			String key = word.getKey();
			double value = word.getValue();
			System.out.println("The key is " + key + " and the probability is " + df.format(value));
		}
*/

		Set<String> spamSet = spamCounts.keySet();
		Iterator<String> spamIterator = spamSet.iterator();
		while (spamIterator.hasNext()) {
			String key = spamIterator.next();
			if(spamCounts.containsKey(key)){
				int previous = spamCounts.get(key);
				double prob = (double) previous/spamSize;
				spamProb.put(key, prob);
			}else{
				spamProb.put(key, 0.0);
			}

		}

/*
		for (Map.Entry<String, Double> word : spamProb.entrySet()) {
			String key = word.getKey();
			double value = word.getValue();
			System.out.println("The key is " + key + " and the probability is " + df.format(value));
		}
*/
		probWisSpam.putAll(mergedHamProb);
		Set<String> probWisSpamSet = probWisSpam.keySet();
		Iterator<String> probWisIterator = probWisSpamSet.iterator();
		while (probWisIterator.hasNext()) {
			String key = probWisIterator.next();
			if(probWisSpam.containsKey(key) && spamProb.containsKey(key)){
				double prob = spamProb.get(key) / (spamProb.get(key) + probWisSpam.get(key));
				//System.out.println("PrSW is " + df.format(prob));
				probWisSpam.put(key, prob);
			}else if (probWisSpam.containsKey(key) && (!spamProb.containsKey(key))){
				probWisSpam.put(key, 0.0);
			}else {
				probWisSpam.put(key, 1.0);
			}
		}


		System.out.println("The size of ProbWisSpam is " + probWisSpam.size());
/*
		for (Map.Entry<String, Double> word : probWisSpam.entrySet()) {
			String key = word.getKey();
			double value = word.getValue();
			System.out.println("The key is " + key + " and the probability is " + df.format(value));
		}
*/
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
			//System.out.println("Test File " + file.toString());
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

	//Function used to parse 2nd directory and calculate probability of spam
	// This section also needs work


	public void parseFileProb(File file) throws IOException{
		//System.out.println("Starting parsing the file:" + file.getAbsolutePath());
		if(file.isDirectory()){
			//parse each file inside the directory
			File[] content = file.listFiles();
			for(File current: content){
				parseFileProb(current);
			}
		}else{
			Scanner scanner2 = new Scanner(file);
			prSF = 0;
			n = 0;
			System.out.println("Current file is " + file.toString());
			// scanning token by token
			Set<String> keys = new HashSet<>();
			while (scanner2.hasNext()){
				String token = scanner2.next();
				if (isValidWord(token)){
					keys.add(token);
				}
			}
			calcProb(keys);
			//System.out.println("File " + file.toString() + " has a prSF of" + df.format(prSF));
		}
	}
	
	private boolean isValidWord(String word){
		String allLetters = "^[a-zA-Z]+$";
		// returns true if the word is composed by only letters otherwise returns false;
		return word.matches(allLetters);
			
	}


	// This section Needs work
	// The math is incorrect I believe

	private void calcProb(Set keys){
		//prSF = 0;
		Iterator<String> keyIterator = keys.iterator();
		while(keyIterator.hasNext()) {
			String key = keyIterator.next();
			if(probWisSpam.containsKey(key)){
				if(probWisSpam.get(key) == 0){

				}else{
					//System.out.println("ProbwiSpam at key is " + key + " " + probWisSpam.get(key));
					n += Math.log(1-(probWisSpam.get(key))) - Math.log(probWisSpam.get(key));
					System.out.println("doing big math n is " + n);
				}

			}
		}
		prSF = 1/(1+( Math.pow(Math.E,n)));
		System.out.println("calculated PRSF is " + prSF);

	}

	/*   public ArrayList<TestFile> loopFolder(File folder, Map<String, Double> map) throws IOException {
        ArrayList<TestFile> testFileList = new ArrayList<>();
        String actualClass = folder.getName();
        if(folder.isDirectory()){
            //parse each file inside the directory
            File[] content = folder.listFiles();
//            System.out.println(content.length);
            for(File current: content){
                testFileList.add(test(current,map,actualClass));
            }
        } else {
            return null;
        }
        return testFileList;
    }
}
*/


/*
	//calculate and print the accuracy and precision
	double numTrue = 0;
	double numFalsePos = 0;
	double numTruePos = 0;
        for (TestFile entry:testFiles) {
		String actualClass = entry.getActualClass();
		double prob = entry.getRawProb();
		if(actualClass.equals("ham") && prob < 0.5){
			numTrue++;
		} if (actualClass.equals("spam") && prob > 0.5){
			numTrue++;
			numTruePos++;
		} if(actualClass.equals("ham") && prob > 0.5){
			numFalsePos++;
		}
	}
*/
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
		File pathDir2 = new File(args[1]);
		File dataDir = new File(args[0]);
		File outFile = new File(args[2]);
		
		WordCounter wordCounter = new WordCounter();
		System.out.println("Hello");
		try{
			wordCounter.parseDir(pathDir);
			//wordCounter.parseFile(dataDir);
			wordCounter.parseFileProb(pathDir2);



		}catch(FileNotFoundException e){
			System.err.println("Invalid input dir: " + dataDir.getAbsolutePath());
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
	}
	
}