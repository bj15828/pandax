package com.gdrc.panda.tran;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.gdrc.panda.PandaException;
import com.google.gson.Gson;

/**
 * 
 * Transaction --> Action --> Result
 * 
 */
public class Transaction implements StateObject {

	public static double DOUBLE_MAX = 9999999999.999999;

	private List<Action> actions;

	private long timestemp;

	public Transaction() {

		actions = new ArrayList();

	}

	public void add(Action action) {

		actions.add(action);
		actions.sort(new ActionCompare());

	}

	public List<Action> actions() {

		return this.actions;
	}

	@Override
	public String toJson() {
		Gson g = new Gson();
		return g.toJson(this);
	}

	@Override
	public byte[] toByte() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fromJson(String json) {
		// TODO Auto-generated method stub

		Gson g = new Gson();
		Transaction a = g.fromJson(json, Transaction.class);

		if (a != null) {
			this.actions = a.actions;

			this.timestemp = a.timestemp;
		}

	}

	@Override
	public void fromByte(byte[] bt) {
		// TODO Auto-generated method stub

	}

	class ActionCompare implements Comparator {

		@Override
		public int compare(Object arg0, Object arg1) {

			Action l = (Action) arg0;
			Action r = (Action) arg1;
			if (l.getSeq() == r.getSeq())
				return 0;
			else if (l.getSeq() > r.getSeq()) {
				return 1;
			} else if (l.getSeq() < r.getSeq())
				return -1;
			return -1;
		}
	}

	public abstract class Action implements Formula {

		protected int seq;

		protected List<Result> results;

		public Action(int seq) {
			this.seq = seq;
			results = new ArrayList();
		}

		public int getSeq() {
			return this.seq;
		}

		public abstract String[] getRefAccount();

		public List<Result> results() {

			return this.results;
		}

	}

	/**
	 * A transfer to B money
	 */
	public class TransferAction extends Action {

		String a;
		String b;
		double money;

		/**
		 * a transfer b
		 * 
		 */
		public TransferAction(String a, String b, double money, int seq) {
			super(seq);
			this.a = a;
			this.b = b;
			this.money = money;

		}

		@Override
		public List<Result> doCalc(Object... args) throws PandaException {

			int i = 0;

			double money1 = Transaction.DOUBLE_MAX;

			double money2 = Transaction.DOUBLE_MAX;

			for (Object arg : args) {

				if (i == 0) {

					money1 = Double.parseDouble(new StringBuffer().append(arg).toString());

				}
				if (i == 1) {
					money2 = Double.parseDouble(new StringBuffer().append(arg).toString());
				}

				i++;

			}

			money1 = money1 - money;
			money2 = money2 + money;

			Result a1 = new Result();
			a1.a = a;
			a1.money = money1;

			Result a2 = new Result();

			a2.a = b;
			a2.money = money2;

			results.add(a1);
			results.add(a2);
			return results;

		}

		@Override
		public String[] getRefAccount() {
			String[] ref = new String[2];
			ref[0] = a;
			ref[1] = b;
			return ref;
		}

	}

	public static enum ACC_TYPE {
		OVER_DRAFT, NON_DRAFT
	};

	/**
	 * Account object
	 * 
	 */
	public class Account {

		String a;
		int type;

	}

	public class AddAction extends Action {

		private String a;
		private double money;

		/**
		 * acct ,actual amount
		 */
		public AddAction(String a, double money, int seq) {
			super(seq);
			this.a = a;
			this.money = money;

		}

		/**
		 * balance amount
		 */
		@Override
		public List<Result> doCalc(Object... args) {

			int i = 0;

			double money1 = Transaction.DOUBLE_MAX;

			for (Object arg : args) {

				if (i == 0) {

					money1 = Double.parseDouble(new StringBuffer().append(arg).toString());

				}

				i++;

			}

			money1 = money1 + money;

			Result a1 = new Result();
			a1.a = a;
			a1.money = money1;
		
			super.results.add(a1);
			

			return super.results;

		}

		@Override
		public String[] getRefAccount() {
			String[] ref = new String[1];
			ref[0] = a;
			return ref;
		}

	}
	
	public class State{
		
		
		
	}

	public class Result {

		String a;
		double money;

		public String toString() {

			return new StringBuffer(a).append("-->").append(money).toString();
		}

		public String getAccount() {
			return this.a;
		}

		public double getMoney() {
			
			
			this.money 
			 = Double.parseDouble(new java.text.DecimalFormat("#.000000").format(this.money));
			return money;
		}

	}

}
