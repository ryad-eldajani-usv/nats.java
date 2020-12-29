// Copyright 2020 The NATS Authors
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.nats.client;


import io.nats.client.ConsumerConfiguration.AckPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubscribeOptionsTests {

    @Test
    public void testBuilder() {
        ConsumerConfiguration cc = new ConsumerConfiguration.Builder().ackPolicy(AckPolicy.All).durable("dur").build();

        SubscribeOptions o = SubscribeOptions.builder().
                attach("foo", "bar").
                configuration("foo", cc).
                pushDirect("pushsubj").
                autoAck(false).durable("durable").pull(1234).
                build();

        assertEquals("foo", o.getStream());
        assertEquals("bar", o.getConsumer());
        assertEquals(1234, o.getPullBatchSize());
        assertEquals("durable", o.getConsumerConfiguration().getDurable());
        assertFalse(o.isAutoAck());
    }

    @Test
    public void testInvalidPullSize() {
        assertThrows(IllegalArgumentException.class, () -> SubscribeOptions.builder().pull(-1));
    }

    @Test
    public void testSetConfigurationNull() {
        assertThrows(IllegalArgumentException.class, () -> SubscribeOptions.builder().build().setConfiguration(null));
    }

    @Test
    public void testCheckStreamName() {
        SubscribeOptions so = SubscribeOptions.builder().build();
        assertThrows(IllegalArgumentException.class, () -> so.setPullDirect(null, "consumer", 1));
        assertThrows(IllegalArgumentException.class, () -> so.setPullDirect("", "consumer", 1));
        assertThrows(IllegalArgumentException.class, () -> so.setPullDirect("stream contains >", "consumer", 1));
        assertThrows(IllegalArgumentException.class, () -> so.setPullDirect("stream contains .", "consumer", 1));
        assertThrows(IllegalArgumentException.class, () -> so.setPullDirect("stream contains *", "consumer", 1));
    }

}