package me.zkingofkill.spartanconfigapi.annotations;


import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
    String path() default "config.yml";
}