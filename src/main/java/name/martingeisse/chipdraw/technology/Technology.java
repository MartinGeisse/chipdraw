package name.martingeisse.chipdraw.technology;

/**
 * Using a simple string / identifier as the technology ID: My original plan was to use an identifier plus a hash code,
 * to detect version changes or other differences in technologies using the same identifier on different machines.
 * However, this is not useful, because most of what defines a technology is in the code, and we can't compute a hash
 * from that in a useful way. Even versioning of a technology can't be done that way. So I'm back to a simple identifier.
 * We should be able to detect changes mostly through the DRC, especially when we include "hint" rules that tell the
 * user, for example, when a trace is wider than necessary. All other versioning should be solved through process and by
 * using a VCS.
 */
public final class Technology {

    private final String id;
    private final int layerCount;

    public Technology(String id, int layerCount) {
        this.id = id;
        this.layerCount = layerCount;
    }

    public String getId() {
        return id;
    }

    public int getLayerCount() {
        return layerCount;
    }
}
