package pers.ai.mysql.binlog.events;

import pers.ai.io.BinaryReader;
import pers.ai.mysql.binlog.EventContext;

public abstract class EventBase {
  public int FixedDataLength = 0;
  private int _eventType;

  protected EventBase(int type) {
    this._eventType = type;
  }

  public int getEventType() {
    return this._eventType;
  }

  public abstract EventBase fill(EventContext context, EventHeader header, BinaryReader reader) throws Exception;

  public abstract String getDescription();
}