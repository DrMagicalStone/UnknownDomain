package unknowndomain.engine.block;

import org.joml.AABBd;
import unknowndomain.engine.component.Component;
import unknowndomain.engine.component.ComponentContainer;
import unknowndomain.engine.registry.RegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public class BlockBase extends RegistryEntry.Impl<Block> implements Block {

    private final ComponentContainer components = new ComponentContainer();

    private AABBd[] boundingBoxes = new AABBd[]{DEFAULT_BOUNDING_BOX};

    @Override
    public AABBd[] getBoundingBoxes() {
        return boundingBoxes;
    }

    public BlockBase setBoundingBoxes(AABBd[] boundingBoxes) {
        this.boundingBoxes = boundingBoxes;
        return this;
    }

    @Override
    public <T extends Component> Optional<T> getComponent(@Nonnull Class<T> type) {
        return components.getComponent(type);
    }

    @Override
    public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
        return components.hasComponent(type);
    }

    @Override
    public <T extends Component> void setComponent(@Nonnull Class<T> type, @Nullable T value) {
        components.setComponent(type, value);
    }

    @Override
    public <T extends Component> void removeComponent(@Nonnull Class<T> type) {
        components.removeComponent(type);
    }

    @Override
    @Nonnull
    public Set<Class<?>> getComponents() {
        return components.getComponents();
    }
}
