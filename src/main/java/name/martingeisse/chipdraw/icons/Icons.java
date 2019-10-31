package name.martingeisse.chipdraw.icons;

import javax.swing.*;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public final class Icons {

	private static final Map<String, ImageIcon> cache = new ConcurrentHashMap<>();

	// prevent instantiation
	private Icons() {
	}

	public static ImageIcon get(String filename) {
		return cache.computeIfAbsent(filename, ignored -> {
			URL url = Icons.class.getResource(filename);
			if (url == null) {
				throw new IllegalArgumentException("no such icon: " + filename);
			}
			return new ImageIcon(url);
		});
	}

}
