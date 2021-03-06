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

package co.cask.tigon.cli;

import com.google.common.util.concurrent.Service;
import org.apache.twill.api.TwillRunResources;

import java.io.File;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Flow Operations.
 */
public interface FlowOperations extends Service {

  /**
   * Starts a Flow.
   * @param jarPath Path to the Flow Jar.
   * @param className Flow ClassName that needs to be started.
   * @param userArgs Map of User Runtime Arguments that can be accessed in the Flow.
   * @param debug Indicates if the Flow needs to be started in debug mode.
   */
  void startFlow(File jarPath, String className, Map<String, String> userArgs, boolean debug);

  /**
   * Get the status of a Flow.
   * @param flowName Name of the Flow.
   * @return {@link State} of Flow.
   */
  State getStatus(String flowName);

  /**
   * List the names of all the Flows currently running.
   * @return List of Flow names.
   */
  List<String> listAllFlows();

  /**
   * Stop a Flow.
   * @param flowName Name of the Flow.
   */
  void stopFlow(String flowName);

  /**
   * Stops a Flow and deletes all the queues associated with the Flow.
   * @param flowName Name of the Flow.
   */
  void deleteFlow(String flowName);

  /**
   * Returns the List of services announced in the Flow.
   * @param flowName Name of the Flow.
   * @return List of Service Names.
   */
  List<String> getServices(String flowName);

  /**
   * Discover the Service endpoints announced in the Flow.
   * @param flowName Name of the Flow.
   * @param service Name of the Service used while announcing the endpoint.
   * @return List of InetSocketAddress
   */
  List<InetSocketAddress> discover(String flowName, String service);

  /**
   * Set the number of instances of Flowlets.
   * @param flowName Name of the Flow.
   * @param flowletName Name of the Flowlet.
   * @param instanceCount
   */
  void setInstances(String flowName, String flowletName, int instanceCount);

  /**
   * Returns the Flowlets in the Flow and the {@link TwillRunResources} of each Flowlet.
   * @param flowName Name of the Flow.
   * @return Map of Flowlet Names and {@link TwillRunResources} of each Flowlet.
   */
  Map<String, Collection<TwillRunResources>> getFlowInfo(String flowName);

  /**
   * Adds a Log Handler to the PrintStream for receiving live logs from the Flow.
   * @param flowName Name of the Flow.
   * @param out PrintStream to which logs need to be sent.
   */
  void addLogHandler(String flowName, PrintStream out);
}
