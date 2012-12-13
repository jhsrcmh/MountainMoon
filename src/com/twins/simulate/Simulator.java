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
 * http://introcs.cs.princeton.edu/java/stdlib/ ����ϵͳ��ȷ��λ��(״̬)��
 * ������У���ϵͳ�������׳�����;�������ԣ����� ������������״̬����������������״̬ȷ������ ����һ�� ��̬��ȡ����ķ����������ò�ӷ�ʽ��
 * 
 * @author twins
 * 
 */
public class Simulator {

	/**
	 * ����ά������,��Ϊ״̬���
	 */
	public static ArrayList<Entity> listA = new ArrayList<Entity>();
	public static ArrayList<Entity> listB = new ArrayList<Entity>();
	public static ArrayList<Entity> paiduiA = new ArrayList<Entity>();
	public static ArrayList<Entity> paiduiB = new ArrayList<Entity>();
	public static ArrayList<Entity> list = new ArrayList<Entity>();

	/**
	 * ÿ�θ��¶���״̬��
	 */
	public static double duilieAstate = 0.0;
	public static double duilieBstate = 0.0;
	public static double duilieAllState = 0.0;

	/**
	 * ����ʵ���������ֵ,���ڴ洢��;ֵ
	 */
	public static double arrivetime = 0.0;
	public static double servertime = 0.0;
	public static double waittime = 0.0;
	public static double leavetime = 0.0;
	public static int diudui = 0;

	/**
	 * �޸ı���֮��Ϳ��Ի�ñ�Ҫ�ķ���ģ����
	 * 
	 * @param total
	 *            �ܹ�����ʱ��
	 * @param lambdaA
	 *            ���ڵ���ʱ����������
	 * @param lambdaB
	 *            �绰����ʱ����������
	 * @param aa
	 *            ���ڷ���ʱ������
	 * @param ab
	 *            ���ڷ���ʱ�䷽��
	 * @param ba
	 *            �绰����ʱ������
	 * @param bb
	 *            �绰����ʱ�䷽��
	 */
	public void simulate(int total, double lambdaA, double lambdaB, double aa,
			double ab, double ba, double bb) {

		/**
		 * define list listA, 1 => ���ڵȴ����� define list listB, 0 => �绰�ȴ����� define
		 * list list => �ܷ������
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

			// ����ʵ�嵽��,�ֲ����Ӹ��Ե�ָ���ֲ���
			Entity am = getEntityA(lambdaA, aa, ab);
			Entity bm = getEntityB(lambdaB, ba, bb);

			// �������AΪ��
			if (paiduiA.isEmpty()) {

				// �������BΪ��
				if (paiduiB.isEmpty()) {

					// �ж�˭�ȵ�������ж���
					// am���ȵ���
					if (am.getArriveTime() >= bm.getArriveTime()) {
						// �жϷ�����״̬
						// ���������״̬�У��ж�am��bm�ķ���״̬
						if (am.getArriveTime() >= list.get(list.size() - 1)
								.getLeaveTime()) {

							// am��ʼ����
							server(am);

							// �ж�bm�Ƿ���Ҫ�Ŷ�
							// bm����Ҫ�Ŷ�
							if (bm.getArriveTime() >= list.get(list.size() - 1)
									.getLeaveTime()) {
								server(bm);
							}
							// bm��Ҫ�Ŷ�
							else {
								paiduiB.add(bm);
							}
						}
						// ������æ��am��Ҫ�Ŷӣ�����ʵ�����ȴ����С�
						else {
							paiduiA.add(am);
							paiduiB.add(bm);
						}
					}

					// bm���ȵ���
					else {
						// �ж�������״̬, lazy
						if (bm.getArriveTime() >= list.get(list.size() - 1)
								.getLeaveTime()) {
							server(bm);
							// bm����ʱam��״̬
							if (am.getArriveTime() >= list.get(list.size() - 1)
									.getLeaveTime()) {
								server(am);
							} else {
								paiduiA.add(am);
							}
						}
					}
				}

				// �������B��Ϊ�� ������AΪ��
				else if (paiduiB.size() >= 5) {

					// A�ȵ���
					if (am.getArriveTime() >= bm.getArriveTime()) {

						// ���A����֮ǰ����������,����B����
						if (am.getArriveTime() >= list.get(list.size() - 1)
								.getLeaveTime()) {

							// ��ȡ���Խ��е��м䴦��ʱ��
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
							// ����������������
						} else {
							paiduiA.add(am); // ����Ŷ�
						}
						diudui++;
					}

					// ���B�ȵ���
					else {
						// dealDuilie(am.getJiangeTime(), bm.getJiangeTime());
						if (am.getArriveTime() > list.get(list.size() - 1)
								.getLeaveTime()) {

							// ��ȡ���Խ��е��м䴦��ʱ��,Ȼ�����bm����
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
							// ����������������
						} else {
							paiduiA.add(am); // ����Ŷ�
						}
						diudui++;
					}
				} else {
					// ���A�ȵ���,��bm����listB
					paiduiB.add(bm);
					if (am.getArriveTime() >= bm.getArriveTime()) {
						// ���A����֮ǰ����������,����B����
						if (am.getArriveTime() >= list.get(list.size() - 1)
								.getLeaveTime()) {

							// ��ȡ���Խ��е��м䴦��ʱ��
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
							// ���������������У��������
							// dealDuilie(am.getJiangeTime(),
							// bm.getJiangeTime());
						}

					}
					// ���B�ȵ���
					else {
						// ֱ�ӽ����������
						// dealDuilie(am.getJiangeTime(), bm.getJiangeTime());
						// ���A����֮ǰ����������,����B����
						if (am.getArriveTime() >= list.get(list.size() - 1)
								.getLeaveTime()) {

							// ��ȡ���Խ��е��м䴦��ʱ��
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
							// ���������������У��������
							// dealDuilie(am.getJiangeTime(),
							// bm.getJiangeTime());
						}
					}
				}
			} else if (!paiduiA.isEmpty() && paiduiB.size() <= 5) {
				// �������
				paiduiA.add(am);
				paiduiB.add(bm);
			} else {
				paiduiA.add(am);
			}

			// ��ʵ�����
			// ���д���,������в����еģ�������һ��ѭ��
			dealDuilie(am.getArriveTime()
					- list.get(list.size() - 1).getLeaveTime(),
					bm.getJiangeTime()
							- list.get(list.size() - 1).getLeaveTime());
			i++;
		}

		// ѭ���������������
		dealDuilieA(10000000);
		dealDuilieB(10000000);
		
		//������������з�����д���
		for(Entity entity: list) {
			if (entity.getWaitTime() <= 0) {
				entity.setWaitTime(0.0);
			}
		}
	}

	/**
	 * ����ʵ��
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

		// ����������
		list.add(temp);
	}

	/**
	 * ���д������ﴦ�������������һ�ֳ��󣬽�������ɫ���õ��÷��õ�λ�ô��� Ȼ����ж�������
	 * 
	 * @param jiangeA
	 * @param jiangeB
	 */
	public void dealDuilie(double jiangeA, double jiangeB) {
		// ������ͬʱ��Ϊ��,���ʱ��������0�Ž��иò���
		// ����ΪΪϵͳ��������ʱ��Ľ��
		// ������������洦�����
		if (jiangeA >= 0 && jiangeB >= 0) {
			if (!paiduiA.isEmpty() && !paiduiB.isEmpty()) {

				// ���A�����ʱ���� С�ڶ��н��еķ���ʱ��
				if (jiangeA <= duilieAstate) {
					dealDuilieA(jiangeA); // ����jianggeA��ʱ�������
				} else if (jiangeA > duilieAstate) { // ������Դ������B,����
					dealDuilieA(duilieAstate);

					// �����һ��ʵ�壬����״̬���У��ж���һ��ʵ���Ƿ���Ҫ�ȴ�?
					Entity app = getEntityA(1, 1, 1);
					double sp = jiangeA - duilieAstate;

					// ����B������;A����
					if (sp < app.getJiangeTime()) {
						dealDuilieB(sp);
					} else {
						// ����B�ļ������
						dealDuilieB(sp);
					}
				}
			} else if (!paiduiA.isEmpty() && paiduiB.isEmpty()) {
				if (jiangeA <= duilieAstate) {
					dealDuilieA(jiangeA); // ����jianggeA��ʱ�������
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
	 * �ȴ�����AǨ�� ���������ʱ����
	 * 
	 * @param jiange
	 */
	public static void dealDuilieA(double jiange) {
		double index = 0;
		int i = 0;
		while (index <= jiange && !paiduiA.isEmpty()) {

			// ȷ������ʵ������,ȷ�����в��շ���
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

		// ���µȴ�����A���Ƴ�Ԫ�أ�����״̬��
		for (int j = 0; j < i; j++) {
			paiduiA.remove(0);
		}

		// �������״̬������ÿ��״̬��
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

		// �����ʱ�䣬���м������
		while (index < jiange) {

			// ȷ������ʵ������
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
		// ���µȴ�����B
		for (int j = 0; j < i; j++) {
			paiduiB.remove(0);// �Ƴ�ͷԪ��
		}

		// �������״̬
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
		double jiange = Math.abs(StdRandom.exp(lambda)); // ��������Ϊ���в��Ե�ʱ����

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
		System.out.println("���ڵ��� " + listA.size() + " ��ʵ�嵽��");
		return entity;
	}

	public static Entity getEntityB(double lambda, double mean, double stddev) {
		Entity entity = new Entity();
		double jiange = Math.abs(StdRandom.exp(lambda));
		if ((listB.size() - 1) >= 1) {
			// �ж��Ƿ�Խ��
			entity.setArriveTime(listB.get(listB.size() - 2).getArriveTime()
					+ jiange);
		} else {
			entity.setArriveTime(jiange);
		}
		entity.setServeTime(Math.abs(StdRandom.gaussian(mean, stddev)));
		entity.setJiangeTime(jiange);
		entity.setPriority(0);
		listB.add(entity);
		System.out.println("�绰����++++++�� " + listB.size() + " ��ʵ�嵽��    �ܷ�����г���"
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
			
			// һ��ͳ�ƽ������ͷſռ䣻
			waitmean.add(wmean);
			servermean.add(smean);
			
			//�ͷſռ�
			free();
			
			w = 0;
			s = 0;
		}
		writeList(waitmean, "waitmean.txt");
		writeList(servermean, "serverMean.txt");
	}
}
