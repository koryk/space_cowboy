import java.util.*;

public class MyBot {
    // The DoTurn function is where your code goes. The PlanetWars object
    // contains the state of the game, including information about all planets
    // and fleets that currently exist. Inside this function, you issue orders
    // using the pw.IssueOrder() function. For example, to send 10 ships from
    // planet 3 to planet 8, you would say pw.IssueOrder(3, 8, 10).
    //
    // There is already a basic strategy in place here. You can use it as a
    // starting point, or you can throw it out entirely and replace it with
    // your own. Check out the tutorials and articles on the contest website at
    // http://www.ai-contest.com/resources.
	
	/*
	 * check the fleets in motion, if the enemy fleets focusing on one planet outnumber that planet plus the 
	 * allied fleets then save it!
	 * if enemy fleet is going to nearby planet, calculate total and send there
	 * Figure 
	 */
    public static void DoTurn(PlanetWars pw) {
      	try{
    	HashMap<Integer, Integer> planetPopulation = new HashMap<Integer, Integer>(), allyfleetmap = new HashMap<Integer, Integer>(), enemyfleetmap = new HashMap<Integer, Integer>();
    	for (Fleet f : pw.MyFleets()){
    		if (allyfleetmap.containsKey(f.DestinationPlanet()))
    			allyfleetmap.put(f.DestinationPlanet(), allyfleetmap.get(f.DestinationPlanet())+f.NumShips());
    		else
    			allyfleetmap.put(f.DestinationPlanet(), f.NumShips());
    	}
    	for (Fleet f : pw.EnemyFleets()){
    		if (enemyfleetmap.containsKey(f.DestinationPlanet()))
    			enemyfleetmap.put(f.DestinationPlanet(), enemyfleetmap.get(f.DestinationPlanet())+f.NumShips());
    		else
    			enemyfleetmap.put(f.DestinationPlanet(), f.NumShips());
    	}
    
	// (1) If we currently have a fleet in flight, just do nothing.
	if (pw.MyFleets().size() >= 1 + Math.floor(pw.MyPlanets().size()/2)) {
	    return;
	}
	
	// (2) Find my strongest planet.
	Planet source = null;
	double sourceScore = Double.MIN_VALUE;
	for (Planet p : pw.MyPlanets()) {
	    double score = (double)p.NumShips();
	    if (score > sourceScore) {
		sourceScore = score;
		source = p;
	    }
	}
	// (3) Find the weakest enemy or neutral planet.
	Planet dest = null;
	double destScore = Double.MIN_VALUE;
	for (Planet p : pw.NotMyPlanets()) {
	    double score = 1.0 / (1 + p.NumShips());
	    if (score > destScore) {
		destScore = score;
		dest = p;
	    }
	}
	// find the best size/distance planet 
	// ((size of planet) / (closest owned planet size))*distance
	double minscore = Integer.MAX_VALUE;
	for (Planet p : pw.NotMyPlanets()){
		double score = p.NumShips();		
		int dist, mind = Integer.MAX_VALUE;
		Planet close=null;
		for (Planet pp : pw.MyPlanets()){
			if ((dist = pw.Distance(pp.PlanetID(), p.PlanetID()))<mind){
				mind= dist;
				close=pp;
			}
		}
		score = mind*score/(1+close.NumShips());
		if (score < minscore && close.NumShips()/2>p.NumShips() && !(allyfleetmap.containsKey(p.PlanetID()))){
			dest = p;
			source = close;
			minscore = score;
		}
	}
	// (4) Send half the ships from my strongest planet to the weakest
	// planet that I do not own.
	if (source != null && dest != null && !allyfleetmap.containsKey(dest.PlanetID())) {
	    int numShips = (2*dest.NumShips());
	    if (numShips>source.NumShips())
	    	return;
	    pw.IssueOrder(source, dest, numShips);
	}
    }catch (Exception e){
    		;
    }
    }

    public static void main(String[] args) {
	String line = "";
	String message = "";
	int c;
	try {
	    while ((c = System.in.read()) >= 0) {
		switch (c) {
		case '\n':
		    if (line.equals("go")) {
			PlanetWars pw = new PlanetWars(message);
			DoTurn(pw);
		        pw.FinishTurn();
			message = "";
		    } else {
			message += line + "\n";
		    }
		    line = "";
		    break;
		default:
		    line += (char)c;
		    break;
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
		// Owned.
	}
    }
}

