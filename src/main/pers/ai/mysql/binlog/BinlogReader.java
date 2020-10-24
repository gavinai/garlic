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
  private EventContext _context;
  private EventHeader _eventHeader;
  private EventBase _currentEvent;
  private ILogger _logger;
  private int _status;

  private BinlogReader(BinaryReader reader) {
    this._reader = reader;
    this._context = new EventContext();
    this._buffer = new byte[19];
    this._eventHeader = new EventHeader();
    this._status = S_NONE;
  }

  public static BinlogReader openFile(String filename) throws Exception {
    BinlogReader r = new BinlogReader(FileReader.open(filename));
    r.verifyFileHeader();
    return r;
  }

  public static ByteBuffer readFixedLengthString(BinaryReader reader, ByteBuffer buffer, int length) throws Exception {
    reader.read(buffer, 0, length);
    return buffer;
  }

  public long getEventTimestamp() {
    return this._eventHeader.Timestamp;
  }

  public long getEventServerId() {
    return this._eventHeader.ServerId;
  }

  public long getEventType() {
    return this._eventHeader.EventType;
  }

  public boolean read() throws Exception {
    this._status = S_NONE;
    if (!this._reader.hasMore()) {
      return false;
    }

    EventHeader header = this._eventHeader;
    try {
      header.Timestamp = 0xFFFFFFFFL & this._reader.readInt4L();
      header.EventType = this._reader.readByte();
      header.ServerId = 0xFFFFFFFFL & this._reader.readInt4L();
      header.EventSize = this._reader.readInt4L();
      if (this._context.FormatDescription == null || this._context.FormatDescription.getBinlogVersion() > 1) {
        header.NextEventPosition = 0xFFFFFFFFL & this._reader.readInt4L();
        header.EventFlag = this._reader.readInt2L();
      } else {
        header.NextEventPosition = 0;
        header.EventFlag = 0;
      }

      this._status = S_EVENT_HEADER;
    } catch (Exception ex) {
      if (this._logger != null) {
        this._logger.log(LoggerLevels.ERROR, "BinlogReader", "Unable to read data. %s", ex.getMessage());
      }
    }

    if (header.EventType == EventTypes.FORMAT_DESCRIPTION_EVENT) {
      this._context.FormatDescription = (FormatDescriptionEvent) this.getEvent();
    }

    return true;
  }

  public EventBase getEvent() throws Exception {
    if (this._status == S_EVENT_HEADER) {
      this._currentEvent = this._context.Events[(int)this._eventHeader.EventType];
      this._currentEvent.fill(this._context, this._eventHeader, this._reader);
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