
/**
 * An ad-hoc package, comprising {@link javaDungAF.DungAF DungAF} and 
 * the utility class {@link javaDungAF.SetComparison SetComparison}.
 *
 * <p>{@link javaDungAF.DungAF DungAF} uses {@link javaDungAF.SetComparison#removeNonMinimalMembersOf(Collection)
 * SetComparison.removeNonMinimalMembersOf(Collection&ltT extends Collection&gt)}, and depends also on Google's 
 * <a href="http://code.google.com/p/guava-libraries/"> guava-libraries</a> (version 10.0.01) - specifically, the 
 * implementation of the <i>n</i>-ary cartesian product provided by <a href="http://docs.guava-libraries.googlecode.com/git-history/v10.0.1/javadoc/com/google/common/collect/Sets.html#cartesianProduct(java.util.List)">{@code com.google.common.collect.Sets.cartesianProduct(List<? extends Set<? extends B>>)}</a>.</p>
 */
package javaDungAF;