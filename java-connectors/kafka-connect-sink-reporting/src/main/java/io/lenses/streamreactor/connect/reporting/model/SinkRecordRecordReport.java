/*
 * Copyright 2017-2024 Lenses.io Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lenses.streamreactor.connect.reporting.model;

import static io.lenses.streamreactor.common.util.ByteConverters.toBytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.connect.sink.SinkRecord;

/**
 * Class that represents report whether {@link SinkRecord} was sent successfully or not.
 */
@Getter
@Slf4j
public class SinkRecordRecordReport implements RecordReport {

  private final SinkRecord originalRecord;
  private final String sendingStatus;

  public SinkRecordRecordReport(SinkRecord sinkRecord, String sendingStatus) {
    this.originalRecord = sinkRecord;
    this.sendingStatus = sendingStatus;
  }

  //TODO: shall we always send with null as key and use header to indicate key?
  public Optional<ProducerRecord<byte[], String>> produceReportRecord(String reportingTopic) {
    List<Header> recordHeaders;
    try {
      recordHeaders = convertToHeaders(originalRecord);
    } catch (IOException e) {
      log.warn(String.format("Couldn't transform record to Report. Report won't be sent. Topic=%s, Offset=%s",
          originalRecord.originalTopic(), originalRecord.kafkaOffset()));
      return Optional.empty();
    }

    return Optional.of(new ProducerRecord<>(reportingTopic, null,
        null, null, sendingStatus, recordHeaders));
  }

  private List<Header> convertToHeaders(SinkRecord originalRecord) throws IOException {
    List<Header> headers = new ArrayList<>();
    headers.add(new RecordHeader(ReportHeadersConstants.INPUT_TOPIC, toBytes(originalRecord.originalTopic())));
    headers.add(new RecordHeader(ReportHeadersConstants.INPUT_OFFSET, toBytes(originalRecord.originalKafkaOffset())));
    headers.add(new RecordHeader(ReportHeadersConstants.INPUT_TIMESTAMP, toBytes(originalRecord.timestamp())));
    headers.add(new RecordHeader(ReportHeadersConstants.INPUT_KEY, toBytes(originalRecord.key())));
    headers.add(new RecordHeader(ReportHeadersConstants.INPUT_PAYLOAD, toBytes(originalRecord.value())));
    return headers;
  }
}
