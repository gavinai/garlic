package pers.ai.mysql.binlog;

import pers.ai.mysql.binlog.events.*;

/**
 * @author  Gavin Ai
 * @version 1.0
 */
public class ReadContext {
  public final EventBase[] Events = new EventBase[] {
          null, //UNKNOWN_EVENT = 0x00;
          null, //START_EVENT_V3 = 0x01;
          new QueryEvent(), //QUERY_EVENT = 0x02;
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
          new AnonymousGtidEvent(), //ANONYMOUS_GTID_EVENT = 0x22;
          new PreviousGtidsEvent(), //PREVIOUS_GTIDS_EVENT = 0x23;
  };

  public int BinlogVersion;
  public int EventHeaderLength;
  public byte[] EventTypeHeaderLength;

  public int getEventTypeHeaderLength(int eventType) {
    if (this.EventTypeHeaderLength != null) {
      return this.EventTypeHeaderLength[eventType - 1];
    }

    return -1;
  }
}