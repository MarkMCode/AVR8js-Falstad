package com.lushprojects.circuitjs1.client;

import com.google.gwt.canvas.dom.client.CanvasGradient;

public class TestElm extends CircuitElm{
    TestElm(int xx, int yy){
	super(xx, yy);
    }
    
   TestElm(int xa, int ya, int xb, int yb, int f,
	    StringTokenizer st){
       super(xa, ya, xb, yb, f);
       
   }
   
   void draw(Graphics g){
       int segments = 16;
	    int i;
	    int ox = 0;
	    //int hs = sim.euroResistorCheckItem.getState() ? 6 : 8;
	    int hs=6;
	    double v1 = volts[0];
	    double v2 = volts[1];
	    setBbox(point1, point2, hs);
	    draw2Leads(g);
	    
	    //   double segf = 1./segments;
	    double len = distance(lead1, lead2);
	    g.context.save();
	    g.context.setLineWidth(3.0);
	    g.context.transform(((double)(lead2.x-lead1.x))/len, ((double)(lead2.y-lead1.y))/len, -((double)(lead2.y-lead1.y))/len,((double)(lead2.x-lead1.x))/len,lead1.x,lead1.y);
	    if (sim.voltsCheckItem.getState() ) {
		CanvasGradient grad = g.context.createLinearGradient(0,0,len,0);
		grad.addColorStop(0, getVoltageColor(g,v1).getHexValue());
		grad.addColorStop(1.0, getVoltageColor(g,v2).getHexValue());
		g.context.setStrokeStyle(grad);
	    } else
		setPowerColor(g, true);
	    if (!sim.euroResistorCheckItem.getState()) {
		g.context.beginPath();
		g.context.moveTo(0,0);
		for (i=0;i<4;i++){
		    g.context.lineTo((1+4*i)*len/16, hs);
		    g.context.lineTo((3+4*i)*len/16, -hs);
		}
		g.context.lineTo(len, 0);
		g.context.stroke();

	    } else {
		g.context.strokeRect(0, -hs, len, 2.0*hs);
	    }
	    g.context.restore();
	    doDots(g);
	    drawPosts(g);
       
   }
 //Calculate lead length when drawn
   void setPoints() {
	super.setPoints();
	calcLeads(8);
   }
   int getDumpType() {
       return 413;
   }

}