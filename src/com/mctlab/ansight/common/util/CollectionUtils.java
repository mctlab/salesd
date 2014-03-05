package com.mctlab.ansight.common.util;

import com.mctlab.ansight.common.constant.AsEmptyConst;

import java.lang.reflect.Array;
import java.util.*;

public class CollectionUtils {

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.size() == 0;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static <T> List<T> arrayToList(T[] array) {
        List<T> list = new ArrayList<T>();
        list.addAll(Arrays.asList(array));
        return list;
    }

    public static Set<Integer> arrayToSet(int[] array) {
        Set<Integer> set = new HashSet<Integer>();
        for (int i : array) {
            set.add(i);
        }
        return set;
    }

    public static <T> void slice(List<T> oldList, List<T> newList, int start, int len) {
        newList.clear();
        int end = start + len;
        for (int i = start; i < end; i++) {
            newList.add(oldList.get(i));
        }
    }

    public static byte[] slice(byte[] array, int start, int len) {
        byte[] r = new byte[len];
        System.arraycopy(array, start, r, 0, len);
        return r;
    }

    public static int[] slice(int[] array, int start, int len) {
        int[] r = new int[len];
        System.arraycopy(array, start, r, 0, len);
        return r;
    }

    public static String[] slice(String[] array, int start, int len) {
        String[] r = new String[len];
        System.arraycopy(array, start, r, 0, len);
        return r;
    }

    public static int[] splitToInt(String s, String split) {
        if (StringUtils.isBlank(s)) {
            return AsEmptyConst.EMPTY_INT_ARRAY;
        }
        String[] ss = s.split(split);
        return toIntArray(ss);
    }

    public static int[] toIntArray(String[] ss) {
        int[] r = new int[ss.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = Integer.valueOf(ss[i]);
        }
        return r;
    }

    public static int[] toIntArray(List<Integer> list) {
        return toPrimitive(list.toArray(new Integer[0]));
    }

    public static int[] toIntArray(Set<Integer> set) {
        if (isEmpty(set)) {
            return new int[0];
        }
        int[] ret = new int[set.size()];
        int i = 0;
        for (Integer value : set) {
            ret[i++] = value == null ? 0 : value;
        }
        return ret;
    }

    public static boolean[] toBooleanArray(List<Boolean> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new boolean[0];
        }
        boolean[] ret = new boolean[list.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }

    public static int[] toPrimitive(Integer[] array) {
        int[] r = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            r[i] = array[i];
        }
        return r;
    }

    public static Integer[] toBoxed(int[] array) {
        Integer[] boxed = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            boxed[i] = array[i];
        }
        return boxed;
    }

    public static boolean in(int value, int[] array) {
        for (int v : array) {
            if (v == value) {
                return true;
            }
        }
        return false;
    }

    public static int[] insert(int[] array, int pos, int value) {
        int[] newArray = new int[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, pos);
        newArray[pos] = value;
        System.arraycopy(array, pos, newArray, pos + 1, array.length - pos);
        return newArray;
    }

    public static int[] remove(int[] array, int start, int len) {
        int[] r = new int[array.length - len];
        System.arraycopy(array, 0, r, 0, start);
        System.arraycopy(array, start + len, r, start, array.length - start - len);
        return r;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] remove(T[] array, int start, int len, Class<T> clazz) {
        T[] r = (T[]) Array.newInstance(clazz, array.length - len);
        System.arraycopy(array, 0, r, 0, start);
        System.arraycopy(array, start + len, r, start, array.length - start - len);
        return r;
    }

    public static <T> boolean every(List<T> list, Tester<T> callback) {
        for (T data : list) {
            if (!callback.test(data)) {
                return false;
            }
        }
        return true;
    }

    /* tester */

    public static interface Tester<T> {
        public boolean test(T data);
    }

    public static <T> void filter(List<T> list, Tester<T> tester) {
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            if (!tester.test(it.next())) {
                it.remove();
            }
        }
    }

    /* to string */

    public static String toString(int[] array) {
        return reduce(toBoxed(array), INT_ARRAY_TO_STRING, "");
    }

    private static final Reducer<Integer, String> INT_ARRAY_TO_STRING = new Reducer<Integer, String>() {
        @Override
        public String reduce(String previousValue, Integer currentValue, int index, Integer[] array) {
            if (StringUtils.isBlank(previousValue)) {
                if (index == array.length - 1) {
                    return "[" + currentValue + "]";
                } else {
                    return "[" + currentValue;
                }
            } else {
                if (index == array.length - 1) {
                    return previousValue + ", " + currentValue + "]";
                } else {
                    return previousValue + ", " + currentValue;
                }
            }
        }
    };

    /* join */

    public static String join(Integer[] array, String separator) {
        Joiner<Integer> joiner = new Joiner<Integer>(separator);
        return reduce(array, joiner, "");
    }

    public static String join(int[] array, String separator) {
        Integer[] boxed = toBoxed(array);
        return join(boxed, separator);
    }

    public static String join(String[] array, String separator) {
        Joiner<String> joiner = new Joiner<String>(separator);
        return reduce(array, joiner, "");
    }

    private static class Joiner<T> implements Reducer<T, String> {

        private final String separator;

        public Joiner(String separator) {
            super();
            this.separator = separator;
        }

        @Override
        public String reduce(String previousValue, T currentValue, int index, T[] array) {
            if (StringUtils.isBlank(previousValue)) {
                return currentValue.toString();
            } else {
                return previousValue + separator + currentValue;
            }
        }
    }

    /* map reduce */

    public static interface Mapper<T, R> {
        public R map(T value);
    }

    public static <T, R> R[] map(T[] values, R[] emptyResults, Mapper<T, R> mapper) {
        for (int i = 0; i < emptyResults.length; i++) {
            emptyResults[i] = mapper.map(values[i]);
        }
        return emptyResults;
    }

    public static interface Reducer<T, R> {
        public R reduce(R previousValue, T currentValue, int index, T[] array);
    }

    public static <T, R> R reduce(T[] array, Reducer<T, R> reducer, R initialValue) {
        if (isEmpty(array)) {
            return initialValue;
        }
        R result = initialValue;
        for (int i = 0; i < array.length; i++) {
            result = reducer.reduce(result, array[i], i, array);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(toString(new int[]{1, 2, 3}));
        System.out.println(join(new int[]{7, 8, 9}, "|"));
    }

}
