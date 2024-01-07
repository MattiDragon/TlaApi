package io.github.mattidragon.tlaapi.impl;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark code that should only be used by plugins of the TLA API and not implementations.
 */
@Documented
@Target({ElementType.MODULE, ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface PluginOnly {
}
