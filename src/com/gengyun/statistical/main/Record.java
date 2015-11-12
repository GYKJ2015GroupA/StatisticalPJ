package com.gengyun.statistical.main;

import java.util.Date;

/**
 * Excel表格中每一条记录抽象成的实体对象 即代表的是一台机器的一次操作【启动、设置频率xxxHZ、切换手动运行、切换自动运行、停止】记录
 * 
 * @author qqzeng
 * 
 */
public class Record implements Comparable<Record> {
	private String name;
	private String opt;
	private Date time;

	public Record(String name, String opt, Date time) {
		super();
		this.name = name;
		this.opt = opt;
		this.time = time;
	}

	public Record() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "Record [name=" + name + ", opt=" + opt + ", time="
				+ time + "]";
	}

	@Override
	public int compareTo(Record o) {
		if(this.getTime().getTime() > o.getTime().getTime()){
			return 1;
		}else if(this.getTime().getTime() < o.getTime().getTime()){
			return -1;
		}else{
			if(!this.getName().equals(o.getName())){
				return this.getName().compareTo(o.getName());
			}else{
				return this.getOpt().compareTo(o.getOpt());
			}
		}
	//return (int) (((this.getTime().getTime() - o.getTime().getTime()) % Integer.MAX_VALUE));
	}

	
}
