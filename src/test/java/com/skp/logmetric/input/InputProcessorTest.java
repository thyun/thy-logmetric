package com.skp.logmetric.input;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.GeneralConsumerTest;
import com.skp.logmetric.config.Config;
import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.input.kafka.GeneralConsumer;
import com.skp.logmetric.input.kafka.InputKafka;
import com.skp.logmetric.input.kafka.InputKafka08;
import com.skp.logmetric.process.ProcessQueueBulk;
import com.skp.util.ResourceHelper;

public class InputProcessorTest {
	private static final Logger logger = LoggerFactory.getLogger(InputProcessorTest.class);
	
	@Before
	public void setUp() {
		ProcessQueueBulk.getInstance().clear();
	}
	
	@Test
	public void testInputKafka() throws IOException, ParseException, InterruptedException {
		// Get config
		String input = ResourceHelper.getResourceString("process.conf");
		Config config = Config.create(input);
		
	    // Create InputProcessor
	    InputProcessor iprocess = new InputProcessor(config);
	    iprocess.init();
	    
	    // Get InputKafka & GeneralConsumer
	    InputKafka inputKafka = (InputKafka) iprocess.getInputPluginList().get(0);	// Assume 1 input plugin
	    GeneralConsumer gconsumer = inputKafka.getConsumerList().get(0);		// Assume 1 GeneralConsumer
	    String topic = inputKafka.getConfig().getTopic();
	    
	    // Apply MockConsumer 
	    MockConsumer<String, String> mockConsumer = createMockConsumer();
	    gconsumer.applyMockConsumer(mockConsumer, topic);
	    
	    // Generate sample data
	    GeneralConsumerTest.generateSampleJson(mockConsumer, topic);
	    
	    // Consume
	    gconsumer.consume();
	    List<LogEvent> elist = ProcessQueueBulk.getInstance().take();
	    assertEquals(200, elist.size());
	}

	private MockConsumer<String, String> createMockConsumer() {
		MockConsumer<String, String> mockConsumer = new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST);
	    return mockConsumer;
	}
	
	@Test
	public void testInputKafka08() throws IOException, ParseException, InterruptedException {
		// Get config
		String input = ResourceHelper.getResourceString("process08.conf");
		Config config = Config.create(input);
		
	    // Create InputProcessor
	    InputProcessor iprocess = new InputProcessor(config);
	    iprocess.init();
	    
	    // Get InputKafka & GeneralConsumer
	    InputKafka08 inputKafka = (InputKafka08) iprocess.getInputPluginList().get(0);	// Assume 1 input plugin
//	    GeneralConsumer gconsumer = inputKafka.getConsumerList().get(0);		// Assume 1 GeneralConsumer
	    String topic = inputKafka.getConfig().getTopic();
	    
	    // Apply MockConsumer 
//	    MockConsumer<String, String> mockConsumer = createMockConsumer();
//	    gconsumer.applyMockConsumer(mockConsumer, topic);
	    
	    // Generate sample data
//	    GeneralConsumerTest.generateSampleJson(mockConsumer, topic);
	    
	    // Consume
	    iprocess.start();
	    Thread.sleep(5000);
	    iprocess.stop();
	    logger.debug("ProcessQueue size=" + ProcessQueueBulk.getInstance().size());
	    List<LogEvent> elist = ProcessQueueBulk.getInstance().take();
	    for (LogEvent e: elist)
	    	logger.debug("e=" + e);
//	    gconsumer.consume();
//	    assertEquals(200, ProcessQueue.getInstance().size());
	}

}
