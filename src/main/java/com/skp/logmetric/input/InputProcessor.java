package com.skp.logmetric.input;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.config.Config;
import com.skp.logmetric.config.ConfigInput;
import com.skp.logmetric.config.ConfigItem;
import com.skp.logmetric.input.kafka.ConfigInputKafka;
import com.skp.logmetric.input.kafka.ConfigInputKafka08;
import com.skp.logmetric.input.kafka.InputKafka;
import com.skp.logmetric.input.kafka.InputKafka08;

import lombok.Data;

@Data
public class InputProcessor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final Config config;
	ArrayList<InputPlugin> inputPluginList = new ArrayList<>();
//	ExecutorService executor;
	
	public InputProcessor(Config config) {
		this.config = config;
	}
	
	public void init() {
		addPlugin();
		for (InputPlugin ip: inputPluginList) {
			ip.init();
		}
	}
	
	public void start() {
		for (InputPlugin ip: inputPluginList) {
			ip.start();
		}
//		int count = inputPluginList.size();
//		executor = Executors.newFixedThreadPool(count);
//		for (InputPlugin ip: inputPluginList) {
//			executor.submit(ip);
//		}
	}
	
	public void stop() {
		for (InputPlugin ip: inputPluginList) {
			ip.stop();
		}
//		executor.shutdown();
	}

	private void addPlugin() {
		ConfigInput configInput = config.getConfigInput();
		for (ConfigItem cp: configInput.getConfigInputList()) {
			if (cp instanceof ConfigInputKafka) {
				inputPluginList.add(new InputKafka((ConfigInputKafka) cp));
			} else if (cp instanceof ConfigInputKafka08) {
				inputPluginList.add(new InputKafka08((ConfigInputKafka08) cp));
			}
		}
	}

}