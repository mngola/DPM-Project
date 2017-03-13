package Testing;

import navigation.Navigation;

public class Navigate extends Thread {
	
	private Navigation navi;

	public Navigate(Navigation navi){
		this.navi = navi;
	}
	
	public void run(){
		navi.travelTo(30.48*2,30.48);
		navi.travelTo(30.48,30.48);
		navi.travelTo(30.48,30.48*2);
		navi.travelTo(30.48*2,0);
		navi.travelTo(0, 0);
		}

}
