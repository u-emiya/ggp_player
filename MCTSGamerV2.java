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

//0929
public final class MCTSGamerV2 extends SampleGamer
{
	 @Override
	 public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	 {
		 initAll();
		 System.out.println("start:::start:::start:::start:::start:::start:::start:::start:::start:::start");
		 System.out.println("apple");
	 }
    /**
     * Employs a simple sample "Monte Carlo" algorithm.
     */
	 int kokoko=0;
	 public static int testApple=0;
    @Override
    public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
    {
        StateMachine theMachine = getStateMachine();
        long start = System.currentTimeMillis();
        long finishBy = timeout - 1000;
        long startnano = System.nanoTime();

        //removePlayoutMemory();

        /*
         * 現在の盤面状態からノードnを作り、
         * rootノードがnull、つまり一番初めの時にはrootノードにnを代入する
         * そうでない場合は、構築されている木からノードnと同じ盤面要素を持つノードを探す。
         * あれば、そのノードの情報を上書きする。
         */
        Node n=new Node(getCurrentState());

    	root=n;

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

        long stop = System.currentTimeMillis();
        long stopnano = System.nanoTime();


        showAllCount=0;
        nodeCount=0;
        //showAll(root);
        System.out.println("---     V 2     ----");
        System.out.println("role number:"+getOwnRoleNumber());
        System.out.println("--- "+kokoko+" turn ----");
        System.out.println(MCTSutils.preprocess(getCurrentState().toString())+":");
        System.out.println("time:"+(stop-start));
        System.out.println("time nano:"+(stopnano-startnano));

/*
        System.out.println(getOwnRoleNumber()+"--show all count;;;"+showAllCount);
        System.out.println(getOwnRoleNumber()+"--nsnull count;;;"+nsnull);
        System.out.println(getOwnRoleNumber()+"--notnsnull count;;;"+notnsnull);
        System.out.println(getOwnRoleNumber()+"--node count;;;"+nodeCount);
        System.out.println(getOwnRoleNumber()+"--expand count;;;"+expandCount);
        System.out.println("total time:"+totalTime+", count:"+count);
        System.out.println("else  time:"+elseTotalTime+", count:"+elseCount);
        System.out.println("average total time:"+((double)totalTime/count));
        System.out.println("average else  time:"+((double)elseTotalTime/elseCount));
        System.out.println("total time:"+superTotalTime);
        System.out.println("average else  time:"+(superTotalTime/500));
        System.out.println("soto total time:"+sotoTotalTime);
        System.out.println("what time:"+whatTime);
        System.out.println("1--isUnecPloe time:"+isUnTime1);
        System.out.println("2--isUnecPloe time:"+isUnTime2);
        System.out.println("3--isUnecPloe time:"+isUnTime3);
        System.out.println("2--random nex time:"+randomNextTime);
        System.out.println("2--random else time:"+elseTimes);
        System.out.println("times of not equal:"+notEqualCount);
        System.out.println("");
        System.out.println("apple time:"+appleTime);
        System.out.println("after time:"+aftersuperTotalTime);
         System.out.println("");
*/

        totalTime=0;elseTotalTime=0;count=0;elseCount=0;superTotalTime=0;whatTime=0;
        sotoTotalTime=0;isUnTime1=0;isUnTime2=0;isUnTime3=0;notEqualCount=0;elseTimes=0;
        randomNextTime=0;
        expandCount=0;
        nsnull=0;
        notnsnull=0;
        kokoko++;

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
    		expandCount++;
    	    this.children.put(n.state,n);
    	}


    }

        public int errorCount=0;
        public long totalTime=0;
        public long elseTotalTime=0;
        public long superTotalTime=0;
        public long sotoTotalTime=0;
        public long whatTime=0;
        public long isUnTime1=0;
        public long isUnTime2=0;
        public long isUnTime3=0;
        public long notEqualCount=0;
        public long randomNextTime=0;

        public long appleTime=0;
        public long aftersuperTotalTime=0;

        public int count=0;
        public int elseCount=0;
        public int elseTimes=0;

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

    	long applestart= System.nanoTime();
    	while(!theMachine.isTerminal(state)) {
    		int childlenMaxSize=theMachine.getLegalMoves(node.state, theMachine.getRoles().get(playerNum)).size();
    		Node beforeNode=node;
    		long startstart = System.nanoTime();

    		node=selectChildNode(node);
    	 	long timetime = System.nanoTime()-startstart;
        	superTotalTime+=timetime;

     		if(beforeNode.children.size()!=childlenMaxSize && isExpandNode) {
     			/*
     			System.out.println("@@@:"+beforeNode.getState());
     			System.out.println("@@@:"+node.getState());
      			System.out.println("@@@:(max,now)---("+childlenMaxSize+" ,"+node.children.size()+" )");
      			*/
      			saveParentNode=beforeNode;
      			saveChildNode=node;
      			isExpandNode=false;
      		}

    		state=node.getState();
      	   	que.add(node);
      		playerNum=(playerNum+1)%(theMachine.getRoles().size());

     	}
	 	long  applestop= System.nanoTime()-applestart;
		appleTime+=applestop;


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
    		v.visitValueCount();
    		v.setWinValue(goalScore);
    		if(parent!=null) {
    			v.setdepthValue(parent.depth+1);
        	}
    		parent=v;
    	}
    	if(saveChildNode!=null) {
    		Node expandNode=new Node(saveChildNode.getState());
    		expandNode.v=saveChildNode.v;
    		expandNode.setWinValue(saveChildNode.winValue);
    		expandNode.setdepthValue(saveParentNode.depth+1);
    		saveParentNode.expand(expandNode);
    	}
    	/*
    	 * キューに保存していたノードを取り出していく。
    	 * つまり、シミュレーションの過程ではじめに発生した盤面情報から順に参照していく。
    	 * nodeSearchメソッドで構築している木にあるか参照し、なかったら拡張して参照を終了する。
    	 */

    	/*
    	saveNode=que.poll();


    	Node nm=nodeSearch(root,saveNode.state);//*taking time

    	//Node nm=nodeSearch(lastNode,saveNode.state);
    	if(nm==null) {
    		lastNode.expand(saveNode);
    		System.out.println("--nm 1--:"+nm.getState());
    		System.out.println("--nm 2--:"+saveNode.getState());
    		return;
    	}
    	for (Node v : que) {
    		testCount++;
    		Node ns=nodeSearch(root,v.state);//*taking time

    		//Node ns=nodeSearch(n,v.state);
    		 *
    		 * 一つ目のif文は、構築している木にない場合の拡張
    		 * 二つ目のif文は、構築している木には存在するが、親子関係が成立していない場合の拡張
    		 *
    		if( ns==null) {
    			nsnull++;
    			saveNode.expand(v);
        		System.out.println("--ns up 1--:"+saveNode.getState());
        		System.out.println("--ns up 1--:"+v.getState());
        		System.out.println();
        		break;
    		}else if(!saveNode.hasChild(v.state)) {
    			notnsnull++;
    			saveNode.expand(ns);
        		System.out.println("--ns down 1--:"+saveNode.getState());
        		System.out.println("--ns down 1--:"+ns.getState());
        		System.out.println();

        		break;
    		}
    		saveNode=ns;
    	}*/

    	//System.out.println("--- select next play ---");
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
    	//System.out.println("MCTSGamerV2 --->>> select Next Play");
     	int winValueTotal=0;
    	for(MachineState key:n.children.keySet()) {
    		Node child=n.children.get(key);
    		/*
    		System.out.println(MCTSutils.preprocess(key.toString()));
    		System.out.println("child.v:"+child.v);
    		System.out.println("win :"+child.winValue[getOwnRoleNumber()]);
    		*/
    		winValueTotal += child.winValue[getOwnRoleNumber()];

        	if(child.v>bestValue) {
        		bestValue=child.v;
        		bestState=key;
        	}
    	}

    	//System.out.println("win total:::"+winValueTotal);

    	long start=System.nanoTime();
    	Map<Move, List<MachineState>> map=theMachine.getNextStates(getCurrentState(),role);
    	whatTime+=System.nanoTime()-start;
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
    	long sotostart = System.nanoTime();


    	if(isUnexploredState(n)) {
    		long start = System.nanoTime();

    		double saveValue=0;
    		for(MachineState key:n.children.keySet()) {
    			Node child=n.children.get(key);
    			double uctValue=uctCalculation(n,child);
    			if(uctValue>saveValue || selectNode==null) {
    				saveValue=uctValue;
    				selectNode=child;
    			}
    		}
    		long time = System.nanoTime()-start;
    		totalTime+=time;
    		count++;

    	}else {
    		long start = System.nanoTime();

    		MachineState state=getUnexploredNextState(n);
    		selectNode=new Node(state);

    		long time = System.nanoTime()-start;
    		elseTotalTime+=time;
    		elseCount++;

    	}
		long sototime = System.nanoTime()-sotostart;
		sotoTotalTime+=sototime;

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
    public MachineState getUnexploredNextState(Node n) throws MoveDefinitionException, TransitionDefinitionException {
    	StateMachine theMachine = getStateMachine();
    	 MachineState nextState;
    		long start,time;
    	while(true) {
    		/*
    		 start = System.nanoTime();
    		List<Move> a=theMachine.getRandomJointMove(n.getState());
    	 	 time = System.nanoTime()-start;
        	isUnTime1+=time;

        	start = System.nanoTime();
        	nextState = theMachine.getNextState(n.getState(),a);
    		time = System.nanoTime()-start;
        	isUnTime2+=time;
        	*/

        	start = System.nanoTime();
        	nextState=theMachine.getRandomNextState(n.getState());
        	time = System.nanoTime()-start;
        	randomNextTime+=time;

            if(!n.children.containsKey(nextState)) {
               	break;
            }else
            	elseTimes++;
    	}
    	return nextState;
    }

    /*
     * Search the same Node as the argument Node in constructed tree.
     */
    public Node nodeSearch(Node n,MachineState key){
		if(n==null) {
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
    int nodeCount=0;
    int expandCount=0;
    int nsnull=0;
    int notnsnull=0;
    public void showAll(Node n) {
    	showAllCount++;

    	//if(n.depth<=globalDepth) {
    		System.out.println("--- show all ---");
    		System.out.println(n.state);
    	  	System.out.println(MCTSutils.preprocess(n.state.toString()));
    		System.out.println("*depth---"+n.depth+"  n:::"+n.v+",w:::"+n.winValue[getOwnRoleNumber()]);
    		System.out.println("w/v:::"+n.winValue[getOwnRoleNumber()]/n.v);
    	//}

    	if(n.children == null){
    		return;
    	}
    	for(MachineState key:n.children.keySet()){
    		//System.out.println("times of i ==== "+i++);
    		Node next=n.children.get(key);
    		//nxt.printItem(nxt.list);
    		nodeCount++;
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