package com.example.core.util;

import cn.hutool.core.bean.BeanPath;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.multi.ListValueMap;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author chenwh3
 */
public class MapUtil {


	public static void sortToMap(List<? extends Map> mapList, String... keys) {
		if (CollUtil.isEmpty(mapList)) {
			return;
		}
		Comparator<Map> c = Arrays.stream(keys).map(e -> {
			String[] s = e.split("\\s+");
			String column = s[0];
			boolean asc = s.length == 1 || StringUtils.equalsAnyIgnoreCase(s[1], "asc", "+");
			Comparator<Map> comparator = Comparator.comparing(map -> StringUtil.toNotNullStr(map.get(column)));
			if (!asc) {
				comparator = comparator.reversed();
			}
			return comparator;
		}).collect(Collectors.reducing((a, b) -> a.thenComparing(b))).orElse(null);
		if (c == null) {
			return;
		}
		mapList.sort(c);
	}




	/**
	 * list根据某个字段转map
	 */
	public static <T, K> Map<K, T> toMap(Collection<T> t, Function<T, K> f1) {
		return toMap(t, f1, Function.identity());
	}

	public static <T, K, V> Map<K, V> toMap(Collection<T> t, Function<T, K> f1, Function<T, V> f2) {
        if (t == null) {
			return Collections.emptyMap();
        }
		return t.stream().collect(Collectors.toMap(f1, f2, (a, b) -> a));
	}

    public static <T, K> ListValueMap<K, T> toMapList(Collection<T> t, Function<T, K> f1) {
        return toMapList(t, f1, Function.identity());
    }

    public static <T, K, V> ListValueMap<K, V> toMapList(Collection<T> t, Function<T, K> f1, Function<T, V> f2) {
        ListValueMap<K, V> list = new ListValueMap<>();
        if (!CollUtil.isEmpty(t)) {
            for (T t1 : t) {
                list.putValue(f1.apply(t1), f2.apply(t1));
            }
        }
        return list;
    }

	public static <T, K, V>  Map<K,Set<V>> toMapSet(List<T> t, Function<T, K> f1, Function<T, V> f2 , Supplier<? extends Set<V>> supplier) {
		if (CollUtil.isEmpty(t)) {
			return Collections.emptyMap();
		}
		Map<K, Set<V>> map = new HashMap<>();
		for (T o : t) {
			K k = f1.apply(o);
			V v = f2.apply(o);
			map.putIfAbsent(k, supplier.get());
			map.get(k).add(v);
		}
		return map;
	}

	/**
	 * list根据两个字段转map
	 */
	public static <T, K, V> MultiKeyMap<K, V> to2KeyMap(List<T> list, Function<T, K> k1, Function<T, K> k2, Function<T, V> v) {
		MultiKeyMap<K, V> map = new MultiKeyMap<>();
		for (T t : list) {
			map.put(k1.apply(t), k2.apply(t), v.apply(t));
		}
		return map;
	}
	/**
	 * list根据两个字段转map
	 */
	public static <T, K, V> MultiKeyMap<K, V> to2KeyMap(List<T> list, Function<T, K> k1, Function<T, K> k2, Function<T, V> v, BinaryOperator<V> mergeFunction) {
		MultiKeyMap<K, V> map = new MultiKeyMap<>();
		for (T t : list) {
			K key1 = k1.apply(t);
			K key2 = k2.apply(t);
			V val = v.apply(t);
			if (mergeFunction != null && map.containsKey(key1, key2)) {
				map.put(key1, key2, mergeFunction.apply(map.get(key1, key2), val));
			} else {
				map.put(key1, key2, val);
			}
		}
		return map;
	}

	public static <T, K> MultiKeyMap<K, T> to2KeyMap(List<T> list, Function<T, K> k1, Function<T, K> k2) {
		return to2KeyMap(list, k1, k2, Function.identity());
	}

	public static <T, K, V> MultiKeyMap<K, List<V>> to2KeyMapList(List<T> list, Function<T, K> k1, Function<T, K> k2, Function<T, V> vFunc) {
		MultiKeyMap<K, List<V>> map = new MultiKeyMap<>();
		for (T t : list) {
			MultiKey<K> key = new MultiKey<>(k1.apply(t), k2.apply(t));
			map.computeIfAbsent(key, (k) -> new ArrayList<>());
			map.get(key).add(vFunc.apply(t));
		}
		return map;
	}


	public static <T> Map<String, String> toMapNotNull(List<T> t, Function<T, ?> f1, Function<T, ?> f2) {
		return toMap(t, f1.andThen(StringUtil::toNotNullStr), f2.andThen(StringUtil::toNotNullStr));
	}



	public  static <T> T getByPath(Map<String,Object> obj ,  String expression, Class<T> t ){
		return Convert.convert(t, new BeanPath(expression).get(obj));
	}

	/**
	 * 按配对键值进行复制
	 */
	public static void fillByKeyMap(Map oriMap, Map aimMap, List<String> oriCols, List<String> aimCols) {
		Set<String> oriSet = new HashSet<>(oriCols);
		//建立列名映射
		Map<String, String> colMap = Stream.iterate(0, i -> i + 1)
				.limit(oriCols.size())
				.collect(Collectors.toMap(oriCols::get, aimCols::get));
		for (String col : oriSet) {
			if (colMap.containsKey(col)) {
				aimMap.put(colMap.get(col), oriMap.get(col));
			}
		}
	}

}
