package pers.ai.mysql.binlog.events;

import pers.ai.io.BinaryReader;

public abstract class EventBase {
  public abstract EventBase fill(EventHeader header, BinaryReader reader) throws Exception;
}