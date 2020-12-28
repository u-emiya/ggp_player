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
public final class MCTSGamer extends SampleGamer
{
	 @Override
	 public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	 {
		  StateMachine theMachine = getStateMachine();
		 System.out.println("happy");
		 initAll();
		 //test
		 int[] aaa= {1,2,3};
		 String hashKey;
		 Node n=new Node(getCurrentState());
		 hashKey=MCTSutils.hash(n.state.toString(),7);
		 System.out.println("hash check for<n>:"+hashKey);
		 addPlayoutMemorys(hashKey,n,aaa);
		 addPlayoutMemorys(hashKey,n,aaa);

		 Node m=new Node(getCurrentState());
		 hashKey=MCTSutils.hash(m.state.toString(),7);
		 System.out.println("hash check for<m>:"+hashKey);
		 addPlayoutMemorys(hashKey,m,aaa);

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
			 System.out.println(s);
		 	 for(Node mmm:lst) {
				 System.out.println("mmm:"+mmm.getState());
			 }
		 }
		 String str="aababbacbadbaebafbagbahbbabbbbbcobdbbebbfbbgbbhxcabcbbccbcdbcebcfbcgbchbdabdbbdcbddbdebdfbdgbdhbeabebbecbedbeebefbegbehbfabfbbfcbfdbfebffbfgbfhbgabgbbgcbgdbgebgfbggbghbhabhbbhcbhdbhebhfbhgbhhb";
		 System.out.println(MCTSutils.hash(str,7));

		 //System.out.println(getMatch().getMatchId());
	        // Do nothing.
	 }
    /**
     * Employs a simple sample "Monte Carlo" algorithm.
     */
    @Override
    public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
    {
        StateMachine theMachine = getStateMachine();
        long start = System.currentTimeMillis();
        long finishBy = timeout - 1000;
        System.out.println(finishBy);
        List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
        Move selection = moves.get(0);

        System.out.println("!!!!!:"+moves);
        if(lastNode!=null) {
        	System.out.println(lastNode.state);
        }
        test=0;
        Node f=null;
        if (moves.size() > 1) {

            Node n=new Node(getCurrentState());
            if(root==null) {
            	root=n;
            }
            else {
            	n=nodeSearch(root,n.state);
            	if(n==null) {
            		n=new Node(getCurrentState());
            	}
            }
            /*
            System.out.println("beforeqwwqdwdqdn.state:::"+n.state);
         	for(MachineState key:n.children.keySet()) {
        		System.out.println("children-state:"+n.children.get(key).state);
        		System.out.println("children---n:"+n.children.get(key).v);
         	}*/
            int count=0;
            while(System.currentTimeMillis()<finishBy) {
            	MonteCalroPlayout(n);
            	count++;
            }
            /*
            System.out.println("afterqwwqdwdqdn.state:::"+n.state);
         	for(MachineState key:n.children.keySet()) {
        		System.out.println("children-state:"+n.children.get(key).state);
         		System.out.println("children---n:"+n.children.get(key).v);
         	}*/
        	System.out.println("testCount:::"+testCount);
            System.out.println("count***"+count);
            //showAll(root);

            selection=selectNextPlay(n,moves);
            /*
            for (MachineState key : playoutMemory.keySet()) {
            	Node nm=playoutMemory.get(key);
                System.out.println(key + ":" + nm.v);
            }*/
            System.out.println("size---"+playoutMemory.size());
            System.out.println("!!!!!!---"+kokko);
        }else {
        	long time[]= {timeout-5000,timeout-4000,timeout-3000,timeout-2000,timeout-1000,timeout-100};
    		int i=0;
        	while(true) {
        		if(System.currentTimeMillis()==time[i]) {
               		System.out.println(i+":"+time[i]);
               		i++;
        		}
        		if(i>5)
        			break;
        	}
        }
        //showAll(root);
        System.out.println("super!!!!!!---"+selection);

        MachineState m=getCurrentState();
        String s=m.toString();
        System.out.println("state string:::"+s);
        System.out.println();

        //showAll(root);
        long stop = System.currentTimeMillis();
        notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
        return selection;
    }

    public void initAll() {
    	root=null;
    	totalPlayerNumber=getStateMachine().getRoles().size();
    	count=0;
    }

    public int count=0;
    public int totalPlayerNumber;
    public Node root=null;
    public int playerNum=0;

    public class Node{
    	int v; //times of visited
    	MachineState state;//Board information
    	int depth;
    	Map<MachineState ,Node>children;

    	int winValue[];
    	double winRate[];

    	Node(MachineState state){
    		this.v=0;
    	    this.state=state;
    	    this.depth=0;
    	    this.children= new HashMap<MachineState ,MCTSGamer.Node>();
    	    //mark!!
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
    		this.depth+=depth;
    	}
    	public void visitValueCount() {
    		this.v++;
    	}

        	//mark!!!
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
    	playerNum=getOwnRoleNumber();
    	int testCountSub=0;
    	while(!theMachine.isTerminal(state)) {
    		node=selectChildNode(node);
      		state=node.getState();
      	   	que.add(node);
      		playerNum=(playerNum+1)%(theMachine.getRoles().size());
      	   	testCountSub++;

        }
    	if(testCount<testCountSub) {
    		testCount=testCountSub;
    		System.out.println("HIT***"+testCount);
    	}
    	List<Role> roleList=theMachine.getRoles();
    	int totalPlayer=roleList.size();
    	int[] goalScore=new int[totalPlayer];

    	for(int i=0;i<totalPlayer;i++) {
    		Role l=roleList.get(i);
    		goalScore[i]=theMachine.getGoal(state,l);
    	}

    	Node parent=null;
    	int kakko=0;
    	for (Node v : que) {
    		//old
    		addPlayoutMemory(v,goalScore);
    		//new
    		String hashKey=MCTSutils.preprocess(v.state.toString());
    		hashKey=MCTSutils.hash(hashKey, 7);
    		addPlayoutMemorys(hashKey,v,goalScore);
    		kakko++;
    		if(parent!=null) {
    			if(test<1)
        			System.out.println(MCTSutils.preprocess(parent.state.toString()));
    			String pa=parent.state.toString();
    			pa=MCTSutils.preprocess(pa);
    			pa=MCTSutils.hash(pa, 7);
        		String jr=hashKey;
        		addBigramMemory(pa,jr);
    		}
    		parent=v;
    	}
    	//test
    	if(test<1)
    	for(String s:bigramMemory.keySet()) {
    		List<String> ls=bigramMemory.get(s);
    		System.out.println("parent::"+s);
    		for(String sss:ls) {
    			System.out.println("child:::"+sss);
    		}
    	}
    	if(test<1)
        	System.out.println(kakko);

    	for (Node v : que) {
    		Node ns=nodeSearch(root,v.state);
    		if(saveNode!=null) {
    			if(/*ns==null ||*/ !saveNode.hasChild(v.state)) {
    				if(ns==null)
    					ns=v;
    				ns.setdepthValue(saveNode.depth+1);
    				ns.setWinValue(goalScore);//mark!!!
    				ns.visitValueCount();
    				saveNode.expand(ns);
    				break;
    			}
    		}

    		else if(ns==null) {
    			supertest++;
    			System.out.println("*******************************************");
    			System.out.println("this state:"+v.state);
    			System.out.println("lastNode state:"+lastNode.state);
    			//showAll(root);

    			ns=v;
    			ns.setdepthValue(lastNode.depth+1);
				ns.setWinValue(goalScore);//mark!!!
				ns.visitValueCount();
				lastNode.expand(ns);
				break;

    		}
			saveNode=ns;
			ns.setWinValue(goalScore);//mark!!!
			ns.visitValueCount();


		}
    	test++;
    }

    public Move selectNextPlay(Node n,List<Move> moves) throws MoveDefinitionException, TransitionDefinitionException {
    	StateMachine theMachine = getStateMachine();
        Role role=getRole();
    	double bestWinRate=0.0;
    	MachineState bestState=null;
    	Move bestMove=null;
    	int bestValue=0;
    	/*
    	System.out.println("children");
    	System.out.println("n.state:"+n.state);
    	*/
    	for(MachineState key:n.children.keySet()) {
    		Node child=n.children.get(key);
    		/*
    		System.out.println(key);
    		System.out.println("depth::"+child.depth);
    		System.out.println("visit::"+child.v+"- win:::"+child.winValue[getOwnRoleNumber()]);
    		System.out.println("winRate:::"+child.winRate[getOwnRoleNumber()]);
    		*/
        	if(child.v>bestValue) {
        		bestValue=child.v;
        		bestState=key;
        	}
    	}
    	lastNode=nodeSearch(root,bestState);


    	System.out.println("bestKey::"+bestState);

    	Map<Move, List<MachineState>> map=theMachine.getNextStates(getCurrentState(),role);
        for(int i=0;i<moves.size();i++) {
        	Move m=moves.get(i);
        	List<MachineState> stateList=map.get(m);
        	if(stateList.contains(bestState)) {
        		bestMove=m;
        		break;
        	}
        	/*
        	if(bestState.equals(theMachine.getRandomNextState(getCurrentState(), role, m))) {
        		bestMove=m;
        		break;
        	}
        	*/

        }
        System.out.println(bestMove);
        return bestMove;
    }


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
    	double C=0.5;

    	if(child.v!=0) {
    		searchValue=Math.sqrt(logVisitedValue/child.v);
    	}else
    		searchValue=Double.MAX_VALUE;
     	child.winRateCalculation();//mark!!
     	double uctValue=child.winRate[playerNum]+C*searchValue;

    	return uctValue;
    }

    public MachineState getUnexploredState(Node n) throws MoveDefinitionException, TransitionDefinitionException {
    	StateMachine theMachine = getStateMachine();
    	 MachineState nextState;
    	while(true) {
    		List<Move> a=theMachine.getRandomJointMove(n.getState());
    		nextState = theMachine.getNextState(n.getState(),a );
            if(!n.children.containsKey(nextState)) {

            	if(playoutMemory.containsKey(nextState)) {
            		Node m=playoutMemory.get(nextState);
               	 	//System.out.println("nextState:"+m.state);
               	 	//System.out.println("visit times:"+m.v+"win value:"+m.winValue[0]);
            	}

            	break;
            }
    	}
    	return nextState;
    }

    public boolean unexploredState(Node n) throws MoveDefinitionException {
    	StateMachine theMachine = getStateMachine();
    	if(n.children==null)
    		return false;

    	int nodeSize=n.children.size();
    	List<Role> roleList=theMachine.getRoles();
    	Role r=roleList.get(playerNum);
    	List<Move> l=theMachine.getLegalMoves(n.getState(),r);
    	System.out.println("  +*+  unexploredState  +*+");
    	System.out.println("node size---"+nodeSize);
    	System.out.println(" l.  size---"+l.size());
    	System.out.println("What role---"+r);
    	System.out.println("role List---"+l);

    	if(nodeSize==l.size()) {
    		return true;
    	}
    	return false;
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

    public void showAll(Node n) {
    	if(n.depth<=1) {
    		System.out.println("--- show all ---");
    		System.out.println(n.state);
    		System.out.println("*depth---"+n.depth+"  n:::"+n.v+",w:::"+n.winValue[getOwnRoleNumber()]);
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
	Map<MachineState ,Node> playoutMemory = new HashMap<MachineState ,MCTSGamer.Node>();
	public int kokko=0;
	public void addPlayoutMemory(Node n,int[] goalScore) {
		Node m;
		kokko++;
		if(playoutMemory.containsKey(n.state)) {
			m=playoutMemory.get(n.state);
		}else {
			m=new Node(n.state);
			playoutMemory.put(m.state,m);
		}
		m.visitValueCount();
		m.setWinValue(goalScore);
	}

	Map<String,ArrayList<Node>> playoutMemorys = new HashMap<>();
	Map<String,ArrayList<String>> bigramMemory = new HashMap<>();
	public void addPlayoutMemorys(String hash,Node n,int[] goalScore) {
		Node m=n;
		if(!playoutMemorys.containsKey(hash)) {
			ArrayList<Node> ls=new ArrayList<>();
			ls.add(n);
			playoutMemorys.put(hash, ls);
		}else {
			//衝突
			ArrayList<Node> ls=playoutMemorys.get(hash);
			boolean flag=false;
			for(Node nod:ls) {
				if(nod.getState().equals(n.getState())) {
					m=nod;
					flag=true;
					break;
				}
			}
			if(!flag) {
				ls.add(n);
			}
		}
		m.visitValueCount();
		m.setWinValue(goalScore);
		//System.out.println("visit::"+m.v+", score::"+m.winValue[0]);
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
        return "MCTSPlayer";
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