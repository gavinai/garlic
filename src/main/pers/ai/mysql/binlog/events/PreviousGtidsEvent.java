package pers.ai.mysql.binlog.events;

import pers.ai.io.BinaryReader;
import pers.ai.mysql.binlog.EventContext;
import pers.ai.mysql.binlog.EventTypes;

public class PreviousGtidsEvent extends EventBase {
  public PreviousGtidsEvent() {
    super(EventTypes.PREVIOUS_GTIDS_EVENT);
  }

  @Override
  public EventBase fill(EventContext context, EventHeader header, BinaryReader reader) throws Exception {
    int remains = (int)header.EventSize - (context.FormatDescription.getEventHeaderLength());
    reader.skip(remains);
    return this;
  }

  @Override
  public String getDescription() {
    return null;
  }
}