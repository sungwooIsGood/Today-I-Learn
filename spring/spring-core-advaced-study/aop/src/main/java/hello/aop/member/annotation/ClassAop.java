package hello.aop.member.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // 클래스 대상
@Retention(RetentionPolicy.RUNTIME) // runtime동안 살아있는 aop
public @interface ClassAop {
}
