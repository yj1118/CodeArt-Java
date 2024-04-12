package com.apros.codeart.util;

import static com.apros.codeart.runtime.Util.propagate;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.apros.codeart.bytecode.ClassGenerator;
import com.google.common.collect.Iterables;

public final class ListUtil {
	private ListUtil() {
	};

	public static <T> T find(Iterable<T> source, Function<T, Boolean> predicate) {
		for (T item : source) {
			if (predicate.apply(item))
				return item;
		}
		return null;
	}

	public static <T> boolean contains(Iterable<T> source, Function<T, Boolean> predicate) {
		return find(source, predicate) != null;
	}

	public static <T> Iterable<T> filter(Iterable<T> source, Function<T, Boolean> predicate) {
		ArrayList<T> items = null;
		for (T item : source) {
			if (predicate.apply(item)) {
				if (items == null)
					items = new ArrayList<T>();
				items.add(item);
			}
		}
		return items == null ? ListUtil.<T>empty() : items;
	}

	public static <T> T find(T[] source, Function<T, Boolean> predicate) {
		for (T item : source) {
			if (predicate.apply(item))
				return item;
		}
		return null;
	}

	public static <T> Iterable<T> filter(T[] source, Function<T, Boolean> predicate) {
		ArrayList<T> items = new ArrayList<T>();
		for (T item : source) {
			if (predicate.apply(item)) {
				if (items == null)
					items = new ArrayList<T>();
				items.add(item);
			}
		}
		return items == null ? ListUtil.<T>empty() : items;
	}

	public static boolean contains(Iterable<Integer> source, Integer target) {
		for (Integer item : source) {
			if (item == target)
				return true;
		}
		return false;
	}

	public static <T> boolean contains(Iterable<T> source, T value, BiFunction<T, T, Boolean> equals) {
		for (T item : source) {
			if (equals.apply(item, value))
				return true;
		}
		return false;
	}

	public static <T> T first(Iterable<T> source) {
		for (T item : source) {
			return item;
		}
		return null;
	}

	public static <T, R> ArrayList<R> map(Iterable<T> source, Function<T, R> selector) {
		ArrayList<R> list = new ArrayList<R>(Iterables.size(source));
		for (T item : source) {
			list.add(selector.apply(item));
		}
		return list;
	}

	public static <T, R> ArrayList<R> map(T[] source, Function<T, R> selector) {
		ArrayList<R> list = new ArrayList<R>(source.length);
		for (T item : source) {
			list.add(selector.apply(item));
		}
		return list;
	}

	public static <T> ArrayList<T> asList(T[] source) {
		ArrayList<T> list = new ArrayList<T>(source.length);
		for (T item : source) {
			list.add(item);
		}
		return list;
	}

	public static <T, R> ArrayList<R> mapMany(T[] source, Function<T, Iterable<R>> selector) {
		ArrayList<R> list = new ArrayList<R>(source.length);
		for (T item : source) {
			var items = selector.apply(item);
			addRange(list, items);
		}
		return list;
	}

	public static <T> T removeFirst(Iterable<T> source, Function<T, Boolean> predicate) {
		var iterator = source.iterator();
		while (iterator.hasNext()) {
			T item = iterator.next();
			if (predicate.apply(item)) {
				iterator.remove(); // 使用迭代器的 remove() 方法安全删除当前元素
				return item;
			}
		}
		return null;
	}

	public static <T> void addRange(AbstractList<T> source, Iterable<T> collection) {
		addRange(source, collection, false);
	}

	public static <T> void addRange(AbstractList<T> source, Iterable<T> collection, boolean distinct) {
		if (collection == null)
			return;
		if (distinct) {
			for (T item : collection) {
				if (!source.contains(item))
					source.add(item);
			}
		} else {
			for (T item : collection) {
				source.add(item);
			}
		}

	}

	public static <T> void addRange(AbstractList<T> source, T[] collection) {
		addRange(source, collection, false);
	}

	public static <T> void addRange(AbstractList<T> source, T[] collection, boolean distinct) {
		if (collection == null)
			return;
		if (distinct) {
			for (T item : collection) {
				if (!source.contains(item))
					source.add(item);
			}
		} else {
			for (T item : collection) {
				source.add(item);
			}
		}
	}

	public static <T> LinkedList<T> reverse(LinkedList<T> source) {
		LinkedList<T> temp = new LinkedList<>(source);
		Collections.reverse(temp);
		return temp;
	}

	@SuppressWarnings("unused")
	public static boolean exists(Iterable<?> e) {
		if (e == null)
			return false;
		for (var t : e)
			return true;
		return false;
	}

	/**
	 * 让集合高效的设置元素，防止2次遍历
	 * 
	 * @param <T>
	 * @param source
	 * @param predicate
	 * @param target
	 */
	public static <T> void set(AbstractList<T> source, Function<T, Boolean> predicate, Supplier<T> getTarget) {
		var iterator = source.listIterator();
		while (iterator.hasNext()) {
			if (predicate.apply(iterator.next())) {
				iterator.set(getTarget.get());
				break; // 找到目标元素后立即退出循环
			}
		}
	}

	private static final Object Empty;

	private static Object createEmpty() {
		try (var cg = ClassGenerator.define()) {

			try (var mg = cg.defineMethodPublicStatic("getList", List.class)) {
				mg.newList().asReadonlyList();
			}

			var cls = cg.toClass();

			var method = cls.getDeclaredMethod("getList");
			return method.invoke(null);
		} catch (Exception e) {
			throw propagate(e);
		}
	}

	private static final int[] EmptyInts;

	static {
		Empty = createEmpty();
		EmptyInts = new int[] {};
	}

	@SuppressWarnings("unchecked")
	public static <T> Iterable<T> empty() {
		return (Iterable<T>) Empty;
	}

	public static int[] emptyInts() {
		return EmptyInts;
	}

}
