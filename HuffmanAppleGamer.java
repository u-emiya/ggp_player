package org.ggp.base.player.gamer.statemachine.sample.gpp_player;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.SimpleDetailPanel;
import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.exception.GamePreviewException;
import org.ggp.base.player.gamer.statemachine.sample.gpp_player.Huffman.node;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;


//1020
public final class HuffmanAppleGamer extends SampleGamer
{
	//int[] boardSize= {-1,-1,Integer.MAX_VALUE,Integer.MAX_VALUE};
	int[] boardSize= {-1,-1,1,1};
	 Map<String,String> haffmanHashMap=new HashMap<>();
	 Map<String,String> wardToCharMap=new HashMap<>();
	 Map<String,Integer> appearMap=new HashMap<>();
	 MCTSutils mu=new MCTSutils();
	 public static final int HASHSIZE=63;
	 //for making hash border
	 static String  spaceHash="";

	 @Override
	 public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	 {
		 initAll();
		 System.out.println("start:::start:::start:::start:::start:::start:::start:::start:::start:::start");
		 long finishBy = timeout - 1000;

		 StateMachine theMachine = getStateMachine();
		 Huffman hf=new Huffman();

		 int times=0;
		 int[] depth=new int[1];
		 String test="";
		 while(System.currentTimeMillis() < finishBy) {
			 MachineState finalState=theMachine.performDepthCharge(getCurrentState(), depth);
			 String state=mu.preprocess(finalState.toString());
			 state=mu.wtnInboardInformation(state);
			 test=mu.pressRoleString(state, wardToCharMap,appearMap);
			 times++;
		 }
		 System.out.println("playout times:"+times);
		 System.out.println("row:"+mu.rowWordToNumberMap.size());
		 System.out.println("column:"+mu.columnWordToNumberMap.size());
		 System.out.println("max board size:( x ,y ) = ( "+boardSize[0]+" , "+boardSize[1]+" )");
		 boardSize[0]=mu.columnWordToNumberMap.size();
		 boardSize[1]=mu.rowWordToNumberMap.size();
		 System.out.println("max board size:( x ,y ) = ( "+boardSize[0]+" , "+boardSize[1]+" )");

		 int max=boardSize[0];
		 if(max<boardSize[1])
			 max=boardSize[1];


		 for(int i=0;i<max+1;i++) {
			 if(wardToCharMap.containsKey(Integer.toString(i))) {
				 appearMap.remove(Integer.toString(i));
				 wardToCharMap.remove(Integer.toString(i));
			 }
		 }
		 int total=0;
		 for(String ccc:appearMap.keySet()) {
			 total+=appearMap.get(ccc);
		 }
		 total=times*boardSize[0]*boardSize[1]-total;
		 if(total>0)
			 appearMap.put("¥",total);
		 System.out.println("after");
		 System.out.println("all of appear map");
		 for(String ccc:appearMap.keySet()) {
			 System.out.println(ccc+"::"+appearMap.get(ccc));

		 }

		 System.out.println("all of ward to char map");
		 for(String ccc:wardToCharMap.keySet()) {
			 System.out.println(ccc+"::"+wardToCharMap.get(ccc));
		 }

		 node nnn=hf.makeNode(appearMap);
		 hf.makeHuffmanTree(nnn);
		 haffmanHashMap=hf.takeHaffmanHash(hf.makeHuffmanCode(nnn));

		 System.out.println("all of huffman hash map");
		 for(String s:haffmanHashMap.keySet()) {
			 System.out.println(wardToCharMap.get(s)+":::"+s+"---"+haffmanHashMap.get(s));
			 if(s.equals("¥"))
				 spaceHash=haffmanHashMap.get(s);
		 }
		 System.out.println(test);
		 System.out.println("MAX(x,y) = ("+boardSize[0]+","+boardSize[1]+")");
		 System.out.println("MIN(x,y) = ("+boardSize[2]+","+boardSize[3]+")");
		 test=mu.makePafeBoard(test);
		 System.out.println(test);
		 System.out.println("length:"+test.length());
		 test=mu.encodeHaffman(test,haffmanHashMap);
		 System.out.println(test);

	 }
    /**
     * Employs a simple sample "Monte Carlo" algorithm.
     */

    @Override
    public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
    {
        StateMachine theMachine = getStateMachine();
        long start = System.currentTimeMillis();

        //long finishBy = timeout - 1000;
        turn++;
        System.out.println("---     huffman     ----");
    	System.out.println("role number:"+getOwnRoleNumber());
        System.out.println("●----"+turn+"----●");

        Node n=new Node(getCurrentState());

    	root=n;

    	//while(System.currentTimeMillis()<finishBy) {
        for(int i=0;i<500;i++) {
        	MonteCalroPlayout(n);
       	}

        List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
        Move selection = moves.get(0);

        if (moves.size() > 1) {
        	selection=selectNextPlay(n,moves);
        }
        globalDepth=n.depth+1;

        long stop = System.currentTimeMillis();
        System.out.println(":"+MCTSutils.preprocess(getCurrentState().toString())+":");

    	System.out.println("select moves:"+selection);
    	showAll(root);
        System.out.println("MCTS node size:"+showAllCount);
        showAllCount=0;
        System.out.println("huffman memory size:"+huffmanMemorys.size());


        System.out.println("time:"+(stop-start));

        System.out.println();

        notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
        return selection;
    }
    /*
     * select next legal move
     * the return type is Move
     */
    public Move selectNextPlay(Node n,List<Move> moves) throws MoveDefinitionException, TransitionDefinitionException {
    	StateMachine theMachine = getStateMachine();
        Role role=getRole();
    	MachineState bestState=null;
    	Move bestMove=null;
    	int bestValue=0;
    	for(MachineState key:n.children.keySet()) {
    		Node child=n.children.get(key);

    		if(child.v>bestValue) {
        		bestValue=child.v;
        		bestState=key;
        	}
    	}

    	Map<Move, List<MachineState>> map=theMachine.getNextStates(getCurrentState(),role);
        for(int i=0;i<moves.size();i++) {
        	Move m=moves.get(i);
        	List<MachineState> stateList=map.get(m);
        	if(stateList.contains(bestState)) {
        		bestMove=m;
        		break;
        	}

        }
        return bestMove;
    }


    public int totalPlayerNumber;//total number of players
    public Node root=null;//root node
    public int playerNum=0;//current player number in simulation

    public void initAll() {
    	root=null;
    	totalPlayerNumber=getStateMachine().getRoles().size();
    	huffmanMemorys = new HashMap<>();
    	turn=0;
    	haffmanHashMap=new HashMap<>();
   	 	wardToCharMap=new HashMap<>();
   	 	appearMap=new HashMap<>();
    	mu=new MCTSutils();
    }

    public class Node{
    	int v; //times of visited
      	int selectionCount; //times of visited
      	MachineState state;//Board information
    	int depth;//depth from root node
    	Map<MachineState ,Node> children;//all of child node

    	int winValue[];//accumulated points
    	double winRate[];//(winValue/v)

    	Node(MachineState state){
    		this.v=0;
    		this.selectionCount=0;
    	    this.state=state;
    	    this.depth=0;
    	    this.children= new HashMap<MachineState ,HuffmanAppleGamer.Node>();
    	    this.winValue=new int[totalPlayerNumber];
    	    this.winRate=new double[totalPlayerNumber];
    	}

    	public MachineState getState() {
    		return this.state;
    	}
    	public void setWinValue(int[] value) {
    		for(int i=0;i<winValue.length;i++) {
    			this.winValue[i]+=value[i];
    		}
    	}


    	public void setdepthValue(int depth) {
    		this.depth=depth;
    	}
    	public void visitValueCount() {
    		this.v++;
    	}
    	public void selectCount() {
    		this.selectionCount++;
    	}

     	public void winRateCalculation() {
    		if(v!=0) {
    	    	//mark!!!
    	    	for(int i=0;i<winValue.length;i++) {
    	    		double value=(double)this.v*100;
    	    		this.winRate[i]=(double)this.winValue[i]/value;
    	    	}
    		}
    		else {
    			errorCount++;
    		}
    	}

    	public void expand(Node n){
    	    this.children.put(n.state,n);
    	    n.selectCount();
    	}

    }
    public int errorCount=0;
    public int turn=0;

    public boolean firstPlayoutState=true;
    /*
     * 一回のシミュレーションの過程で発生したノードはキューに突っ込む
     */
    public void MonteCalroPlayout(Node n) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
    	StateMachine theMachine = getStateMachine();
    	Node node=n;
    	Node saveParentNode=null, saveChildNode=null;
    	MachineState state=n.getState();
    	Queue<Node> que=new ArrayDeque<>();
    	que.add(n);
    	playerNum=getCurrentPlayerNum();
    	boolean isExpandNode=true;

    	while(!theMachine.isTerminal(state)) {
    		int childlenMaxSize=theMachine.getLegalMoves(node.state, theMachine.getRoles().get(playerNum)).size();
    		Node beforeNode=node;
    		node=selectChildNode(node);
    		if(beforeNode.children.size()!=childlenMaxSize && isExpandNode) {
     			saveParentNode=beforeNode;
      			saveChildNode=node;
      			isExpandNode=false;
      		}
    		state=node.getState();
      	   	que.add(node);
      		playerNum=(playerNum+1)%(theMachine.getRoles().size());
     	}
		firstPlayoutState=true;


    	List<Role> roleList=theMachine.getRoles();
    	int[] goalScore=new int[totalPlayerNumber];
    	for(int i=0;i<totalPlayerNumber;i++) {
    		Role l=roleList.get(i);
    		goalScore[i]=theMachine.getGoal(state,l);
    	}

    	/*
    	 * シミュレーションの過程で発生した盤面情報をHuffmanメモリに保存する。
    	 * この時にキューに保存しているノードに訪れた回数、報酬を記録する。
    	 */
      	Node parent=null;
      	for (Node v : que) {
    		v.visitValueCount();
    		v.setWinValue(goalScore);
    		if(parent!=null) {
    			v.setdepthValue(parent.depth+1);
        	}
    		String huffman=makeHuffmanCode(v.state.toString());
    		addHufMemo(huffman,v,goalScore);
    		v.selectCount();
    		parent=v;
    	}
    	if(saveChildNode!=null) {
    		Node expandNode=new Node(saveChildNode.getState());
    		expandNode.v=saveChildNode.v;
    		expandNode.setWinValue(saveChildNode.winValue);
    		expandNode.setdepthValue(saveParentNode.depth+1);
    		saveParentNode.expand(expandNode);
    	}

    	return;
    }

    /*
     * select child node
     * if Node of argument is in MCTS tree, select child node from it
     * and else
     */
    public Node selectChildNode(Node n) throws MoveDefinitionException, TransitionDefinitionException {
    	Node selectNode=null;
    	if(isUnexploredState(n)) {
    		double saveValue=0;
    		for(MachineState key:n.children.keySet()) {
    			Node child=n.children.get(key);
    			double uctValue=uctCalculation(n,child);
    			if(uctValue>saveValue || selectNode==null) {
    				saveValue=uctValue;
    				selectNode=child;
    			}
    		}
    	}else {
    		selectNode=getUnexploredNextNode(n);
    		firstPlayoutState=false;
    	}

    	return selectNode;

    }
    /*
     * get own role number in List<Role>
     */
    public int getOwnRoleNumber() {
    	StateMachine theMachine = getStateMachine();
    	List<Role> roleList=theMachine.getRoles();
    	Role l=getRole();
    	for(int i=0;i<roleList.size();i++) {
    		if(roleList.get(i).equals(l))
    			return i;
    	}
    	return 100;
    }
    /*
     * calculate the uct value from the arguments.
     */
    public double uctCalculation(Node parent,Node child) {
    	double logVisitedValue=Math.log(parent.v);
    	double searchValue=0;
    	double C=0.7;

    	if(child.v!=0) {
    		searchValue=Math.sqrt(logVisitedValue/child.v);
    	}else {
    		searchValue=Double.MAX_VALUE;
    	}
     	child.winRateCalculation();//mark!!
     	double uctValue=child.winRate[playerNum]+C*searchValue;

    	return uctValue;
    }

    /*
     * Return true if argument Node has child nodes that are all kinds of next board information.
     */
    public boolean isUnexploredState(Node n) throws MoveDefinitionException, TransitionDefinitionException {
        StateMachine theMachine = getStateMachine();
    	if(n.children==null)
    		return false;

      	int nodeSize=n.children.size();
      	int nextSize=theMachine.getLegalMoves(n.state, theMachine.getRoles().get(playerNum)).size();

    	if(nodeSize==nextSize) {
    		return true;
    	}

    	return false;


    }

    /*
     *Get the next game state which is not in MCTS tree.
     *First, select the state from playout memory.
     *If it is not, then use StateMachine method to get the next state.
     */

    public Node getUnexploredNextNode(Node n) throws MoveDefinitionException, TransitionDefinitionException {
    	StateMachine theMachine = getStateMachine();
    	 MachineState nextState;
    	 Node selectHuffmanNode=null;

    	while(true) {
    		List<Move> a=theMachine.getRandomJointMove(n.getState());
    		nextState = theMachine.getNextState(n.getState(),a );
            if(!n.children.containsKey(nextState)) {
            	if(firstPlayoutState) {
            		String huffman=makeHuffmanCode(nextState.toString());
            		selectHuffmanNode=selectHuffmanMemory(huffman);
            	}
               	break;
            }
    	}
    	if(selectHuffmanNode==null) {
    		return new Node(nextState);
    	}
    	else {
    		selectHuffmanNode.winRateCalculation();
    		Node returnNode=new Node(nextState);
    		returnNode.setWinValue(selectHuffmanNode.winValue);
    		returnNode.v=selectHuffmanNode.v;

    		return returnNode;
    	}
    }


    /*
     * Return the current player number who can play the legal move;
     */
    public int getCurrentPlayerNum() throws MoveDefinitionException, TransitionDefinitionException {
    	StateMachine theMachine=getStateMachine();
    	int num=getOwnRoleNumber();

    	if(isSimultaneous()) {
    		return num;
    	}

    	List<Move> ls=theMachine.getRandomJointMove(getCurrentState());
    	for(int i=0;i<ls.size();i++) {
    		String str=ls.get(i).toString();
    		char[] apple = str.toCharArray();
    		for(char c:apple){
    			if(c=='(' || c==')'){
    				return i;
    			}
    		}
    	}

    	return num;
    }
    /*
     * Return true if the playing game is simultaneous game.
     */
    public boolean isSimultaneous() throws MoveDefinitionException {
     	StateMachine theMachine=getStateMachine();
    	List<Move> ls=theMachine.getRandomJointMove(getCurrentState());
    	int count=0;
    	for(int i=0;i<ls.size();i++) {
    		String str=ls.get(i).toString();
    		char[] apple = str.toCharArray();
    		for(char c:apple){
    			if(c=='(' || c==')'){
    				count++;
    				break;
    			}
    		}
    	}
    	if(count>1)
    		return true;
    	else
    		return false;
    }
    /*
     * Print all of Nodes in constructed tree.
     */
    int globalDepth=0;
    int showAllCount=0;
    public void showAll(Node n) {
    	showAllCount++;
    	/*
    	if(n.depth<=globalDepth) {

    		System.out.println("--- show all ---");
    		System.out.println(n.state);
    	  	System.out.println(MCTSutils.preprocess(n.state.toString()));
    		System.out.println("*depth---"+n.depth+"  n:::"+n.v+",w:::"+n.winValue[getOwnRoleNumber()]);
    		System.out.println("w/v:::"+n.winValue[getOwnRoleNumber()]/n.v);
    	}

    	if(n.children == null){
    		return;
    	}*/
    	for(MachineState key:n.children.keySet()){
    		//System.out.println("times of i ==== "+i++);
    		Node next=n.children.get(key);
    		//nxt.printItem(nxt.list);
    		showAll(next);
    	}
    }

	/*
	 * Return true if the second argument is the next game state by the first argument.
	 */
	public boolean isNextState(MachineState state,MachineState nextState) throws MoveDefinitionException, TransitionDefinitionException {
    	StateMachine theMachine = getStateMachine();
    	List<MachineState> ls=theMachine.getNextStates(state);
    	if(ls.contains(nextState))
    		return true;
    	return false;
	}


	/*
	 * Return true following epsilon greedy method.
	 */
	public boolean ipsilonGreedy() {
		double rnd=Math.random();
		double ipsilon=0.2;
		if((1-ipsilon)>rnd)
			return true;
		return false;
	}

	/*part of huffman*/
	Map<Long,huffmanSet> huffmanMemorys = new HashMap<>();

	public static class huffmanSet{
		long[] code=new long[10];
		int size=0;
		int numDigit=0;
		Node n;

		public huffmanSet(String str,Node n) {
			setCode(str);
			this.n=n;
		}

		public void setCode(String str) {
			char c[]=str.toCharArray();
			String result="";
			for(int i=0;i<c.length;i++) {
				result+=c[i];
				numDigit++;
				if(result.length()>(HASHSIZE-1)) {
					//String binary=result.substring(0,63);
					code[size++]=Long.parseLong(result,2);
					result="";
				}
			}
			String border,padding;
			if(spaceHash.equals("0")) {
				border="1";
				padding="0";
			}else {
				border="0";
				padding="1";
			}


			if(result.length()>0) {
				result+=border;
				for(int i=result.length();i<HASHSIZE;i++)
					result+=padding;

				code[size++]=Long.parseLong(result,2);
			}
		}

		public void updateNode(int[] goalScore,int depth) {
			this.n.visitValueCount();
			this.n.setWinValue(goalScore);
			this.n.setdepthValue(depth);

		}
	}

	public String makeHuffmanCode(String state) {
		state=mu.preprocess(state);
		state=mu.wtnInboardInformation(state);
		state=mu.pressRoleString(state, wardToCharMap,appearMap);
	    state=mu.makePafeBoard(state);
	    String hash=mu.encodeHaffman(state,haffmanHashMap);

		return hash;
	}

	public void addHufMemo(String hash,Node n,int[] goalScore) {

		Node m=new Node(n.state);
		m.visitValueCount();
		m.setWinValue(goalScore);
		m.setdepthValue(n.depth);
		huffmanSet hs=new huffmanSet(hash,m);

		long key=hs.code[0];
		for(int i=1;i<hs.size;i++)
			key=key^hs.code[i];

		if(!huffmanMemorys.containsKey(key)) {
			huffmanMemorys.put(key,hs);
			return;
		}else {
			//衝突
			hs=huffmanMemorys.get(key);


			if(hs.n.state.equals(n.state)) {
				hs.updateNode(goalScore,n.depth);
				return;
			}
		}

	}

	public Node selectHuffmanMemory(String state) {
		Node matchNode=null;
		huffmanSet hs=new huffmanSet(state,null);

		long key=hs.code[0];
		for(int i=1;i<hs.size;i++) {
			key=key^hs.code[i];
		}
		 matchNode=matchHashCode(key,hs);
		 /*
		 if(matchNode!=null) {
		     return matchNode;
		 }
		 return matchNode;
		 */
		 Node similarNode=new Node(null);

		 originalHash=key;
		 similarNode=searchSimilarHash(0,2,key,similarNode);
		 Node returnNode=new Node(getCurrentState());
		 if(matchNode!=null) {
			 returnNode.setWinValue(matchNode.winValue);
			 returnNode.v+=matchNode.v;
		 }
		 if(similarNode!=null) {
			 returnNode.setWinValue(similarNode.winValue);
			 returnNode.v+=similarNode.v;
		 }

		 if(returnNode.v==0) {
			 return null;
		 }
		return returnNode;

	}
	public long originalHash=0;

	public Node searchSimilarHash(int start,int depth, long hash,Node saveNode ){
		long subHash=0;
		if(depth==0)
			return saveNode;
		for(int i=start;i<HASHSIZE;i++) {
			long a=(long)Math.pow(2, i);
			subHash=hash^a;
			if(huffmanMemorys.containsKey(subHash)) {
				if(searchSimilarTwoBitLength(originalHash,subHash)<6) {
					Node n=huffmanMemorys.get(subHash).n;
					saveNode.setWinValue(n.winValue);
					saveNode.v+=n.v;

				}
			}
			saveNode=searchSimilarHash(i+1,depth-1,subHash,saveNode);
		}
		return saveNode;
	}

	public  Node matchHashCode(long l,huffmanSet hs) {
		if(huffmanMemorys.containsKey(l)) {
			 huffmanSet semiHs=huffmanMemorys.get(l);
			 for(int i=0;i<hs.size;i++) {
				 if(hs.code[i]!=semiHs.code[i])
					 return null;
			 }
			 return semiHs.n;
		 }
		return null;
	}

	public int searchSimilarTwoBitLength(long l1, long l2) {

		long result=l1^l2;
		String xorStr=Long.toBinaryString(result);
		int flag =-1;
		int inx=0;
		for(inx=0;inx<xorStr.length();inx++) {
			char cc=xorStr.charAt(inx);
			if(cc=='1') {
				if(flag==-1)
					flag=inx;
				else
					break;
			}
		}
		int length=inx-flag-1;
		return length;
	}




    @Override
    public void stateMachineStop() {
        // Do nothing.
    }
    /**
     * Uses a CachedProverStateMachine
     */
    @Override
    public StateMachine getInitialStateMachine() {
        return new CachedStateMachine(new ProverStateMachine());
    }

    @Override
    public String getName() {
        return "HuffmanCherryPlayer";
    }

    @Override
    public DetailPanel getDetailPanel() {
        return new SimpleDetailPanel();
    }

    @Override
    public void preview(Game g, long timeout) throws GamePreviewException {
        // Do nothing.
    }

    @Override
    public void stateMachineAbort() {
        // Do nothing.
    }
}


