package be.vubrooster.ejb.events;

import java.lang.reflect.Method;
import java.util.*;

/**
 * EventDispatcher
 *
 * @author Maxim Van de Wynckel
 * @date 08-May-16
 */
public class EventDispatcher {
    private static EventDispatcher instance = null;
    private final Map<Class<?>, List<RegisteredListener>> listeners = Collections.synchronizedMap(new HashMap<Class<?>, List<RegisteredListener>>());

    public EventDispatcher(){
        instance = this;
    }

    /**
     * Get instance
     *
     * @return event dispatcher instance
     */
    public static EventDispatcher getInstance(){
        if (instance == null){
            new EventDispatcher();
        }
        return instance;
    }

    /**
     * Register a listener
     *
     * @param listener the listener to register
     */
    public void registerListener(Listener listener){
        Class<?> c = listener.getClass();
        for (Method m : c.getMethods()) {
            if (m.getAnnotation(EventHandler.class) != null && m.getParameterTypes().length == 1 && Event.class.isAssignableFrom(m.getParameterTypes()[0])) {
                RegisteredListener reglistener = new RegisteredListener(listener, m);
                Class<?> eventType = m.getParameterTypes()[0];
                List<RegisteredListener> methods = listeners.get(eventType);
                if (methods == null) {
                    methods = new ArrayList<>();
                    listeners.put(eventType, methods);
                }
                methods.add(reglistener);
            }
        }
    }

    /**
     * Notify all listeners that an event has occurred
     *
     * @param event the event to call
     */
    public void callEvent(Event event){
        List<RegisteredListener> methods = new ArrayList<>();
        Class<?> eventType = event.getClass();
        while (true) {
            List<RegisteredListener> m = listeners.get(eventType);
            if (m != null) {
                methods.addAll(m);
            }
            if (eventType == Event.class) {
                break;
            }
            eventType = eventType.getSuperclass();
        }
        for (RegisteredListener method : methods) {
            try {
                method.handleEvent(event);
            } catch (Throwable t) {

            }
        }
    }
}
