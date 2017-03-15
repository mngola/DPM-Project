package navigation;

public class ObstacleAvoidance extends Thread{

	FullNavigator nav;
	boolean safe;
	
	public ObstacleAvoidance(FullNavigator navi){
		nav = navi;
		safe = false;
	}
	
	
	public void run(){
		
		/*
		 * The "avoidance" just stops and turns to heading 0
		 * to make sure that the threads are working properly.
		 * 
		 * If you want to call travelTo from this class you
		 * MUST call travelTo(x,y,false) to go around the
		 * state machine
		 * 
		 */
		
		nav.setSpeeds(0, 0);
		nav.turnTo(0,true);
		nav.goForward(5, false); //using false means the Navigation method is used
		safe = true;
	}


	public boolean resolved() {
		return safe;
	}
}
