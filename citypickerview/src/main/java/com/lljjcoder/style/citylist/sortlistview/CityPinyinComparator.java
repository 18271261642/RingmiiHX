package com.lljjcoder.style.citylist.sortlistview;

import com.lljjcoder.bean.CityBeanNew;
import com.lljjcoder.bean.ProvinceBeanNew;

import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class CityPinyinComparator implements Comparator<CityBeanNew> {

	public int compare(CityBeanNew o1, CityBeanNew o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
