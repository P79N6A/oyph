package common.utils;

import java.util.concurrent.locks.ReentrantLock;

import play.cache.Cache;

/**
 * 可重入的互斥锁,工具类
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月23日
 */
public class ReentrantLockUtil {
	
	/**
	 * 加锁，并将锁放入缓存中
	 *
	 * @param cacheKey 缓存key
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月23日
	 */
	public static void lock(String cacheKey) {
		getLock(cacheKey).lock();
	}

	/**
	 * 解锁，从缓存中取出
	 *
	 * @param cacheKey 缓存key
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月23日
	 */
	public static void unLock(String cacheKey) {
		ReentrantLock lock = getLock(cacheKey);
		if(lock == null){
			
			return;
		}
		
		if(lock.isHeldByCurrentThread()){
			lock.unlock();
		}

	}
	
	/**
	 * 清除锁缓存
	 *
	 * @param cacheKey
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月23日
	 */
	public static void cleanCacheLock(String cacheKey) {
		ReentrantLock lock = (ReentrantLock) Cache.get(cacheKey);
		if(lock == null){
			
			return;
		}
		
		if(lock.isHeldByCurrentThread()){
			lock.unlock();
		}
		
		if(!lock.hasQueuedThreads()){
			Cache.delete(cacheKey);
		}
	}
	
	/**
	 * 获取对象锁,每个缓存key对应一个锁
	 *
	 * @param cacheKey 缓存key
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月23日
	 */
	private static ReentrantLock getLock(String cacheKey) {
		ReentrantLock lock = (ReentrantLock) Cache.get(cacheKey);
		if (lock == null) {
			synchronized (ReentrantLockUtil.class) {  //单例，一个cacheKey，一个锁
				lock = (ReentrantLock) Cache.get(cacheKey);
				if (lock == null) {
					lock = new ReentrantLock();
					Cache.set(cacheKey, lock);
				}
			}
		}
		
		return lock;
	}

}
