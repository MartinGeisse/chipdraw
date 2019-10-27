package name.martingeisse.chipdraw.technology;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class TechnologyId {

    private final long hash1, hash2, hash3, hash4;
    private final String name;

    public TechnologyId(long hash1, long hash2, long hash3, long hash4, String name) {
        this.hash1 = hash1;
        this.hash2 = hash2;
        this.hash3 = hash3;
        this.hash4 = hash4;
        this.name = name;
    }

    public long getHash1() {
        return hash1;
    }

    public long getHash2() {
        return hash2;
    }

    public long getHash3() {
        return hash3;
    }

    public long getHash4() {
        return hash4;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TechnologyId that = (TechnologyId) o;
        return new EqualsBuilder()
            .append(hash1, that.hash1)
            .append(hash2, that.hash2)
            .append(hash3, that.hash3)
            .append(hash4, that.hash4)
            .append(name, that.name)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(hash1)
            .append(hash2)
            .append(hash3)
            .append(hash4)
            .append(name)
            .toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        appendHash(hash1, builder);
        appendHash(hash2, builder);
        appendHash(hash3, builder);
        appendHash(hash4, builder);
        builder.append('-');
        builder.append(name);
        return builder.toString();
    }

    private static void appendHash(long hash, StringBuilder builder) {
        for (int i = 0; i < 16; i++) {
            builder.append(Character.forDigit((int)(hash >>> 60), 16));
            hash <<= 4;
        }
    }

}
