package co.pishfa.accelerate.ui.controller;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public interface Paginator {

	boolean hasPagination();

	int getPageSize();

	int getPageStart();

	int getCurrentPage();

	int getNumOfPages();

	int getCount();

	String gotoPage(int page);

	String nextPage();

	boolean hasNextPage();

	String prevPage();

	boolean hasPrevPage();

	String lastPage();

	String firstPage();

}
