package org.ggp.base.player.gamer.statemachine.sample.gpp_player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

public final class HuffmanBananaGamer extends SampleGamer
{
	//int[] boardSize= {-1,-1,Integer.MAX_VALUE,Integer.MAX_VALUE};
	int[] boardSize= {-1,-1,1,1};
	 Map<String,String> haffmanHashMap=new HashMap<>();
	 Map<String,String> wardToCharMap=new HashMap<>();
	 Map<String,Integer> appearMap=new HashMap<>();
	 MCTSutils mu=new MCTSutils();

	 @Override
	 public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	 {
		 initAll();
		 System.out.println("start:::start:::start:::start:::start:::start:::start:::start:::start:::start");
		 long finishBy = timeout - 1000;

		 StateMachine theMachine = getStateMachine();
		 //System.out.println(getMatch().getGame().getRules());
		 //MCTSutils mu=new MCTSutils();
		 Huffman hf=new Huffman();

		 //Map<String,String> wardToCharMap=new HashMap<>();
		 //Map<String,Integer> appearMap=new HashMap<>();
		 int times=0;
		 int[] depth=new int[1];
		 String test="";
		 while(System.currentTimeMillis() < finishBy) {
			 MachineState finalState=theMachine.performDepthCharge(getCurrentState(), depth);
			 String state=mu.preprocess(finalState.toString());
			 state=mu.wtnInboardInformation(state);
			 //mu.searchBoardSize(state, boardSize);
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
		 //for(i=0;i<10;i++) {
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
		 }
		 System.out.println(test);
		 System.out.println("MAX(x,y) = ("+boardSize[0]+","+boardSize[1]+")");
		 System.out.println("MIN(x,y) = ("+boardSize[2]+","+boardSize[3]+")");
		 //test=mu.makePerfectBoard(test,boardSize);
		 test=mu.makePafeBoard(test);
		 System.out.println(test);
		 System.out.println("length:"+test.length());
		 test=mu.encodeHaffman(test,haffmanHashMap);
		 System.out.println(test);

	 }
    /**
     * Employs a simple sample "Monte Carlo" algorithm.
     */
	 int kokoko=0;

    @Override
    public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
    {
        StateMachine theMachine = getStateMachine();
        long start = System.currentTimeMillis();
        long finishBy = timeout - 1000;
        turn++;
        System.out.println("●----"+turn+"----●");
        System.out.println(getCurrentState());
        System.out.println(makeHuffmanCode(getCurrentState().toString()));
        //removePlayoutMemory();

        /*
         * 現在の盤面状態からノードnを作り、
         * rootノードがnull、つまり一番初めの時にはrootノードにnを代入する
         * そうでない場合は、構築されている木からノードnと同じ盤面要素を持つノードを探す。
         * あれば、そのノードの情報を上書きする。
         */
        Node n=new Node(getCurrentState());

    	root=n;
    	lastNode=root;
    	int playCount=0;
        //while(System.currentTimeMillis()<finishBy) {
        for(int i=0;i<500;i++) {
        	playCount++;
        	MonteCalroPlayout(n);
        	//System.out.println(playCount);
       	}



        /*
         * movesが複数の要素を持つ場合は、次の手を選択するためにselectNectPlayメソッドを使用する。
         * そうでない場合（交互手番のゲームにおいて相手の番であった時も）は、moves.get(0)がselectionとなる。
         */
        List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
        Move selection = moves.get(0);

        if (moves.size() > 1) {
        	selection=selectNextPlay(n,moves);
        }
        globalDepth=n.depth+1;

        lastNode=n;
        long stop = System.currentTimeMillis();
        showAllCount=0;
        //showAll(root);
        //System.out.println("show all count;;;"+showAllCount);
        System.out.println(n.state.toString());
        System.out.println(MCTSutils.preprocess(n.state.toString()));
    	System.out.println("regal  moves:"+moves);
    	System.out.println("select moves:"+selection);
    	System.out.println("times of select playout memory:"+testCount);
    	double percent=(double)testCount/(double)supertest;
    	System.out.println(supertest+"---testCount percentage::"+percent);
    	System.out.println("total state number::"+totalStateNumber);
    	int pmcount=0;
    	System.out.println("huffman memory---size:"+huffmanMemorys.size());
    	/*
    	for(long s:huffmanMemorys.keySet()) {
			huffmanSet hs=huffmanMemorys.get(s);
			System.out.println("state:"+hs.n.getState().toString());
			for(int i=0;i<hs.size;i++) {
				System.out.println(hs.code[i]);
	    	}
    	}*/
      	System.out.println("acchived in ipsilon process::"+pmcount);
      	System.out.println("play count:"+playCount);
    	testCount=0;
    	supertest=0;
    	realSerect=0;
    	totalStateNumber=0;
    	/*
    	System.out.println("all of huffman");
    	for(long s:huffmanMemorys.keySet()) {
			huffmanSet hs=huffmanMemorys.get(s);
			Node apple=hs.n;
			System.out.println("key--"+s);
			System.out.println("state:"+apple.getState().toString());
			for(int i=0;i<hs.size;i++) {
				System.out.println("hash:"+hs.code[i]);
			}
			for(int i=0;i<apple.winValue.length;i++)
				System.out.println(i+":win---"+apple.winValue[i]);
			System.out.println("visit---"+apple.v);
    	}*/
    	System.out.println("icchi:"+icchi);
     	System.out.println("semi icchi:"+semiIcchi);
    	System.out.println("hs count:"+hsCount);
    	icchi=0;semiIcchi=0;hsCount=0;

    	System.out.println("new count:"+newCount);
     	System.out.println("bad count:"+badCount);
     	System.out.println("semibad count:"+semibadCount);
     	System.out.println("else bad count:"+elsebadCount);
    	System.out.println("crash count:"+crashCount);
    	System.out.println(this.makeHuffmanCode(getCurrentState().toString()));
    	badCount=0;semibadCount=0;elsebadCount=0;crashCount=0;newCount=0;elsebadCount=0;
    	System.out.println();


        notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
        //System.out.println("error count:::"+errorCount);
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
    		/*
    		System.out.println(MCTSutils.preprocess(key.toString()));
    		System.out.println("child.v:"+child.v);
    		System.out.println("win rate:"+child.winValue[getOwnRoleNumber()]);
    		 */
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
    	MachineState state;//Board information
    	int depth;//depth from root node
    	Map<MachineState ,Node> children;//all of child node

    	int winValue[];//accumulated points
    	double winRate[];//(winValue/v)

    	Node(MachineState state){
    		this.v=0;
    	    this.state=state;
    	    this.depth=0;
    	    this.children= new HashMap<MachineState ,HuffmanBananaGamer.Node>();
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
    			System.out.println("error:have not visited");
    			System.out.println("v---"+this.v+", winValue---"+winValue[playerNum]);
    			System.out.println(MCTSutils.preprocess(this.state.toString()));
    		}
    	}

    	public void expand(Node n){
    	    this.children.put(n.state,n);
    	}

    	public boolean hasChild(MachineState state) {
    		for(MachineState key:this.children.keySet()) {
    			if(key.equals(state)) {
    				return true;
    			}
    		}
    		return false;
    	}

    }

    public int apple=40;
    public int testCount=0;
    public int realSerect=0;
    public int supertest=0;
    public int totalStateNumber=0;
    public int turn=0;
    public Node lastNode=null; //前回の盤面情報
    public int errorCount=0;

    public boolean firstPlayoutState=true;
    /*
     * 一回のシミュレーションの過程で発生したノードはキューに突っ込む
     */
    public void MonteCalroPlayout(Node n) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
    	StateMachine theMachine = getStateMachine();
    	Node node=n;
    	Node saveNode=null;
    	MachineState state=n.getState();
    	Queue<Node> que=new ArrayDeque<>();
    	que.add(n);
    	playerNum=getCurrentPlayerNum();

    	while(!theMachine.isTerminal(state)) {
    		node=selectChildNode(node);
    		state=node.getState();
      	   	que.add(node);
      		playerNum=(playerNum+1)%(theMachine.getRoles().size());
      		totalStateNumber++;
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
    	//test0718
    	Map<MachineState, Integer> testMap=new HashMap<>();
    	Map<MachineState, Integer> testMapVisit=new HashMap<>();
    	//Map<String,Integer> fakeMap=new HashMap<>();
    	//Map<String,String> fakeHashMap=new HashMap<>();


    	Node parent=null;
    	for (Node v : que) {
    		//System.out.println("state:"+v.state.toString());
    		testMap.put(v.state, v.winValue[0]+v.winValue[1]);
    		testMapVisit.put(v.state, v.v);
    		v.visitValueCount();
    		v.setWinValue(goalScore);
    		if(parent!=null) {
    			v.setdepthValue(parent.depth+1);
        	}
    		String huffman=makeHuffmanCode(v.state.toString());
    		//addHuffmanMemory(huffman,v,goalScore);
    		addHufMemo(huffman,v,goalScore);
    		parent=v;
    	}
    	/*
    	 * キューに保存していたノードを取り出していく。
    	 * つまり、シミュレーションの過程ではじめに発生した盤面情報から順に参照していく。
    	 * nodeSearchメソッドで構築している木にあるか参照し、なかったら拡張して参照を終了する。
    	 */
    	saveNode=que.poll();
    	Node nm=nodeSearch(root,saveNode.state);//*taking time

    	//Node nm=nodeSearch(lastNode,saveNode.state);
    	if(nm==null) {
    		System.out.println("HHHHHHHHHHHHHIIIIIIIIIIIIITTTTTTTTT");
    		lastNode.expand(saveNode);
    		System.out.println("expand:"+MCTSutils.preprocess(saveNode.state.toString()));
    		return;
    	}
    	for (Node v : que) {
    		Node ns=nodeSearch(root,v.state);//*taking time
    		//Node ns=nodeSearch(n,v.state);
    		/*
    		 * 一つ目のif文は、構築している木にない場合の拡張
    		 * 二つ目のif文は、構築している木には存在するが、親子関係が成立していない場合の拡張
    		 */
    		if( ns==null) {
    			/*
    			System.out.println("null part");
    			System.out.println("state:::"+MCTSutils.pressRoleString(MCTSutils.preprocess(v.state.toString()), fakeHashMap, fakeMap));
    			System.out.println("huffman score:[0]---"+testMap.get(v.state));
    			System.out.println("goal score:[0]---"+goalScore[0]+", [1]---"+goalScore[1]);
    			System.out.println("v g  score:[0]---"+v.winValue[0]+", [1]---"+v.winValue[1]);
    			System.out.println("huffman visit:"+testMapVisit.get(v.state));
    			System.out.println("this visit:"+v.v);
    			*/
    			saveNode.expand(v);
        		break;
    		}else if(!saveNode.hasChild(v.state)) {
    			/*
    			System.out.println("has chiled part");
    			System.out.println("state:::"+MCTSutils.pressRoleString(MCTSutils.preprocess(v.state.toString()), fakeHashMap, fakeMap));
    			System.out.println("huffman score:[0]---"+testMap.get(v.state));
    			System.out.println("goal score:[0]---"+goalScore[0]+", [1]---"+goalScore[1]);
    			System.out.println("v g  score:[0]---"+v.winValue[0]+", [1]---"+v.winValue[1]);
    			System.out.println("huffman visit:"+testMapVisit.get(v.state));
    			System.out.println("this visit:"+v.v);*/
    			saveNode.expand(ns);
        		break;
    		}
    		saveNode=ns;
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
    		/*
    		MachineState state=getUnexploredNextState(n);
    		selectNode=new Node(state);*/
    		selectNode=getUnexploredNextNode(n);
    		firstPlayoutState=false;
    		//System.out.println("OOMOTO:"+n.state);
    		//System.out.println("select:"+selectNode.state);
    		//System.out.println("sub   :"+subNode.state);
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

    	List<MachineState> l=theMachine.getNextStates(n.state);
    	List<MachineState> nextStates=new ArrayList<MachineState>(new HashSet<>(l));
    	if(nodeSize==nextStates.size()) {
    		return true;
    	}

    	return false;
    }

    /*
     *Get the next game state which is not in MCTS tree.
     *First, select the state from playout memory.
     *If it is not, then use StateMachine method to get the next state.
     */
    /*
    public MachineState getUnexploredNextState(Node n) throws MoveDefinitionException, TransitionDefinitionException {
    	StateMachine theMachine = getStateMachine();
    	 MachineState nextState;

    	 supertest++;

    	while(true) {
    		List<Move> a=theMachine.getRandomJointMove(n.getState());
    		nextState = theMachine.getNextState(n.getState(),a );
            if(!n.children.containsKey(nextState)) {
               	break;
            }
    	}


    	return nextState;
    }
*/

    int selectNullCount=0;
    int selectGetCount=0;

    public Node getUnexploredNextNode(Node n) throws MoveDefinitionException, TransitionDefinitionException {
    	StateMachine theMachine = getStateMachine();
    	 MachineState nextState;
    	 Node selectHuffmanNode=null;
    	 supertest++;

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
    		selectNullCount++;
    		return new Node(nextState);
    	}
    	else {
    		selectGetCount++;
    		selectHuffmanNode.winRateCalculation();
    		Node returnNode=new Node(nextState);
    		returnNode.setWinValue(selectHuffmanNode.winValue);
    		returnNode.v=selectHuffmanNode.v;
    		/*
    		System.out.println("OOMOTO :"+n.state);
    		System.out.println("nextsta:"+nextState);
    		System.out.println("huffman:"+selectHuffmanNode.getState());
    		System.out.println("own win Value:"+selectHuffmanNode.winValue[getOwnRoleNumber()]+", visit times:"+selectHuffmanNode.v);*/
    		return returnNode;
    	}
    }

    /*
     * Search the same Node as the argument Node in constructed tree.
     */
    public Node nodeSearch(Node n,MachineState key){
		if(n.children==null) {
    		return null;
    	}
	    if(n.state.equals(key)){
	        return n;
		}
	    if(n.children.containsKey(key)) {
			return n.children.get(key);
	    }else {
	    	for(MachineState k:n.children.keySet()){
	    		Node next=n.children.get(k);
	    		next=nodeSearch(next,key);
	    		if(next!=null)
	    			return next;
	    	}
	    }
	    return null;
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
    	*/
    	if(n.children == null){
    		System.out.println("HIIIIT");
    		return;
    	}
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
				if(result.length()>62) {
					//String binary=result.substring(0,63);
					code[size++]=Long.parseLong(result,2);
					result="";
				}
			}
			if(result.length()>0) {
				result+="1";
				for(int i=result.length();i<63;i++)
					result+="0";

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
		  //MCTSutils mu=new MCTSutils();

	      state=mu.preprocess(state);
	      //System.out.println("omae ga hannin ka");
	 	  state=mu.wtnInboardInformation(state);

	      state=mu.pressRoleString(state, wardToCharMap,appearMap);
	       // System.out.println(state);
	        //System.out.println("[0]---"+boardSize[0]+", [1]---"+boardSize[1]);
	        //System.out.println("[2]---"+boardSize[2]+", [3]---"+boardSize[3]);
	      //state=mu.makePerfectBoard(state,boardSize);
	      state=mu.makePafeBoard(state);
	        //System.out.println("lenght:"+state.length());

	      String hash=mu.encodeHaffman(state,haffmanHashMap);
			//System.out.println(hash);

		return hash;
	}

	int newCount=0;
	int crashCount=0;
	int badCount=0;
	int semibadCount=0;
	int elsebadCount=0;
	static int testADD=0;
	public void addHuffmanMemory(String hash,Node n,int[] goalScore) {

		Node m=new Node(n.state);
		m.visitValueCount();
		m.setWinValue(goalScore);
		m.setdepthValue(n.depth);
		huffmanSet hs=new huffmanSet(hash,m);
		// hash=hash.substring(0,8);


		 //int key=Integer.parseInt(hash, 2);
		long key=hs.code[0];
		if(!huffmanMemorys.containsKey(key)) {
			huffmanMemorys.put(key,hs);
			newCount++;
			return;
		}else {
			//衝突
			hs=huffmanMemorys.get(key);
			//hs.updateNode(goalScore,n.depth);


			if(hs.n.state.equals(n.state)) {
				hs.updateNode(goalScore,n.depth);
				badCount++;
				return;
			}/*else {
				for(int i=1;i<hs.size;i++) {
					long semiKey=hs.code[i];
					key=key ^ semiKey;
					if(!huffmanMemorys.containsKey(key)) {
						elsebadCount++;
						huffmanMemorys.put(key,hs);
						return;
					}else if(i==hs.size-1){
						hs=huffmanMemorys.get(key);
						hs.updateNode(goalScore,n.depth);
						semibadCount++;
						return;
					}
				}*/
				/*
				System.out.println(MCTSutils.pressRoleString(MCTSutils.preprocess(hs.n.state.toString()),  fakeHashMap,fakeMap));
				System.out.println(MCTSutils.pressRoleString(MCTSutils.preprocess(n.state.toString()),  fakeHashMap,fakeMap));
				System.out.println();
				*/
			//}
			/*
			if(testADD==0) {
				testADD++;
				System.out.println("hs;"+hs.n.state);
				System.out.println("n;;"+n.state);
				String hsState=makeHuffmanCode(hs.n.state.toString());
				String nState=makeHuffmanCode(n.state.toString());
				System.out.println("hsState:"+hsState);
				System.out.println("nState:"+nState);
				System.out.println("hs code");
				for(int i=0;i<hs.size;i++){
					System.out.println(hs.code[i]);
				}
				System.out.println("hash:"+hash);
				System.out.println("code list");
				for(long l:hs.code) {
					System.out.print(l+",");
				}
				System.out.println();

				System.out.println("all of huffman memories");
				for(long l:huffmanMemorys.keySet()) {
					huffmanSet h=huffmanMemorys.get(l);
					System.out.print(l+"---");
					for(long ll:h.code) {
						System.out.print(ll+",");
					}
					System.out.println();
				}
				System.out.println("size;;;"+huffmanMemorys.size());
				String state=n.state.toString();
				System.out.println(state);
				state=mu.preprocess(state);
				System.out.println(state);
				state=mu.wtnInboardInformation(state);
				System.out.println(state);
				state=mu.pressRoleString(state, wardToCharMap,appearMap);
				System.out.println(state);
				state=mu.makePafeBoard(state);
				System.out.println(state);
				System.out.println("lenght:"+state.length());
				String banana=mu.encodeHaffman(state,haffmanHashMap);
				System.out.println(banana);
				System.out.println("The end ttene");
			}*/
			//hs.updateNode(goalScore,n.depth);
			crashCount++;
		}

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
			newCount++;
			return;
		}else {
			//衝突
			hs=huffmanMemorys.get(key);
			//hs.updateNode(goalScore,n.depth);


			if(hs.n.state.equals(n.state)) {
				hs.updateNode(goalScore,n.depth);
				badCount++;
				return;
			}/*else {
				for(int i=1;i<hs.size;i++) {
					long semiKey=hs.code[i];
					key=key ^ semiKey;
					if(!huffmanMemorys.containsKey(key)) {
						elsebadCount++;
						huffmanMemorys.put(key,hs);
						return;
					}else if(i==hs.size-1){
						hs=huffmanMemorys.get(key);
						hs.updateNode(goalScore,n.depth);
						semibadCount++;
						return;
					}
				}*/
				/*
				System.out.println(MCTSutils.pressRoleString(MCTSutils.preprocess(hs.n.state.toString()),  fakeHashMap,fakeMap));
				System.out.println(MCTSutils.pressRoleString(MCTSutils.preprocess(n.state.toString()),  fakeHashMap,fakeMap));
				System.out.println();
				*/
			//}
			/*
			if(testADD==0) {
				testADD++;
				System.out.println("hs;"+hs.n.state);
				System.out.println("n;;"+n.state);
				String hsState=makeHuffmanCode(hs.n.state.toString());
				String nState=makeHuffmanCode(n.state.toString());
				System.out.println("hsState:"+hsState);
				System.out.println("nState:"+nState);
				System.out.println("hs code");
				for(int i=0;i<hs.size;i++){
					System.out.println(hs.code[i]);
				}
				System.out.println("hash:"+hash);
				System.out.println("code list");
				for(long l:hs.code) {
					System.out.print(l+",");
				}
				System.out.println();

				System.out.println("all of huffman memories");
				for(long l:huffmanMemorys.keySet()) {
					huffmanSet h=huffmanMemorys.get(l);
					System.out.print(l+"---");
					for(long ll:h.code) {
						System.out.print(ll+",");
					}
					System.out.println();
				}
				System.out.println("size;;;"+huffmanMemorys.size());
				String state=n.state.toString();
				System.out.println(state);
				state=mu.preprocess(state);
				System.out.println(state);
				state=mu.wtnInboardInformation(state);
				System.out.println(state);
				state=mu.pressRoleString(state, wardToCharMap,appearMap);
				System.out.println(state);
				state=mu.makePafeBoard(state);
				System.out.println(state);
				System.out.println("lenght:"+state.length());
				String banana=mu.encodeHaffman(state,haffmanHashMap);
				System.out.println(banana);
				System.out.println("The end ttene");
			}*/
			//hs.updateNode(goalScore,n.depth);
			crashCount++;
		}

	}

	public int icchi=0;
	public int semiIcchi=0;
	public int hsCount=0;
	public Node selectHuffmanMemory(String state) {
		double min=1.0;
		Node n=null;
		huffmanSet hs=new huffmanSet(state,null);
		/*if(huffmanMemorys.containsKey(hs.code[0])) {
			 huffmanSet a=huffmanMemorys.get(hs.code[0]);
			 semiIcchi++;
			 if(a.code[1]==hs.code[1]) {
				 icchi++;
				 return a.n;
			 }
		 }*/
		long key=hs.code[0];
		for(int i=1;i<hs.size;i++) {
			key=key^hs.code[i];
		}
		 n=matchHashCode(key,hs);
		 if(n!=null) {
			 hsCount++;
			 return n;

		 }/*
		for(int i=0;i<63;i++) {
			long a=(long)Math.pow(2, i);
			long key=hs.code[0]^a;
			 n=matchHashCode(key,hs);
			 if(n!=null)
				 return n;
		}*/
		 if(min<0.15) {
			 return n;
		 }
		else
			return null;
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

	public  double minHashModoki(long[] l1,long[] l2,double numberOfDigits) {
		int min=l1.length;
		int count=0,l1Length=0,l2Length=0,length;
		if(l2.length<min)
			min=l2.length;
		for(int i=0;i<min;i++) {
			long apple=l1[i]^l2[i];
			count+=Long.bitCount(apple);
		}

		double per=(double)count/numberOfDigits;
		return per;
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
        return "HuffmanBananaPlayer";
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


