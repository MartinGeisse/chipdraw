package name.martingeisse.chipdraw.pixel.global_tools.longcell;

/**
 *
 */
public abstract class BooleanTerm {

	protected BooleanTerm() {
	}

	public static final class Variable extends BooleanTerm {

		private final char name;

		public Variable(char name) {
			this.name = name;
		}

		public char getName() {
			return name;
		}

	}

	public static abstract class BinaryOperation extends BooleanTerm {

		private final BooleanTerm leftOperand;
		private final BooleanTerm rightOperand;

		protected BinaryOperation(BooleanTerm leftOperand, BooleanTerm rightOperand) {
			this.leftOperand = leftOperand;
			this.rightOperand = rightOperand;
		}

		public BooleanTerm getLeftOperand() {
			return leftOperand;
		}

		public BooleanTerm getRightOperand() {
			return rightOperand;
		}

	}

	public static final class And extends BinaryOperation {

		public And(BooleanTerm leftOperand, BooleanTerm rightOperand) {
			super(leftOperand, rightOperand);
		}

	}

	public static final class Or extends BinaryOperation {

		public Or(BooleanTerm leftOperand, BooleanTerm rightOperand) {
			super(leftOperand, rightOperand);
		}

	}

}
