//Created by Mark Megarry in July 2020 as a debug example for ThreeFalstad
double oldTransform[] = Arrays.copyOf(transform, 6);	//Save transform
Context2d context = cv.getContext2d();	//Create context2d from cv
	for(int elm = 0; elm<elmList.size(); elm++) {
	    	CircuitElm currentElm = elmList.elementAt(elm);	//Select element
	    	String elmText = "Elm " + String.valueOf(elm);	//Create string
	    	context.setTransform(transform[0], transform[1], transform[2],	//Set transform
			    transform[3], transform[4], transform[5]);
		Graphics g=new Graphics(context);	//Create Graphics object
		currentElm.drawValues(g, elmText, 20);	//Display element no. of each elm
		transform = oldTransform;		//Restore global transform var
		context.setTransform(1, 0, 0, 1, 0, 0);	//Restore transform of context
	}
