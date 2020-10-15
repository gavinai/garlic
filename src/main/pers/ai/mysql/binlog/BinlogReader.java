package pers.ai.mysql.binlog;

import pers.ai.io.BinaryReader;
import pers.ai.io.ByteBuffer;
import pers.ai.io.FileReader;
import pers.ai.logger.ILogger;
import pers.ai.logger.LoggerLevels;
import pers.ai.mysql.binlog.events.*;

/**
 * @author  Gavin Ai
 * @version 1.0
 */
public class BinlogReader {
  private static final int S_NONE = 0;
  private static final int S_EVENT_HEADER = 1;
  private static final int S_EVENT_PAYLOAD = 2;

  private BinaryReader _reader;
  private byte[] _buffer;
  private ReadContext _context;
  private EventHeader _currentEventHeader;
  private EventBase _currentEvent;
  private ILogger _logger;
  private int _status;

  private BinlogReader(BinaryReader reader) {
    this._reader = reader;
    this._context = new ReadContext();
    this._context.BinlogVersion = 0;
    this._buffer = new byte[19];
    this._currentEventHeader = new EventHeader();
    this._status = S_NONE;
  }

  public static BinlogReader openFile(String filename) throws Exception {
    BinlogReader r = new BinlogReader(FileReader.open(filename));
    r.verifyFileHeader();
    return r;
  }

  public static ByteBuffer readFixedLengthString(BinaryReader reader, ByteBuffer buffer, int length) throws Exception {
    buffer.inflate(length);
    reader.read(buffer, 0, length);
    return buffer;
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

  public void setVersion(int ver) {
    this._context.BinlogVersion = ver;
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
      // TODO: the following 2 lines only works for ver 1+.
      this._currentEventHeader.NextEventPosition = 0xFFFFFFFFL & this._reader.readInt4L();
      this._currentEventHeader.EventFlag = this._reader.readInt2L();

      this._status = S_EVENT_HEADER;
      if (this._context.BinlogVersion == 0) {
        // The first event, the FormatDescriptionEvent for ver 4+
        this.readEvent();
      }

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
      this._currentEvent = this._context.Events[(int)this._currentEventHeader.EventType];
      this._currentEvent.fill(this._context, this._currentEventHeader, this._reader);
      this._status = S_EVENT_PAYLOAD;
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