package common.interfaces;

/**
 * 可实现缓存的标识(系统设置，数据字典)
 *
 * @description 本接口主要给系统设置和数据字典等经常使用到的表使用,加入缓存机制后建议覆盖findByID(),findAll()两个方法<br>
 * 				让这两个方法也从缓存中取值。findByColumn()由于涉及到查询条件了，不建议覆盖<br>
 * 				如果该表涉及到直接调用save()和deleteByID()方法进行数据更改操作的，在service中必须覆盖这两个方法，覆盖时完成后需要手动的进行缓存刷新操作
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月4日
 */
public interface ICacheable {
	
	/** 添加或者刷新缓存 */
	public void addAFlushCache();
	
	/** 获取缓存中的数据 */
	public <K> K getCache();
	
	/** 清空缓存 */
	public void clearCache();
	
}
