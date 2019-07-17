package raycasting.math;

import raycasting.rendering.RenderMath;

public class Angle {
	private double measure;
	
	private Angle(double measure) {
		this.measure = measure;
		fix();
	}
	
	public static Angle fromDeg(double measure) {
		return new Angle(measure * Math.PI / 180.0);
	}
	
	public static Angle fromRad(double measure) {
		return new Angle(measure);
	}
	
	public static Angle asin(double val) {
		return new Angle(Math.asin(val));
	}
	
	public static Angle acos(double val) {
		return new Angle(Math.acos(val));
	}
	
	public static Angle atan(double val) {
		return new Angle(Math.atan(val));
	}
	
	public static Angle atan2(double x, double y) {
		return new Angle(Math.atan2(y, x));
	}
	
	public double getDeg() {
		return measure * 180.0 / Math.PI;
	}
	
	public double getRad() {
		return measure;
	}
	
	public double sin() {
		return Math.sin(measure);
	}
	
	public double sinh() {
		return Math.sinh(measure);
	}
	
	public double cos() {
		return Math.cos(measure);
	}
	
	public double cosh() {
		return Math.cosh(measure);
	}
	
	public double tan() {
		return Math.tan(measure);
	}
	
	public double tanh() {
		return Math.tanh(measure);
	}
	
	public Angle add(Angle other) {
		return new Angle(measure + other.measure);
	}
	
	public Angle sub(Angle other) {
		return new Angle(measure - other.measure);
	}
	
	public Angle mult(double num) {
		return new Angle(measure*num);
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Angle && Math.abs(((Angle) other).getRad() - getRad()) < RenderMath.EPSILON;
	}
	
	private void fix() {
		measure %= 2 * Math.PI;
		if(measure < 0) {
			measure += 2 * Math.PI;
		}
	}
}
