package pers.ai.mysql.binlog;

import pers.ai.io.BinaryReader;
import pers.ai.io.FileReader;
import pers.ai.logger.ILogger;
import pers.ai.logger.LoggerLevels;
import pers.ai.mysql.binlog.events.*;

public class BinlogReader {
  private static final int VERSION_1 = 1;
  private static final int VERSION_2 = 2;
  private static final int VERSION_3 = 3;
  private static final int VERSION_4 = 4;

  private static final int S_NONE = 0;
  private static final int S_EVENT_HEADER = 1;
  private static final int S_EVENT_BODY = 2;

  private final EventBase[] _events = new EventBase[] {
          null, //UNKNOWN_EVENT = 0x00;
          null, //START_EVENT_V3 = 0x01;
          null, //QUERY_EVENT = 0x02;
          null, //STOP_EVENT = 0x03;
          null, //ROTATE_EVENT = 0x04;
          null, //INTVAR_EVENT = 0x05;
          null, //LOAD_EVENT = 0x06;
          null, //SLAVE_EVENT = 0x07;
          null, //CREATE_FILE_EVENT = 0x08;
          null, //APPEND_BLOCK_EVENT = 0x09;
          null, //EXEC_LOAD_EVENT = 0x0a;
          null, //DELETE_FILE_EVENT = 0x0b;
          null, //NEW_LOAD_EVENT = 0x0c;
          null, //RAND_EVENT = 0x0d;
          null, //USER_VAR_EVENT = 0x0e;
          new FormatDescriptionEvent(), //FORMAT_DESCRIPTION_EVENT = 0x0f;
          null, //XID_EVENT = 0x10;
          null, //BEGIN_LOAD_QUERY_EVENT = 0x11;
          null, //EXECUTE_LOAD_QUERY_EVENT = 0x12;
          null, //TABLE_MAP_EVENT = 0x13;
          null, //WRITE_ROWS_EVENTv0 = 0x14;
          null, //UPDATE_ROWS_EVENTv0 = 0x15;
          null, //DELETE_ROWS_EVENTv0 = 0x16;
          null, //WRITE_ROWS_EVENTv1 = 0x17;
          null, //UPDATE_ROWS_EVENTv1 = 0x18;
          null, //DELETE_ROWS_EVENTv1 = 0x19;
          null, //INCIDENT_EVENT = 0x1a;
          null, //HEARTBEAT_EVENT = 0x1b;
          null, //IGNORABLE_EVENT = 0x1c;
          null, //ROWS_QUERY_EVENT = 0x1d;
          null, //WRITE_ROWS_EVENTv2 = 0x1e;
          null, //UPDATE_ROWS_EVENTv2 = 0x1f;
          null, //DELETE_ROWS_EVENTv2 = 0x20;
          null, //GTID_EVENT = 0x21;
          null, //ANONYMOUS_GTID_EVENT = 0x22;
          null, //PREVIOUS_GTIDS_EVENT = 0x23;
  };

  private BinaryReader _reader;
  private IBinlogReaderEvent _event;
  private byte[] _buffer;
  private EventPreviewArgs _previewArgs;
  private int _binlogVersion;
  private EventHeader _currentEventHeader;
  private EventBase _currentEvent;
  private ILogger _logger;
  private int _status;

  private BinlogReader(BinaryReader reader, int ver) {
    this._reader = reader;
    this._binlogVersion = ver;
    this._event = null;
    this._buffer = new byte[19];
    this._previewArgs = new EventPreviewArgs();
    this._currentEventHeader = new EventHeader();
    this._status = S_NONE;
  }

  public static BinlogReader openFile(String filename, int ver) throws Exception {
    BinlogReader r = new BinlogReader(FileReader.open(filename), ver);
    r.verifyFileHeader();
    return r;
  }

  public long getEventTimestamp() {
    return this._currentEventHeader.Timestamp;
  }

  public long getEventServerId() {
    return this._currentEventHeader.ServerId;
  }

  public long getEventType() {
    return this._currentEventHeader.EventType;
  }

  public boolean read() throws Exception {
    this._status = S_NONE;
    if (!this._reader.hasMore()) {
      return false;
    }

    try {
      this._currentEventHeader.Timestamp = 0xFFFFFFFFL & this._reader.readInt4L();
      this._currentEventHeader.EventType = this._reader.readByte();
      this._currentEventHeader.ServerId = 0xFFFFFFFFL & this._reader.readInt4L();
      this._currentEventHeader.EventSize = this._reader.readInt4L();
      if (this._binlogVersion > VERSION_1) {
        this._currentEventHeader.NextEventPosition = 0xFFFFFFFFL & this._reader.readInt4L();
        this._currentEventHeader.EventFlag = this._reader.readInt2L();
      }

      this._status = S_EVENT_HEADER;
      return true;
    } catch (Exception ex) {
      if (this._logger != null) {
        this._logger.log(LoggerLevels.ERROR, "BinlogReader", "Unable to read data. %s", ex.getMessage());
      }

      return false;
    }
  }

  public EventBase readEvent() throws Exception {
    if (this._status == S_EVENT_HEADER) {
      this._currentEvent = this._events[(int)this._currentEventHeader.EventType];
      this._currentEvent.fill(this._currentEventHeader, this._reader);
    }

    return this._currentEvent;
  }

  private void verifyFileHeader() throws Exception {
    int read = this._reader.read(this._buffer, 0, 4);
    if (read != 4) {
      throw new Exception("Invalid Binlog file.");
    }

    if (this._buffer[0] == (byte)0xFE && this._buffer[1] == 0x62 && this._buffer[2] == 0x69 && this._buffer[3] == 0x6E) {
      ; // magic FE'bin'
    } else {
      throw new Exception("Unrecognized file header.");
    }
  }
}