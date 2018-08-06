package life.qbic.portal.presenter.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Sorter {

    public static  <K, V extends Comparable<? super V>> List<K> getInsertOrder(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        List<K> sortedKeys = new ArrayList<>();
        for(Map.Entry<K,V> entry :list){
            sortedKeys.add(entry.getKey());
        }
        return sortedKeys;
    }
}
