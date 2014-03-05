package com.mctlab.ansight.common.util;

import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IdUtils {

    /**
     * 从数据结构 T 中将 id 提取出来
     */
    public static interface IdStripper<T> {
        public int strip(T t);
    }

    /**
     * 将 raw 重新排序, 顺序按照其 id 在 ids 中的顺序, 排序后输出到 ret
     */
    public static <T> void permute(List<T> raw, int[] ids, T[] ret, IdStripper<T> stripper) {
        if (ArrayUtils.isEmpty(ids) || ArrayUtils.isEmpty(ret) || ids.length != ret.length) {
            throw new IllegalArgumentException("id array and object array must have the same size");
        }
        SparseArray<T> sa = new SparseArray<T>(ids.length);
        for (T t : raw) {
            sa.put(stripper.strip(t), t);
        }
        for (int i = 0; i < ret.length; i++) {
            ret[i] = sa.get(ids[i]);
        }
    }

    /**
     * 将 raw 中的 value 重新排序, 顺序按照其 key 在 ids 中的顺序, 排序后输出到 ret
     */
    public static void permute(List<Pair<Integer, Integer>> raw, int[] ids, int[] ret) {
        if (ArrayUtils.isEmpty(ids) || ArrayUtils.isEmpty(ret) || ids.length != ret.length) {
            throw new IllegalArgumentException("id array and object array must have the same size");
        }
        SparseIntArray sia = new SparseIntArray(ids.length);
        for (Pair<Integer, Integer> t : raw) {
            sia.put(t.first, t.second);
        }
        for (int i = 0; i < ret.length; i++) {
            ret[i] = sia.get(ids[i]);
        }
    }

    public static <T> Set<Integer> getIdSet(List<T> datas, IdStripper<T> stripper) {
        Set<Integer> idSet = new HashSet<Integer>();
        for (T data : datas) {
            if (data != null) {
                idSet.add(stripper.strip(data));
            }
        }
        return idSet;
    }

    public static int[] getMissedIds(Set<Integer> cachedIdSet, int[] required) {
        if (CollectionUtils.isEmpty(required)) {
            return new int[0];
        }
        List<Integer> missedIdList = new ArrayList<Integer>();
        for (int id : required) {
            if (!cachedIdSet.contains(id)) {
                missedIdList.add(id);
            }
        }
        return CollectionUtils.toIntArray(missedIdList);
    }

}
