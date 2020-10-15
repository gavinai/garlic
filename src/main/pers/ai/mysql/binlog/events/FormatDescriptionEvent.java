package pers.ai.mysql.binlog.events;

import pers.ai.io.BinaryReader;
import pers.ai.io.ByteBuffer;
import pers.ai.mysql.binlog.ReadContext;

public class FormatDescriptionEvent extends EventBase {
  public short BinlogVersion;
  public ByteBuffer MysqlVersion;
  public long CreateTimestamp;
  public byte EventHeaderLength;
  public ByteBuffer EventTypeHeaderLength;

  public FormatDescriptionEvent() {
    this.MysqlVersion = new ByteBuffer(50);
    this.EventTypeHeaderLength = new ByteBuffer(0);
  }

  public EventBase fill(ReadContext context, EventHeader header, BinaryReader reader) throws Exception {
    this.BinlogVersion = reader.readInt2L();
    reader.read(this.MysqlVersion, 0, 50);
    this.CreateTimestamp = 0xFFFFFFFFL & reader.readInt4L();
    this.EventHeaderLength = reader.readByte();
    int remains = (int)header.EventSize - (this.EventHeaderLength + 2 + 50 + 4 + 1);
    this.EventTypeHeaderLength.inflate(remains);
    reader.read(this.EventTypeHeaderLength, 0, remains);

    // update the context.
    context.BinlogVersion = this.BinlogVersion;
    context.EventHeaderLength = this.EventHeaderLength;
    context.EventTypeHeaderLength = this.EventTypeHeaderLength.getPayload();
    return this;
  }

  public String getMysqlVersion() {
    return new String(MysqlVersion.getPayload(), 0, MysqlVersion.getLength());
  }
}