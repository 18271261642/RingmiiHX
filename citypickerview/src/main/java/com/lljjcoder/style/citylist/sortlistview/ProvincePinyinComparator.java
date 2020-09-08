package com.lljjcoder.style.citylist.sortlistview;

import com.lljjcoder.bean.ProvinceBeanNew;

import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class ProvincePinyinComparator implements Comparator<ProvinceBeanNew> {

	public int compare(ProvinceBeanNew o1, ProvinceBeanNew o2) {
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
