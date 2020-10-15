package pers.ai.mysql.binlog;

public class EventTypes {
  public static final byte UNKNOWN_EVENT = 0x00;
  public static final byte START_EVENT_V3 = 0x01;
  public static final byte QUERY_EVENT = 0x02;
  public static final byte STOP_EVENT = 0x03;
  public static final byte ROTATE_EVENT = 0x04;
  public static final byte INTVAR_EVENT = 0x05;
  public static final byte LOAD_EVENT = 0x06;
  public static final byte SLAVE_EVENT = 0x07;
  public static final byte CREATE_FILE_EVENT = 0x08;
  public static final byte APPEND_BLOCK_EVENT = 0x09;
  public static final byte EXEC_LOAD_EVENT = 0x0a;
  public static final byte DELETE_FILE_EVENT = 0x0b;
  public static final byte NEW_LOAD_EVENT = 0x0c;
  public static final byte RAND_EVENT = 0x0d;
  public static final byte USER_VAR_EVENT = 0x0e;
  public static final byte FORMAT_DESCRIPTION_EVENT = 0x0f;
  public static final byte XID_EVENT = 0x10;
  public static final byte BEGIN_LOAD_QUERY_EVENT = 0x11;
  public static final byte EXECUTE_LOAD_QUERY_EVENT = 0x12;
  public static final byte TABLE_MAP_EVENT = 0x13;
  public static final byte WRITE_ROWS_EVENTv0 = 0x14;
  public static final byte UPDATE_ROWS_EVENTv0 = 0x15;
  public static final byte DELETE_ROWS_EVENTv0 = 0x16;
  public static final byte WRITE_ROWS_EVENTv1 = 0x17;
  public static final byte UPDATE_ROWS_EVENTv1 = 0x18;
  public static final byte DELETE_ROWS_EVENTv1 = 0x19;
  public static final byte INCIDENT_EVENT = 0x1a;
  public static final byte HEARTBEAT_EVENT = 0x1b;
  public static final byte IGNORABLE_EVENT = 0x1c;
  public static final byte ROWS_QUERY_EVENT = 0x1d;
  public static final byte WRITE_ROWS_EVENTv2 = 0x1e;
  public static final byte UPDATE_ROWS_EVENTv2 = 0x1f;
  public static final byte DELETE_ROWS_EVENTv2 = 0x20;
  public static final byte GTID_EVENT = 0x21;
  public static final byte ANONYMOUS_GTID_EVENT = 0x22;
  public static final byte PREVIOUS_GTIDS_EVENT = 0x23;

  public static final byte[] EVENTS_V4 = new byte[] {
          START_EVENT_V3 , QUERY_EVENT, STOP_EVENT , ROTATE_EVENT , INTVAR_EVENT , LOAD_EVENT , SLAVE_EVENT, CREATE_FILE_EVENT, APPEND_BLOCK_EVENT , EXEC_LOAD_EVENT, DELETE_FILE_EVENT, NEW_LOAD_EVENT , RAND_EVENT , USER_VAR_EVENT , FORMAT_DESCRIPTION_EVENT , XID_EVENT, BEGIN_LOAD_QUERY_EVENT , EXECUTE_LOAD_QUERY_EVENT , TABLE_MAP_EVENT, WRITE_ROWS_EVENTv0 , UPDATE_ROWS_EVENTv0, DELETE_ROWS_EVENTv0, WRITE_ROWS_EVENTv1 , UPDATE_ROWS_EVENTv1, DELETE_ROWS_EVENTv1, INCIDENT_EVENT , HEARTBEAT_EVENT, IGNORABLE_EVENT, ROWS_QUERY_EVENT , WRITE_ROWS_EVENTv2 , UPDATE_ROWS_EVENTv2, DELETE_ROWS_EVENTv2, GTID_EVENT , ANONYMOUS_GTID_EVENT , PREVIOUS_GTIDS_EVENT
  };
}
