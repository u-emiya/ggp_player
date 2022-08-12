package org.ggp.base.player.gamer.statemachine.sample.gpp_player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.SimpleDetailPanel;
import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.exception.GamePreviewException;
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


//0406
//add comment 0104
public final class HuffmanDorianGamer extends SampleGamer
{
	//int[] boardSize= {-1,-1,Integer.MAX_VALUE,Integer.MAX_VALUE};
	int[] boardSize= {-1,-1,1,1};
	 //Map<String,String> haffmanHashMap=new HashMap<>();
	 Map<String,String> haffmanHashMapV2=new HashMap<>();

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

	     turn=0;
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
		 /*
		 node nnn=hf.makeNode(appearMap);
		 hf.makeHuffmanTree(nnn);
		 haffmanHashMap=hf.takeHaffmanHash(hf.makeHuffmanCode(nnn));

		 System.out.println("all of huffman hash map");
		 for(String s:haffmanHashMap.keySet()) {
			 System.out.println(wardToCharMap.get(s)+":::"+s+"---"+haffmanHashMap.get(s));
			 if(s.equals("¥"))
				 spaceHash=haffmanHashMap.get(s);
		 }
		 */
		 int index=0;
		 haffmanHashMapV2.put("¥",Integer.toBinaryString(index++));

		 for(String ccc:wardToCharMap.keySet()) {
			 haffmanHashMapV2.put(ccc,Integer.toBinaryString(index));
			 index++;
		 }

		 System.out.printf("\nafter changed test\n");
		 for(String ccc: haffmanHashMapV2.keySet()) {
			 System.out.println(ccc+"::"+haffmanHashMapV2.get(ccc));
		 }



		 /*
		 mu.fixedLengthHuffman(haffmanHashMap);
		 System.out.println("AFTER-----all of huffman hash map");
		 for(String s:haffmanHashMap.keySet()) {
			 System.out.println(wardToCharMap.get(s)+":::"+s+"---"+haffmanHashMap.get(s));
			 if(s.equals("¥"))
				 spaceHash=haffmanHashMap.get(s);
		 }*/

		 System.out.println(test);
		 System.out.println("MAX(x,y) = ("+boardSize[0]+","+boardSize[1]+")");
		 System.out.println("MIN(x,y) = ("+boardSize[2]+","+boardSize[3]+")");
		 test=mu.makePafeBoard(test);
		 test="rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrbbbbbbbb";
		 System.out.println(test);
		 System.out.println("length:"+test.length());
		 test=mu.encodeHaffman(test,haffmanHashMapV2);
		 System.out.println(test);
		 System.out.println("length:"+test.length());


	 }

	 public int testTimes=0;

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
        testTimes++;
        System.out.println("select count:"+selectCount);
        System.out.println("bunbo  count:"+bunboCount);
        System.out.println("per count:"+((double)selectCount/bunboCount));
        selectCount=0;bunboCount=0;
        long stop = System.currentTimeMillis();

        notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
        return selection;
    }
    /*
     * select next legal move
     * the return type is Move
     * changed the way to next state from child.v to child.selectionCount
     */
    public Move selectNextPlay(Node n,List<Move> moves) throws MoveDefinitionException, TransitionDefinitionException {
    	StateMachine theMachine = getStateMachine();
        Role role=getRole();
    	MachineState bestState=null;
    	Move bestMove=null;
    	int bestValue=0;
    	for(MachineState key:n.children.keySet()) {
    		Node child=n.children.get(key);

     		if(child.selectionCount>bestValue) {
        		bestValue=child.selectionCount;
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
    	haffmanHashMapV2=new HashMap<>();
   	 	wardToCharMap=new HashMap<>();
   	 	appearMap=new HashMap<>();
    	mu=new MCTSutils();
    }

    public class Node{
    	int v; //times of visited
      	int selectionCount; //times of actual selected node
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
    	    this.children= new HashMap<MachineState ,HuffmanDorianGamer.Node>();
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
    public int selectCount=0;//test0210
    public int expandCount=0;//test0210
    public int bunboCount=0;//test0508


    public boolean firstPlayoutState=true;
    /*
     * Playing MCTS method.
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

    	//Playing simulation until final state is reached.
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

		//Calculate rewards for all of players
    	List<Role> roleList=theMachine.getRoles();
    	int[] goalScore=new int[totalPlayerNumber];
    	for(int i=0;i<totalPlayerNumber;i++) {
    		Role l=roleList.get(i);
    		goalScore[i]=theMachine.getGoal(state,l);
    	}

    	/*
    	 * The board information generated in the course of the simulation is stored
    	 * in the search experience.
    	 * At this time, the node stored in the queue is recorded visited times and reward.
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
    		expandCount++;
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
     * this method uses selectionCount in UCT calculation.
     */
    public double uctCalculationV2(Node parent,Node child) {
    	double logVisitedValue=Math.log(parent.selectionCount);
    	double searchValue=0;
    	double C=0.7;

    	if(child.selectionCount!=0) {
    		searchValue=Math.sqrt(logVisitedValue/child.selectionCount);
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
            		//System.out.println("ORIGINAL:"+mu.preprocess(nextState.toString()));
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
	Map<huffmanSet,Node> huffmanMemorys = new HashMap<>();

	public static class huffmanSet{
		long[] code=new long[10];
		List<Long> codeList= new ArrayList<Long>();
		int size=0;
		int numDigit=0;

		public huffmanSet(String str) {
			setCode(str);
		}

		/*
		 * Divide bit string of the argument into 64-digit segments.
		 */
		public void setCode(String str) {
			char c[]=str.toCharArray();
			String result="";
			for(int i=0;i<c.length;i++) {
				result+=c[i];
				numDigit++;
				if(result.length()>(HASHSIZE-1)) {
					//String binary=result.substring(0,63);
					//code[size++]=Long.parseLong(result,2);
					codeList.add(Long.parseLong(result,2));
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
				//code[size++]=Long.parseLong(result,2);
				codeList.add(Long.parseLong(result,2));

			}
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof huffmanSet) {
				return codeList.equals(((huffmanSet) other).codeList);
			}
			return false;
		}
		@Override
		public int hashCode() {
			Long[] l=codeList.toArray(new Long[codeList.size()]);
			return Arrays.hashCode(l);
		}

		/*
		 * Update node information
		 */
		/*
		public void updateNode(int[] goalScore,int depth) {
			this.n.visitValueCount();
			this.n.setWinValue(goalScore);
			this.n.setdepthValue(depth);

		}
		*/
	}

	/*
	 *  make the hash value by huffmanCode from argument state
	 */
	public String makeHuffmanCode(String state) {
		state=mu.preprocess(state);
		state=mu.wtnInboardInformation(state);
		state=mu.pressRoleString(state, wardToCharMap,appearMap);
	    state=mu.makePafeBoard(state);
	    String hash=mu.encodeHaffman(state,haffmanHashMapV2);

		return hash;
	}
	/*
	 * Record information about the argument in the search experience.
	 * hash key is bit string about the state, hash value is huffmanSet
	 */
	public void addHufMemo(String hash,Node n,int[] goalScore) {

		Node m=new Node(n.state);
		m.visitValueCount();
		m.setWinValue(goalScore);
		m.setdepthValue(n.depth);
		//huffmanSet hs=new huffmanSet(hash,m); ***KOUJI***
		/*
		 * create key for huffmanMemorys
		 */
		huffmanSet hs = new huffmanSet(hash);

		if(!huffmanMemorys.containsKey(hs)) {
			huffmanMemorys.put(hs,m);
			return;
		}else {
			//衝突
			Node existNode=huffmanMemorys.get(hs);

			if(existNode.state.equals(n.state)) {
				existNode.visitValueCount();
				existNode.setWinValue(goalScore);
				existNode.setdepthValue(n.depth);

				return;
			}
		}

	}
	/*
	 * return Node which have the sum of all the information of the selected nodes
	 * from the search experience.
	 */
	public Node selectHuffmanMemory(String state) {
		Node matchNode=null;
		huffmanSet hs=new huffmanSet(state);

		//matchNode=matchHashCode(key,hs);
		matchNode=huffmanMemorys.get(hs);
		 /*
		  * if you want to get the same information as argument state from the search experience,
		  * Use if(matchNode!=null) ~   return matchNode; and Set the comment out   Node similarNode=new Node(null); ~ return returnNode;
		  */

		bunboCount++;
		 if(matchNode!=null) {
			 selectCount++;
		     return matchNode;
		 }
		 return matchNode;

		 /*
		  * if you want to get the information similar to the argument state from the search experience,
		  * Use   Node similarNode=new Node(null); ~ return returnNode; and Set the comment out if(matchNode!=null) ~   return matchNode;
		  */
		/*
		 Node similarNode=new Node(null);
		 originalHash=hs.codeList;

		 //  searchSimilarHash's second argument is hamming length
		 bunboCount++;
		 similarNode=searchSimilarHash(0,2,hs,similarNode);

		 if(testTimes<1) {
			 System.out.println("RESULT");
			 for(Long l:hs.codeList)
				 System.out.print(Long.toBinaryString(l));
			 System.out.println();
			 System.out.println("win value::"+similarNode.winValue[0]);
			 System.out.println("visit time:"+similarNode.v);
			 System.out.println();
		 }

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
	*/
	}

	//public long originalHash=0;//use to calculate hamming length
	public List<Long> originalHash;//use to calculate hamming length
	/*
	 * Recursive function
	 * Finally, return Node which has sum of similar state visit times and values.
	 */
	public Node searchSimilarHash(int start,int depth, huffmanSet hs,Node saveNode ){
		huffmanSet subHash=new huffmanSet("");
		subHash.codeList=new ArrayList<Long>(hs.codeList);
		if(depth==0)
			return saveNode;

		for(int i=start;i<HASHSIZE*hs.codeList.size();i++) {
			int index=i/HASHSIZE;
			int j=i-HASHSIZE*index;
			long a=(long)Math.pow(2, j);

			subHash.codeList.set(index, hs.codeList.get(index)^a);

			if(huffmanMemorys.containsKey(subHash)) {
				//if(searchSimilarTwoBitLength(originalHash,subHash.codeList)<5) {
					selectCount++;
					Node n=huffmanMemorys.get(subHash);
					saveNode.setWinValue(n.winValue);
					saveNode.v+=n.v;
					if(testTimes<1) {
						 for(Long l:subHash.codeList)
							 System.out.print(Long.toBinaryString(l));
						 System.out.println();
						System.out.println("win value::"+n.winValue[0]);
						System.out.println("visit time:"+n.v);
					}
				//}
			}
			if(i==HASHSIZE-1)
				subHash.codeList.set(index, hs.codeList.get(index));

			saveNode=searchSimilarHash(i+1,depth-1,subHash,saveNode);
		}
		return saveNode;
	}
	/*
	 * return Node same as hash key about first argument from Exploration Experience.
	 */
	/*
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
	}*/
	/*
	 * return humming length calculated from two argumentsz
	 */

	public static int searchSimilarTwoBitLength(List<Long> l1, List<Long> l2) {
		List<String> result=new ArrayList<String>();
		if(l1.size()!=l2.size())
			return 100;
		for(int i=0;i<l1.size();i++) {
			long l=l1.get(i) ^ l2.get(i);
			String xorStr=Long.toBinaryString(l);
			result.add(xorStr);
		}
		List<Integer> flag=new ArrayList<Integer>();
		int inx=0,jnx=0;
		for(jnx=0;jnx<result.size();jnx++) {
			String str=result.get(jnx);
			for(inx=0;inx<str.length() && flag.size()<2;inx++) {
				char cc=str.charAt(inx);
				if(cc=='1' && flag.size()<2)
					flag.add(inx+(HASHSIZE-str.length()));
			}
			if(flag.size()==2)
				break;
		}
		int length=0;
		if(flag.size()==2)
			length=flag.get(1)+(HASHSIZE*jnx)-flag.get(0)-1;
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
        return "HuffmanDorianPlayer";
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


