package cc.mcyx.arona.core.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PACKAGE,ElementType.METHOD})
public @interface Command {
    String value();

    String permission() default "";

    String noPermission() default "§c你没有权限执行";

    String description() default "";

    String[] aliases() default {};
}
