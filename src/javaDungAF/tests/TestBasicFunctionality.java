
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
import javaDungAF.DungAF;

/**
 * A class to test the basic functionality of {@link javaDungAF.DungAF}.
 *
 * <p> This class tests -
 * 
 * <ul>
 * <li> {@link javaDungAF.DungAF}'s constructors; </li>
 * <li> methods which modify the AFs recorded by {@link javaDungAF.DungAF} objects; </li>
 * <li> methods concerning the comparison of {@link javaDungAF.DungAF} objects; </li> 
 * <li> methods concerning conflict between arguments; </li> 
 * <li> methods concerning the acceptability of arguments with respect to sets of arguments; </li>
 * <li> {@link javaDungAF.DungAF#getRandomDungAF(int,int,int,int,Collection) getRandomDungAF(int, int, int, int, Collection&ltString&gt)}; and </li>
 * <li> {@link javaDungAF.DungAF#recordsExtsOfType(String) recordsExtsOfType(String)}.</li>
 * </ul>
 *
 * </p>
 * This class must be run with assertions enabled.
 */
public class TestBasicFunctionality {
	
	static DungAF af;
	static DungAF anotherAf;
	static String testName;
	static boolean expected;
	
	public static void main(String args[]) {
				
		testConstructors();
		testAddition();
		testRemoval();
		testComparison();
		testConflictFunc();
		testAcceptabilityFunc();
		testMiscMethods();
		
		System.out.println("TestBasicFunctionality: passed, if assertions were enabled...");
		
		assert false : "...and they were.";
		
		System.out.println("...but they weren't!");
	}

	
	private static void testConstructors() {
		
		testName = "construct with non-empty set of attacks alone";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		expected = af.equals(new DungAF(Arrays.asList("a","b","c","d","e","f"),										
										Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, 
													  new String[]{"e","f"})));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "construct with incomplete set of arguments, non-empty set of attacks";
		af = new DungAF(Arrays.asList("g","h"), Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"},
															  new String[]{"e","f"}));
		expected = af.equals(new DungAF(Arrays.asList("a","b","c","d","e","f","g","h"),										
										Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, 
													  new String[]{"e","f"})));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "construct with malformed attacks";
		af.clear();
		try { 
			af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d","e"})); 
		} catch (IllegalArgumentException e) { 
			// do nothing.
		}
		expected = af.equals(new DungAF());
		assert expected : ("Failed test \"" + testName + "\".");
				
		af.clear();
		try { 
			af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{}));
		} catch (IllegalArgumentException e) { 
			// do nothing.
		}
		expected = af.equals(new DungAF());
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
	}
	
	private static void testAddition() {

		testName = "add args already in af";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		af.addArgs("a","b");
		expected = af.equals(new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, 
													  new String[]{"e","f"})));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------

		testName = "add args not already in af";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		af.addArgs("g","h");
		expected = af.equals(new DungAF(Arrays.asList("g","h"), Arrays.asList(new String[]{"a","b"}, 
																			  new String[]{"c","d"}, 
																			  new String[]{"e","f"})));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------

		testName = "add atts already in af";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		af.addAtts(new String[]{"a","b"}, new String[]{"c","d"});
		expected = af.equals(new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, 
													  new String[]{"e","f"})));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "add atts not already in af";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		af.addAtts(new String[]{"b","c"}, new String[]{"f","g"}, new String[]{"h","d"});
	    expected = af.equals(new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, 
													  new String[]{"e","f"}, new String[]{"b","c"}, 
													  new String[]{"f","g"}, new String[]{"h","d"})));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "add malformed atts";
		af.clear();
		try { 
			af.addAtts(new String[]{"a","b"}, new String[]{"a"});
		} catch (IllegalArgumentException e) {
			// do nothing.	
		}
		expected = af.equals(new DungAF()) ;
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "addition: check values returned";
		af.clear();
		expected = af.addArgs("a") && !af.addArgs("a") 
				&& af.addAtts(new String[]{"b","b"}) && !af.addAtts(new String[]{"b","b"}) 
				&& af.ensureSubsumes(new DungAF(Collections.singleton(new String[]{"c","c"}))) 
				&& !af.ensureSubsumes(new DungAF(Collections.singleton(new String[]{"c","c"})));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
	}
		
	private static void testRemoval() {
			
		testName = "remove args in af";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		af.removeArgs("a","c");
		expected = af.equals(new DungAF(Arrays.asList("b","d"), Collections.singleton(new String[]{"e","f"})));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "remove args not in af";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		af.removeArgs("g","h");
		expected = af.equals(new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, 
													  new String[]{"e","f"})));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "remove atts in af";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		af.removeAtts(new String[]{"a","b"}, new String[]{"c","d"});
		expected = af.equals(new DungAF(Arrays.asList("a","b","c","d","e","f"), 
										Collections.singleton(new String[]{"e","f"})));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "remove atts not in af";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		af.removeAtts(new String[]{"b","c"}, new String[]{"f","g"}, new String[]{"h","d"});
	    expected = af.equals(new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, 
													  new String[]{"e","f"})));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
				
		testName = "remove malformed atts";
		af.clear();
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		try { 
			af.removeAtts(new String[]{"a","b"}, new String[]{"a"});
		} catch(IllegalArgumentException e) { 
			// do nothing.
		}
		expected = af.equals(new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, 
													  new String[]{"e","f"}))) ;
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "removal: check values returned";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		expected = af.removeArgs("a") && !af.removeArgs("a") 
				&& af.removeAtts(new String[]{"c","d"}) && !af.removeAtts(new String[]{"c","d"}) 
				&& af.ensureDisjointWith(new DungAF(Collections.singleton(new String[]{"e","f"}))) 
				&& !af.ensureDisjointWith(new DungAF(Collections.singleton(new String[]{"e","f"})));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
	}
	
	private static void testComparison() {
		
		testName = "equal AFs are equal";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		anotherAf = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		expected = af.equals(anotherAf) ;
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------

		testName = "unequal AFs (args) are unequal";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		anotherAf = new DungAF(Arrays.asList("g"), Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, 
																 new String[]{"e","f"}));
		expected = !af.equals(anotherAf) ;
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------

		testName = "unequal AFs (atts) are unequal";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		anotherAf = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"f","e"}));
		expected = !af.equals(anotherAf) ;
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "subsuming AFs subsume (args)";
		af = new DungAF(Arrays.asList("g"), Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"},
														  new String[]{"e","f"}));
		anotherAf = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		expected = af.subsumes(anotherAf) && !anotherAf.subsumes(af);
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "subsuming AFs subsume (atts)";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		anotherAf = new DungAF(Arrays.asList("e","f"), Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}));
		expected = af.subsumes(anotherAf) && !anotherAf.subsumes(af);
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "disjoint AFs are disjoint";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		anotherAf = new DungAF(Arrays.asList("g","h"), Arrays.asList(new String[]{"i","j"}, new String[]{"k","l"}));
		expected = af.isDisjointWith(anotherAf);
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
	}
	
	private static void testConflictFunc() {
		
		HashSet<String> tempSetStr0;
		HashSet<String> tempSetStr1;
		
		testName = "conflict: empty set is conflict-free";
		expected = af.hasAsConflictFreeSet(new HashSet<String>())
				&& af.containsNoConflictAmong(new HashSet<String>());
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------	
		
		testName = "conflict: arg-set disjoint with this AF";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		expected = !af.hasAsConflictFreeSet(Arrays.asList("g","h"))
				&& af.containsNoConflictAmong(Arrays.asList("g","h"));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------

		testName = "conflict: conflict-free subset of args";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		expected = af.hasAsConflictFreeSet(Arrays.asList("a","c","e"))
				&& af.containsNoConflictAmong(Arrays.asList("a","c","e"));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "conflict: non-conflict-free subset of args";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		expected = !af.hasAsConflictFreeSet(Arrays.asList("a","b","d"))
				&& !af.containsNoConflictAmong(Arrays.asList("a","b","d"));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "collnIsInConflictWithAnyOf(argColl, args): argColl and args are in this AF, but not in conflict";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		expected = !af.collnIsInConflictWithAnyOf(Arrays.asList("a", "c"), "e", "f");
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "collnIsInConflictWithAnyOf(argColl, args): argColl and args are in this AF, and are in conflict";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		expected = af.collnIsInConflictWithAnyOf(Arrays.asList("a", "b"), "b");
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "union conflict: argColls are in this AF, and are not in conflict"; 
		tempSetStr0 = new HashSet<String>(Arrays.asList("a","c"));
		tempSetStr1 = new HashSet<String>(Arrays.asList("e"));		
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		expected = af.hasUnionOfAsConflictFreeSet(
						 new HashSet<HashSet<String>>(Arrays.asList(tempSetStr0,tempSetStr1)))
				&& af.containsNoConflictAmongUnionOf(
						 new HashSet<HashSet<String>>(Arrays.asList(tempSetStr0,tempSetStr1)));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "union conflict: argColls are in this AF, and are in conflict";
		tempSetStr0 = new HashSet<String>(Arrays.asList("a","c"));
		tempSetStr1 = new HashSet<String>(Arrays.asList("b"));	
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		expected = !af.hasUnionOfAsConflictFreeSet(
						  new HashSet<HashSet<String>>(Arrays.asList(tempSetStr0,tempSetStr1)))
				&& !af.containsNoConflictAmongUnionOf(
						  new HashSet<HashSet<String>>(Arrays.asList(tempSetStr0,tempSetStr1)));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "union conflict: argColls are not all in this AF, are not in conflict";
		tempSetStr0 = new HashSet<String>(Arrays.asList("a","g"));
		tempSetStr1 = new HashSet<String>(Arrays.asList("e"));
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		expected = !af.hasUnionOfAsConflictFreeSet(
						  new HashSet<HashSet<String>>(Arrays.asList(tempSetStr0,tempSetStr1)))
				&& af.containsNoConflictAmongUnionOf(
						  new HashSet<HashSet<String>>(Arrays.asList(tempSetStr0,tempSetStr1)));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
	}
	
	private static void testAcceptabilityFunc() {

		testName = "acceptable sets: to the whole set of arguments";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		expected = af.getArgsAcceptedBy(af.getArgs()).equals(new HashSet<String>(Arrays.asList("a","c","e")))
				   && af.argsAccept(af.getArgs(), Arrays.asList("a","c","e"));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
		
		testName = "acceptable sets: to a set which completely defends an attacked argument outside of it";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, 
									  new String[]{"e","f"}, new String[]{"f","g"}));
		expected = af.getArgsAcceptedBy(Arrays.asList("e")).equals(new HashSet<String>(Arrays.asList("a","c","e","g")))
				&& af.argsAccept(Arrays.asList("e"), Arrays.asList("a","c","e","g"));
		assert expected : ("Failed test \"" + testName + "\".");
		
		//-------------------------------------
	}
	
	private static void testMiscMethods() {
		
		testName = "getRandomDungAF(...) with silly constraints";
		
		af = null;
		try {
			af = DungAF.getRandomDungAF(-3, -1, 5, 5, Arrays.asList("a", "b", "c", "d", "e"));
		} catch (IllegalArgumentException e) {
			// do nothing.	
		}
		assert null == af : ("Failed test \"" + testName + "\": negative maxArgs");
		
		//-------------------------------------
		
		try {
			af = DungAF.getRandomDungAF(5, 5, -3, -1, Arrays.asList("a", "b", "c", "d", "e"));
		} catch (IllegalArgumentException e) {
			// do nothing.	
		}
		assert null == af: ("Failed test \"" + testName + "\": negative maxAtts.");
		
		//-------------------------------------
		
		try {
			DungAF.getRandomDungAF(5, 4, 5, 5, Arrays.asList("a", "b", "c", "d", "e"));
		} catch (IllegalArgumentException e) {
			// do nothing.	
		}
		assert null == af : ("Failed test \"" + testName + "\": minArgs > maxArgs.");
		
		//-------------------------------------
		
		try {
			DungAF.getRandomDungAF(5, 5, 5, 5, Arrays.asList("a", "b", "c", "d", "d"));
		} catch (IllegalArgumentException e) {
			// do nothing.	
		}
		assert null == af : ("Failed test \"" + testName + "\": too few unique arguments in argPool for minArgs.");
		
		//-------------------------------------
		
		try {
			DungAF.getRandomDungAF(5, 5, 5, 4, Arrays.asList("a", "b", "c", "d", "e"));
		} catch (IllegalArgumentException e) {
			// do nothing.	
		}
		assert null == af : ("Failed test \"" + testName + "\": minAtts > maxAtts.");
		
		//-------------------------------------
		
		try {
			DungAF.getRandomDungAF(4, 4, 17, 17, Arrays.asList("a", "b", "c", "d", "e"));
		} catch (IllegalArgumentException e) {
			// do nothing.	
		}
		assert null == af : ("Failed test \"" + testName + "\": maxArgs is too small for minAtts.");
		
		//-------------------------------------
		
		try {
			DungAF.getRandomDungAF(4, 6, 26, 26, Arrays.asList("a", "b", "c", "d", "d"));
		} catch (IllegalArgumentException e) {
			// do nothing.	
		}
		assert null == af : ("Failed test \"" + testName + "\": too few unique arguments in argPool for minAtts.");
		
		//-------------------------------------
		
		testName = "recordsExtsOfType";
		af = new DungAF(Arrays.asList(new String[]{"a","b"}, new String[]{"c","d"}, new String[]{"e","f"}));
		af.getGroundedExt(); 
		af.getStableExts();
		anotherAf = new DungAF(af);
		af.getSemiStableExts();
		expected = (af.recordsExtsOfType("grounded") == anotherAf.recordsExtsOfType("grounded")) 
		&& (af.recordsExtsOfType("stable") == anotherAf.recordsExtsOfType("stable")) 
		&& af.recordsExtsOfType("semiStable") != anotherAf.recordsExtsOfType("semiStable");
		assert expected : ("Failed test \"" + testName + " (a)\"");
		
		af.addArgs("g");
		expected = (af.recordsExtsOfType("grounded") != anotherAf.recordsExtsOfType("grounded")) 
		&& (af.recordsExtsOfType("stable") != anotherAf.recordsExtsOfType("stable")) 
		&& af.recordsExtsOfType("semiStable") == anotherAf.recordsExtsOfType("semiStable");
		assert expected : ("Failed test \"" + testName + " (b)\"");
		
		//-------------------------------------
	}	
}
