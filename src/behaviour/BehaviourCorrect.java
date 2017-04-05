package behaviour;

import navigation.Navigation;
import lejos.hardware.Sound;
import lejos.robotics.subsumption.Behavior;

public class BehaviourCorrect implements Behavior {

	@Override
	public boolean takeControl() {
		return Navigation.startCorrect;
	}

	@Override
	public void action() {
		try {
			Sound.twoBeeps();
			Thread.yield();
			Thread.sleep(2000);
		} catch(Exception ex) {
			
		}
	}

	@Override
	public void suppress() {
		Navigation.startCorrect = false;
	}

}
