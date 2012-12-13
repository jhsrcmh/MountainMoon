package com.twins.simulate;
/**
 * Simulate the action of the entity, defines the entity.
 * %w(priority, arriveTime, serverTime)
 * @author twins
 *
 */
public class Entity {
	/**
	 * 初始化状态表，设置等待时间为double waitTime = leaveTime(i) - serveTime(i) - arriveTime(i-1)
	 * if waitTime < 0
	 * 	waitTime = 0;
	 * end
	 */
	private int priority;
	private double serveTime;
	private double arriveTime;
	private double leaveTime;
	private double waitTime;
	private double jiangeTime;
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public double getServeTime() {
		return serveTime;
	}
	public void setServeTime(double serveTime) {
		this.serveTime = serveTime;
	}
	public double getArriveTime() {
		return arriveTime;
	}
	public void setArriveTime(double arriveTime) {
		this.arriveTime = arriveTime;
	}
	public double getLeaveTime() {
		return leaveTime;
	}
	public void setLeaveTime(double leaveTime) {
		this.leaveTime = leaveTime;
	}
	public double getWaitTime() {
		return waitTime;
	}
	public void setWaitTime(double waitTime) {
		this.waitTime = waitTime;
	}
	public double getJiangeTime() {
		return jiangeTime;
	}
	public void setJiangeTime(double jiangeTime) {
		this.jiangeTime = jiangeTime;
	}
	
	
}
