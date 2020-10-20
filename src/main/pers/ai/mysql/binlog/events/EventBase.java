package pers.ai.mysql.binlog.events;

import pers.ai.io.BinaryReader;
import pers.ai.mysql.binlog.EventContext;

public abstract class EventBase {
  public int FixedDataLength = 0;
  public abstract EventBase fill(EventContext context, EventHeader header, BinaryReader reader) throws Exception;
}