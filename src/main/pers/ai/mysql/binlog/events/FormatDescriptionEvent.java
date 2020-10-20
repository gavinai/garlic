package pers.ai.mysql.binlog.events;

import pers.ai.io.BinaryReader;
import pers.ai.io.ByteBuffer;
import pers.ai.mysql.Version;
import pers.ai.mysql.binlog.BinlogVersions;
import pers.ai.mysql.binlog.EventContext;

public class FormatDescriptionEvent extends EventBase {
  public static final int CHECK_SUM_NONE = 0;
  public static final int CHECK_SUM_CRC_32 = 1;

  public short BinlogVersion;
  public long CreateTimestamp;
  public int EventHeaderLength;
  public ByteBuffer EventTypeHeaderLength;
  public Version ServerVersion;
  public int ChecksumType;
  public int ChecksumValue;

  public FormatDescriptionEvent() {
    this.BinlogVersion = 0;
    this.EventTypeHeaderLength = new ByteBuffer(0);
    this.EventHeaderLength = 0;
    this.ChecksumType = CHECK_SUM_NONE;
  }

  public EventBase fill(EventContext context, EventHeader header, BinaryReader reader) throws Exception {
    //  -  2                binlog-version
    //  -  string[50]       mysql-server version
    //  -  4                create timestamp
    //  -  1                event header length
    //  -  string[p]        event type header lengths

    // binlog-version
    this.BinlogVersion = reader.readInt2L();

    // mysql-server version
    ByteBuffer version = new ByteBuffer(50);
    reader.read(version, 0, 50);
    this.ServerVersion = this.parseVersionNumbers(version.getPayload());

    // create timestamp
    this.CreateTimestamp = 0xFFFFFFFFL & reader.readInt4L();

    // event header length
    this.EventHeaderLength = reader.readByte();

    // event type header lengths
    int headerLenMapsLength;
    if (this.ServerVersion.compareTo(5, 6, 0) <= 0) {
      // https://dev.mysql.com/doc/refman/5.6/en/replication-options-binary-log.html#sysvar_binlog_checksum
      headerLenMapsLength = (int)header.EventSize - (this.EventHeaderLength + 2 + 50 + 4 + 1) - 4 - 1;
    } else {
      headerLenMapsLength = (int)header.EventSize - (this.EventHeaderLength + 2 + 50 + 4 + 1);
    }
    this.EventTypeHeaderLength.inflate(headerLenMapsLength);
    reader.read(this.EventTypeHeaderLength, 0, headerLenMapsLength);

    // binlog-checksum = {NONE|CRC32}
    this.ChecksumType = reader.readByte();

    // checksum
    this.ChecksumValue = reader.readInt4L();

    return this;
  }

  public Version parseVersionNumbers(byte[] data) {
    int[] numbers = new int[3];
    StringBuilder sb = new StringBuilder(5);
    int gpidx = 0;
    for (int i = 0; i < data.length; i++) {
      byte b = data[i];
      if (b == (byte)0x2E) {
        numbers[gpidx++] = Integer.parseInt(sb.toString());
        sb.setLength(0);
      } else if (b == 0) {
        break;
      } else {
        sb.append((char)b);
      }
    }

    numbers[gpidx++] = Integer.parseInt(sb.toString());
    return new Version(numbers);
  }
}