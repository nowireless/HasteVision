package com.kennedyrobotics.vision;

import java.util.Comparator;

	/**
	 * A structure to hold measurements of a particle
	 * @author Ryan
	 *
	 */
	public class ParticleReport implements Comparator<ParticleReport>, Comparable<ParticleReport>{
		public int index;
		public double percentAreaToImageArea;
		public double area;
		public double boundingRectLeft;
		public double boundingRectTop;
		public double boundingRectRight;
		public double boundingRectBottom;
		public double comX;
		public double comY;
		
		public int compareTo(ParticleReport r)
		{
			return (int)(r.area - this.area);
		}
		
		public int compare(ParticleReport r1, ParticleReport r2)
		{
			return (int)(r1.area - r2.area);
		}
		
		@Override
		public String toString() {
			return "[" + 
					"Index " + index + 
					" Area: " + area + 
					" BRL " + boundingRectTop + 
					" BRT " + boundingRectTop +
					" BRR " + boundingRectRight +
					" BRB " + boundingRectBottom +
					" COMx " + comX +
					" COMy " + comY +
					"]";
		}
	};