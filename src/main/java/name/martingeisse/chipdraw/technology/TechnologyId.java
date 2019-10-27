package name.martingeisse.chipdraw.technology;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public final class TechnologyId implements Serializable {

    private final long hash1, hash2;
    private final String name;

    public TechnologyId(long hash1, long hash2, String name) {
        this.hash1 = hash1;
        this.hash2 = hash2;
        this.name = name;
    }

    public long getHash1() {
        return hash1;
    }

    public long getHash2() {
        return hash2;
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
            .append(name, that.name)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(hash1)
            .append(hash2)
            .append(name)
            .toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        appendHash(hash1, builder);
        appendHash(hash2, builder);
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
