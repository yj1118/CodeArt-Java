package apros.codeart.context;

import static apros.codeart.runtime.Util.any;

import java.util.HashSet;

import apros.codeart.pooling.IReusable;
import apros.codeart.pooling.Pool;
import apros.codeart.pooling.PoolConfig;
import apros.codeart.pooling.Util;
import apros.codeart.util.Func;

/**
 * 每个应用程序会话都会有一个唯一的共生器对象，该对象是从池中分配和回收。 每个应用程序会话可以利用共生器对象创建若干个可以被回收或释放的对象。
 * 这些对象在应用程序会话结束时，要么被回收，要么被释放。
 */
final class Symbiosis implements IReusable {

	private HashSet<Object> _items = new HashSet<Object>();

	private Symbiosis() {
	}

	public void add(Object item) {
		// 回收和释放，所以没有实现这两个接口的对象不用加入到集合中
		if (any(item, IReusable.class, AutoCloseable.class)) {
			_items.add(item);
		}
	}

	public int getCount() {
		return _items.size();
	}

	@Override
	public void clear() throws Exception {
		Util.stop(_items);
		_items.clear();
	}

	private static Pool<Symbiosis> _pool = new Pool<Symbiosis>(() -> {
		return new Symbiosis();
	}, PoolConfig.onlyMaxRemainTime(300));

	private final static String _sessionKey = "__Symbiosis.Current";

	/**
	 * 
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Symbiosis getCurrent() throws Exception {
		return ContextSession.<Symbiosis>obtainItem(_sessionKey, _pool);
	}

	public static <T> T obtain(Pool<T> pool, Func<T> creator) throws Exception {
		var temp = pool.borrow();
		getCurrent().add(temp);
		return temp.getItem();
	}

}
