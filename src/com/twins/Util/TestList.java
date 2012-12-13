package com.twins.Util;
import java.util.ArrayList;

public class TestList {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		list.add("hello");
		list.add("test");
		list.add("testee");
		for(int i = 0; i < list.size(); i++) {
			list.remove(i);
		}
		System.out.println(list);
	}

}
