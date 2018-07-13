package com.bunker.bkframework.server.working;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface BKWork {
	public boolean enable() default true;
	public boolean isPublic() default true;
	public String key();
	public String []input() default {};
	public String []output() default {};
}