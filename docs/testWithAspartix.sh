
# ------------------------------------------------------------------------------------------
# NOTE: This script must be run from the directory containing dlv.bin. 
#
# It was written for ASPARTIX v.0.9 and DLV system BEN/Oct 14 2010, running on MacOS 10.5.8
# ------------------------------------------------------------------------------------------

#!/bin/sh

ITERATIONS="100"
RESULTS_DIRECTORY="$PWD/testWithAspartixResults"
MIN_ARGS="5"
MAX_ARGS="5"
MIN_ATTS="5"
MAX_ATTS="5"

mkdir -p $RESULTS_DIRECTORY

# interpretAF() gets ASPARTIX's interpretations of the AF provided in 'af_"$i"_for_aspartix.dl'.
interpetAF() {

echo "------------------------------------------------"
echo "ASPARTIX - the *admissible* sets:"
echo
./dlv.bin -silent adm.dl af_"$i"_for_aspartix.dl -filter=in,input_error
echo
echo "------------------------------------------------"
echo

echo "------------------------------------------------"
echo "ASPARTIX - the *complete* extensions:"
echo
./dlv.bin -silent comp.dl af_"$i"_for_aspartix.dl -filter=in,input_error
echo
echo "------------------------------------------------"
echo	

echo "------------------------------------------------"
echo "ASPARTIX - the *grounded* extension:"
echo
./dlv.bin -silent ground.dl af_"$i"_for_aspartix.dl -filter=in,input_error
echo
echo "------------------------------------------------"
echo

echo "------------------------------------------------"
echo "ASPARTIX - the *preferred* extensions:"
echo
./dlv.bin -silent prefex.dl af_"$i"_for_aspartix.dl -filter=in,input_error
echo
echo "------------------------------------------------"
echo

echo "------------------------------------------------"
echo "ASPARTIX - the *semiStable* extensions:"
echo
./dlv.bin -silent semi_stable.dl af_"$i"_for_aspartix.dl -filter=in,input_error
echo
echo "------------------------------------------------"
echo

echo "------------------------------------------------"
echo "ASPARTIX - the *stable* extensions:"
echo
./dlv.bin -silent stable.dl af_"$i"_for_aspartix.dl -filter=in,input_error
echo
echo "------------------------------------------------"
echo

echo "------------------------------------------------"
echo "ASPARTIX - the *ideal* extension:"
echo
./dlv.bin -silent -cautious ideal.dl af_"$i"_for_aspartix.dl -filter=in,input_error
echo 
echo "------------------------------------------------"

}

# run TestWithAspartix to make the input-files for ASPARTIX, and to write DungAF's interpretations of the AFs
# provided therein to results-files.
java javaDungAF.tests.TestWithAspartix $ITERATIONS write $MIN_ARGS $MAX_ARGS $MIN_ATTS $MAX_ATTS $PWD $RESULTS_DIRECTORY admissible complete grounded preferred semiStable stable ideal

# call interpretAF() to add ASPARTIX's interpretations of the AFs to the results-files.
for (( i = 1 ; i <= $ITERATIONS ; i++ )) ; do

interpetAF $i >> "$RESULTS_DIRECTORY"/af_"$i"_results.dl
echo "$i AFs interpreted"

done

# run TestWithAspartix again, this time to check that in each results-file, the two sets of interpretations are the 
# same. 
java javaDungAF.tests.TestWithAspartix $ITERATIONS check $RESULTS_DIRECTORY
