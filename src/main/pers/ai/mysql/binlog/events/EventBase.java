package pers.ai.mysql.binlog.events;

import pers.ai.io.BinaryReader;
import pers.ai.io.ByteBuffer;
import pers.ai.mysql.binlog.ReadContext;

public abstract class EventBase {
  public int FixedDataLength = 0;
  public abstract EventBase fill(ReadContext context, EventHeader header, BinaryReader reader) throws Exception;
}