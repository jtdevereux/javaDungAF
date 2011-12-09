
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
import java.lang.reflect.*;
import javaDungAF.DungAF;
import javaDungAF.SetComparison;

/**
 * A class to test 
 * {@link javaDungAF.DungAF#getDefenceSetsAround(String) getDefenceSetsAround(String)}. It checks
 * that, given the argument <i>arg</i>, the method returns a set of argument-sets which includes -
 *
 * <ol>
 * <li> no strict superset of a defence-set around <i>arg</i>; </li> 
 * <li> no non-admissible set; and </li>
 * <li> every defence-set around <i>arg</i>. </li> 
 * </ol>
 *
 * The class runs this test for all arguments in 100 AFs, each comprising 20 arguments and 20 attacks, and each
 * generated by {@link javaDungAF.DungAF#getRandomDungAF(int,int,int,int,Collection) getRandomDungAF(int, int, int, int, Collection&ltString&gt)}.
 *
 * <p> The class assumes the correctness of 
 * {@link javaDungAF.DungAF#admissibleSetsContain(Collection[]) admissibleSetsContain(Collection&ltString&gt... )}
 * and of {@link javaDungAF.DungAF#getAdmissibleSets() getAdmissibleSets()}. The former method is very simple, while the
 * latter can be checked with {@link javaDungAF.tests.TestWithAspartix TestWithAspartix}. </p>
 *
 */
public class TestDefenceSets {
	
	static HashSet<String> argPool;
	static DungAF af;
	static final int ITERATIONS = 100;
	static final int MIN_ARGS = 20;
	static final int MAX_ARGS = 20;
	static final int MIN_ATTS = 20;
	static final int MAX_ATTS = 20; 
	
	public static void main(String args[]) {
		
		argPool = new HashSet<String>();												
		for(int i = 97; i <= 122; i++) { 
			argPool.add(Character.toString((char) i)); 
		}
		
		for (int i = 1; i <= ITERATIONS; i++) {
			af = DungAF.getRandomDungAF(MIN_ARGS, MAX_ARGS, MIN_ATTS, MAX_ATTS, argPool);
			checkDefenceSets();
			System.out.println("\n" + "getDefenceSetsAround(String) tested for all arguments in " + i + " AFs.");
		}		
		
		System.out.println("\n" + "TestDefenceSets: passed.");
	}

	private static void checkDefenceSets() {
		
		HashSet<HashSet<String>> returnedDefSets;
		HashSet<HashSet<String>> argSets;
		HashMap<String, HashSet<HashSet<String>>> argsToSubsumedAdmiSetSets = 
																	new HashMap<String, HashSet<HashSet<String>>>();
		HashMap<String, HashSet<HashSet<String>>> argsToInadmissibleReturnedSetSets = 
																	new HashMap<String, HashSet<HashSet<String>>>();
		HashMap<String, HashSet<HashSet<String>>> argsToMissingDefSetSets = 
																	new HashMap<String, HashSet<HashSet<String>>>();
		boolean resultsOK = true;
		
		for (String nextArg : af.getArgs()) {
			returnedDefSets = af.getDefenceSetsAround(nextArg);
			
			/* check that returnedDefSets includes no strict superset of a defence-set for nextArg, assuming that
			 getAdmissibleSets() is correct. */
			argSets = new HashSet<HashSet<String>>();
			
			for (HashSet<String> nextReturnedDefSet : returnedDefSets) {
				for (HashSet<String> nextAdmiSet : af.getAdmissibleSets()) {
					if ((nextReturnedDefSet.size() > nextAdmiSet.size()) &&
						nextAdmiSet.contains(nextArg) &&
						nextReturnedDefSet.containsAll(nextAdmiSet)) {
						
						argSets.add(nextAdmiSet);
						break;
					}
				}
			}
			
			argsToSubsumedAdmiSetSets.put(nextArg, argSets);
			
			/* check that returnedDefSets includes only admissible sets, assuming that
			admissibleSetsContain(Collection<String>... ) is correct. */
			argSets = new HashSet<HashSet<String>>();
			
			for (HashSet<String> nextSet : returnedDefSets) {
				if(!af.admissibleSetsContain(nextSet)) {
					argSets.add(nextSet);
				}
			}
			
			argsToInadmissibleReturnedSetSets.put(nextArg, argSets);
			
			/* check that returnedDefSets includes all defence-sets for nextArg, assuming that getAdmissibleSets() is 
			 correct. */
			argSets = new HashSet<HashSet<String>>();
			
			for (HashSet<String> nextAdmiSet : af.getAdmissibleSets()) {
				if (nextAdmiSet.contains(nextArg)) {
					resultsOK = false;
					for (HashSet<String> nextReturnedSet : returnedDefSets) {
						if (nextAdmiSet.containsAll(nextReturnedSet)) {
							resultsOK = true;
							break;
						}
					}					
					if (!resultsOK) {
						argSets.add(nextAdmiSet);
					}
				}
			}
			
			SetComparison.removeNonMinimalMembersOf(argSets);
			argsToMissingDefSetSets.put(nextArg, argSets);	
		}
		
		for (String nextArg : af.getArgs()) {
			if (!argsToSubsumedAdmiSetSets.get(nextArg).isEmpty()) {
				resultsOK = false;
				System.out.println("\n" + "Given \"" + nextArg + "\", getDefenceSetsAround(String) returns set(s) "
								 + "which strictly subsume the following admissible sets containing " 
								 + "'" + nextArg + "' (which do not necessarily comprise all such sets): \n");
				for (HashSet<String> nextSet : argsToSubsumedAdmiSetSets.get(nextArg)) {
					System.out.println(nextSet);
				}
				System.out.println("\n" + "AF is: " + af);
			}
			
			if (!argsToInadmissibleReturnedSetSets.get(nextArg).isEmpty()) {
				resultsOK = false;
				System.out.println("\n" + "Given \"" + nextArg + "\", getDefenceSetsAround(String) returns the "
								   + "following inadmissible sets: \n");
				for (HashSet<String> nextSet : argsToInadmissibleReturnedSetSets.get(nextArg)) {
					System.out.println(nextSet);
				}
				System.out.println("\n" + "AF is: " + af);
			}
			
			if (!argsToMissingDefSetSets.get(nextArg).isEmpty()) {
				resultsOK = false;
				System.out.println("\n" + "Given \"" + nextArg + "\", getDefenceSetsAround(String) fails to return the "
								   + "following defence-sets around '" + nextArg + "': \n");
				for (HashSet<String> nextSet : argsToMissingDefSetSets.get(nextArg)) {
					System.out.println(nextSet);
				}
				System.out.println("\n" + "AF is: " + af);
			}
		}
		
		if (!resultsOK) {
			throw new RuntimeException("\n" + "TestDefenceSets: *failed* - see above for details.");
		}
	}
}