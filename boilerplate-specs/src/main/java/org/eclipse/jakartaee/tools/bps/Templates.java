/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.eclipse.jakartaee.tools.bps;

import org.tomitribe.util.collect.ObjectMap;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Templates {

    private static final Pattern PATTERN = Pattern.compile("(\\{)(\\w+)(})");

    public static void interpolate(final ExtractParentPom.Pom pom) {
        final ObjectMap map = new ObjectMap(pom);

        boolean interpolating = true;
        while (interpolating) {
            interpolating = false;
            for (final Map.Entry<String, Object> entry : map.entrySet()) {
                final Object value = entry.getValue();

                final String raw = toString(value);
                final String formatted = format(raw, map);

                if (raw.equals(formatted)) continue;
                if (formatted.contains("{")) continue;

                interpolating = true;
                entry.setValue(formatted);
            }
        }
    }

    public static String toString(final Object value) {
        if (value == null) return "";
        return value.toString();
    }

    static String format(final String input, final Map<String, Object> map) {
        final Matcher matcher = PATTERN.matcher(input);
        final StringBuffer buf = new StringBuffer();
        while (matcher.find()) {
            final String key = matcher.group(2);
            final Object value = map.get(key);
            if (value != null) {
                try {
                    matcher.appendReplacement(buf, toString(value));
                } catch (final Exception e) {
                    //Ignore
                }
            }
        }
        matcher.appendTail(buf);
        return buf.toString();
    }
}
