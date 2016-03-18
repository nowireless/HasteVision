package com.kennedyrobotics.vision;

/**
 * Structure to represent the scores for the various tests used for target identification
 * @author Ryan
 *
 */
public class Scores {
	double area;
	double aspect;
	
	@Override
	public String toString() {
		return "[Aspect: " + aspect + ", Area: " + area + "]";
	}
};