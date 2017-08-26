package com.gdrc.panda.tran;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.gdrc.panda.PandaException;
import com.google.gson.Gson;

/**
 * 
 * Transaction --> Action --> Result
 * 
 */
public class Transaction1 implements StateObject {

	public static double DOUBLE_MAX = 9999999999.999999;

	private List<Action> actions;

	private long timestemp;

	private List<State> in;
	private List<State> out;

	public Transaction1() {
		
		this.out = new ArrayList();
		actions = new ArrayList();
		

	}

	public void addAction(Action action){
		
		this.actions.add(action);
	}
	
	
	/**
	 * 
	 * in + action = out
	 * compute Out List  
	 * @throws PandaException 
	 * 
	 * */
	public void doCalc() throws PandaException{
		
		List<State> finalStates  = new ArrayList();
		
		List<State> result = null;
		for(Iterator<Action> it = actions.iterator(); it.hasNext(); ){
			
			Action a = it.next();
			
			if(it.hasNext())
			{
				
				if(result == null)
					result = a.doCalc(in);
				else
					result = a.doCalc(result);
			}else{
				result = a.doCalc(result);
				out = result;
			}
			
			
		}
		
		
	}
	
	
	

	public List<Action> getActions() {
		return actions;
	}





	public void setActions(List<Action> actions) {
		this.actions = actions;
	}





	public long getTimestemp() {
		return timestemp;
	}





	public void setTimestemp(long timestemp) {
		this.timestemp = timestemp;
	}





	public List<State> getIn() {
		return in;
	}





	public void setIn(List<State> in) {
		this.in = in;
	}





	public List<State> getOut() {
		return out;
	}





	public void setOut(List<State> out) {
		this.out = out;
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
		Transaction1 a = g.fromJson(json, Transaction1.class);

		if (a != null) {
			this.actions = a.actions;
			this.in = a.in;
			this.out = a.out;

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

		protected List<State> results;

		public Action(int seq) {
			this.seq = seq;
			results = new ArrayList();
		}

		public int getSeq() {
			return this.seq;
		}

		public abstract String[] getRefAccount();

		public List<State> results() {

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
		public List<State> doCalc(Object... args) throws PandaException {

			

			double money1 = Transaction1.DOUBLE_MAX;

			double money2 = Transaction1.DOUBLE_MAX;

			if (!(args[0] instanceof List)) {
				throw new PandaException("Input Object should  be notnull or  List<State>");

			}

			List<State> list = (List<State>) args[0];
			
			List<State> listNotChange = new ArrayList();
			listNotChange.addAll(list);
			for (Iterator<State> it = list.iterator(); it.hasNext();) {

				State s = it.next();
				
				if (s.getAccount().equals(a)) {
					money1 = s.getMoney();
					listNotChange.remove(s);
				}
				if (s.getAccount().equals(b)) {
					money2 = s.getMoney();
					listNotChange.remove(s);
				}
				
			}
			

			if (money1 == Transaction1.DOUBLE_MAX || money2 == Transaction1.DOUBLE_MAX) {
				throw new PandaException("Input List<State> is not suitable for Action " + this.toString());
			}

			money1 = money1 - money;
			money2 = money2 + money;

			State a1 = new State();
			a1.a = a;
			a1.money = money1;

			State a2 = new State();

			a2.a = b;
			a2.money = money2;

			results.add(a1);
			results.add(a2);
			
			
			results.addAll(listNotChange);
			return results;

		}

		@Override
		public String[] getRefAccount() {
			String[] ref = new String[2];
			ref[0] = a;
			ref[1] = b;
			return ref;
		}

		public String toString() {

			return this.a + "->" + this.b + ":" + this.money;
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
		public List<State> doCalc(Object... args) throws PandaException {

			int i = 0;
			

			double money1 = Transaction1.DOUBLE_MAX;

			if (!(args[0] instanceof List)) {
				throw new PandaException("Input Object should  be notnull or  List<State>");

			}

			List<State> list = (List<State>) args[0];
			
			List<State> listNotChange = new ArrayList();
			listNotChange.addAll(list);
			

			for (Iterator<State> it = (Iterator) list.iterator(); it.hasNext();) {

				State s = it.next();
				if (s.getAccount().equals(a)) {
					money1 = s.getMoney();
					listNotChange.remove(s);
				}

			}

			if (money1 == Transaction1.DOUBLE_MAX) {
				throw new PandaException("Input List<State> is not suitable for Action :" + this.toString());
			}
			
			money1 = money1 + money;

			State a1 = new State();
			a1.a = a;
			a1.money = money1;

			super.results.add(a1);
			

			results.addAll(listNotChange);

			return super.results;

		}

		@Override
		public String[] getRefAccount() {
			String[] ref = new String[1];
			ref[0] = a;
			return ref;
		}

		public String toString() {

			return "->" + this.a + ":" + this.money;
		}

	}

}
