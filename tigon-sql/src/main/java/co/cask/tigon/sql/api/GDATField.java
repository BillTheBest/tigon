/*
 * Copyright 2014 Cask, Inc.
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

package co.cask.tigon.sql.api;

/**
 * Defines a GDAT Field.
 */
public interface GDATField {
  /**
   * Get the Field Name.
   * @return Field Name.
   */
  String getName();

  /**
   * Get the Field Type.
   * @return Field Type.
   */
  GDATFieldType getType();

  /**
   * Sliding Window Type.
   * @return the Sliding Window Type.
   */
  GDATSlidingWindowAttribute getSlidingWindowType();
}
