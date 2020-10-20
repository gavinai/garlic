package pers.ai.mysql.binlog.events;

import pers.ai.io.BinaryReader;
import pers.ai.io.ByteBuffer;
import pers.ai.mysql.binlog.BinlogReader;
import pers.ai.mysql.binlog.BinlogVersions;
import pers.ai.mysql.binlog.EventContext;

public class QueryEvent extends EventBase {
  public long SlaveProxyId;
  public long ExecutionTime;
  public int ErrorCode;
  public ByteBuffer StatusVars;
  public ByteBuffer Schema;
  public ByteBuffer Query;

  public QueryEvent() {
    this.StatusVars = new ByteBuffer(0);
    this.Schema = new ByteBuffer(0);
    this.Query = new ByteBuffer(0);
  }

  public EventBase fill(EventContext context, EventHeader header, BinaryReader reader) throws Exception {
    // Post-header
    this.SlaveProxyId = 0xFFFFFFFFL & reader.readInt4L();
    this.ExecutionTime = 0xFFFFFFFFL & reader.readInt4L();
    int schemaLength = 0xFF & reader.readByte();
    this.ErrorCode = 0xFFFF & reader.readInt2L();
    int statusVarsLength;
    if (context.FormatDescription.BinlogVersion >= BinlogVersions.VERSION_4) {
      statusVarsLength = 0xFFFF & reader.readInt2L();
    } else {
      throw new Exception("Not supported.");
    }

    // Payload
    this.StatusVars = BinlogReader.readFixedLengthString(reader, this.StatusVars, statusVarsLength);
    this.Schema = BinlogReader.readFixedLengthString(reader, this.Schema, schemaLength);
    reader.skip(1); // - skip 0
    int remains = (int)header.EventSize - (context.FormatDescription.EventHeaderLength + 4 + 4 + 1 + 2 + 2 + statusVarsLength + schemaLength + 1);
    this.Query = BinlogReader.readFixedLengthString(reader, this.Query, remains);
    return this;
  }

  public String getQuery() {
    return new String(this.Query.getPayload(), 0, this.Query.getLength());
  }
}