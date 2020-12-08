package org.ggp.base.player.gamer.statemachine.sample.gpp_player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public final class SampleMonteCarloGamer extends SampleGamer
{
	 @Override
	 public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	 {
		 System.out.println("happy");
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



        if (moves.size() > 1) {
            int[] moveTotalPoints = new int[moves.size()];
            int[] moveTotalAttempts = new int[moves.size()];

            Node n=new Node(getCurrentState());
            if(root==null) {
            	System.out.println("firtst");
            	root=n;
            }
            else {
            	n=nodeSearch(root,n.state);
            	if(n==null)
            		n=new Node(getCurrentState());
            }

            int count=0;
            while(System.currentTimeMillis()<timeout-8000) {
            	MonteCalroPlayout(n);
            	count++;
            }
            System.out.println("count***"+count);
            showAll(root);

            // Perform depth charges for each candidate move, and keep track
            // of the total score and total attempts accumulated for each move.
            for (int i = 0; true; i = (i+1) % moves.size()) {
                if (System.currentTimeMillis() > finishBy)
                    break;
                int theScore = performDepthChargeFromMove(getCurrentState(), moves.get(i));
                moveTotalPoints[i] += theScore;
                moveTotalAttempts[i] += 1;
            }

            // Compute the expected score for each move.
            double[] moveExpectedPoints = new double[moves.size()];
            for (int i = 0; i < moves.size(); i++) {
                moveExpectedPoints[i] = (double)moveTotalPoints[i] / moveTotalAttempts[i];
            }

            // Find the move with the best expected score.
            int bestMove = 0;
            double bestMoveScore = moveExpectedPoints[0];
            for (int i = 1; i < moves.size(); i++) {
                if (moveExpectedPoints[i] > bestMoveScore) {
                    bestMoveScore = moveExpectedPoints[i];
                    bestMove = i;
                }
            }
            selection = moves.get(bestMove);
        }
        /*if(root!=null)
        	showAll(root);*/
        long stop = System.currentTimeMillis();
        notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
        return selection;
    }


    private int[] depth = new int[1];
    int performDepthChargeFromMove(MachineState theState, Move myMove) {
        StateMachine theMachine = getStateMachine();
        try {
            MachineState finalState = theMachine.performDepthCharge(theMachine.getRandomNextState(theState, getRole(), myMove), depth);
            return theMachine.getGoal(finalState, getRole());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public int count=0;

    public class Node{
    	int w; //times of winner
    	int v; //times of visited
    	MachineState state;//Board information
    	double uctValue;
    	double winRate;
    	int depth;
    	Map<MachineState ,Node>children;

    	Node(MachineState state){
    	    this.state=state;
    	    this.w=0;
    	    this.v=0;
    	    this.depth=0;
    	    this.uctValue=0;
    	    this.children= new HashMap<MachineState ,SampleMonteCarloGamer.Node>();
    	}
    	public MachineState getState() {
    		return this.state;
    	}
    	public void setWinValue(int w) {
    		this.w+=w;
    	}
    	public void setdepthValue(int depth) {
    		this.depth+=depth;
    	}
    	public void VisitValueCount() {
    		this.v++;
    	}

    	public void uctCalculation() {
    		double logVisitedValue=Math.log(v);
    		double searchValue=Math.sqrt(logVisitedValue/children.size());
    		uctValue=winRate+searchValue;
    	}
    	public void winRateCalculation() {
    		if(v!=0)
    			winRate=w/v;
    		else
    			System.out.println("error:have not visited");
    	}

    	public void expand(Node n){
    	    this.children.put(n.state,n);
    	}


    }

    public Node root=null;
    public int playerNum=0;

    public int test=0;
    public int apple=7;

    public void MonteCalroPlayout(Node n) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
    	StateMachine theMachine = getStateMachine();
    	Node node=n;
    	Node saveNode=null;
    	MachineState state=n.getState();
    	Deque<Node> que=new ArrayDeque<>();
    	que.push(n);
    	playerNum=getRoleNumber();
    	if(test<=apple) {
    		System.out.println("test:::"+test);
    		System.out.println(n.state);
    	}

    	while(!theMachine.isTerminal(state)) {
    		node=selectChildNode(node);
      		state=node.getState();
      		que.push(node);
      		playerNum=(playerNum+1)%(theMachine.getRoles().size());
      	   	if(test<=apple) {
      	   		System.out.println(node.state);
      	   	}
        }
    	int goalScore=theMachine.getGoal(state, getRole());
    	int ko=0;
    	for (Node v : que) {
    		ko++;
    		Node ns=nodeSearch(root,v.state);


    		if(ns==null && test<=apple)
    			System.out.println("v.state:::"+v.state);
    		int a=0;
    		if(ns!=null && ns!=root) {
    			//System.out.println("ns.state:::"+ns.state);
    			//System.out.println("saveNode:::"+saveNode.state);
    			a=100;
    		}
    		if(saveNode!=null)
    		if(ns!=null && nodeSearch(root,saveNode.state)==null) {
    			saveNode.setdepthValue(ns.depth+1);
    			saveNode.setWinValue(goalScore);
				saveNode.VisitValueCount();
    			ns.expand(saveNode);

				if(test<=apple) {
            		System.out.println("expand!!---ns.expand(saveNode.state)");
            		System.out.println("ns.state:::"+ns.state);
        			System.out.println("saveNode:::"+saveNode.state);
        			System.out.println("vvvisit:"+saveNode.v);
		    		System.out.println("wwwin:::"+saveNode.w);

    	 	   	}

    			if(a==100) {
    			//	showAll(ns);
    			}

    		}
			saveNode=v;
			if(ns!=null) {
				ns.setWinValue(goalScore);
	    		ns.VisitValueCount();

		    	if(test<=apple) {
		    		System.out.println("state:"+ns.state);
		    		System.out.println("visit:"+ns.v);
		    		System.out.println("win:::"+ns.w);
		    	}
			}

		}
     	if(test==apple ||test==40) {
     		System.out.println("test==apple");
     		showAll(root);
     	}
    	test++;
    }

    public Node selectChildNode(Node n) throws MoveDefinitionException, TransitionDefinitionException {
    	Node selectNode=null;
    	if(unexploredState(n)) {
    		double saveValue=0;
    		for(MachineState key:n.children.keySet()) {
    			Node child=n.children.get(key);
    			double uctValue=uctCalculation(n,child);
    			if(uctValue>saveValue) {
    		    	if(test<=apple) {
    		    		System.out.println("!!!change!!!:::"+uctValue);
    		    	}
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

    public int getRoleNumber() {
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
    	if(test<=apple)
    		System.out.println("uctvalue");
    	double logVisitedValue=Math.log(parent.v);
    	double searchValue=0;
    	double C=50.0;

    	if(child.v!=0) {
    		searchValue=Math.sqrt(logVisitedValue/child.v);
    	}else
    		searchValue=Double.MAX_VALUE;
     	child.winRateCalculation();
     	if(test<=apple) {
     		System.out.println("child state:"+child.state);
    		System.out.println("winRate:::::"+child.winRate);
    		System.out.println("searchValue:"+searchValue);
    		System.out.println("parenttimes:"+parent.v);
    		System.out.println("win total:::"+child.w);
    		System.out.println("visit times:"+child.v);
     	}
     	double uctValue=child.winRate+C*searchValue;

    	return uctValue;
    }

    public MachineState getUnexploredState(Node n) throws MoveDefinitionException, TransitionDefinitionException {
    	StateMachine theMachine = getStateMachine();
    	 MachineState nextState;
    	int apple=0;
    	while(true) {
    		List<Move> a=theMachine.getRandomJointMove(n.getState());
            nextState = theMachine.getNextState(n.getState(),a );
            if(!n.children.containsKey(nextState))
           	  break;
          /* if(apple==0) {
    			showAll(root);
           }
    		if(apple<=10) {
    			System.out.println("sadfska::"+n.state);
    			System.out.println("example::"+nextState);
    		}
           apple++;*/

    	}
    	//System.out.println("enf:"+nextState);
    	return nextState;
    }

    public boolean unexploredState(Node n) throws MoveDefinitionException {
    	StateMachine theMachine = getStateMachine();
    	if(n==null)
    		System.out.println("hit");
    	if(n.children==null)
    		return false;

    	int nodeSize=n.children.size();
    	/*System.out.println("test::"+n.state);
    	System.out.println(n.children);
    	System.out.println(nodeSize);*/
    	List<Role> roleList=theMachine.getRoles();
    	Role r=roleList.get(playerNum);
    	List<Move> l=theMachine.getLegalMoves(n.getState(),r);
    	/*System.out.println(l);
    	System.out.println(l.size());*/

    	if(nodeSize==l.size()) {
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
    		System.out.println("*depth---"+n.depth+"  n:::"+n.v+",w:::"+n.w);
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
        return "SampleMonteCarlo_in_git";
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