package name.martingeisse.chipdraw.unspice.input;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Handles include directives and line continuations.
 */
public final class SpiceLineReader implements Closeable {

	private LineNumberReader lineNumberReader;
	private String lookahead;

	public SpiceLineReader(File file) throws IOException {
		this(new FileInputStream(file));
	}

	public SpiceLineReader(InputStream inputStream) throws IOException {
		this(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
	}

	public SpiceLineReader(Reader reader) throws IOException {
		this(new LineNumberReader(reader));
	}

	public SpiceLineReader(LineNumberReader lineNumberReader) throws IOException {
		this.lineNumberReader = lineNumberReader;
	}

	@Override
	public void close() throws IOException {
		lineNumberReader.close();
	}

	public String readLine() throws IOException{

		// read first line
		if (lookahead == null) {
			lookahead = lineNumberReader.readLine();
			if (lookahead == null) {
				return null;
			}
		}

		// look for continuations
		String line = lookahead;
		lookahead = null;
		while (true) {
			String anotherLine = lineNumberReader.readLine();
			if (anotherLine == null) {
				break;
			}
			if (anotherLine.startsWith("+")) {
				line = line + ' ' + anotherLine.substring(1);
			} else {
				lookahead = anotherLine;
				break;
			}
		}

		return line;
	}

}
