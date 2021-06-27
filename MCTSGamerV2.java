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


public final class MCTSGamerV2 extends SampleGamer
{
	 @Override
	 public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	 {
		 initAll();
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
    	/*
    	if(root==null) {
        	root=n;
        	lastNode=root;
        }
        else{
        	long beforeTime=System.currentTimeMillis();
        	Node nm=nodeSearch(root,n.state);//*taking time
        	long afterTime=System.currentTimeMillis();
        	long apaman=afterTime-beforeTime;
        	System.out.println("first brack:"+(afterTime-beforeTime));

        	if(nm!=null) {
        		n=nm;
        	}
        }
    	 */

        //while(System.currentTimeMillis()<finishBy) {
        for(int i=0;i<500;i++) {
        	MonteCalroPlayout(n);
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
        testCount=0;
    	supertest=0;
    	notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
        //System.out.println("error count:::"+errorCount);
        return selection;
    }

    public int totalPlayerNumber;//total number of players
    public Node root=null;//root node
    public int playerNum=0;//current player number in simulation

    public void initAll() {
    	root=null;
    	totalPlayerNumber=getStateMachine().getRoles().size();
    	playoutMemorys = new HashMap<>();
    	bigramMemory = new HashMap<>();
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
    	    this.children= new HashMap<MachineState ,MCTSGamerV2.Node>();
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
    public int supertest=0;
    public Node lastNode=null; //前回の盤面情報
    public int errorCount=0;

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
     	}


    	List<Role> roleList=theMachine.getRoles();
    	int[] goalScore=new int[totalPlayerNumber];
    	for(int i=0;i<totalPlayerNumber;i++) {
    		Role l=roleList.get(i);
    		goalScore[i]=theMachine.getGoal(state,l);
    	}

    	/*
    	 * シミュレーションの過程で発生した盤面情報をbigramのメモリとplayoutのメモリに保存する。
    	 * この時にキューに保存しているノードに訪れた回数、報酬を記録する。
    	 */
    	Node parent=null;
    	for (Node v : que) {
    		String hashKey=MCTSutils.preprocess(v.state.toString());
    		long hashInteger=MCTSutils.hashInteger(hashKey, 5);
    		v.visitValueCount();
    		v.setWinValue(goalScore);
    		if(parent!=null) {
    			String pa=parent.state.toString();
    			pa=MCTSutils.preprocess(pa);
    			long paInteger=MCTSutils.hashInteger(pa, 5);
        		long jr=hashInteger;
        		addBigramMemory(paInteger,jr);
        		v.setdepthValue(parent.depth+1);
        	}
    		addPlayoutMemorys(hashInteger,v,goalScore);
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
    		lastNode.expand(saveNode);
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
    			saveNode.expand(v);
        		break;
    		}else if(!saveNode.hasChild(v.state)) {
    			saveNode.expand(ns);
        		break;
    		}
    		saveNode=ns;
    	}

    	return;
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
    		MachineState state=getUnexploredNextState(n);
    		selectNode=new Node(state);
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
    public MachineState getUnexploredNextState(Node n) throws MoveDefinitionException, TransitionDefinitionException {
    	StateMachine theMachine = getStateMachine();
    	 MachineState nextState;

    	 /*
    	  //part of select playout memory
    	 Node nm=selectPlayoutMemory(n);
    	 if(nm!=null) {
        	 if(ipsilonGreedy()) {
        			 return nm.state;
        	 }
    	 }
    	  */
    	while(true) {
    		List<Move> a=theMachine.getRandomJointMove(n.getState());
    		nextState = theMachine.getNextState(n.getState(),a );
            if(!n.children.containsKey(nextState)) {
               	break;
            }
    	}
    	return nextState;
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
    		return;
    	}
    	for(MachineState key:n.children.keySet()){
    		//System.out.println("times of i ==== "+i++);
    		Node next=n.children.get(key);
    		//nxt.printItem(nxt.list);
    		showAll(next);
    	}
    }

    ///**part of bigram **///

	//key---hash,value---its value
	Map<Long,ArrayList<Node>> playoutMemorys = new HashMap<>();
    //key---parent,value---child
	Map<Long,ArrayList<Long>> bigramMemory = new HashMap<>();

	/*
	 * If the argument hash has not been registered in playout memory,
	 * registered hash and Node from argument in playout memory.
	 * If the argument hash has already been registered in playout memory and
	 * the argument Node has also been registered in it,
	 * update the memory same as the argument,
	 * else the hash and Node from argument is registered in playout memory.
	 */
	public void addPlayoutMemorys(long hash,Node n,int[] goalScore) {
		Node m=new Node(n.state);
		m.visitValueCount();
		m.setWinValue(goalScore);
		m.setdepthValue(n.depth);

		if(!playoutMemorys.containsKey(hash)) {
			ArrayList<Node> ls=new ArrayList<>();
			ls.add(m);
			playoutMemorys.put(hash, ls);
		}else {
			//衝突
			ArrayList<Node> ls=playoutMemorys.get(hash);
			boolean flag=false;
			for(Node nod:ls) {
				if(nod.getState().equals(m.getState())) {
					nod.visitValueCount();
					nod.setWinValue(goalScore);
					nod.setdepthValue(n.depth);
					flag=true;
					break;
				}
			}
			if(!flag) {
				ls.add(m);
			}
		}

	}
	/*
	 * remove playout memory which will be not appeared in future development
	 */
	public void removePlayoutMemory() {
		ArrayList<Long> sls=new ArrayList<>();
		for(long s:playoutMemorys.keySet()) {
			ArrayList<Node> nls=playoutMemorys.get(s);
			Node m=nls.get(0);
			if(m.depth<globalDepth) {
				//System.out.println("m.depth:"+m.depth+", globalDepth:"+globalDepth);
				sls.add(s);
			}
		}
		for(long s:sls) {
			//System.out.println(MCTSutils.preprocess(playoutMemorys.get(s).get(0).state.toString()));
			playoutMemorys.remove(s);
		}
	}
	/*
	 * Return Node which is selected by selection probability of playoutMemory's winRate value.
	 */
	public Node selectPlayoutMemory(Node n) throws MoveDefinitionException, TransitionDefinitionException {
		Long key=getHashCode(n);
		ArrayList<Long> BMls=bigramMemory.get(key);

		if(BMls==null)
			return null;

		double totalScore=0.0;
		ArrayList<Node> saveNodeList=new ArrayList<Node>();

		for(long s:BMls) {
			if(playoutMemorys.containsKey(s)) {
				ArrayList<Node> nls=playoutMemorys.get(s);

				for(Node m:nls) {
					if(n.hasChild(m.state) || !isNextState(n.state,m.state)) {
						continue;
					}
					m.winRateCalculation();
					totalScore+=m.winRate[playerNum];
					saveNodeList.add(m);
				}
			}
		}

		double rnd=Math.random();
		//System.out.println("rnd:: :"+rnd);
		double apple=0.0;
		for(Node m:saveNodeList) {
			if(totalScore==0.0)
				continue;
			apple+=m.winRate[playerNum]/totalScore;
			//System.out.println("apple:::"+apple);
			if(apple>rnd)
				return m;
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
	 * Registered the argument information in bigramMemory.
	 */
	public void addBigramMemory(long parent,long child) {
		if(!bigramMemory.containsKey(parent)) {
			ArrayList<Long> ls=new ArrayList<>();
			ls.add(child);
			bigramMemory.put(parent, ls);
		}else {
			List<Long> ls=bigramMemory.get(parent);
			ls.add(child);
		}
	}
	/*
	 * Return true if argument Node has been already registered in playout memory.
	 */

	public Node searchPlayoutMemory(Node n) {
		for(long s:playoutMemorys.keySet()){
    		ArrayList<Node> ls=playoutMemorys.get(s);
    		for(Node m:ls) {
    			if(m.state.equals(n.state)) {
    				return m;
    			}
    		}
    	}
		return null;
	}
	/*
	 * Return the hash as Long type which is converted from the argument.
	 */
	public Long getHashCode(Node n) {
		String str=n.state.toString();
		str=MCTSutils.preprocess(str);
		return MCTSutils.hashInteger(str, 5);
	}

	/*
	 * Return true following epsilon greedy method.
	 */
	public boolean ipsilonGreedy() {
		double rnd=Math.random();
		double ipsilon=0.7;
		if((1-ipsilon)>rnd)
			return true;
		return false;
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
        return "MCTSPlayerV2";
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


