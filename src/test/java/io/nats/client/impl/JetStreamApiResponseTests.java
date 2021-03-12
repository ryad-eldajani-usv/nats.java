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

package io.nats.client.impl;

import org.junit.jupiter.api.Test;

import static io.nats.client.impl.JetStreamApiResponse.NO_TYPE;
import static io.nats.client.utils.ResourceUtils.dataAsString;
import static org.junit.jupiter.api.Assertions.*;

public class JetStreamApiResponseTests {

    static class TestApiResponse extends JetStreamApiResponse<TestApiResponse> {
        TestApiResponse(String json) {
            super(json);
        }
    }

    @Test
    public void testNotError() {
        TestApiResponse jsApiResp = new TestApiResponse(dataAsString("ConsumerInfo.json"));
        assertFalse(jsApiResp.hasError());
        assertNull(jsApiResp.getError());
    }

    @Test
    public void testErrorResponse() {
        String text = dataAsString("ErrorResponses.json.txt");
        String[] jsons = text.split("~");

        TestApiResponse jsApiResp = new TestApiResponse(jsons[0]);
        assertTrue(jsApiResp.hasError());
        assertEquals("code_and_desc_response", jsApiResp.getType());
        assertEquals(500, jsApiResp.getErrorCode());
        assertEquals("the description", jsApiResp.getDescription());
        assertEquals("the description (500)", jsApiResp.getError());
        JetStreamApiException jsApiEx = new JetStreamApiException(jsApiResp);
        assertEquals(500, jsApiEx.getErrorCode());
        assertEquals("the description", jsApiEx.getErrorDescription());

        jsApiResp = new TestApiResponse(jsons[1]);
        assertTrue(jsApiResp.hasError());
        assertEquals("zero_and_desc_response", jsApiResp.getType());
        assertEquals(0, jsApiResp.getErrorCode());
        assertEquals("the description", jsApiResp.getDescription());
        assertEquals("the description (0)", jsApiResp.getError());
        jsApiEx = new JetStreamApiException(jsApiResp);
        assertEquals(0, jsApiEx.getErrorCode());
        assertEquals("the description", jsApiEx.getErrorDescription());

        jsApiResp = new TestApiResponse(jsons[2]);
        assertTrue(jsApiResp.hasError());
        assertEquals("non_zero_code_only_response", jsApiResp.getType());
        assertEquals(500, jsApiResp.getErrorCode());
        assertNull(jsApiResp.getDescription());
        assertEquals("Unknown JetStream Error (500)", jsApiResp.getError());
        jsApiEx = new JetStreamApiException(jsApiResp);
        assertEquals(500, jsApiEx.getErrorCode());
        assertNull(jsApiEx.getErrorDescription());

        jsApiResp = new TestApiResponse(jsons[3]);
        assertTrue(jsApiResp.hasError());
        assertEquals("no_code_response", jsApiResp.getType());
        assertEquals(TestApiResponse.NOT_SET, jsApiResp.getErrorCode());
        assertEquals("no code", jsApiResp.getDescription());
        assertEquals("no code (-1)", jsApiResp.getError());
        jsApiEx = new JetStreamApiException(jsApiResp);
        assertEquals(-1, jsApiEx.getErrorCode());
        assertEquals("no code", jsApiEx.getErrorDescription());

        jsApiResp = new TestApiResponse(jsons[4]);
        assertTrue(jsApiResp.hasError());
        assertEquals("empty_response", jsApiResp.getType());
        assertEquals(TestApiResponse.NOT_SET, jsApiResp.getErrorCode());
        assertNull(jsApiResp.getDescription());
        assertTrue(jsApiResp.getError().startsWith("Unknown JetStream Error:"));
        jsApiEx = new JetStreamApiException(jsApiResp);
        assertEquals(-1, jsApiEx.getErrorCode());
        assertNull(jsApiEx.getErrorDescription());

        jsApiResp = new TestApiResponse(jsons[5]);
        assertFalse(jsApiResp.hasError());
        assertEquals("not_error_response", jsApiResp.getType());
        jsApiEx = new JetStreamApiException(jsApiResp);
        assertEquals(-1, jsApiEx.getErrorCode());
        assertNull(jsApiEx.getErrorDescription());

        jsApiResp = new TestApiResponse(jsons[6]);
        assertTrue(jsApiResp.hasError());
        assertEquals(NO_TYPE, jsApiResp.getType());
        assertEquals(NO_TYPE, jsApiResp.getType()); // coverage!
    }
}
