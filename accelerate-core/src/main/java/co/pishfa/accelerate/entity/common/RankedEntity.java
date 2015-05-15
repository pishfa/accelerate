/**
 * 
 */
package co.pishfa.accelerate.entity.common;

/**
 * Defines a total ordering among instances of this type of entity.
 * Rank is usually global and unique that is it ranked entity among all other entities of the same type but
 * it may be the rank among some partitioning of the data.
 * 
 * 
 * @author Taha Ghasemi
 * 
 */
public interface RankedEntity<K> extends Entity<K> {

	public void setRank(int rank);

	public int getRank();

}
