package edu.sdsu.cs;
	

	/*CS 310 T-Th 5:30 PM
	  Program 1
	  Stefan Carnahan
	  Melissa Conley
	 */
	import java.io.*;
	import java.nio.file.Files;
	import java.nio.file.Path;
	import java.nio.file.Paths;
	import java.util.*;
	

	public class App {
	    public static void main(String[] args) throws IOException {
	        String inputPath = System.getProperty("user.dir");
	        Path startPath = Paths.get(inputPath);
	

	        if (args.length != 0) {
	            startPath = Paths.get(args[0]);
	            if (!Files.exists(startPath)) {
	                startPath = Paths.get(inputPath);
	                System.out.println("Invalid argument. " +
	                        "Running program from current directory: " + startPath);
	            }
	        }
	

	        ArrayList<File> fileList = fileAdder(startPath);
	        fileProcessor(fileList);
	    }
	

	    private static ArrayList<File> fileAdder(Path inputFile) {
	        File curFile = inputFile.toFile();
	        File parentDirectory = curFile.getParentFile();
	        File curDirectory[] = parentDirectory.listFiles();
	        boolean filesToGo = true;
	

	        ArrayList<File[]> directoryList = new ArrayList<>();
	        int directoryIndex = 0;
	

	        ArrayList<File> fileList = new ArrayList<>();
	        int fileIndex = 0;
	

	        while (filesToGo) {
	            if (curDirectory != null) {
	                for (File addedFile : curDirectory) {
	                    String curFileName = addedFile.getName().toLowerCase();
	                    if (curFileName.endsWith(".txt")
	                            || curFileName.endsWith(".java")) {
	                        System.out.println("Found "
	                                + addedFile.toString() + ".");
	                        fileList.add(fileIndex, addedFile);
	                        fileIndex++;
	                    }
	                    if (addedFile.isDirectory()) {
	                        File newFile = new File(addedFile.getAbsolutePath());
	                        File nextFileDirectory[] = newFile.listFiles();
	                        directoryList.add(directoryIndex, nextFileDirectory);
	                    }
	                }
	                if (directoryList.isEmpty()) {
	                    filesToGo = false;
	                } else {
	                    curDirectory = directoryList.get(directoryIndex);
	                    directoryList.remove(directoryIndex);
	                }
	            }
	        }
	        return fileList;
	    }
	

	    private static void fileProcessor(ArrayList<File> fileList)
	            throws IOException {
	        //File Processor
	        int processIndex;
	        File processedFile;
	        String fileForStats;
	        for (processIndex = 0; processIndex < fileList.size(); processIndex++) {
	            processedFile = fileList.get(processIndex);
	            BufferedReader reader =
	                    new BufferedReader(new FileReader(processedFile));
	            System.out.println("Reading " + processedFile.toString() + "...");
	            String curLine;
	            StringBuilder sb = new StringBuilder();
	            while ((curLine = reader.readLine()) != null) {
	                sb.append(curLine);
	                sb.append("\n");
	            }
	            fileForStats = sb.toString();
	            String statsForFile = getStats(fileForStats);
	            createStatsFile(processedFile, statsForFile);
	        }
	    }
	

	    private static void createStatsFile(File processedFile, String statsForFile)
	            throws IOException {
	        String addStats = processedFile.toString() + ".stats";
	        File targetFile = new File(addStats);
	        FileOutputStream fos = new FileOutputStream(targetFile, false);
	        DataOutputStream dos =
	                new DataOutputStream(new BufferedOutputStream(fos));
	        System.out.println("Writing " + targetFile + "...");
	        dos.writeUTF(statsForFile);
	        dos.close();
	    }
	

	    private static String getStats(String fileText) {
	        ArrayList<Integer> charCount = new ArrayList<>();
	        StringTokenizer tokenizer = new StringTokenizer(fileText);
	        ArrayList<Integer> tokenMatches = new ArrayList<>();
	        ArrayList<String> tokensWithMostOcc;
	        double averageLineLength;
	        int tokenCount;
	        ArrayList<String> tokensInFileText = new ArrayList<>();
	

	        //Put the tokens into the ArrayList:
	        while (tokenizer.hasMoreTokens()) {
	            tokensInFileText.add(tokenizer.nextToken());
	            tokenMatches.add(0);
	        }
	

	        //Metric 1:Print length of longest line in string
	        charCount.add(0);
	        charCount = lengthsOfEachLine(charCount, fileText);
	        charCount = sortArrayList(charCount);
	        String stats = ("Length of longest line = "
	                + charCount.get(charCount.size() - 1));
	

	        //Metric 2:Print average line length
	        averageLineLength = findAverageLineLength(charCount);
	        stats += "\nAverage line length = " + averageLineLength;
	

	        //Metric 3: Number of unique space-delineated tokens (case-sensitive)
	        tokenMatches = findNumberOfTokenMatches(fileText);
	        ArrayList<Integer> thirdTokenMatches = new ArrayList<>();
	        for (int i = 0; i < tokenMatches.size()
	                && tokenMatches.size() != 0; i++) {
	            thirdTokenMatches.add(tokenMatches.get(i));
	        }
	        int numberOfUniqueTokens = 0;
	        if (tokenMatches.size() != 0) {
	            numberOfUniqueTokens =
	                    findNumberOfUniqueTokens(tokenMatches, tokensInFileText);
	        }
	        stats += ("\nNumber of unique space-delineated tokens " +
	                "(case-sensitive): " + numberOfUniqueTokens);
	

	        //Metric 4: Number of unique space-delineated tokens (case-insensitive)
	        int allCapsNumberOfUniqueTokens;
	        String fileText2 = fileText;
	        fileText2 = fileText2.toUpperCase();
	        ArrayList<Integer> allCapsTokenMatches;
	        allCapsTokenMatches = findNumberOfTokenMatches(fileText);
	        ArrayList<String> allCapsTokensInFileText = new ArrayList<>();
	        //Put the tokens into the ArrayList:
	        StringTokenizer tokenizer2 = new StringTokenizer(fileText2);
	        while (tokenizer2.hasMoreTokens()) {
	            allCapsTokensInFileText.add(tokenizer2.nextToken());
	        }
	        allCapsTokenMatches = findNumberOfTokenMatches(fileText2);
	        allCapsNumberOfUniqueTokens =
	                findNumberOfUniqueTokens(allCapsTokenMatches,
	                        allCapsTokensInFileText);
	        stats += ("\nNumber of unique space-delineated tokens " +
	                "(case-insensitive): " + allCapsNumberOfUniqueTokens);
	

	        //Metric 5:Print number of tokens
	        tokenCount = findNumberOfTokens(fileText);
	        stats += ("\nNumber of all space-delineated tokens: " + tokenCount);
	

	        //Metric 6: Most frequently occurring tokens:
	        int maxOcc = findMax(allCapsTokenMatches);
	        tokensWithMostOcc =
	                mostOccs(allCapsTokenMatches, allCapsTokensInFileText, maxOcc);
	        stats += ("\nMost frequently occurring token(s): " + tokensWithMostOcc);
	

	        //Metric 7: Count of most frequently occurring token (case-insensitive)
	        maxOcc = findMax(allCapsTokenMatches);
	        stats += ("\nCount of most frequently occurring token " +
	                "(case-insensitive): " + maxOcc);
	

	        //Metric 8: 10 most frequent tokens with their counts (case-insensitive)
	        ArrayList<Integer> secondAllCapsTokenMatches = new ArrayList<>();
	        for (int i = 0; i < allCapsTokenMatches.size(); i++) {
	            secondAllCapsTokenMatches.add(allCapsTokenMatches.get(i));
	        }
	        ArrayList<Integer> tenMaxOcc;
	        ArrayList<String> tenTokensWithMostOcc;
	        tenMaxOcc = findTenMax(allCapsTokenMatches);
	        tenTokensWithMostOcc = tenMostOccs(secondAllCapsTokenMatches,
	                allCapsTokensInFileText, tenMaxOcc);
	        stats += ("\n10 most frequent tokens with their counts " +
	                "(case-insensitive) = " + tenTokensWithMostOcc);
	

	        //Metric 9:  10 least frequent tokens with their counts
	        // (case-insensitive)
	        ArrayList<Integer> thirdAllCapsTokenMatches = new ArrayList<>();
	        for (int i = 0; i < secondAllCapsTokenMatches.size(); i++) {
	            thirdAllCapsTokenMatches.add(secondAllCapsTokenMatches.get(i));
	        }
	        ArrayList<Integer> tenMinOcc;
	        ArrayList<String> tenTokensWithLeastOcc;
	        tenMinOcc = findTenMin(secondAllCapsTokenMatches);
	        tenTokensWithLeastOcc = tenLeastOccs(thirdAllCapsTokenMatches,
	                allCapsTokensInFileText, tenMinOcc);
	        stats += ("\n10 least frequent tokens with their counts " +
	                "(case-insensitive) = " + tenTokensWithLeastOcc);
	        return stats;
	    }
	

	    //Seperate string into lines and find lengths of each:
	    private static ArrayList<Integer>
	    lengthsOfEachLine(ArrayList<Integer> charCount, String fileText) {
	        int charIndex;
	        int lineIndex = 0;
	        int temporaryforIncrementing = 0;
	

	        //Iterate through every character in the text:
	        for (charIndex = 0; charIndex < fileText.length(); charIndex++) {
	            //Split it at every '\n' you encounter by moving to the next
	            if (fileText.charAt(charIndex) == '\n') {
	                lineIndex++;
	                charCount.add(0);
	                temporaryforIncrementing = 0;
	            } else {
	                temporaryforIncrementing++;
	                charCount.add(lineIndex, temporaryforIncrementing);
	                charCount.remove(lineIndex + 1);
	            }
	        }
	        return charCount;
	    }
	

	    //Sort charCount ArrayList:
	    private static ArrayList<Integer>
	    sortArrayList(ArrayList<Integer> arrayToSort) {
	        int lengthOfCharCount = arrayToSort.size();
	        int sortIndex;
	        int smallestIndex;
	        int currentIndex;
	        int swapVar;
	

	        for (sortIndex = 0; sortIndex < lengthOfCharCount - 1; sortIndex++) {
	            smallestIndex = sortIndex;
	            for (currentIndex = sortIndex + 1;
	                 currentIndex < lengthOfCharCount; currentIndex++) {
	                if (arrayToSort.get(currentIndex)
	                        < arrayToSort.get(smallestIndex)) {
	                    smallestIndex = currentIndex;
	                }
	            }
	            swapVar = arrayToSort.get(smallestIndex);
	            arrayToSort.add(smallestIndex, arrayToSort.get(sortIndex));
	            arrayToSort.remove(smallestIndex + 1);
	            arrayToSort.add(sortIndex, swapVar);
	            arrayToSort.remove(sortIndex + 1);
	        }
	        return arrayToSort;
	    }
	

	

	    private static double findAverageLineLength(ArrayList<Integer> charCount) {
	        int sumIndex;
	        int sum = 0;
	        double averageLineLength;
	

	        for (sumIndex = 0; sumIndex < charCount.size(); sumIndex++) {
	            sum = sum + charCount.get(sumIndex);
	        }
	        averageLineLength = (double) sum / charCount.size();
	        return averageLineLength;
	    }
	

	    private static int findNumberOfTokens(String fileText) {
	        StringTokenizer tokenizer = new StringTokenizer(fileText);
	        int tokenCount = 0;
	

	        while (tokenizer.hasMoreTokens()) {
	            tokenCount++;
	            tokenizer.nextToken();
	        }
	        return tokenCount;
	    }
	

	    private static ArrayList<Integer>
	    findNumberOfTokenMatches(String fileText) {
	        StringTokenizer tokenizer = new StringTokenizer(fileText);
	        ArrayList<Integer> tokenMatches = new ArrayList<>();
	        ArrayList<String> tokensInFileText = new ArrayList<>();
	        int matchesTemporary = 0;
	        int tokenIndex1;
	

	        //Put the tokens into the ArrayList:
	        while (tokenizer.hasMoreTokens()) {
	            tokensInFileText.add(tokenizer.nextToken());
	            tokenMatches.add(0);
	        }
	        //Iterate through every token:
	        for (tokenIndex1 = 0; tokenIndex1
	                < tokensInFileText.size() - 1; tokenIndex1++) {
	            //Get number of matches from the tokens AFTER the current token:
	            for (int tokenIndex2 = tokenIndex1 + 1;
	                 tokenIndex2 < tokensInFileText.size(); tokenIndex2++) {
	                if (tokensInFileText.get(tokenIndex1).equals
	                        (tokensInFileText.get(tokenIndex2))) {
	                    matchesTemporary++;
	                }
	            }
	            //Get number of matches from the tokens BEFORE the current token:
	            for (int tokenIndex2 = tokenIndex1 - 1;
	                 tokenIndex2 >= 0; tokenIndex2--) {
	                if (tokensInFileText.get(tokenIndex1).equals
	                        (tokensInFileText.get(tokenIndex2))) {
	                    matchesTemporary++;
	

	                }
	            }
	            tokenMatches.add(tokenIndex1, matchesTemporary + 1);
	            tokenMatches.remove(tokenIndex1 + 1);
	            matchesTemporary = 0;
	        }
	        //Check last token: Same thing but the for loop starts
	        // from the end and goes backwards
	        //(This is seperate from the rest
	        // because this one doesn't check any tokens after it)
	        for (int tokenIndex2 = tokenIndex1 - 1;
	             tokenIndex2 >= 0; tokenIndex2--) {
	            if (tokensInFileText.get(tokenIndex1).equals
	                    (tokensInFileText.get(tokenIndex2))) {
	                matchesTemporary++;
	            }
	        }
	        tokenMatches.add(tokenIndex1, matchesTemporary + 1);
	

	        if (tokenIndex1 + 1 < tokenMatches.size()) {
	            tokenMatches.remove(tokenIndex1 + 1);
	        }
	        return tokenMatches;
	    }
	

	    private static int findMax(ArrayList<Integer> list) {
	        int maxSoFar = 0;
	

	        for (int i = 0; i < list.size(); i++) {
	            if (list.get(i) > maxSoFar) {
	                maxSoFar = list.get(i);
	            }
	        }
	        return maxSoFar;
	    }
	

	    private static int findMin(ArrayList<Integer> list) {
	        int minSoFar = 999999999;
	

	        for (int i = 0; i < list.size(); i++) {
	            if (list.get(i) < minSoFar) {
	                minSoFar = list.get(i);
	            }
	        }
	        return minSoFar;
	    }
	

	    private static ArrayList<String>
	    mostOccs(ArrayList<Integer> tokenMatches,
	             ArrayList<String> tokens, int max) {
	        ArrayList<String> resultTokens = new ArrayList<>(1);
	

	        for (int index = 0; index < tokenMatches.size(); index++) {
	            boolean equalTest = false;
	            if (tokenMatches.get(index) == max) {
	                if (index == 0 && tokens.size() != 0) {
	                    resultTokens.add(tokens.get(index));
	                } else {
	                    for (int index2 = index - 1; index2 >= 0; index2--) {
	                        if (equalTest == false) {
	                            equalTest = tokens.get(index).equals(tokens.get
	                                    (index2));
	                        }
	                    }
	                    if (equalTest == false && tokens.size() != 0) {
	                        resultTokens.add(tokens.get(index));
	                    }
	                }
	            }
	        }
	        return resultTokens;
	    }
	

	    private static ArrayList<Integer> findTenMax(ArrayList<Integer> list) {
	

	        int indexOfObjectToRemove;
	        ArrayList<Integer> maxList = new ArrayList<>();
	        for (int i = 0; list.size() > 0; i++) {
	            int temp = findMax(list);
	            maxList.add(temp);
	            while (list.indexOf(temp) != -1) {
	                indexOfObjectToRemove = list.indexOf(temp);
	                list.remove(indexOfObjectToRemove);
	            }
	            if (maxList.size() > 10) {
	                maxList.remove(maxList.size() - 1);
	            }
	        }
	        return maxList;
	    }
	

	    private static ArrayList<Integer> findTenMin(ArrayList<Integer> list) {
	        int indexOfObjectToRemove;
	        ArrayList<Integer> minList = new ArrayList<>();
	

	        for (int i = 0; list.size() > 0; i++) {
	            int temp = findMin(list);
	            minList.add(temp);
	            while (list.indexOf(temp) != -1) {
	                indexOfObjectToRemove = list.indexOf(temp);
	                list.remove(indexOfObjectToRemove);
	            }
	        }
	        while (minList.size() > 10) {
	            minList.remove(minList.size() - 1);
	        }
	        return minList;
	    }
	

	    private static ArrayList<String>
	    tenMostOccs(ArrayList<Integer> tokenMatches,
	                ArrayList<String> tokens, ArrayList<Integer> tenMaxs) {
	        ArrayList<String> maxListTokens = new ArrayList<>();
	

	        for (int i = 0; i < tenMaxs.size(); i++) {
	            maxListTokens.addAll(mostOccs(tokenMatches,
	                    tokens, tenMaxs.get(i)));
	            while (maxListTokens.size() > 10) {
	                maxListTokens.remove(maxListTokens.size() - 1);
	            }
	        }
	        return maxListTokens;
	    }
	

	    private static ArrayList<String>
	    tenLeastOccs(ArrayList<Integer> tokenMatches,
	                 ArrayList<String> tokens, ArrayList<Integer> tenMins) {
	        ArrayList<String> minListTokens = new ArrayList<>();
	

	        for (int i = 0; i < tenMins.size(); i++) {
	            minListTokens.addAll(mostOccs(tokenMatches,
	                    tokens, tenMins.get(i)));
	            while (minListTokens.size() > 10) {
	                minListTokens.remove(minListTokens.size() - 1);
	            }
	        }
	        return minListTokens;
	    }
	

	    private static int findNumberOfUniqueTokens
	            (ArrayList<Integer> tokenMatches,
	             ArrayList<String> tokensInFileText) {
	        ArrayList<String> uniqueTokens;
	        int numOfOccs = 1;
	

	        if (tokenMatches.size() != 0) {
	            uniqueTokens = mostOccs(tokenMatches, tokensInFileText, numOfOccs);
	            return uniqueTokens.size();
	        } else return 0;
	    }
	}

