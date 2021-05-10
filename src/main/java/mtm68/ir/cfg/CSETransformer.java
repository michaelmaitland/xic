package mtm68.ir.cfg;

public class CSETransformer {

	/*
	 * Algorithm: 
	 * 
	 * Compute reaching expressions, that is, find statements of the form
	 * n : v <- x binop y, st the path from n to s does not compute x binop y nor
	 * define x nor y.
	 * 
	 * Choose fresh temp w, and for such n, rewrite as:
	 *   n : w <- x bop y
	 *   n' : v <- w
	 *   
	 * Finally, modify statement s to be:
	 *   s : t <- w
	 * 
	 */
	public void doCSE() {
		
	}
}
