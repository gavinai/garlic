package pers.ai.mysql.binlog;

public final class EventPreviewArgs {
  public long Timestamp;
  public long ServerId;
  public byte EventType;
  public boolean Ignore;

  EventPreviewArgs() {
    this.Ignore = false;
  }

  EventPreviewArgs renew(long timestamp, byte eventType, long serverId) {
    this.Timestamp = timestamp;
    this.EventType = eventType;
    this.ServerId = serverId;
    this.Ignore = false;
    return this;
  }
}