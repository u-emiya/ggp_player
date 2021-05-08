package org.ggp.base.player.gamer.statemachine.sample.gpp_player;

import java.util.ArrayList;
import java.util.Collections;

public class MCTSutils {

	public static String hash(String str,int lengthOfHash){
		byte[] ascii=str.getBytes();

		int[][]  sliceAscii=new int[ascii.length/lengthOfHash+1][lengthOfHash];
		int k=0;
		for(int i=0;i<(ascii.length/lengthOfHash)+1;i++) {
			for(int j=0;j<lengthOfHash;j++) {
				if(k<ascii.length)
					sliceAscii[i][j]=ascii[k++];
				else
					sliceAscii[i][j]=0;
			}
		}
		int[] mixAscii=new int[lengthOfHash];
		for(int i=0;i<mixAscii.length;i++) {
			for(int j=0;j<sliceAscii.length;j++) {
				mixAscii[i]+=(int)(Math.pow(sliceAscii[j][i],Math.log(j+2)))%256;
			}

			mixAscii[i]%=256;
		}

		String result="";
		for(int i=0;i<mixAscii.length;i++) {
			if(mixAscii[i]<16)
				result+=Integer.toString(0, 16);

			result+=Integer.toString(mixAscii[i], 16);

		}
		return result;
	}

	public static long hashInteger(String str,int lengthOfHash){
		byte[] ascii=str.getBytes();

		long[][]  sliceAscii=new long[ascii.length/lengthOfHash+1][lengthOfHash];
		int k=0;
		for(int i=0;i<(ascii.length/lengthOfHash)+1;i++) {
			for(int j=0;j<lengthOfHash;j++) {
				if(k<ascii.length)
					sliceAscii[i][j]=ascii[k++];
				else
					sliceAscii[i][j]=0;
			}
		}
		long[] mixAscii=new long[lengthOfHash];
		for(int i=0;i<mixAscii.length;i++) {
			for(int j=0;j<sliceAscii.length;j++) {
				//System.out.print(sliceAscii[j][i]+":"+(long)(Math.pow(sliceAscii[j][i],Math.log(j+2))*10)+" ");
				mixAscii[i]+=(long)(Math.pow(sliceAscii[j][i],Math.log(j+2))*10);
			}
			//System.out.println();

			if(mixAscii[i]<0)
				System.out.println(mixAscii[i]);
			mixAscii[i]%=512;
		}
		//System.out.println();

		long result=0;
		double apple=1;
		for(int i=0;i<mixAscii.length;i++) {
			apple=Math.pow(10,i*3);
			//System.out.println(mixAscii[i]*apple);
			result += mixAscii[i]*apple;
		}
		return result;

	}

	public static void testPrint(int[] a) {
		for(int i=0;i<a.length;i++) {
			System.out.println(i+":"+a[i]);
		}
	}

	public static String preprocess(String str) {
		str=str.replace("true", "").replace(" ", "").replace("cell", "");
		char[] c =str.toCharArray();
		String s="";
		ArrayList<String> ls=new ArrayList<String>();
		for(int i=0;i<c.length;i++) {
			if(c[i]=='(') {
				int j;
				for(j=i+2;c[j]!=')';j++) {
					s+=c[j];
				}
				//s+=c[j];
				ls.add(s);
				s="";
				i=j;
			}
		}

		Collections.sort(ls);

		for(String apple:ls) {
			if(apple.contains("step")||apple.contains("control"))
				continue;
			s+=apple;

		}


		return s;
	}

	public int saveCount=0;
	public void setCount(int i) {
		saveCount=i;
	}
}
