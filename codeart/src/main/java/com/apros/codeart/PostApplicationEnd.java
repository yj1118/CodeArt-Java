package com.apros.codeart;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 结束之后
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PostApplicationEnd {
	String method() default "disposed";
	ActionPriority value() default ActionPriority.User;
}