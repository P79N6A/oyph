package common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 模拟登录拦截标志注解
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年5月30日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SimulatedCheck {

}
