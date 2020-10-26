package pers.ai.mysql.binlog.events;

import pers.ai.io.BinaryReader;
import pers.ai.io.ByteBuffer;
import pers.ai.mysql.Version;
import pers.ai.mysql.binlog.EventContext;
import pers.ai.mysql.binlog.EventTypes;

import java.sql.Timestamp;

public class TableMapEvent extends EventBase {
  private long _tableId;

  public TableMapEvent() {
    super(EventTypes.TABLE_MAP_EVENT);
  }

  @Override
  public EventBase fill(EventContext context, EventHeader header, BinaryReader reader) throws Exception {
//    post-header:
//    if post_header_len == 6 {
//      4              table id
//    } else {
//      6              table id
//    }
//    2              flags
//
//    payload:
//    1              schema name length
//    string         schema name
//    1              [00]
//    1              table name length
//    string         table name
//    1              [00]
//    lenenc-int     column-count
//    string.var_len [length=$column-count] column-def
//    lenenc-str     column-meta-def
//    n              NULL-bitmask, length: (column-count + 8) / 7

    int postHeaderLength = context.FormatDescription.getEventTypeHeaderLength(this.getEventType());
    if (postHeaderLength == 6) {
      this._tableId = reader.readInt4L();
    } else {
      this._tableId = reader.readLong6L();
    }

    return this;
  }

  @Override
  public String getDescription() {
    StringBuilder desc = new StringBuilder();
    return desc.toString();
  }
}