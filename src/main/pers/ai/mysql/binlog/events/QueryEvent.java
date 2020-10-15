package pers.ai.mysql.binlog.events;

import pers.ai.io.BinaryReader;
import pers.ai.io.ByteBuffer;
import pers.ai.mysql.binlog.BinlogReader;
import pers.ai.mysql.binlog.BinlogVersions;
import pers.ai.mysql.binlog.ReadContext;

public class QueryEvent extends EventBase {
  public long SlaveProxyId;
  public long ExecutionTime;
  public int SchemaLength;
  public int ErrorCode;
  public ByteBuffer StatusVars;

  public QueryEvent() {
    this.StatusVars = new ByteBuffer(0);
  }

  public EventBase fill(ReadContext context, EventHeader header, BinaryReader reader) throws Exception {
    // Post-header
    this.SlaveProxyId = 0xFFFFFFFFL & reader.readInt4L();
    this.ExecutionTime = 0xFFFFFFFFL & reader.readInt4L();
    this.SchemaLength = 0xFF & reader.readByte();
    this.ErrorCode = 0xFFFF & reader.readInt2L();
    int length;
    if (context.BinlogVersion >= BinlogVersions.VERSION_4) {
      length = 0xFFFF & reader.readInt2L();
    } else {
      length = 00;// TODO:
    }

    // Payload
    this.StatusVars = BinlogReader.readFixedLengthString(reader, StatusVars, length);

    int remains = (int)header.EventSize - (context.EventHeaderLength);
    reader.skip(remains);
    return this;
  }
}