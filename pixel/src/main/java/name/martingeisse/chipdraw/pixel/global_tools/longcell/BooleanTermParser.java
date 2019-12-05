package name.martingeisse.chipdraw.pixel.global_tools.longcell;

import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

/**
 *
 */
public final class BooleanTermParser {

	private final String text;
	private int position;

	public BooleanTermParser(String text) throws UserVisibleMessageException {
		text = text.replace(" ", "").toLowerCase();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if ((c < 'a' || c > 'z') && c != '+' && c != '(' && c != ')') {
				throw new UserVisibleMessageException("");
			}
		}
		this.text = text;
		this.position = 0;
	}

	public BooleanTerm parse() throws UserVisibleMessageException {
		BooleanTerm term = parseSubterm();
		if (position != text.length()) {
			throw new UserVisibleMessageException("unexpected character: '" + text.charAt(position));
		}
		return term;
	}

	private BooleanTerm parseSubterm() throws UserVisibleMessageException {
		checkNotFinished("subterm");
		char c = text.charAt(position);
		position++;
		if (c == ')') {
			throw new UserVisibleMessageException("empty parenthesized subterm");
		}
		BooleanTerm term = parseAnd();
		while (position < text.length() && text.charAt(position) == '+') {
			position++;
			term = new BooleanTerm.Or(term, parseAnd());
		}
		return term;
	}

	private BooleanTerm parseAnd() throws UserVisibleMessageException {
		BooleanTerm term = parsePrimary();
		while (position < text.length()) {
			char c = text.charAt(position);
			if (c != '(' && (c < 'a' || c > 'z')) {
				break;
			}
			term = new BooleanTerm.And(term, parsePrimary());
		}
		return term;
	}

	private BooleanTerm parsePrimary() throws UserVisibleMessageException {
		checkNotFinished("variable or opening parenthesis");
		char c = text.charAt(position);
		position++;
		if (c == '(') {
			BooleanTerm term = parseSubterm();
			if (position == text.length() || text.charAt(position) != ')') {
				throw new UserVisibleMessageException("missing closing parenthesis");
			}
			position++;
			return term;
		} else if (c >= 'a' && c <= 'z') {
			return new BooleanTerm.Variable(c);
		} else {
			throw new UserVisibleMessageException("expected variable or opening parenthesis, found: " + c);
		}
	}

	private void checkNotFinished(String expected) throws UserVisibleMessageException {
		if (position == text.length()) {
			throw new UserVisibleMessageException("unexpected end of input; expected: " + expected);
		}
	}

}
