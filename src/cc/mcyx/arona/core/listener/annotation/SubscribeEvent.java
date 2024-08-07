package cc.mcyx.arona.core.listener.annotation;

import org.bukkit.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SubscribeEvent {
    EventPriority priority() default EventPriority.NORMAL;

    boolean ignoreCancelled() default false;
}
