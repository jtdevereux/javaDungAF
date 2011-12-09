
//	Copyright 2011 University of Dundee
//
//	Licensed under the Apache License, Version 2.0 (the "License");
//	you may not use this file except in compliance with the License.
//	You may obtain a copy of the License at
//
//	http://www.apache.org/licenses/LICENSE-2.0
//
//	Unless required by applicable law or agreed to in writing, software
//	distributed under the License is distributed on an "AS IS" BASIS,
//	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//	See the License for the specific language governing permissions and
//	limitations under the License.

package javaDungAF.tests;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import javaDungAF.DungAF;

/**
 * A class to test the semantics-related functionality of {@link javaDungAF.DungAF}, using ASPARTIX (v.0.9).
 *
 * <p> This class allows the output of {@link javaDungAF.DungAF} to be checked against the output of ASPARTIX. One or 
 * more semantics may be tested at a time, and the tests may involve any number of randomly-generated AFs of specified 
 * dimensions (involving up to 26 arguments). ASPARTIX implements all of the semantics implemented by 
 * {@link javaDungAF.DungAF}, except the eager semantics and the preferred-sceptical semantics. </p>
 * 
 * <p> This class requires that the DLV system is installed, and should be executed via a script. That script must -
 * 
 * <ol>
 * <li> run this class, in order (i) to generate AFs; (ii) to write those AFs to input-files for the DLV system; and
 * (iii) to write {@link javaDungAF.DungAF}'s interpretation of those AFs to results-files; </li>
 * <li> call the DLV system to obtain ASPARTIX's interpretations of those AFs, writing those interpretations to the 
 * results-files; and </li>
 * <li> run this class again to compare the interpretations of {@link javaDungAF.DungAF} and ASPARTIX. </li>
 * </ol>
 *
 * A template bash-script is provided <a href="../../testWithAspartix.sh">here</a>. This script ensures that the 
 * results provided by ASPARTIX are written to results-files in a particular format. The functionality of this class  
 * depends on certain aspects of that format. </p>
 *
 * <p> Any disagreement between {@link javaDungAF.DungAF} and ASPARTIX is reported via the standard output stream,   
 * along with details of the AF and the semantics involved. </p>
 *
 */
public class TestWithAspartix {
	
	/**
	 * The number of AFs to be used in testing.
	 */
	static int iterations;
	
	/**
	 * The least number of arguments each AF must contain.
	 */
	static int minArgs;
	
	/**
	 * The greatest number of arguments each AF may contain. Must be no greater than 26.
	 */
	static int maxArgs;
	
	/**
	 * The least number of attacks each AF must contain.
	 */
	static int minAtts;
	
	/**
	 * The greatest number of attacks each AF may contain.
	 */
	static int maxAtts;
	
	/**
	 * The semantics with respect to which tests are to be done.
	 */
	static String[] semanticsToTest;
	
	/**
	 * The unique-extension semantics implemented by {@link javaDungAF.DungAF}.
	 */
	static final HashSet<String> UNIQUE_EXTENSION_SEMANTICS = new HashSet<String>(Arrays.asList("eager","grounded",
																								"ideal",
																								"preferredSceptical"));
	
	/**
	 * The semantics implemented by {@link javaDungAF.DungAF}.
	 */
	static final HashSet<String> IMPLEM_SEMANTICS = new HashSet<String>(Arrays.asList("admissible","complete","eager",
																					  "grounded", "ideal", "preferred",
																					  "preferred-sceptical", 
																					  "stable", "semiStable"));
	/**
	 * The name of the directory for the input-files for the DLV system.
	 */
	static String dlvDirectory;
	
	/**
	 * The name of the directory for the results-files.
	 */
	static String resultsDirectory;
		
	/**
	 * Sets fields and calls {@link #writeFilesWithDungAF() writeFilesWithDungAF()} or 
	 * {@link #compareResults() compareResults()}, depending on the second item in {@code args}. In the latter case, the
	 * method also reports whether any discrepancies were found in the results, via the standard output stream.
	 *
	 * @param args several {@code String}s. This class must be executed twice by a script, as described
	 * {@linkplain TestWithAspartix above}. On the first occasion, at least 9 parameters are required, all but one of 
	 * which correspond to fields of this class - 
	 * <br/><br/>
	 * <ol>
	 * <li> the number of AFs to be used (i.e. the number of tests) ({@link #iterations iterations}); </li>
	 * <li> "write"; </li>
	 * <li> the least number of arguments each AF must contain ({@link #minArgs minArgs} must not exceed 26); </li>
	 * <li> the greatest number of arguments each AF may contain ({@link #maxArgs maxArgs}); </li>
	 * <li> the least number of attacks each AF must contain ({@link #minAtts minAtts}); </li>
	 * <li> the greatest number of attacks each AF may contain ({@link #maxAtts maxAtts}); </li>
	 * <li> the name of the directory for the input-files for the DLV system ({@link #dlvDirectory dlvDirectory}); </li>
	 * <li> the name of the directory for the results-files ({@link #resultsDirectory resultsDirectory}); </li>
	 * <li> a member of {@link #IMPLEM_SEMANTICS IMPLEM_SEMANTICS} ({@link #semanticsToTest semanticsToTest}); </li>					
	 * <li> another member of {@link #IMPLEM_SEMANTICS IMPLEM_SEMANTICS} ({@link #semanticsToTest semanticsToTest}); 
	 * </li>
	 * <li> etc. ({@link #semanticsToTest semanticsToTest}). </li>
	 * </ol>
	 * <br/>
	 * On the second occasion, there must be only 3 parameters:
	 * <br/><br/>
	 * <ol>
	 * <li> the number of AFs to be used (as above); </li>
	 * <li> "check"; and </li>
	 * <li> the path to the directory for the results-files (as above). </li>
	 * </ol>
	 * <br/>
	 * @throws IllegalArgumentException if the parameters are not as specified above.
	 */	
	public static void main(String args[]) throws IllegalArgumentException {
						
		boolean testPassed;
		
		try {
			iterations = Integer.parseInt(args[0]);
			
			if (args[1].equals("write")) {
				if (Integer.parseInt(args[2]) > 26) {
					throw new IllegalArgumentException("by 'TestWithAspartix.main([..., minArgs,...])' --- "
													   + "'minArgs' > 26.");	
				}
				
				minArgs = Integer.parseInt(args[2]);
				maxArgs = Integer.parseInt(args[3]);
				minAtts = Integer.parseInt(args[4]);
				maxAtts = Integer.parseInt(args[5]);
				dlvDirectory = args[6];
				resultsDirectory = args[7];
				
				semanticsToTest = Arrays.copyOfRange(args, 8, args.length);
				
				if (IMPLEM_SEMANTICS.containsAll(Arrays.asList(semanticsToTest))) {
					writeFilesWithDungAF();
				} else {
					throw new IllegalArgumentException(
								"at least one of the specified semantics is not implemented by DungAF.class.");	
				}
				
			} else if (args[1].equals("check")) {
				resultsDirectory = args[2];
				testPassed = compareResults();
				System.out.println("TestWithAspartix: " + (testPassed ? "passed." : "*failed*!"));
				
			} else {
				throw new IllegalArgumentException("by 'TestWithAspartix.main(String...)' --- "
												   + "second parameter is neither \"write\" nor \"check\".");	
			}
		} catch (Exception e) {
			throw new RuntimeException();	// should never happen.
		}
		
	}
	
	/**
	 * Generates AFs, writes those AFs to results-files and to input-files for the DLV system, and writes 
	 * {@link javaDungAF.DungAF}'s interpretations of those AFs to the results-files.
	 * 
	 * <p> The relevant fields determine the number and dimensions of the AFs which are generated. </p>
	 */
	private static void writeFilesWithDungAF() throws IllegalAccessException, NoSuchMethodException, 
													  InvocationTargetException, IOException {
		
		DungAF tempAF;
		HashSet<String> argPool = new HashSet<String>();
		HashSet<HashSet<String>> extsSet;
		String nl = System.getProperty("line.separator"); // as recommended by Java API docs, instead of "\n".
		String heading;
		String nextLine;
		String methodName;
		Method semanticsMeth;
		boolean nextSemanticsIsUniqueExtension;
		File inputFile;
		File resultsFile;
		BufferedWriter toFile;
				
		/* populate argPool with a--z (ASPARTIX requires that arguments begin with lower-case letters). */
		for(int i = 97; i <= 122; i++) { 
			argPool.add(Character.toString((char) i));
		}
		
		for(int i = 1; i <= iterations; i++){
			inputFile = new File(dlvDirectory, ("af_" + i + "_for_aspartix.dl"));
			resultsFile = new File(resultsDirectory, ("af_" + i + "_results.dl"));
			inputFile.delete();
			resultsFile.delete();
			
			/* write AFs to files in format required by the DLV system. It would be possible to use the same file for 
			 both an AF and its extensions, but it would not be straightforward. The script calls the DLV system not 
			 just once, but for each of the specified semantics; and on each occasion, ASPARTIX's interpretation is 
			 written to file. If it was written to the input-file, it would need to be commented-out before the 
			 input-file was passed to the DLV system the next time. */
			tempAF = DungAF.getRandomDungAF(minArgs,maxArgs,minAtts,maxAtts,argPool);
			
			
			toFile = new BufferedWriter(new FileWriter(inputFile));
			do {				
				toFile.write(nl + nl);
				toFile.write("% ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + nl);
				toFile.write("% ~~~~~~~~~~~~ argumentation framework #" + i + " ~~~~~~~~~~~~" + nl);
				toFile.write("% ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + nl + nl);
				
				for(String nextArg : tempAF.getArgs()) {
					toFile.write("arg("+nextArg+")." + nl);
				}				
				toFile.write(nl);
				for(String[] nextAtt : tempAF.getAtts()) {
					toFile.write("att(" + nextAtt[0] + "," + nextAtt[1] + ")." + nl);
				}
				
				if (!resultsFile.exists())  {
					toFile.close();
					toFile = new BufferedWriter(new FileWriter(resultsFile));
				} else {
					break;
				}
				
			} while (true);
			
			/* for each of the specified semantics, write the extensions prescribed for the AF by DungAF.class */
			toFile.write(nl + nl);
			toFile.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + nl);
			toFile.write("    interpretation of AF #" + i + " (DungAF.class) " + nl);
			toFile.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + nl);
			
			for (String nextSemantics : semanticsToTest) {
				nextSemanticsIsUniqueExtension = UNIQUE_EXTENSION_SEMANTICS.contains(nextSemantics);
				
				/* build method name*/
				methodName = "get" + nextSemantics.substring(0,1).toUpperCase() + nextSemantics.substring(1);
				methodName += nextSemantics.equals("admissible") ? "Set" : "Ext";
				methodName += nextSemanticsIsUniqueExtension ? "" : "s";
				
				extsSet = new HashSet<HashSet<String>>();
				
				/* get extensions */
				semanticsMeth = tempAF.getClass().getMethod(methodName);
				
				if(nextSemanticsIsUniqueExtension){
					extsSet.add((HashSet<String>) semanticsMeth.invoke(tempAF));
				} else {
					extsSet = (HashSet<HashSet<String>>) semanticsMeth.invoke(tempAF);
				}
			
				/* write extensions */
				heading = "*" + nextSemantics + (nextSemantics.equals("admissible") ? "* set" : "* extension");
				if (nextSemanticsIsUniqueExtension) {
					heading = "the " + heading;
				} else {
					heading = (extsSet.size() != 1) ? extsSet.size() + " " + heading + "s" :
					extsSet.size() + " " + heading;	
				}
				heading = "DungAF.class - " + heading;
				
				toFile.write(nl + nl);
				toFile.write("-----------------------------------------------" + nl);
				toFile.write(heading+":" + nl + nl);
				for (HashSet<String> nextSet : extsSet) {
					toFile.write("" + nextSet + "" + nl);
				}
				toFile.write(nl + "-----------------------------------------------" + nl);
			}
			
			toFile.write(nl + nl);
			
			/* the DLV system will write ASPARTIX's interpretation to the same file. */
			toFile.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + nl);				
			toFile.write("    interpretation of AF #" + i + " (ASPARTIX)" + nl);
			toFile.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + nl + nl);
			
			toFile.close();
		}
	}
	
	/**
	 * Compares interpretations of AFs provided by {@link javaDungAF.DungAF} against interpretations provided by
	 * ASPARTIX. Details of any discrepancies are reported via the standard output stream.
	 *
	 * <p> The interpretations are read from the results-files which have been generated by 
	 * {@link #writeFilesWithDungAF() writeFilesWithDungAF()} and by the DLV system. </p>
	 *
	 * <p> This method also deletes the (redundant) input-files generated by 
	 * {@link #writeFilesWithDungAF() writeFilesWithDungAF()}, but does not delete the results-files. </p>  
	 */	
	private static boolean compareResults() throws FileNotFoundException, IOException {
		
		String nl = System.getProperty("line.separator"); // as recommended by Java API docs, instead of "\n".
		String semantics;
		String nextLine;
		HashSet<String> extension;
		HashSet<HashSet<String>> extsSet;
		HashMap<String,HashSet<HashSet<String>>> dAFSemanticsToExtSets;
		HashMap<String,HashSet<HashSet<String>>> aspSemanticsToExtSets;
		BufferedReader fromResultsFile;
		File inputFile;
		boolean testPassed = true;
		
		/* for each resultsFile... */		 
		for (int i = 1; i<= iterations; i++){			
			/* ...first tidy away its corresponding input-file, which is no longer needed... */
			inputFile = new File(dlvDirectory, ("af_" + i + "_for_aspartix.dl"));
			inputFile.delete();

			
			/* then check the DungAF.class results against the ASPARTIX results. */
			dAFSemanticsToExtSets = new HashMap<String,HashSet<HashSet<String>>>();
			aspSemanticsToExtSets = new HashMap<String,HashSet<HashSet<String>>>();
			
			fromResultsFile = new BufferedReader(new FileReader(resultsDirectory + "/af_" + i + "_results.dl"));
			
			/* first read the sets of extensions provided by DungAF.class */
			
			/* skip lines preceding the first section-begins( "---...") line. */
			do {
				nextLine = fromResultsFile.readLine();
			} while (nextLine.isEmpty() || nextLine.charAt(0) != '-');
			
			do {
				if (nextLine.charAt(0) == '~') {
					break;	// '~' character indicates that heading of ASPARTIX-part has been reached. 
				} else {
					nextLine = fromResultsFile.readLine();  // skip section-begins( "---...") line. 
					semantics = nextLine.substring(nextLine.indexOf("*")+1, nextLine.lastIndexOf("*"));
					extsSet = new HashSet<HashSet<String>>();
					
					fromResultsFile.readLine();				// skip blank line preceding extension(s).
					nextLine = fromResultsFile.readLine();	// nextLine is an extension or the blank line.
					
					while (!nextLine.isEmpty()) {
						nextLine = nextLine.substring(nextLine.indexOf("[")+1, nextLine.lastIndexOf("]"));
						
						extension = new HashSet<String>();						
						for (int k = 0; k < nextLine.length(); k+=3) {
							extension.add(nextLine.substring(k,k+1));
						}
						extsSet.add(extension);
						
						nextLine = fromResultsFile.readLine(); // proceed to next extension or blank line.
					}
					
					dAFSemanticsToExtSets.put(semantics, extsSet);
					
					/* skip section-ends line and blank line(s) following section. */
					fromResultsFile.readLine();
					do {
						nextLine = fromResultsFile.readLine();
					} while (nextLine.isEmpty());
				}					
			}
			while (true);
						
			/* now read the sets of extensions provided by ASPARTIX */
			
			/* skip rest of ASPARTIX-part-heading */
			for (int j = 0; j < 2; j++) {
				fromResultsFile.readLine();
			}
			
			do {						
				fromResultsFile.readLine();   // skip blank line which precedes section.
				fromResultsFile.readLine();   // skip section-begins ("-----...") line.
				
				nextLine = fromResultsFile.readLine();
				
				if (null == nextLine) {
					break;
				} else {
					semantics = nextLine.substring(nextLine.indexOf("*")+1, nextLine.lastIndexOf("*"));
					fromResultsFile.readLine();	// skip blank line preceding extension(s).
					
					extsSet = new HashSet<HashSet<String>>();
					
					/* the DLV system outputs the ideal extension one argument per line. The script also outputs a blank
					 line after the final argument, and then the section-ending ("---...") line. */
					if(semantics.equals("ideal")){
						extension = new HashSet<String>();
						do {
							nextLine = fromResultsFile.readLine();

							if (!nextLine.isEmpty()) {
								extension.add(nextLine.substring(0,1));	
							} else {
								fromResultsFile.readLine(); // skip section-ending ("---...") line.
								break;
							}
						}
						while (true);
						
						extsSet.add(extension);
						
					} else {
						/* for other semantics, the DLV system outputs each extension on a separate line. The script 
						 adds a blank line after the final extension, and then the section-ending ("---...") line. */
						do {
							nextLine = fromResultsFile.readLine();
							
							if (!nextLine.isEmpty()) {
								if (nextLine.charAt(0) == '-') {
									break;	// nextLine is section-ending "---..." line.
								} else {
									extension = new HashSet<String>();
									for(int j = 4; j < nextLine.length(); j+=7) {
										extension.add(nextLine.substring(j,j+1));
									}							
									extsSet.add(extension);
								}
							}
						} while (true); 
					}
					aspSemanticsToExtSets.put(semantics, extsSet);
				}
			} while (true);			
			
			for (String nextSemantics : dAFSemanticsToExtSets.keySet()) {
				if (!dAFSemanticsToExtSets.get(nextSemantics).equals(aspSemanticsToExtSets.get(nextSemantics))) {
					System.out.println(nl + "ERROR: DungAF.class and ASPARTIX disagree with respect to AF #" 
									   + i + " and the *" + nextSemantics + "* semantics." );
					testPassed = false;
				}
			}
		}
		
		return testPassed;
	}
}
