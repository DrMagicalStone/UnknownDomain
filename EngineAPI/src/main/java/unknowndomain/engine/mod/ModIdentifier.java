package unknowndomain.engine.mod;

import java.net.URL;
import java.util.Objects;

public class ModIdentifier {
    private final String group, id, version;

    protected ModIdentifier(String group, String id, String version) {
        this.group = group;
        this.id = id;
        this.version = version;
    }

    /**
     * TODO check the modid style
     */
    public static ModIdentifier of(String group, String modid, String version) {
        Objects.requireNonNull(modid);
        Objects.requireNonNull(version);
        return new ModIdentifier(group, modid, version);
    }

    public static ModIdentifier from(String s) {
        String[] split = s.split(":");
        if (split.length != 3 || split[0].equals("") || split[1].equals("") || split[2].equals(""))
            throw new IllegalArgumentException("Invalid mod identifier syntax: " + s);
        return new ModIdentifier(split[0], split[1], split[2]);
    }

    public String getGroup() {
        return group;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String toString() {
        return group + ":" + id + ":" + version;
    }

    public URL toURL() {
        return null; // TODO implement this
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModIdentifier that = (ModIdentifier) o;
        return Objects.equals(group, that.group) &&
                Objects.equals(id, that.id) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, id, version);
    }
}
