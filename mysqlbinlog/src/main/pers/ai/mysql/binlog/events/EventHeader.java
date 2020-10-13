package pers.ai.mysql.binlog.events;

public class EventHeader {
  public long Timestamp;
  public long ServerId;
  public byte EventType;
  public long EventSize;
  public long NextEventPosition;
  public short EventFlag;

  public EventHeader() {
    this.Timestamp = 0L;
    this.ServerId = -1L;
    this.EventType = -1;
    this.EventSize = 0;
    this.NextEventPosition = -1;
    this.EventFlag = 0;
  }
}
