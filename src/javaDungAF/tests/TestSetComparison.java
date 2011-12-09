
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
import javaDungAF.SetComparison;

/**
 * A class to test {@link javaDungAF.SetComparison SetComparison}'s
 * {@link javaDungAF.SetComparison#removeNonMinimalMembersOf(Collection) 
 * removeNonMinimalMembersOf(Collection&ltT extends Collection&gt)}
 * and
 * {@link javaDungAF.SetComparison#removeNonMaximalMembersOf(Collection) 
 * removeNonMaximalMembersOf(Collection&ltT extends Collection&gt)} methods.
 * 
 * The methods are tested with the following input -
 *
 * <ol>
 * <li> a set of two comparable {@code String}-sets; </li>
 * <li> a set of two incomparable {@code String}-sets; </li>
 * <li> the empty set; </li>
 * <li> a set containing only one {@code String}-set; </li>
 * <li> a list of three {@code String}-sets, such that two have the same contents, and are comparable with the third; 
 * </li>
 * <li> a list of three {@code String}-sets, such that no two have the same contents, and two are comparable with 
 * one another; and </li>
 * <li> a set comprising two comparable sets and one list, the list containing the same as one of the sets. </li>
 * </ol>
 *
 * This class must be run with assertions enabled.
 */
public class TestSetComparison {
	
	public static void main(String args[]) {
	
		String testName;
		
		HashSet<Collection> setColl = new HashSet<Collection>();
		HashSet<String> setStr0 = new HashSet<String>(Arrays.asList("a"));
		HashSet<String> setStr1 = new HashSet<String>(Arrays.asList("a", "b"));
		HashSet<String> setStr2 = new HashSet<String>(Arrays.asList("c"));
		
		HashSet<HashSet<String>> setSetStr = new HashSet<HashSet<String>>();
		ArrayList<HashSet<String>> listSetStr = new ArrayList<HashSet<String>>();
		
		//-------------------------------------
		
		testName = "set of sets - comparable members";
		setSetStr = new HashSet<HashSet<String>>(Arrays.asList(setStr0, setStr1));
		SetComparison.removeNonMinimalMembersOf(setSetStr);
		assert setSetStr.equals(new HashSet<HashSet<String>>(Arrays.asList(setStr0)))
		: "removeNonMinimalMembersOf(Collection) failed test \"" + testName + "\""
		  + "\n\n Original collection: " + new HashSet<HashSet<String>>(Arrays.asList(setStr0, setStr1))
		  + "\n\n Resulting collection: " + setSetStr;		
		setSetStr.add(setStr1);
		SetComparison.removeNonMaximalMembersOf(setSetStr);
		assert setSetStr.equals(new HashSet<HashSet<String>>(Arrays.asList(setStr1)))
		: "removeNonMaximalMembersOf(Collection) failed test \"" + testName + "\""
		  + "\n\n Original collection: " + new HashSet<HashSet<String>>(Arrays.asList(setStr0, setStr1))
		  + "\n\n Resulting collection: " + setSetStr;		
		
		//-------------------------------------
		
		testName = "set of sets - incomparable members";
		setSetStr = new HashSet<HashSet<String>>(Arrays.asList(setStr0, setStr2));
		SetComparison.removeNonMinimalMembersOf(setSetStr);
		assert setSetStr.equals(new HashSet<HashSet<String>>(Arrays.asList(setStr0, setStr2)))
		: "removeNonMinimalMembersOf(Collection) failed test \"" + testName + "\""
		+ "\n\n Original collection: " + new HashSet<HashSet<String>>(Arrays.asList(setStr0, setStr2))
		+ "\n\n Resulting collection: " + setSetStr;
		SetComparison.removeNonMaximalMembersOf(setSetStr);
		assert setSetStr.equals(new HashSet<HashSet<String>>(Arrays.asList(setStr0, setStr2)))
		: "removeNonMaximalMembersOf(Collection) failed test \"" + testName + "\""
		  + "\n\n Original collection: " + new HashSet<HashSet<String>>(Arrays.asList(setStr0, setStr2))
		  + "\n\n Resulting collection: " + setSetStr;
		
		//-------------------------------------
		
		testName = "set of sets - the empty set";
		setSetStr.clear();
		SetComparison.removeNonMinimalMembersOf(setSetStr);
		assert setSetStr.equals(new HashSet<HashSet<String>>())
		: "removeNonMinimalMembersOf(Collection) failed test \"" + testName + "\".";
		SetComparison.removeNonMaximalMembersOf(setSetStr);
		assert setSetStr.equals(new HashSet<HashSet<String>>())
		: "removeNonMaximalMembersOf(Collection) failed test \"" + testName + "\".";
		
		//-------------------------------------
		
		testName = "a singleton set";
		setSetStr = new HashSet<HashSet<String>>(Arrays.asList(setStr0));
		SetComparison.removeNonMinimalMembersOf(setSetStr);
		assert setSetStr.equals(new HashSet<HashSet<String>>(Arrays.asList(setStr0)))
		: "removeNonMinimalMembersOf(Collection) failed test \"" + testName + "\""
		  + "\n\n Original collection: " + new HashSet<HashSet<String>>(Arrays.asList(setStr0))
		  + "\n\n Resulting collection: " + setSetStr;
		SetComparison.removeNonMaximalMembersOf(setSetStr);
		assert setSetStr.equals(new HashSet<HashSet<String>>(Arrays.asList(setStr0)))
		: "removeNonMaximalMembersOf(Collection) failed test \"" + testName + "\""
		  + "\n\n Original collection: " + new HashSet<HashSet<String>>(Arrays.asList(setStr0))
		  + "\n\n Resulting collection: " + setSetStr;
		
		//-------------------------------------
		
		testName = "two sets are the same and are comparable with the third";
		listSetStr = new ArrayList<HashSet<String>>(Arrays.asList(setStr0, setStr1, setStr1));
		SetComparison.removeNonMinimalMembersOf(listSetStr);
		assert listSetStr.equals(new ArrayList<HashSet<String>>(Arrays.asList(setStr0)))
		: "removeNonMinimalMembersOf(Collection) failed test \"" + testName + "\""
		  + "\n\n Original collection: " + new ArrayList<HashSet<String>>(Arrays.asList(setStr0, setStr1, setStr1))
		  + "\n\n Resulting collection: " + listSetStr;
		listSetStr = new ArrayList<HashSet<String>>(Arrays.asList(setStr0, setStr1, setStr1));	
		SetComparison.removeNonMaximalMembersOf(listSetStr);
		assert listSetStr.equals(new ArrayList<HashSet<String>>(Arrays.asList(setStr1, setStr1)))
		: "removeNonMaximalMembersOf(Collection) failed test \"" + testName + "\""
		  + "\n\n Original collection: " + new ArrayList<HashSet<String>>(Arrays.asList(setStr0, setStr1, setStr1))
		  + "\n\n Resulting collection: " + listSetStr;
		
		//-------------------------------------
		
		testName = "no two sets are the same, two sets are comparable with one another";
		listSetStr = new ArrayList<HashSet<String>>(Arrays.asList(setStr0, setStr1, setStr2));
		SetComparison.removeNonMinimalMembersOf(listSetStr);
		assert listSetStr.equals(new ArrayList<HashSet<String>>(Arrays.asList(setStr0, setStr2)))
		: "removeNonMinimalMembersOf(Collection) failed test \"" + testName + "\""
		  + "\n\n Original collection: " + new ArrayList<HashSet<String>>(Arrays.asList(setStr0, setStr1, setStr2))
		  + "\n\n Resulting collection: " + listSetStr;
		listSetStr = new ArrayList<HashSet<String>>(Arrays.asList(setStr0, setStr1, setStr2));
		SetComparison.removeNonMaximalMembersOf(listSetStr);
		assert listSetStr.equals(new ArrayList<HashSet<String>>(Arrays.asList(setStr1, setStr2)))
		: "removeNonMaximalMembersOf(Collection) failed test \"" + testName + "\""
		  + "\n\n Original collection: " + new ArrayList<HashSet<String>>(Arrays.asList(setStr0, setStr1, setStr2))
		  + "\n\n Resulting collection: " + listSetStr;
		
		//-------------------------------------
		
		testName = "set comprising two comparable sets and one list, the list containing the same as one of the sets";
		setColl = new HashSet(Arrays.asList(setStr0, setStr1, new ArrayList<String>(setStr1)));
		SetComparison.removeNonMinimalMembersOf(setColl);
		assert setColl.equals(new HashSet(Arrays.asList(setStr0))) 
		: "removeNonMinimalMembersOf(Collection) failed test \"" + testName + "\""
		  + "\n\n Original collection: " + new HashSet(Arrays.asList(setStr0, setStr1, new ArrayList<String>(setStr1)))
		  + "\n\n Resulting collection: " + setColl;
		setColl = new HashSet(Arrays.asList(setStr0, setStr1, new ArrayList<String>(setStr1)));
		SetComparison.removeNonMaximalMembersOf(setColl);
		assert setColl.equals(new HashSet(Arrays.asList(setStr1, new ArrayList<String>(setStr1)))) 
		: "removeNonMaximalMembersOf(Collection) failed test \"" + testName + "\"" 
		  + "\n\n Original collection: " + new HashSet(Arrays.asList(setStr0, setStr1, new ArrayList<String>(setStr1)))
		  + "\n\n Resulting collection: " + setColl;
		
		//-------------------------------------
		
		System.out.println("TestSetComparison: passed, if assertions were enabled...");
		
		assert false : "...and they were.";
		
		System.out.println("...but they weren't!");
	}

}
