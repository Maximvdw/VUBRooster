package be.vubrooster.ejb.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Maxim Van de Wynckel
 * @date 08-May-16
 */
public class RegisteredListener {
    private final Listener listener;
    private final Method method;

    public RegisteredListener(Listener l, Method m) {
        this.listener = l;
        this.method = m;
        this.method.setAccessible(true);
    }

    public void handleEvent(Event e) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this.method.invoke(listener, e);
    }

    public Method getMethod() {
        return this.method;
    }
}