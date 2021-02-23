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

/**
 * SampleMonteCarloGamer is a simple state-machine-based Gamer. It will use a
 * pure Monte Carlo approach towards picking moves, doing simulations and then
 * choosing the move that has the highest expected score. It should be slightly
 * more challenging than the RandomGamer, while still playing reasonably fast.
 *
 * However, right now it isn't challenging at all. It's extremely mediocre, and
 * doesn't even block obvious one-move wins. This is partially due to the speed
 * of the default state machine (which is slow) and mostly due to the algorithm
 * assuming that the opponent plays completely randomly, which is inaccurate.
 *
 * @author Sam Schreiber
 */
public final class MCTSbigramGamer extends SampleGamer
{
	 @Override
	 public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	 {
		 StateMachine theMachine = getStateMachine();
		 System.out.println("happy");
		 initAll();
		 /*
		 //test
		 int[] aaa= {1,2,3};
		 String hashKey;
		 Node n=new Node(getCurrentState());
		 hashKey=MCTSutils.hash(n.state.toString(),7);
		 System.out.println("hash check for<n>:"+hashKey);
		 addPlayoutMemorys(hashKey,n,aaa);
		 addPlayoutMemorys(hashKey,n,aaa);

		 Node m=new Node(getCurrentState());
		 m.visitValueCount();

		 hashKey=MCTSutils.hash(m.state.toString(),7);
		 System.out.println("hash check for<m>:"+hashKey);
		 addPlayoutMemorys(hashKey,m,aaa);
		 System.out.println("Node:"+m.getState());
		 System.out.println("mmm.visit:"+m.v+", mmm.win:"+m.winValue[0]);

		 MachineState sta=theMachine.getRandomNextState(getCurrentState());
		 Node nm=new Node(sta);
		 hashKey=MCTSutils.hash(nm.state.toString(),7);
		 System.out.println("hash check for<nm>:"+hashKey);
		 addPlayoutMemorys(hashKey,nm,aaa);

		 sta=theMachine.getRandomNextState(getCurrentState());
		 Node nmnm=new Node(sta);
		 hashKey=MCTSutils.hash(nmnm.state.toString(),7);
		 System.out.println("hash check for<nmnm>:"+hashKey);
		 addPlayoutMemorys(hashKey,nmnm,aaa);
		 for(String s:playoutMemorys.keySet()) {
			 List<Node> lst=playoutMemorys.get(s);
			 System.out.println("hash key"+s);
		 	 for(Node mmm:lst){
				 System.out.println("Node:"+mmm.getState());
				 System.out.println("mmm.visit:"+mmm.v+", mmm.win:"+mmm.winValue[0]);

			 }
		 }
		 String str="aababbacbadbaebafbagbahbbabbbbbcobdbbebbfbbgbbhxcabcbbccbcdbcebcfbcgbchbdabdbbdcbddbdebdfbdgbdhbeabebbecbedbeebefbegbehbfabfbbfcbfdbfebffbfgbfhbgabgbbgcbgdbgebgfbggbghbhabhbbhcbhdbhebhfbhgbhhb";
		 System.out.println(MCTSutils.hash(str,7));
		*/
		 //System.out.println(getMatch().getMatchId());
	     // Do nothing.
		 System.out.println("start:::start:::start:::start:::start:::start:::start:::start:::start:::start");
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
        List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
        Move selection = moves.get(0);

        System.out.println("!!!!!moves:"+moves);
        if(lastNode!=null) {
        	System.out.println(lastNode.state);
        }
        //removePlayoutMemory();
        test=0;
        Node n=new Node(getCurrentState());
        if(root==null) {
        	root=n;
        	lastNode=root;
        }
        else{
        	Node nm=nodeSearch(root,n.state);
        	if(nm!=null) {
        		n=nm;
        	}
        }

        //while(System.currentTimeMillis()<finishBy) {
        for(int i=0;i<500;i++) {
        	MonteCalroPlayout(n);
       	}
        System.out.println("testCount:::"+testCount);
        System.out.println((kokoko++)+"::count***"+test);

        if (moves.size() > 1) {
        	selection=selectNextPlay(n,moves);
        }
        System.out.println("super!!!!!!---"+selection);

        MachineState m=getCurrentState();
        String s=m.toString();
        System.out.println("state string:::"+s);
        globalDepth=n.depth+1;
        //showAll(n);
        /*
		for(String ss:playoutMemorys.keySet()) {
    		List<Node> lss=playoutMemorys.get(ss);
    		for(Node sss:lss) {
    			//System.out.println("state:"+MCTSutils.preprocess(sss.state.toString()));
    			System.out.println("depth:"+sss.depth+", visit:"+sss.v);
    			if(sss.depth==0)
    				System.out.println("state:"+sss.state.toString());

    		}
    	}
    	*/

        lastNode=n;
        System.out.println("size::"+playoutMemorys.size());
        long stop = System.currentTimeMillis();
        notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
        System.out.println(System.currentTimeMillis()-start);
        return selection;
    }

    public int totalPlayerNumber;//total number of players
    public Node root=null;//root node
    public int playerNum=0;//actual current player number

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
    	    this.children= new HashMap<MachineState ,MCTSbigramGamer.Node>();
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
    		else
    			System.out.println("error:have not visited");
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

    public int test=0;
    public int apple=40;
    public int testCount=0;
    public int supertest=0;
    public Node lastNode=null;

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
    	int totalPlayer=roleList.size();
    	int[] goalScore=new int[totalPlayer];
    	for(int i=0;i<totalPlayer;i++) {
    		Role l=roleList.get(i);
    		goalScore[i]=theMachine.getGoal(state,l);
    	}

    	Node parent=null;
    	for (Node v : que) {
    		String hashKey=MCTSutils.preprocess(v.state.toString());
    		hashKey=MCTSutils.hash(hashKey, 7);
    		v.visitValueCount();
    		v.setWinValue(goalScore);
    		if(parent!=null) {
    			String pa=parent.state.toString();
    			pa=MCTSutils.preprocess(pa);
    			pa=MCTSutils.hash(pa, 7);
        		String jr=hashKey;
        		addBigramMemory(pa,jr);
        		v.setdepthValue(parent.depth+1);
        	}
    		addPlayoutMemorys(hashKey,v,goalScore);
    		parent=v;
    	}

    	saveNode=que.poll();
    	Node nm=nodeSearch(root,saveNode.state);
    	//Node nm=nodeSearch(lastNode,saveNode.state);
    	if(nm==null) {
    		System.out.println("HHHHHHHHHHHHHIIIIIIIIIIIIITTTTTTTTT");
    		lastNode.expand(saveNode);
    		System.out.println("expand:"+MCTSutils.preprocess(saveNode.state.toString()));
    	}
    	for (Node v : que) {
    		Node ns=nodeSearch(root,v.state);
    		//Node ns=nodeSearch(n,v.state);
    		if( ns==null) {
    			saveNode.expand(v);
        		break;
    		}else if(!saveNode.hasChild(v.state)) {
    			saveNode.expand(ns);
        		break;
    		}
    		saveNode=ns;
    	}

    	test++;
    }

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
    	//lastNode=nodeSearch(root,bestState);

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

    String abokado="abokado";
    List<Double> testLs=new ArrayList<>();

    public Node selectChildNode(Node n) throws MoveDefinitionException, TransitionDefinitionException {
    	Node selectNode=null;
    	if(unExploredState(n)) {
    		double saveValue=0;
    		for(MachineState key:n.children.keySet()) {
    			Node child=n.children.get(key);
    			double uctValue=uctCalculation(n,child);
    			if(uctValue>saveValue) {
    				saveValue=uctValue;
    				selectNode=child;
    			}
    		}
    	}else {
    		MachineState state=getUnexploredState(n);
    		selectNode=new Node(state);
    	}
    	return selectNode;

    }

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

    public MachineState getUnexploredState(Node n) throws MoveDefinitionException, TransitionDefinitionException {
    	StateMachine theMachine = getStateMachine();
    	 MachineState nextState;
    	 Node nm=selectPlayoutMemory(n);
    	 if(nm!=null) {
    		 /*
    		 System.out.println("parent:"+MCTSutils.preprocess(n.state.toString()));
    		 System.out.println("child::"+MCTSutils.preprocess(nm.state.toString()));
    		 System.out.println("visit::"+nm.v);
    		 System.out.println("winrate:"+nm.winRate[playerNum]);
    		 System.out.println("rnd:"+rnd);
    		 */
    		 double score=nm.winRate[playerNum];
        	 double rnd=Math.random();
        	 if(score>rnd) {
        		 return nm.state;
        	 }
         }
    	while(true) {
    		List<Move> a=theMachine.getRandomJointMove(n.getState());
    		nextState = theMachine.getNextState(n.getState(),a );
            if(!n.children.containsKey(nextState)) {
            	/*
            	String str=MCTSutils.preprocess(nextState.toString());
            	str=MCTSutils.hash(str, 7);
            	if(playoutMemorys.containsKey(str)) {
            		List<Node> m=playoutMemorys.get(str);
            		for(int i=0;i<m.size();i++) {
               	 		//System.out.println("nextState:"+m.get(i).state);
               	 		Node nnn=nodeSearch(root,m.get(i).state);

               	 		if(nnn==null) {
               	 			System.out.println("null+++null+++nulll");
               	 		}else
               	 			System.out.println("nnn:"+nnn.state);

               	 		//System.out.println("visit times:"+m.v+"win value:"+m.winValue[0]);
            		}
            	}
    			*/
            	break;
            }
    	}
    	return nextState;
    }

    public boolean unExploredState(Node n) throws MoveDefinitionException, TransitionDefinitionException {
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

    public int getCurrentPlayerNum() throws MoveDefinitionException, TransitionDefinitionException {
    	StateMachine theMachine=getStateMachine();
    	int num=getOwnRoleNumber();

    	if(isSimultane()) {
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

    public boolean isSimultane() throws MoveDefinitionException {
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
//getrandomnextState
    int globalDepth=0;
    public void showAll(Node n) {
    	if(n.depth<=globalDepth) {
    		System.out.println("--- show all ---");
    		System.out.println(n.state);
    		System.out.println(MCTSutils.preprocess(n.state.toString()));
    		System.out.println("*depth---"+n.depth+"  n:::"+n.v+",w:::"+n.winValue[getOwnRoleNumber()]);
    		System.out.println("w/v:::"+n.winValue[getOwnRoleNumber()]/n.v);
    	}
    	if(n.children == null){
    		System.out.println("HIIIIT");
    		return;
    	}
    	int i=0;
    	for(MachineState key:n.children.keySet()){
    		//System.out.println("times of i ==== "+i++);
    		Node next=n.children.get(key);
    		//nxt.printItem(nxt.list);
    		showAll(next);
    	}
    }
    //test method

	Map<String,ArrayList<Node>> playoutMemorys = new HashMap<>();
	Map<String,ArrayList<String>> bigramMemory = new HashMap<>();
	public void addPlayoutMemorys(String hash,Node n,int[] goalScore) {
		Node m=new Node(n.state);
		if(!playoutMemorys.containsKey(hash)) {
			ArrayList<Node> ls=new ArrayList<>();
			ls.add(m);
			m.visitValueCount();
			m.setWinValue(goalScore);
			m.setdepthValue(n.depth);
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
		//System.out.println("visit::"+m.v+", score::"+m.winValue[0]);
	}

	public void removePlayoutMemory() {
		ArrayList<String> sls=new ArrayList<>();
		for(String s:playoutMemorys.keySet()) {
			ArrayList<Node> nls=playoutMemorys.get(s);
			Node m=nls.get(0);
			if(m.depth<globalDepth) {
				//System.out.println("m.depth:"+m.depth+", globalDepth:"+globalDepth);
				sls.add(s);
			}
		}
		for(String s:sls) {
			//System.out.println(MCTSutils.preprocess(playoutMemorys.get(s).get(0).state.toString()));
			playoutMemorys.remove(s);
		}
	}

	public Node selectPlayoutMemory(Node n) {
		String key=getHashCode(n);
		ArrayList<String> ls=bigramMemory.get(key);
		if(ls==null)
			return null;
		double saveScore=0.0;
		int visit=0;
		Node saven=null;
		//System.out.println("same");
		for(String s:ls) {
			if(playoutMemorys.containsKey(s)) {
				ArrayList<Node> nls=playoutMemorys.get(s);
				Node m=nls.get(0);
				m.winRateCalculation();
				double score=m.winRate[playerNum];
				/*System.out.println("memory:"+MCTSutils.preprocess(m.state.toString())+", hasChild:"+n.hasChild(m.state));
				System.out.println(getStateMachine().getRoles().get(playerNum)+"---Score:"+score+", visit:"+m.v);
				System.out.println("UCT:"+uctCalculation(n,m));*/
				if(saveScore<=score && visit<=m.v && !n.hasChild(m.state)) {
					double last=saveScore/visit;
					double now=score/m.v;
					double apple=now/(last+now);
					//System.out.println("hit:"+apple+", last:"+last+", now:"+now);
					if(visit==0||apple>Math.random()) {
						//System.out.println("hithithit");
						saveScore=score;
						saven=m;
						visit=m.v;
					}
				}
			}
		}
		return saven;
	}

	public void addBigramMemory(String parent,String child) {
		if(!bigramMemory.containsKey(parent)) {
			ArrayList<String> ls=new ArrayList<>();
			ls.add(child);
			bigramMemory.put(parent, ls);
		}else {
			List<String> ls=bigramMemory.get(parent);
			ls.add(child);
		}
	}

	public boolean hitPlayoutMemory(Node n) {
		for(String s:playoutMemorys.keySet()){
    		ArrayList<Node> ls=playoutMemorys.get(s);
    		for(Node m:ls) {
    			if(m.state.equals(n.state)) {
    				return true;
    			}
    		}
    	}
		return false;
	}

	public String getHashCode(Node n) {
		String str=n.state.toString();
		str=MCTSutils.preprocess(str);
		return MCTSutils.hash(str, 7);
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
        return "MCTSbigramPlayer";
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