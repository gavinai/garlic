package pers.ai.mysql.binlog.events;

import pers.ai.io.BinaryReader;
import pers.ai.io.ByteBuffer;
import pers.ai.mysql.binlog.ReadContext;

public class PreviousGtidsEvent extends EventBase {
  public PreviousGtidsEvent() {
  }

  public EventBase fill(ReadContext context, EventHeader header, BinaryReader reader) throws Exception {
    int remains = (int)header.EventSize - (context.EventHeaderLength);
    reader.skip(remains);
    return this;
  }
}