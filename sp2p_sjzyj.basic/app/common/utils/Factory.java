package common.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import play.Logger;
import services.base.BaseService;
import daos.base.BaseDao;

/**
 * Service和Dao的工厂类
 * @author Administrator
 *
 */
public class Factory {
	
	/** 只提供相应的静态方法 */
	private Factory(){}
	
	/**
	 * services容器(所有service必须是IBaseService的实现)
	 */
	private static Map<String, BaseService> services = new HashMap<String, BaseService>();
	
	/**
	 * simpleServices容器(所有service不是IBaseService的实现，不需要指定的model泛型)
	 */
	private static Map<String, Object> simpleServices = new HashMap<String, Object>();
	
	/**
	 * daos容器
	 */
	private static Map<String, BaseDao> daos = new HashMap<String, BaseDao>();
	
	/**
	 * 返回Service的实例(要求传入该Service的实现类的class)
	 *
	 * @param clazz service实现类的class对象
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static <S extends BaseService> S getService(Class<S> clazz) {
		String name = clazz.getCanonicalName();
		BaseService service = services.get(name);
		if (service == null) {
			service = (S) newInstance(clazz);
			services.put(name, service);
		}
		
		return (S) service;
	}
	
	

	
	/**
	 * 返回Service的实例(要求传入该Service的实现类的完整类名:如services.impl.SystemOptionService)
	 *
	 * @param clazzName service实现类的完整类名
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static <S extends BaseService> S getService(String clazzName) {
		Class clazz = null;
		try {
			clazz = Class.forName(clazzName);
			if(clazz != null){
				
				return (S) getService(clazz);
			} 
		} catch (ClassNotFoundException e) {
			
			Logger.info(e, "没有找到类名为:%s的service", clazzName);
		}
		
		return null;
	}
	
	/**
	 * 返回Dao的实例(要求传入该Dao的实现类的class)
	 *
	 * @param clazz dao的实现类的class对象
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static <D extends BaseDao> D getDao(Class<D> clazz) {
		String name = clazz.getCanonicalName();
		BaseDao dao = daos.get(name);
		if (dao == null) {
			dao = (D) newInstance(clazz);
			daos.put(name, dao);
		}
		return (D) dao;
	}
	
	/**
	 * 返回dao的实例(要求传入该dao的实现类的完整类名:如daos.impl.SystemOptionsDao)
	 *
	 * @param clazzName dao实现类的完整类名
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static <D extends BaseDao> D getDao(String clazzName) {
		Class clazz = null;
		try {
			clazz = Class.forName(clazzName);
			if (clazz != null) {

				return (D) getDao(clazz);
			}
		} catch (ClassNotFoundException e) {
			
			Logger.info(e, "没有找到类名为:%s的dao", clazzName);
		}
		
		return null;
	}
	
	/**
	 * 返回SimpleService的实例(要求传入该SimpleService的实现类的class)
	 *
	 * @param clazz
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月26日
	 */
	public static <SS> SS getSimpleService(Class clazz) {
		String name = clazz.getCanonicalName();
		SS ss = (SS) simpleServices.get(name);
		if (ss == null) {
			ss = (SS) newInstance(clazz);
			simpleServices.put(name, ss);
		}
		
		return ss;
	}
	
	/**
	 * 返回SimpleService的实例(要求传入该SimpleService的实现类的完整类名:如services.impl.FeeService)
	 *
	 * @param defaultImpl
	 * @param impl
	 * @param module
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月26日
	 */
	public static <SS> SS getSimpleService(String defaultImpl,String impl,boolean module) {
		Class clazz = null;
		try {
			if (module) {
				clazz = Class.forName(impl);
			} else {
				clazz = Class.forName(defaultImpl);
			}
			
			if(clazz != null){
				
				return (SS)getSimpleService(clazz);
			} 
		} catch (ClassNotFoundException e) {
			if (module) {
				Logger.info(e, "没有找到类名为:\t%s" + "\t的类", defaultImpl);
			} else {
				Logger.info(e, "没有找到类名为:\t%s" + "\t的类", impl);
			}
		}
		
		return null;
	}
	
	/**
	 * 执行某个类对应的参数的方法
	 * 
	 * @description 执行某个类对应的参数的方法该方法主要用在两个依赖的模块之间，例如Main模块用到M1模块[需要插拔的模块]的东西，<br>
	 * 在Main模块需要调用M1模块的代码(通常是Service方法)，比如UserM1Service的method1()方法为了使复杂度见到最低，<br>
	 * 此时method1的参数支持基本类型、String、Map、List。同时返回值也尽量使用上面几种类型
	 *
	 * @param clazzName 完整类名
	 * @param methodName 方法名
	 * @param args 方法参数
	 * @return 方法自行后的结果(void返回为null)
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static <T> T methodInvoke(String clazzName,String methodName,Object ... args) {
		Class clazz = null;
		Object instance = null;
		Method method = null;
		T t = null;
		try {
			//获得类clazz
			clazz = Class.forName(clazzName);
			if (clazz == null) {
				return null;
			}
			
			//实例化对象
			instance = getService(clazz);
			if (instance == null) {
				return null;
			}
			
			//获得对应参数列表的方法
			if ( (args != null) && (args.length > 0) ) {
				Class[] argClazzs = new Class[args.length];
				for (int i = 0; i < args.length; i++) {
					argClazzs[i] = args[i].getClass();
				}
				method = clazz.getMethod(methodName, argClazzs);
			} else {
				method = clazz.getMethod(methodName);
			}
			
			t = (T) method.invoke(instance, args);
			
			return t;	
		} catch (ClassNotFoundException e) {
			Logger.info(e, "没有找到类名为:\t%s\t的类", clazzName);
		} catch (SecurityException e) {
			Logger.info(e, null);
		} catch (NoSuchMethodException e) {
			Logger.info(e, "没有找到对应参数，且名为:\t%s\t的方法", methodName);
		} catch (IllegalArgumentException e) {
			Logger.info(e, null);
		} catch (IllegalAccessException e) {
			Logger.info(e, null);
		} catch (InvocationTargetException e) {
			Logger.info(e, null);
		}
		
		return null;
	}
	
	/**
	 * 通过反射调用某个类的默认构造方法实例化一个该类的对象
	 *
	 * @param clazz
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	private static Object newInstance(Class clazz) {
		Object obj = null;
		try {
			Constructor constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			obj = constructor.newInstance();
			constructor.setAccessible(false);
		} catch (SecurityException e) {
			Logger.info(e, null);
		} catch (NoSuchMethodException e) {
			Logger.info(e, null);
		} catch (IllegalArgumentException e) {
			Logger.info(e, null);
		} catch (InstantiationException e) {
			Logger.info(e, null);
		} catch (IllegalAccessException e) {
			Logger.info(e, null);
		} catch (InvocationTargetException e) {
			Logger.info(e, null);
		}

		return obj;
	}
	
}
