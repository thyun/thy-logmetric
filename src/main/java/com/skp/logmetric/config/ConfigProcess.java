package com.skp.logmetric.config;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

@Data
public class ConfigProcess {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	JSONArray ja;
	List<ConfigProcessItem> configProcessList = new ArrayList<ConfigProcessItem>();

	public ConfigProcess(JSONArray ja) {
		super();
		this.ja = ja;
		init();
	}

	private void init() {
		for (int i=0; i<ja.size(); i++) {
			JSONObject j = (JSONObject) ja.get(i);
			ConfigProcessItem item = createConfigProcessItem(j);
			if (item != null)
				configProcessList.add(item);
		}
		
	}

	private ConfigProcessItem createConfigProcessItem(JSONObject j) {
		String type = (String) j.get("type");
		if ("match".equals(j.get("type"))) {
			return new ConfigProcessMatch(j);
		} else if ("metrics".equals(type)) {
			return new ConfigProcessMetrics(j);
		}
		return null;
	}

	public void prepare() {
		for (int i=0; i<configProcessList.size(); i++) {
			ConfigProcessItem item = (ConfigProcessItem) configProcessList.get(i);
			item.prepare();
		}
	}
}
