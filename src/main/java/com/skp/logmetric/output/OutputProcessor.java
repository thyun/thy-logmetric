package com.skp.logmetric.output;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.config.Config;
import com.skp.logmetric.config.ConfigInput;
import com.skp.logmetric.config.ConfigItem;
import com.skp.logmetric.config.ConfigOutput;
import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.event.MetricEvent;
import com.skp.logmetric.output.kafka.ConfigOutputKafka;
import com.skp.logmetric.output.kafka.ConfigOutputKafka08;
import com.skp.logmetric.output.kafka.OutputKafka;
import com.skp.logmetric.output.kafka.OutputKafka08;

import lombok.Data;

@Data
public class OutputProcessor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final Config config;
	ArrayList<OutputPlugin> outputPluginList = new ArrayList<>();
	
	public OutputProcessor(Config config) {
		this.config = config;
	}
	
	public void init() {
		addPlugin();
		for (OutputPlugin ip: outputPluginList) {
			ip.init();
		}
	}
	
	public void start() {
		for (OutputPlugin ip: outputPluginList) {
			ip.start();
		}
	}
	
	public void stop() {
		for (OutputPlugin ip: outputPluginList) {
			ip.stop();
		}
	}
	
	public void put(List<LogEvent> elist) {
		for (OutputPlugin op: outputPluginList) {
			try {
				op.getOutputQueue().put(elist);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void addPlugin() {
		ConfigOutput configOutput = config.getConfigOutput();
		for (ConfigItem cp: configOutput.getConfigOutputList()) {
			if (cp instanceof ConfigOutputFile) {
				outputPluginList.add(new OutputFile((ConfigOutputFile) cp));
			} else if (cp instanceof ConfigOutputKafka) {
				outputPluginList.add(new OutputKafka((ConfigOutputKafka) cp));
			} else if (cp instanceof ConfigOutputKafka08) {
				outputPluginList.add(new OutputKafka08((ConfigOutputKafka08) cp));
			}
		}
	}

}
