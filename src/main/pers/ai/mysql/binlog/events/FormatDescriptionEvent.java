package pers.ai.mysql.binlog.events;

import pers.ai.io.BinaryReader;
import pers.ai.io.ByteBuffer;
import pers.ai.mysql.Version;
import pers.ai.mysql.binlog.EventContext;
import pers.ai.mysql.binlog.EventTypes;

import java.sql.Timestamp;

public class FormatDescriptionEvent extends EventBase {
  public static final int CHECK_SUM_NONE = 0;
  public static final int CHECK_SUM_CRC_32 = 1;

  private short _binlogVersion;
  private Version _serverVersion;
  private long _createTimestamp;
  private int _eventHeaderLength;
  private ByteBuffer _eventTypeHeaderLength;
  private int _checksumType;
  private int _checksumValue;

  public FormatDescriptionEvent() {
    super(EventTypes.FORMAT_DESCRIPTION_EVENT);
    this._binlogVersion = 0;
    this._createTimestamp = 0;
    this._eventHeaderLength = 0;
    this._eventTypeHeaderLength = new ByteBuffer(0);
    this._checksumType = CHECK_SUM_NONE;
    this._checksumValue = 0;
  }

  @Override
  public EventBase fill(EventContext context, EventHeader header, BinaryReader reader) throws Exception {
    //  -  2                binlog-version
    //  -  string[50]       mysql-server version
    //  -  4                create timestamp
    //  -  1                event header length
    //  -  string[p]        event type header lengths

    // binlog-version
    this._binlogVersion = reader.readInt2L();

    // mysql-server version
    ByteBuffer version = new ByteBuffer(50);
    reader.read(version, 0, 50);
    this._serverVersion = this.parseVersionNumbers(version.getPayload());

    // create timestamp
    this._createTimestamp = 0xFFFFFFFFL & reader.readInt4L();

    // event header length
    this._eventHeaderLength = reader.readByte();

    // event type header lengths
    int headerLenMapsLength;
    if (this._serverVersion.compareTo(5, 6, 0) <= 0) {
      // https://dev.mysql.com/doc/refman/5.6/en/replication-options-binary-log.html#sysvar_binlog_checksum
      headerLenMapsLength = (int)header.EventSize - (this._eventHeaderLength + 2 + 50 + 4 + 1) - 4 - 1;
    } else {
      headerLenMapsLength = (int)header.EventSize - (this._eventHeaderLength + 2 + 50 + 4 + 1);
    }

    reader.read(this._eventTypeHeaderLength, 0, headerLenMapsLength);

    // binlog-checksum = {NONE|CRC32}
    this._checksumType = reader.readByte();

    // checksum
    this._checksumValue = reader.readInt4L();

    return this;
  }

  @Override
  public String getDescription() {
    StringBuilder desc = new StringBuilder();
    desc.append("binlog-version: ").append(this._binlogVersion).append("\r\n");
    desc.append("mysql-server version: ").append(this._serverVersion.Major).append(".").append(this._serverVersion.Minor).append(".").append(this._serverVersion.Revision).append("\r\n");
    desc.append("create timestamp: ").append(new Timestamp(this._createTimestamp * 1000).toGMTString()).append("\r\n");
    desc.append("event header length: ").append(this._eventHeaderLength).append("\r\n");
    desc.append("event type header lengths: ");
    byte[] lengths = this._eventTypeHeaderLength.getPayload();
    for (int i = 0; i < lengths.length; i++) {
      desc.append(String.format("%02X ", lengths[i]));
    }
    desc.append("\r\n");
    desc.append("binlog-checksum: ").append(this._checksumType == CHECK_SUM_CRC_32 ? "CRC32(" : "NONE(").append(this._checksumType).append(")\r\n");
    return desc.toString();
  }

  public short getBinlogVersion() {
    return this._binlogVersion;
  }

  public int getEventHeaderLength() {
    return this._eventHeaderLength;
  }

  public int getEventTypeHeaderLength(int eventType) {
    byte[] data = this._eventTypeHeaderLength.getPayload();
    if (data != null) {
      return data[eventType - 1];
    }

    return -1;
  }

  public int getCheckSumType() {
    return this._checksumType;
  }

  private Version parseVersionNumbers(byte[] data) {
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