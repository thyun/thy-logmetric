package com.skp.logmetric.event;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

@Data
public class MetricEvent extends LogEvent {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	Date createTime;
	Date updateTime;
	int sampling;
	HashMap<String, MetricFieldStats> metricFieldStatsHashMap = new HashMap<>();
	
	public MetricEvent(String tkey, String tvalue) {
		super(tvalue);
		this.put(tkey, tvalue);
		createTime = new Date();
		updateTime = createTime;
	}

	public void sampling() {
		sampling++;
		updateTime = new Date();
	}

	public void stats(String meter, Long o) {
		MetricFieldStats ms = getMetricStats(meter, o);
		ms.apply(o);
	}

	public void stats(String meter, Double o) {
		MetricFieldStats ms = getMetricStats(meter, o);
		ms.apply(o);
		
	}

	public void stats(String meter, String o) {
		MetricFieldStats ms = getMetricStats(meter, o);
		ms.apply(o);
	}

	private MetricFieldStats getMetricStats(String meter, Object value) {
		MetricFieldStats ms = metricFieldStatsHashMap.get(meter);
		if (ms == null) {
			ms = new MetricFieldStats(meter, value);
			metricFieldStatsHashMap.put(meter, ms);
		}
		return ms;
	}
	
	public String export() {
		JSONObject j = new JSONObject();
		j.put("sampling", this.getSampling());
		
		Iterator<String> it = this.keys();
		while (it.hasNext()) {
			String key = it.next();
			j.put(key, this.get(key));
		}
		
		for (MetricFieldStats ms: metricFieldStatsHashMap.values()) {
			ms.export(j);
//			sb.append(" ");
//			sb.append(ms.toString());
		}
		
		return j.toString();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("MetricEvent");
		sb.append(" key=" + this.getKey());
		sb.append(" sampling=" + this.getSampling());
		
		Iterator<String> it = this.keys();
		while (it.hasNext()) {
			String key = it.next();
			sb.append(" " + key + "=" + this.getString(key));
		}
		
		for (MetricFieldStats ms: metricFieldStatsHashMap.values()) {
			sb.append(" ");
			sb.append(ms.toString());
		}
		return sb.toString();
	}

	public boolean afterCreateTime(int secs) {
		Date current = new Date();
		
		long diff = current.getTime() - createTime.getTime();
//		logger.debug("current - createTime=" + diff);
		if (diff > secs * 1000)
			return true;
		return false;
	}

}
