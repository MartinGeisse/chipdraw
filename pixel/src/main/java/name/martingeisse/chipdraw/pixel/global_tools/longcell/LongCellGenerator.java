package name.martingeisse.chipdraw.pixel.global_tools.longcell;

import name.martingeisse.chipdraw.pixel.design.Design;
import name.martingeisse.chipdraw.pixel.design.Technologies;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

/**
 *
 */
public class LongCellGenerator {

	public static Design generate(String text) throws UserVisibleMessageException {

		BooleanTerm term = new BooleanTermParser(text).parse();

		//
		// Problem: The main problem in the "LongCell" generator is routing the gate signals. Naive transistor
		// placement can be done by turning the function term into a list of minterms, then running a single diffusion
		// branch per minterm horizontally (parallel to the ground line) and adding a transistor for each variable in
		// the minterm. However, connecting to the gates of those transistors is more complex to do automatically,
		// and for more complex gates, depends on placing the transistors wisely.
		//

		// return new Design(Technologies.Concept.TECHNOLOGY, 5, 5);

		throw new UserVisibleMessageException("this tool is in an early planning stage");
	}

}
