package lazyElement;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@FunctionalInterface
public interface Lazy<T> extends Supplier<T> {
    abstract class Cache {
        private volatile static Map<Integer, Object> instances = new HashMap<>();

        private static synchronized Object getInstance(int instanceId, Supplier<Object> create) {
            Object instance = instances.get(instanceId);
            if (instance == null) {
                synchronized (Cache.class) {
                    instance = instances.get(instanceId);
                    if (instance == null) {
                        instance = create.get();
                        instances.put(instanceId, instance);
                    }
                }
            }
            return instance;
        }
    }

    @Override
    default T get() {
        return (T) Cache.getInstance(this.hashCode(), () -> init());
    }

    T init();
}