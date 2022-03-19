package name.martingeisse.chipdraw.pixel.scmos.magic;

import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 *
 */
class MagicFileReadHelper {

	private final File file;

	public MagicFileReadHelper(File file) {
		this.file = file;
	}

	public void read() throws IOException, UserVisibleMessageException {
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			try (InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.ISO_8859_1)) {
				try (LineNumberReader in = new LineNumberReader(inputStreamReader)) {
					if (!readNonemptyLineNoNull(in).equals("magic")) {
						throw new IOException("expected 'magic' line");
					}
					String techLine = readNonemptyLineNoNull(in);
					if (!techLine.startsWith("tech ")) {
						throw new IOException("expected 'tech' line'");
					}
					handleTechLine(techLine.substring(5).trim());
					if (!readNonemptyLineNoNull(in).startsWith("timestamp ")) {
						throw new IOException("expected 'timestamp' line'");
					}
					while (true) {
						String line = readNonemptyLine(in);
						if (line == null) {
							break;
						}
						parseLine(line);
					}
				}
			}
		}
	}

	protected void handleTechLine(String tech) throws UserVisibleMessageException {
	}

	private static String readNonemptyLineNoNull(LineNumberReader in) throws IOException {
		String line = readNonemptyLine(in);
		return (line == null ? "*** EOF ***" : line);
	}

	private static String readNonemptyLine(LineNumberReader in) throws IOException {
		while (true) {
			String line = in.readLine();
			if (line == null) {
				return null;
			}
			line = line.trim();
			if (!line.isEmpty()) {
				return line;
			}
		}
	}

	protected void parseLine(String line) throws UserVisibleMessageException {
		if (line.startsWith("<<")) {
			parseSectionLine(line);
		} else if (line.startsWith("rect ")) {
			parseRectLine(line);
		} else if (line.startsWith("rlabel ")) {
			parseRlabelLine(line);
		} else {
			throw new UserVisibleMessageException("invalid line: " + line);
		}
	}

	protected void parseSectionLine(String line) throws UserVisibleMessageException {
		if (!line.endsWith(">>")) {
			throw new UserVisibleMessageException("invalid section line: " + line);
		}
		handleSectionLine(line.substring(2, line.length() - 2).trim());
	}

	protected void handleSectionLine(String section) throws UserVisibleMessageException {
	}

	protected void parseRectLine(String line) throws UserVisibleMessageException {
		String[] segments = StringUtils.split(line.substring(5).trim());
		if (segments.length != 4) {
			throw new UserVisibleMessageException("invalid rect line: " + line);
		}
		int a = Integer.parseInt(segments[0]);
		int b = Integer.parseInt(segments[1]);
		int c = Integer.parseInt(segments[2]);
		int d = Integer.parseInt(segments[3]);
		handleRectLine(Math.min(a, c), Math.min(b, d), Math.max(a, c), Math.max(b, d));
	}

	protected void handleRectLine(int x1, int y1, int x2, int y2) throws UserVisibleMessageException {
	}

	protected void parseRlabelLine(String line) throws UserVisibleMessageException {
		// ignored for now
	}

}
