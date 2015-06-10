package net.lvcy.main;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		AP ap=new AP("D:/test.txt");
		ap.iterator(50);
		ap.resultSAR("D:/testSAR.txt");
		
	}
}
