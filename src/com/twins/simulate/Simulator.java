package com.twins.simulate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import com.twins.Util.*;

/**
 * http://introcs.cs.princeton.edu/java/stdlib/ 进入系统，确定位置(状态)，
 * 清理队列，而系统中则容易出现中途清理，所以，我们 设置两个清理状态，结束服务清理，和状态确定清理。 方法一： 动态获取结果的方法，不采用插队方式。
 * 
 * @author twins
 * 
 */
public class Simulator {

	/**
	 * 申明维护队列,作为状态标记
	 */
	public static ArrayList<Entity> listA = new ArrayList<Entity>();
	public static ArrayList<Entity> listB = new ArrayList<Entity>();
	public static ArrayList<Entity> paiduiA = new ArrayList<Entity>();
	public static ArrayList<Entity> paiduiB = new ArrayList<Entity>();
	public static ArrayList<Entity> list = new ArrayList<Entity>();

	/**
	 * 每次更新队列状态表
	 */
	public static double duilieAstate = 0.0;
	public static double duilieBstate = 0.0;
	public static double duilieAllState = 0.0;

	/**
	 * 声明实体基本属性值,用于存储中途值
	 */
	public static double arrivetime = 0.0;
	public static double servertime = 0.0;
	public static double waittime = 0.0;
	public static double leavetime = 0.0;
	public static int diudui = 0;

	/**
	 * 修改变量之后就可以获得必要的仿真模拟结果
	 * 
	 * @param total
	 *            总共运行时间
	 * @param lambdaA
	 *            窗口到达时间期望倒数
	 * @param lambdaB
	 *            电话到达时间期望倒数
	 * @param aa
	 *            窗口服务时间期望
	 * @param ab
	 *            窗口服务时间方差
	 * @param ba
	 *            电话服务时间期望
	 * @param bb
	 *            电话服务时间方差
	 */
	public void simulate(int total, double lambdaA, double lambdaB, double aa,
			double ab, double ba, double bb) {

		/**
		 * define list listA, 1 => 窗口等待队列 define list listB, 0 => 电话等待队列 define
		 * list list => 总服务队列
		 */
		Entity firstEntity = new Entity();
		firstEntity.setArriveTime(0);
		firstEntity.setJiangeTime(0);

		firstEntity.setServeTime(Math.abs(StdRandom.gaussian(1, 1)));
		firstEntity.setLeaveTime(firstEntity.getServeTime());

		int prority = StdRandom.uniform(2);
		if (prority == 0) {
			firstEntity.setPriority(0);
			listB.add(firstEntity);
		} else {
			firstEntity.setPriority(1);
			listA.add(firstEntity);
		}
		list.add(firstEntity);
		int i = 1;
		while (i < total) {

			// 两个实体到达,分布服从各自的指数分布。
			Entity am = getEntityA(lambdaA, aa, ab);
			Entity bm = getEntityB(lambdaB, ba, bb);

			// 如果队列A为空
			if (paiduiA.isEmpty()) {

				// 如果队列B为空
				if (paiduiB.isEmpty()) {

					// 判断谁先到达，根据判断走
					// am首先到达
					if (am.getArriveTime() >= bm.getArriveTime()) {
						// 判断服务器状态
						// 如果服务器状态闲，判断am和bm的服务状态
						if (am.getArriveTime() >= list.get(list.size() - 1)
								.getLeaveTime()) {

							// am开始服务
							server(am);

							// 判断bm是否需要排队
							// bm不需要排队
							if (bm.getArriveTime() >= list.get(list.size() - 1)
									.getLeaveTime()) {
								server(bm);
							}
							// bm需要排队
							else {
								paiduiB.add(bm);
							}
						}
						// 服务器忙，am需要排队，所有实体进入等待队列。
						else {
							paiduiA.add(am);
							paiduiB.add(bm);
						}
					}

					// bm首先到达
					else {
						// 判定服务器状态, lazy
						if (bm.getArriveTime() >= list.get(list.size() - 1)
								.getLeaveTime()) {
							server(bm);
							// bm服务时am的状态
							if (am.getArriveTime() >= list.get(list.size() - 1)
									.getLeaveTime()) {
								server(am);
							} else {
								paiduiA.add(am);
							}
						}
					}
				}

				// 如果队列B不为空 而队列A为空
				else if (paiduiB.size() >= 5) {

					// A先到达
					if (am.getArriveTime() >= bm.getArriveTime()) {

						// 如果A到达之前服务器空闲,处理B队列
						if (am.getArriveTime() >= list.get(list.size() - 1)
								.getLeaveTime()) {

							// 获取可以进行的中间处理时间
							dealDuilieB(am.getArriveTime()
									- list.get(list.size() - 1).getLeaveTime());
							if (am.getArriveTime() < list.get(list.size() - 1)
									.getLeaveTime()) {
								paiduiA.add(am);
							} else {
								server(am);
							}
						} else if (am.getArriveTime() == list.get(
								list.size() - 1).getLeaveTime()) {
							server(am);
							// 服务结束，清理队列
						} else {
							paiduiA.add(am); // 添加排队
						}
						diudui++;
					}

					// 如果B先到达
					else {
						// dealDuilie(am.getJiangeTime(), bm.getJiangeTime());
						if (am.getArriveTime() > list.get(list.size() - 1)
								.getLeaveTime()) {

							// 获取可以进行的中间处理时间,然后进行bm处理
							dealDuilieB(am.getArriveTime()
									- list.get(list.size() - 1).getLeaveTime());
							if (am.getArriveTime() < list.get(list.size() - 1)
									.getLeaveTime()) {
								paiduiA.add(am);
							} else {
								server(am);
							}
						} else if (am.getArriveTime() == list.get(
								list.size() - 1).getLeaveTime()) {
							server(am);
							// 服务结束，清理队列
						} else {
							paiduiA.add(am); // 添加排队
						}
						diudui++;
					}
				} else {
					// 如果A先到达,放bm进入listB
					paiduiB.add(bm);
					if (am.getArriveTime() >= bm.getArriveTime()) {
						// 如果A到达之前服务器空闲,处理B队列
						if (am.getArriveTime() >= list.get(list.size() - 1)
								.getLeaveTime()) {

							// 获取可以进行的中间处理时间
							dealDuilieB(am.getArriveTime()
									- list.get(list.size() - 1).getLeaveTime());
							if (am.getArriveTime() == list.get(list.size() - 1)
									.getLeaveTime()) {
								server(am);
							} else {
								paiduiA.add(am);
							}
						} else {
							paiduiA.add(am);
							// 服务结束，清理队列，放在最后
							// dealDuilie(am.getJiangeTime(),
							// bm.getJiangeTime());
						}

					}
					// 如果B先到达
					else {
						// 直接进行清零操作
						// dealDuilie(am.getJiangeTime(), bm.getJiangeTime());
						// 如果A到达之前服务器空闲,处理B队列
						if (am.getArriveTime() >= list.get(list.size() - 1)
								.getLeaveTime()) {

							// 获取可以进行的中间处理时间
							dealDuilieB(am.getArriveTime()
									- list.get(list.size() - 1).getLeaveTime());
							if (am.getArriveTime() == list.get(list.size() - 1)
									.getLeaveTime()) {
								server(am);
							} else {
								paiduiA.add(am);
							}
						} else {
							paiduiA.add(am);
							// 服务结束，清理队列，放在最后
							// dealDuilie(am.getJiangeTime(),
							// bm.getJiangeTime());
						}
					}
				}
			} else if (!paiduiA.isEmpty() && paiduiB.size() <= 5) {
				// 各自添加
				paiduiA.add(am);
				paiduiB.add(bm);
			} else {
				paiduiA.add(am);
			}

			// 新实体进入
			// 队列处理,清除所有不该有的，进入下一集循环
			dealDuilie(am.getArriveTime()
					- list.get(list.size() - 1).getLeaveTime(),
					bm.getJiangeTime()
							- list.get(list.size() - 1).getLeaveTime());
			i++;
		}

		// 循环结束，处理队列
		dealDuilieA(10000000);
		dealDuilieB(10000000);
		
		//处理结束，进行服务队列处理。
		for(Entity entity: list) {
			if (entity.getWaitTime() <= 0) {
				entity.setWaitTime(0.0);
			}
		}
	}

	/**
	 * 服务实体
	 * 
	 * @param et
	 */
	public static void server(Entity et) {
		Entity temp = new Entity();

		temp.setArriveTime(et.getArriveTime());
		temp.setPriority(et.getPriority());
		temp.setLeaveTime(list.get(list.size() - 1).getLeaveTime()
				+ et.getServeTime());
		temp.setPriority(et.getPriority());
		temp.setJiangeTime(et.getJiangeTime());
		temp.setWaitTime(temp.getLeaveTime() - temp.getArriveTime());
		temp.setServeTime(et.getServeTime());

		// 加入服务队列
		list.add(temp);
	}

	/**
	 * 队列处理，这里处理队列是这样的一种抽象，将各个角色放置到该放置的位置处， 然后进行队列清理。
	 * 
	 * @param jiangeA
	 * @param jiangeB
	 */
	public void dealDuilie(double jiangeA, double jiangeB) {
		// 两队列同时不为空,间隔时间必须大于0才进行该操作
		// 必须为为系统给出服务时间的结果
		// 在这个方法里面处理参数
		if (jiangeA >= 0 && jiangeB >= 0) {
			if (!paiduiA.isEmpty() && !paiduiB.isEmpty()) {

				// 如果A到达的时间间隔 小于队列进行的服务时间
				if (jiangeA <= duilieAstate) {
					dealDuilieA(jiangeA); // 服务jianggeA的时间段序列
				} else if (jiangeA > duilieAstate) { // 如果可以处理队列B,进行
					dealDuilieA(duilieAstate);

					// 获得下一个实体，放在状态队列，判定下一个实体是否需要等待?
					Entity app = getEntityA(1, 1, 1);
					double sp = jiangeA - duilieAstate;

					// 清理B队列中途A到达
					if (sp < app.getJiangeTime()) {
						dealDuilieB(sp);
					} else {
						// 按照B的间隔处理
						dealDuilieB(sp);
					}
				}
			} else if (!paiduiA.isEmpty() && paiduiB.isEmpty()) {
				if (jiangeA <= duilieAstate) {
					dealDuilieA(jiangeA); // 服务jianggeA的时间段序列
				} else {
					dealDuilieA(duilieAstate);
				}
			} else if (paiduiA.isEmpty() && !paiduiB.isEmpty()) {
				if (jiangeA <= duilieAstate) {
					dealDuilieB(jiangeA);
				} else {
					dealDuilieB(jiangeA);
				}
			}
		}
	}

	/**
	 * 等待队列A迁移 处理参数，时间间隔
	 * 
	 * @param jiange
	 */
	public static void dealDuilieA(double jiange) {
		double index = 0;
		int i = 0;
		while (index <= jiange && !paiduiA.isEmpty()) {

			// 确定各个实体属性,确保队列不空服务。
			if (!paiduiA.isEmpty() && i <= (paiduiA.size() - 1)) {
				if ((paiduiA.get(i).getJiangeTime() + index) <= jiange) {
					server(paiduiA.get(i));
					index += paiduiA.get(i).getServeTime();
					i++;
				} else {
					break;
				}
			} else {
				break;
			}
		}

		// 更新等待队列A，移除元素，更新状态表。
		for (int j = 0; j < i; j++) {
			paiduiA.remove(0);
		}

		// 处理队列状态，更新每个状态表
		if (paiduiA.isEmpty()) {
			duilieAstate = 0.0;
		} else {
			for (Entity entity : paiduiA) {
				duilieAstate += entity.getServeTime();
			}
		}
	}

	public static void dealDuilieB(double jiange) {
		double index = 0;
		int i = 0;

		// 如果有时间，进行间隔处理
		while (index < jiange) {

			// 确定各个实体属性
			if (!paiduiB.isEmpty() && i <= (paiduiB.size() - 1)) {
				if ((paiduiB.get(i).getJiangeTime() + index) <= jiange) {
					server(paiduiB.get(i));
					index += paiduiB.get(i).getJiangeTime();
					i++;
				} else {
					break;
				}
			} else {
				break;
			}
		}
		// 更新等待队列B
		for (int j = 0; j < i; j++) {
			paiduiB.remove(0);// 移除头元素
		}

		// 处理队列状态
		if (paiduiB.isEmpty()) {
			duilieAstate = 0.0;
		} else {
			for (Entity entity : paiduiB) {
				duilieBstate += entity.getServeTime();
			}
		}
	}

	public static Entity getEntityA(double lambda, double mean, double stddev) {
		Entity entity = new Entity();
		double jiange = Math.abs(StdRandom.exp(lambda)); // 到达间隔极为进行测试的时间间隔

		if ((listA.size() - 1) >= 1) {
			entity.setArriveTime(listA.get(listA.size() - 2).getArriveTime()
					+ jiange);
		} else {
			entity.setArriveTime(jiange);
		}
		entity.setServeTime(Math.abs(StdRandom.gaussian(mean, stddev)));
		entity.setJiangeTime(jiange);
		entity.setPriority(1);
		listA.add(entity);
		System.out.println("窗口到达 " + listA.size() + " 个实体到达");
		return entity;
	}

	public static Entity getEntityB(double lambda, double mean, double stddev) {
		Entity entity = new Entity();
		double jiange = Math.abs(StdRandom.exp(lambda));
		if ((listB.size() - 1) >= 1) {
			// 判定是否越界
			entity.setArriveTime(listB.get(listB.size() - 2).getArriveTime()
					+ jiange);
		} else {
			entity.setArriveTime(jiange);
		}
		entity.setServeTime(Math.abs(StdRandom.gaussian(mean, stddev)));
		entity.setJiangeTime(jiange);
		entity.setPriority(0);
		listB.add(entity);
		System.out.println("电话到达++++++第 " + listB.size() + " 个实体到达    总服务对列长度"
				+ list.size());
		return entity;
	}
	public static void getWaitTime(String filename) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					filename)));
			for(Entity entity: list)
				writer.write(Double.toString(entity.getWaitTime())+ '\n');
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void getArriveTime(String filename) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					filename)));
			for(Entity entity: list)
				writer.write(Double.toString(entity.getArriveTime())+ '\n');
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void getServerTime(String filename) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					filename)));
			for(Entity entity: list)
				writer.write(Double.toString(entity.getServeTime())+ '\n');
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void getLeaveTime(String filename) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					filename)));
			for(Entity entity: list)
				writer.write(Double.toString(entity.getLeaveTime())+ '\n');
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void writeList(ArrayList<Double> doublelist, String filename) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					filename)));
			for(Double d: doublelist)
				writer.write(Double.toString(d)+ '\n');
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void free() {
		listA = null;
		listB = null;
		paiduiA = null;
		paiduiB = null;
		list = null;
		listA = new ArrayList<Entity>();
		listB = new ArrayList<Entity>();
		paiduiA = new ArrayList<Entity>();
		paiduiB = new ArrayList<Entity>();
		list = new ArrayList<Entity>();
	}
	public static void main(String args[]) {
		ArrayList<Double> waitmean = new ArrayList<Double>();
		ArrayList<Double> servermean = new ArrayList<Double>();
		double wmean, w = 0.0;
		double smean, s = 0.0;
		for(int i = 0; i < 700; i++)
		{
			Simulator simulator = new Simulator();
			simulator.simulate(1000, 0.1, 0.05, 8, 3, 16, 4);
			for(Entity entity: list)
			{
				w += entity.getWaitTime();
				s += entity.getServeTime();
			}
			wmean = w / list.size();
			smean = s / list.size();
			
			// 一次统计结束，释放空间；
			waitmean.add(wmean);
			servermean.add(smean);
			
			//释放空间
			free();
			
			w = 0;
			s = 0;
		}
		writeList(waitmean, "waitmean.txt");
		writeList(servermean, "serverMean.txt");
	}
}
