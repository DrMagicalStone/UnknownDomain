package unknowndomain.engine.event.reflect;

import unknowndomain.engine.event.misc.EventListener;
import unknowndomain.engine.event.misc.EventListenerFactory;

import java.lang.reflect.Method;

public class ReflectEventListenerFactory implements EventListenerFactory {

    private static final EventListenerFactory INSTANCE = new ReflectEventListenerFactory();

    public static EventListenerFactory instance() {
        return INSTANCE;
    }

    @Override
    public EventListener create(Class<?> eventType, Object owner, Method method, boolean isStatic) throws Exception {
        method.setAccessible(true);
        return isStatic ?
                event -> method.invoke(null, event) :
                event -> method.invoke(owner, event);
    }
}
