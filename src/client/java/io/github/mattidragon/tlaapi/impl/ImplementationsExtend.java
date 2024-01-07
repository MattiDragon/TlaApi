package io.github.mattidragon.tlaapi.impl;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark classes that implementations of the TLA API should extend and plugins should call methods on.
 * Has no real effect and only serves as a marker to clarify intended api usage.
 * @see PluginsExtend
 * @see ImplementationOnly
 */
@Documented
@Target(ElementType.TYPE)
public @interface ImplementationsExtend {
}
