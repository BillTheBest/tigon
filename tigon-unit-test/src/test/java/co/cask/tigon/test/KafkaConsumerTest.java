/*
 * Copyright © 2014 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package co.cask.tigon.test;

import co.cask.tigon.api.annotation.HashPartition;
import co.cask.tigon.api.annotation.ProcessInput;
import co.cask.tigon.api.flow.Flow;
import co.cask.tigon.api.flow.FlowSpecification;
import co.cask.tigon.api.flow.flowlet.AbstractFlowlet;
import co.cask.tigon.api.flow.flowlet.FlowletContext;
import co.cask.tigon.sql.flowlet.AbstractKafkaConsumerFlowlet;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.apache.twill.internal.kafka.EmbeddedKafkaServer;
import org.apache.twill.internal.kafka.client.ZKKafkaClientService;
import org.apache.twill.internal.utils.Networks;
import org.apache.twill.internal.zookeeper.DefaultZKClientService;
import org.apache.twill.internal.zookeeper.InMemoryZKServer;
import org.apache.twill.kafka.client.Compression;
import org.apache.twill.kafka.client.FetchedMessage;
import org.apache.twill.kafka.client.KafkaPublisher;
import org.apache.twill.zookeeper.ZKClientService;
import org.apache.zookeeper.Watcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class KafkaConsumerTest extends TestBase {
  private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumerTest.class);

  private static InMemoryZKServer zkServer;
  private static EmbeddedKafkaServer kafkaServer;
  private static KafkaPublisher kafkaPublisher;
  private static ZKKafkaClientService  zkKafkaClientService;
  private static ZKClientService zkClientService;
  private static int kafkaPort;

  @BeforeClass
  public static void beforeClass() throws Exception {
    zkServer = InMemoryZKServer.builder().build();
    zkServer.startAndWait();

    Properties kafkaConfig = generateKafkaConfig();
    kafkaServer = new EmbeddedKafkaServer(kafkaConfig);
    kafkaServer.startAndWait();
    kafkaPort = Integer.valueOf(kafkaConfig.getProperty("port"));

    zkClientService = new DefaultZKClientService(zkServer.getConnectionStr(), 10000, null,
                                                                 ImmutableMultimap.<String, byte[]>of());
    zkClientService.startAndWait();
    zkKafkaClientService = new ZKKafkaClientService(zkClientService);
    zkKafkaClientService.startAndWait();
    kafkaPublisher = zkKafkaClientService.getPublisher(KafkaPublisher.Ack.ALL_RECEIVED,
                                                                      Compression.NONE);
    LOG.info("Started kafka server on port {}", kafkaPort);
  }

  @AfterClass
  public static void afterClass() {
    kafkaServer.stopAndWait();
    zkServer.stopAndWait();
    zkKafkaClientService.stopAndWait();
  }

  private static Properties generateKafkaConfig() throws IOException {
    int port = Networks.getRandomPort();
    Preconditions.checkState(port > 0, "Failed to get random port.");

    Properties prop = new Properties();
    prop.setProperty("broker.id", "1");
    prop.setProperty("port", Integer.toString(port));
    prop.setProperty("num.network.threads", "2");
    prop.setProperty("num.io.threads", "2");
    prop.setProperty("socket.send.buffer.bytes", "1048576");
    prop.setProperty("socket.receive.buffer.bytes", "1048576");
    prop.setProperty("socket.request.max.bytes", "104857600");
    prop.setProperty("log.dir", tmpFolder.newFolder().getAbsolutePath());
    prop.setProperty("num.partitions", "2");
    prop.setProperty("log.flush.interval.messages", "10000");
    prop.setProperty("log.flush.interval.ms", "1000");
    prop.setProperty("log.retention.hours", "1");
    prop.setProperty("log.segment.bytes", "536870912");
    prop.setProperty("log.cleanup.interval.mins", "1");
    prop.setProperty("zookeeper.connect", zkServer.getConnectionStr());
    prop.setProperty("zookeeper.connection.timeout.ms", "1000000");

    return prop;
  }

  @Test
  public void test() throws Exception {
    Map<String, String> runtimeArgs = Maps.newHashMap();
    runtimeArgs.put("zkConnectionStr", zkServer.getConnectionStr());
    FlowManager flowManager = deployFlow(TestFlow.class, runtimeArgs);

    TimeUnit.SECONDS.sleep(5);

    flowManager.stop();
  }

  public static final class TestFlow implements Flow {

    @Override
    public FlowSpecification configure() {
      return FlowSpecification.Builder.with()
        .setName("testFlow")
        .setDescription("")
        .withFlowlets()
        .add("kafka", new KafkaConsumer(), 1)
        .add("sink", new SinkFlowlet(), 1)
        .connect()
        .from("kafka").to("sink")
        .build();
    }
  }

  private static final class KafkaConsumer extends AbstractKafkaConsumerFlowlet {

    @Override
    protected void create() {
      setTopic("testTopic");
//      setZKString(zkServer.getConnectionStr());
    }

    @Override
    public void initialize(FlowletContext context) throws Exception {
      setZKString(context.getRuntimeArguments().get("zkConnectionStr"));
      super.initialize(context);
    }

    @Override
    protected void consume(Iterator<FetchedMessage> messages) throws Exception {
      // do nothing
    }
  }

  private static final class SinkFlowlet extends AbstractFlowlet {

    private static final Logger LOG = LoggerFactory.getLogger(SinkFlowlet.class);

    @Override
    public void initialize(FlowletContext context) throws Exception {
      LOG.info("Starting SinkFlowlet.");
    }

    @HashPartition("integer")
    @ProcessInput
    public void process(Integer value) throws Exception {
      LOG.info("Processing event.");
    }
  }

}
