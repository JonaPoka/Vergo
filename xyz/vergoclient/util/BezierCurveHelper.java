package xyz.vergoclient.util;

import java.util.ArrayList;
import java.util.Arrays;

public class BezierCurveHelper {
	
	public BezierCurveHelper(Point... points) {
		addPoints(points);
	}
	
	public Point getPoint() {
		Point point = lerpAllPoints();
		return point;
	}
	
	public void createThirdPoint() {
		if (points.size() != 2) {
			System.out.println("You need to have exactly two points to use this method");
		}
		
		points.add(1, createRotatePoint(points.get(0), points.get(1), 60));
		
//		points.add(1, createRotatePoint(points.get(0), points.get(1), 90));
//		points.add(2, createRotatePoint(points.get(2), points.get(0), 90));
		
	}
	
	public void addProgress(double progress) {
		this.progress += progress;
		if (this.progress > 1)
			this.progress = 1;
		if (this.progress < 0)
			this.progress = 0;
	}
	
	public int getNumberOfPoints() {
		return points.size();
	}
	
	public double getProgress() {
		return progress;
	}
	
	public void addPoints(Point... points) {
		this.points.addAll(Arrays.asList(points));
	}
	
	public void clearPoints() {
		points.clear();
	}
	
	public void clearProgress() {
		progress = 0;
	}
	
	// All the target points
	public ArrayList<Point> points = new ArrayList<>();
	
	// The "t" value, between 0 and 1 and represents the progress
	private double progress = 0;
	
	public static class Point {
		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
		public double x = 0, y = 0;
	}
	
	private Point lerpAllPoints() {
		if (points.size() <= 1) {
			System.err.println("You need at least two points to create a bezier curve");
			return null;
		}
		ArrayList<Point> tempPoints = new ArrayList<>(), otherTempPoints = new ArrayList<>();
		tempPoints.addAll(points);
		Point lastPoint = null;
		while (true) {
			if (!tempPoints.isEmpty())
				lastPoint = null;
			for (Point point : tempPoints) {
				if (lastPoint == null) {
					lastPoint = point;
//					System.out.println(lastPoint + "");
					continue;
				}
				lastPoint = lerp(lastPoint, point);
				otherTempPoints.add(lastPoint);
			}
			tempPoints.clear();
			tempPoints.addAll(otherTempPoints);
			otherTempPoints.clear();
			if (tempPoints.isEmpty()) {
				break;
			}
		}
		return lastPoint;
	}
	
	private Point lerp(Point point0, Point point1) {
		// 1 - t * P_0 + t * P_1
		Point point = new Point(0, 0);
		point.x = ((1 - progress) * point0.x) + (progress * point1.x);
		point.y = ((1 - progress) * point0.y) + (progress * point1.y);
		return point;
	}
	
	private Point createRotatePoint(Point point0, Point point1, float rotate) {
//        double xDiff = point0.x - point1.x;
//        double yDiff = point0.y - point1.y;
//		float rotateOffset = (float) (Math.atan2(yDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
//		if (point0.x < 0) {
//			rotate += rotateOffset;
//		}else {
//			rotate -= rotateOffset;
//		}
//		rotate += rotateOffset;
		double distance = MiscellaneousUtils.get2dDistance(point0.x, point0.y, point1.x, point1.y);
		double x = point0.x + ((Math.cos(Math.toRadians(rotate)) * distance));
		distance = 20;
		double y = point0.y - ((Math.sin(Math.toRadians(rotate)) * distance));
//		y += 500;
		return new Point(x, y);
	}
	
}
