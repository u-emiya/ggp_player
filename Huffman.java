package org.ggp.base.player.gamer.statemachine.sample.gpp_player;

import java.util.HashMap;
import java.util.Map;

public class Huffman {
	public class node{
		node parent;
		node left;
		node right;
		node next;
		int times;
		char chr;

		node(node p,node l,node r,node n,int f,char c){
			this.parent=p;
			this.left=l;
			this.right=r;
			this.next=n;
			this.times=f;
			this.chr=c;
		}

	}

	public class code{
		char chr;
		int value;
		int bit;
		code next;
		public code(char chr) {
			this.chr=chr;
		}
	}

	public node searchNode(node n,char c) {
		while(n!=null) {
			if(n.chr==c) {
			return n;
			}
			n=n.next;
		}
		return n;
	}

	public node newNode(char c) {
		node n;
		n=new node(null,null,null,null,1,c);
		return n;
	}

	public node makeNode(Map<String,Integer> map) {
		node head=null;
		node tail=null;
		for(String s:map.keySet()) {
			 int value=map.get(s);
			 node n=newNode(s.toCharArray()[0]);
			 n.times=value;
			 if(head==null) {
				head=n;
				tail=n;
			 }else{
				tail.next=n;
				tail=n;
			 }

		 }
		return head;
	}

	public node[] getChild(node left,node right,node head) {
		int first=Integer.MAX_VALUE;
		int second=Integer.MAX_VALUE;
		left=null;
		right=null;
		while(head!=null) {
			if(head.parent==null) {
				if(first>head.times && second>head.times){
					first=head.times;
					right=left;
					left=head;
				}else if(second>head.times) {
					second=head.times;
					right=head;
				}
			}

			head=head.next;
		}
		node[] n= new node[2];
		n[0]=left;
		n[1]=right;
		return n;
	}

	public void makeHuffmanTree(node head) {
		node left=null;
		node right=null;
		//node tail=null;
		node add=null;

		if(head==null) {
			System.out.println("error:head is NULL");
			return;
		}
		node tail=head;
		while(tail.next!=null) {
			tail=tail.next;
		}
		node[] leftright=getChild(left,right,head);
		left=leftright[0];
		right=leftright[1];

		while(left!=null && right!=null) {
			add = newNode('\0');
			left.parent=add;
			right.parent=add;
			add.left=left;
			add.right=right;
			add.times=left.times+right.times;

			tail.next=add;
			tail=add;
			leftright=getChild(left,right,head);
			left=leftright[0];
			right=leftright[1];
		}
	}
	public code makeHuffmanCode(node n) {
		node child=null;
		node parent=null;
		code c=null;
		code top=null;
		//code add=null;

		int value=0;
		int bit=0;

		while(n!=null &&n.chr!='\0') {
			value=0;
			bit=0;

			code add=new code(n.chr);
			child=n;
			parent=child.parent;
			while(parent!=null) {
				if(parent.left==child) {
					value=value+(0<<bit);

				}else if(parent.right==child) {
					int ab=value+(1<<bit);
					value=value+(1<<bit);

				}
				bit++;
				child=parent;
				parent=parent.parent;
			}
			add.value=value;
			add.bit=bit;
			if(add.bit==0){
				add.bit=1;
			}
			if(c==null) {
				c=add;
				top=c;
				c.next=null;
			}else {
				c.next=add;
				c=c.next;
				c.next=null;
			}
			n=n.next;
		}
		return top;
	}

	public Map<String,String> takeHaffmanHash(code head) {
		code c=head;
		Map<String,String> haffmanHash=new HashMap<>();
		while(c!=null) {
			String s="";
			int length=Integer.toBinaryString(c.value).length();
			for(int i=0;i<c.bit-length;i++)
				s+="0";
			s+=Integer.toBinaryString(c.value);
			haffmanHash.put(String.valueOf(c.chr), s);
			c=c.next;
		}
		return haffmanHash;

	}
}
