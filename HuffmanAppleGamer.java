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


//0826
public final class HuffmanAppleGamer extends SampleGamer
{
	//int[] boardSize= {-1,-1,Integer.MAX_VALUE,Integer.MAX_VALUE};
	int[] boardSize= {-1,-1,1,1};
	 Map<String,String> haffmanHashMap=new HashMap<>();
	 Map<String,String> wardToCharMap=new HashMap<>();
	 Map<String,Integer> appearMap=new HashMap<>();
	 Map<Integer,Integer> depthTimesMap=new HashMap<>();//0602
	 MCTSutils mu=new MCTSutils();
	 public static final int HASHSIZE=63;
	 //for making hash border
	 static String  spaceHash="";

	 @Override
	 public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	 {
		 initAll();
		 initHuffmanAbility();
	     turn=0;
		 System.out.println("start:::start:::start:::start:::start:::start:::start:::start:::start:::start");
		 long finishBy = timeout - 1000;

		 StateMachine theMachine = getStateMachine();
		 Huffman hf=new Huffman();

		 int times=0;
		 int[] depth=new int[1];
		 String test="";
		 for(int i=0;i<5;i++) {
			 MachineState finalState=theMachine.performDepthCharge(getCurrentState(), depth);
			 String state=mu.preprocess(finalState.toString());
			 state=mu.wtnInboardInformation(state);
			 test=mu.pressRoleString(state, wardToCharMap,appearMap);
			 times++;
		 }

		 boardSize[0]=mu.columnWordToNumberMap.size();
		 boardSize[1]=mu.rowWordToNumberMap.size();

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

		 node nnn=hf.makeNode(appearMap);
		 hf.makeHuffmanTree(nnn);
		 haffmanHashMap=hf.takeHaffmanHash(hf.makeHuffmanCode(nnn));

		 for(String s:haffmanHashMap.keySet()) {
			 if(s.equals("¥"))
				 spaceHash=haffmanHashMap.get(s);
		 }
		 //mu.fixedLengthHuffman(haffmanHashMap);

		 //start new function
		 for(int playTimes=0;playTimes<5;playTimes++) {
			 long startnano = System.nanoTime();

			 System.out.println("-----"+(playTimes+1)+"-----");
			 root=null;
			 Node n=null;
			 Node nextNode=null;
			 pnInPreGame=0;
			 for(int i=0;i<10;i++) {
				 long start = System.currentTimeMillis();

				 if(root==null) {
					 n=new Node(getCurrentState());
					 root=n;
				 }else
					 n=nextNode;

				 for(int j=0;j<50;j++) {
					 MonteCalroPlayout(n);
				 }

				 MachineState bestState=null;
				 int bestValue=0;
				 for(MachineState key:n.children.keySet()) {
					 Node child=n.children.get(key);
					 if(child.winRate[pnInPreGame]>bestValue) {
					 //if(child.selectionCount>bestValue) {
						 bestValue=child.selectionCount;
						 bestState=key;
						 nextNode=child;
			     	}
				 }
				 long stop = System.currentTimeMillis();
				 System.out.print("time:"+(stop-start)+"---");
				 System.out.println(i+":"+bestState);
				 //showAllForBFS(root);

				 if(bestState==null)
					 for(MachineState key:n.children.keySet()) {
						 System.out.println(key);
					 }
				 pnInPreGame=(pnInPreGame+1)%totalPlayerNumber;
				// break;

			 }
			 long stopnano = System.nanoTime();
			 System.out.println("time nano:"+(stopnano-startnano));

		 }
		 initAll();
		 pnInPreGame=0;

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

        Node n=null;
      	if(root==null) {
            n=new Node(getCurrentState());
        	root=n;
      	}else if(!isLastOwnPlay) {
        	if(nextSelectNode!=null) {
        		for(Node apple:nextSelectNode.children.values())

        			if(apple.getState().equals(getCurrentState())) {
        				n=apple;
        				System.out.println("hit sss:"+n.getState());
        				break;
        			}
        		}

        	else
        		n=nodeSearch(root,getCurrentState());
      	}else
      		n=nextSelectNode;

    	if(nextSelectNode!=null) {
    		System.out.println("getCurrentState:"+getCurrentState());
    		System.out.println("next Root Node :"+nextSelectNode.getState());
    	}
    	if(n==null) {
    		System.out.println("nextSelectNode:::"+nextSelectNode.getState());
    		for(Node apple:nextSelectNode.children.values())
    			System.out.println("child:::"+apple.getState());

    	}
    	System.out.println("n root:"+n.getState());
    	if(n.parent!=null)
    		System.out.println("n parent:"+n.parent.getState());
		System.out.println("visit times:"+n.v);
		System.out.println("win value:0---"+n.winValue[0]+", 1---"+n.winValue[1]);

    	//while(System.currentTimeMillis()<finishBy) {
        for(int i=0;i<500;i++) {
        	MonteCalroPlayout(n);
       	}

        List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
        Move selection = moves.get(0);
        /*
        if (moves.size() > 1)
        	selection=selectNextPlay(n,moves);
        */
        if(selection.toString().equals("noop"))
           	isLastOwnPlay=false;
        else {
        	selection=selectNextPlay(n,moves);
           	isLastOwnPlay=true;
        }
        globalDepth=n.depth+1;
        testTimes++;

        //showAll(root);
        /*
        if(turn<5)
        	if(root.getState().equals(n.getState()))
        		showAllForBFS(root);
        	else
        		showAllForBFS(n);
		*/
        //checkDifferenceNumOfBit

        //System.out.println("total node count  :"+showAllCount);
        System.out.println("total count in BFS:"+showAllBFSCount);
        System.out.println("select count:"+selectCount);
        System.out.println("bunbo  count:"+bunboCount);
        System.out.println("per count:"+((double)selectCount/bunboCount));
        System.out.println("hit count in tree for BFS :"+hitCountBFS);
        System.out.println("per count:"+((double)hitCountBFS/selectCount));
        for(int i:depthTimesMap.keySet())
        	System.out.println(i+":"+depthTimesMap.get(i));
        System.out.println("depth max in this simulation:"+depthMax);
        printCheckResult();
        System.out.println("aaa:"+aaa);
        System.out.println();

        selectCount=0;bunboCount=0;hitCount=0;hitCountBFS=0;showAllCount=0;showAllBFSCount=0;depthMax=0;
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
        		nextSelectNode=child;
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
    public int pnInPreGame=0;//pn ... player number. need in meta game function
    public Node nextSelectNode=null;//this node is selected regal move in my turn
    public boolean isLastOwnPlay=true;//indicate the last turn is own play

    public void initAll() {
    	root=null;
    	totalPlayerNumber=getStateMachine().getRoles().size();
    	huffmanMemorys = new HashMap<>();
    	turn=0;
    	mu=new MCTSutils();

    	depthTimesMap=new HashMap<>();
    	nextSelectNode=null;
    	isLastOwnPlay=true;
    }

    public void initHuffmanAbility() {
    	haffmanHashMap=new HashMap<>();
   	 	wardToCharMap=new HashMap<>();
   	 	appearMap=new HashMap<>();
    }

    public class Node{
    	int v; //times of visited
      	int selectionCount; //times of actual selected node
      	MachineState state;//Board information
    	int depth;//depth from root node
    	Map<MachineState ,Node> children;//all of child node
    	Node parent;//parent node for this

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
    	    this.parent=null;
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
    		n.parent=this;
    	    this.children.put(n.state,n);
    	    n.selectCount();
    	}

    }
    public int errorCount=0;
    public int turn=0;
    public int selectCount=0;//test0210
    public int expandCount=0;//test0210
    public int bunboCount=0;//test0508
    public int hitCount=0;//test0517
    public int hitCountBFS=0;//test0602
    public int showAllCount=0;//test0602
    public int showAllBFSCount=0;//test0602


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
    	//playerNum=getCurrentPlayerNum();
    	playerNum=(getCurrentPlayerNum()+pnInPreGame)%(theMachine.getRoles().size());
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
            		//System.out.println();
            		//System.out.println("ORIGINAL:"+mu.preprocess(nextState.toString()));
            		//System.out.println("vr.state:"+nextState.toString());
            		String huffman=makeHuffmanCode(nextState.toString());
            		expandState=nextState;
            		targetRootNode=n;

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
    		String state=next.state.toString();
        	state=mu.preprocess(state);
    		state=mu.wtnInboardInformation(state);
    		state=mu.pressRoleString(state, wardToCharMap,appearMap);
    		//System.out.println(showAllCount+":"+state);
    		//nxt.printItem(nxt.list);
    		showAll(next);
    	}
    }

    public void showAllForBFS(Node r) {
    	Queue<Node> que=new ArrayDeque<>();
    	List<Node> searchedNodelist=new ArrayList<>();
    	Map<Node,Integer> depthMap=new HashMap<>();
    	int depth=0;
        que.add(r);
  		depthMap.put(r,depth);
  		List<MachineState> mslist=new ArrayList<>();//test0629
  		int apple=0,banana=0,count=0;
        while(!que.isEmpty()) {
        	Node n=que.poll();
        	if(!n.equals(r))
        		depth=depthMap.get(n)+1;

        	//depthMap.put(n,depth);

        	if(!searchedNodelist.contains(n)) {
        		showAllBFSCount++;
        		//if(showAllBFSCount<101)
        		System.out.printf("%4d:%s---real depth:%4d ,,, %d\n",depth,n.getState(),n.depth,count++);
        		//checkDifferenceNumOfBit(n);
        		if(n.depth==4)
        			if(!mslist.contains(n.getState())){
        				apple++;
        				mslist.add(n.getState());
        			}
        			else
        				banana++;


        	}
        	else
        		continue;

        	searchedNodelist.add(n);
        	for(Node child:n.children.values()) {
        		que.add(child);
        		depthMap.put(child,depth);
        	}
        	if(n.parent!=null) {
        		que.add(n.parent);
        		depthMap.put(n.parent,depth);
        	}
        }
        System.out.println("apple---"+apple+", banana---"+banana);
    }
    public int depthMax=0;
    public MachineState expandState;
    public Node targetRootNode;
    public void nodeSearchForBFS(Node r,MachineState target) {
    	Queue<Node> que=new ArrayDeque<>();
    	List<Node> searchedNodelist=new ArrayList<>();
    	Map<Node,Integer> depthMap=new HashMap<>();
    	int depth=0;
    	que.add(r);
    	depthMap.put(r,depth);
    	//System.out.println("target :"+r.getState());
    	//System.out.println("similar:"+ms);

        while(!que.isEmpty()) {
        	Node n=que.poll();
        	if(!n.equals(r))
        		depth=depthMap.get(n)+1;
        	if(depth>20)
        		continue;
        	if(!searchedNodelist.contains(n)) {
        		if(n.getState().equals(target)) {
        			hitCountBFS++;
        			/*
        			System.out.println("depth---"+depth);
        			System.out.print("search  root:");
        			printSimpleState(r.getState().toString());
        			System.out.print("expand    :::");
        			printSimpleState(expandState.toString());
        			System.out.print("similar   :::");
        			printSimpleState(target.toString());

        			String state=expandState.toString();
            		state=mu.preprocess(state);
            		state=mu.wtnInboardInformation(state);
            		state=mu.pressRoleString(state, wardToCharMap,appearMap);
            	    state=mu.makePafeBoard(state);
            		System.out.println("expand---"+state);
            		String apple=state;

            		state=target.toString();
            		state=mu.preprocess(state);
            		state=mu.wtnInboardInformation(state);
            		state=mu.pressRoleString(state, wardToCharMap,appearMap);
            	    state=mu.makePafeBoard(state);
            		System.out.println("similar--"+state);

            	    String hash=mu.encodeHaffman(apple,haffmanHashMap);
            		System.out.println("expand---"+hash);
            	    hash=mu.encodeHaffman(state,haffmanHashMap);
            		System.out.println("similar--"+hash);
            		System.out.println();
            		*/

        			if(depthTimesMap.containsKey(depth))
        				depthTimesMap.put(depth, depthTimesMap.get(depth)+1);
        			else
        				depthTimesMap.put(depth, 1);

        			return;
        		}
        	}
        	else
        		continue;

        	searchedNodelist.add(n);
        	for(Node child:n.children.values()) {
        		que.add(child);
        		depthMap.put(child,depth);
        	}
        	if(n.parent!=null) {
        		que.add(n.parent);
        		depthMap.put(n.parent,depth);
        	}
        }
        if(depth>depthMax)
        	depthMax=depth;
    }
    public int testtest=0;
    public int aaa=0,bbb=0;
    Map<Integer,  Map<Integer, Integer>> checkDepthMap =new HashMap<>();
    Map<MachineState, List<MachineState>> visitedStatesMap =new HashMap<>();
    //key:depth*100+length, value:int[21] 距離をindexとして、エンカウント回数を要素として記録する。

    public void checkDifferenceNumOfBit(Node r) {
    	Queue<Node> que=new ArrayDeque<>();
    	List<Node> searchedNodelist=new ArrayList<>();
    	Map<Node,Integer> depthMap=new HashMap<>();
    	int depth=0;
    	que.add(r);
    	depthMap.put(r,depth);

        while(!que.isEmpty()) {
        	Node n=que.poll();
        	if(!n.equals(r))
        		depth=depthMap.get(n)+1;
        	if(depth>20)
        		continue;

        	if(!searchedNodelist.contains(n)) {
        		boolean flag=true;
        		if(visitedStatesMap.containsKey(r.getState())) {
        			List<MachineState> l=visitedStatesMap.get(r.getState());
        			if(l.contains(n.getState()))
        				flag=false;
        			else
        				l.add(n.getState());
        		}
        		else {
        			List<MachineState> l = new ArrayList<>();
        			l.add(n.getState());
        			visitedStatesMap.put(r.getState(), l);
        		}/*
        		if(testtest<100 && flag) {
        			System.out.println(mu.preprocess(r.getState().toString()));
        			System.out.println(mu.preprocess(n.getState().toString()));
        			System.out.println();
        		}*/
        		if(flag) {

        			String str=makeHuffmanCode(n.getState().toString());
        			huffmanSet comparisionHf = new huffmanSet(str);
        			str=makeHuffmanCode(r.getState().toString());
        			huffmanSet targetHf = new huffmanSet(str);
        			int num=0;
	        		for(int i=0;i<targetHf.codeList.size();i++) {
	        			long xorNum=comparisionHf.codeList.get(i) ^ targetHf.codeList.get(i);
	        			num+= Long.bitCount(xorNum);
	        		}
	        		int key=n.depth*100+depth;
	        		if(n.depth==2 && depth==2 && r.depth<n.depth)
	        			aaa++;//test
	        		if(checkDepthMap.containsKey(key)) {
	        			Map<Integer, Integer> map=checkDepthMap.get(key);
	        			if(map.containsKey(num))
	        				map.replace(num, map.get(num)+1);
	        			else
	        				map.put(num, 1);
	        		}
	        		else {
	        			Map<Integer, Integer> map = new HashMap<>();
	        			map.put(num,1);
	        			checkDepthMap.put(key, map);
	        		}
	        		if(n.depth==2 && depth==2) {
	        			System.out.println("bbb:::"+bbb);bbb++;
	        			System.out.println("length---"+depth);
	        			System.out.println("comparision:"+n.getState()+", depth---"+n.depth);
	        			System.out.println("t a r g e t:"+r.getState()+", depth---"+r.depth);
	        			System.out.print("comparision:");
	        			comparisionHf.printCode();
	        			System.out.print("t a r g e t:");
	        			targetHf.printCode();
	        			System.out.println("how many?---"+num);
	        			System.out.println();
	        			testtest++;
	        		}
        		}
        	}
        	else
        		continue;

        	searchedNodelist.add(n);
        	for(Node child:n.children.values()) {
        		que.add(child);
        		depthMap.put(child,depth);
        	}
        	if(n.parent!=null) {
        		que.add(n.parent);
        		depthMap.put(n.parent,depth);
        	}
        }
        if(depth>depthMax)
        	depthMax=depth;
    }

    public void printCheckResult() {
    	for(int i=0;i<20;i++)
    		for(int j=0;j<20;j++) {
    			int key=j*100+i;
    			Map<Integer,Integer> map=checkDepthMap.get(key);
    			if(map!=null) {
    				System.out.println("length---"+i+", depth---"+j);
    				for(Integer k:map.keySet())
    					System.out.print(k+":"+map.get(k)+", ");
        			System.out.println();
    			}
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

		public void printCode() {
			for(Long l:codeList)
				System.out.print(Long.toBinaryString(l));
			System.out.println();
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

		@Override
		public String toString()
		{
			String str="";
			for(Long l:this.codeList) {
				str+=Long.toBinaryString(l);
			}
			return str;
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
	    String hash=mu.encodeHaffman(state,haffmanHashMap);

		return hash;
	}

	//test0606
	public void printSimpleState(String state) {
		state=mu.preprocess(state);
	 	state=mu.wtnInboardInformation(state);
	 	state=mu.pressRoleString(state, wardToCharMap,appearMap);
	 	System.out.println(state);
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
		/*
		bunboCount++;
		 if(matchNode!=null) {
			 selectCount++;
		     return matchNode;
		 }
		 return matchNode;
	*/
		 /*
		  * if you want to get the information similar to the argument state from the search experience,
		  * Use   Node similarNode=new Node(null); ~ return returnNode; and Set the comment out if(matchNode!=null) ~   return matchNode;
		  */

		 Node similarNode=new Node(null);
		 originalHash=hs.codeList;

		 //  searchSimilarHash's second argument is hamming length
		 bunboCount++;
		 similarNode=searchSimilarHash(0,2,hs,similarNode);
		 /*
		 if(testTimes<1) {
			 System.out.println("RESULT");
			 int apple=0;
			 for(Long l:hs.codeList) {
				 System.out.print(Long.toBinaryString(l));
				 apple+=Long.toBinaryString(l).length();
			 }
			 System.out.println();
			 System.out.println("code length::"+apple);
			 System.out.println("win value::"+similarNode.winValue[0]);
			 System.out.println("visit time:"+similarNode.v);
			 System.out.println();
		 }*/

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
					/*
					//egg test 0517
					Node test=nodeSearch(root,n.getState());
					if(test!=null)
						hitCount++;
					 */
					//nodeSearchForBFS(root,n.getState());

					//egg test 0624 ここではないかも
					nodeSearchForBFS(targetRootNode,n.getState());
					//checkDifferenceNumOfBit(targetRootNode);
					/*
					if(testTimes<1) {
						 for(Long l:subHash.codeList)
							 System.out.print(Long.toBinaryString(l));
						 System.out.println();
						System.out.println("aaaawin value::"+n.winValue[0]);
						System.out.println("visit time:"+n.v);
					}*/
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
        return "HuffmanApplePlayer";
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


