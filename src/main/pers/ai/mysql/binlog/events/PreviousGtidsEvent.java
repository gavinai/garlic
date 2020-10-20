package pers.ai.mysql.binlog.events;

import pers.ai.io.BinaryReader;
import pers.ai.mysql.binlog.EventContext;

public class PreviousGtidsEvent extends EventBase {
  public PreviousGtidsEvent() {
  }

  public EventBase fill(EventContext context, EventHeader header, BinaryReader reader) throws Exception {
    int remains = (int)header.EventSize - (context.FormatDescription.EventHeaderLength);
    reader.skip(remains);
    return this;
  }
}