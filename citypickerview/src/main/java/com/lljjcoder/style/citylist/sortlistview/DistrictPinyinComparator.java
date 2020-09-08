package com.lljjcoder.style.citylist.sortlistview;

import com.lljjcoder.bean.DistrictBeanNew;
import com.lljjcoder.bean.ProvinceBeanNew;

import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class DistrictPinyinComparator implements Comparator<DistrictBeanNew> {

	public int compare(DistrictBeanNew o1, DistrictBeanNew o2) {
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
