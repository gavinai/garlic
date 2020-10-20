package pers.ai.mysql.binlog.events;

public class QueryStatusVarFlags {
  public static final int Q_FLAGS2_CODE = 0x00;
  public static final int Q_SQL_MODE_CODE = 0x01;
  public static final int Q_CATALOG = 0x02;
  public static final int Q_AUTO_INCREMENT = 0x03;
  public static final int Q_CHARSET_CODE = 0x04;
  public static final int Q_TIME_ZONE_CODE = 0x05;
  public static final int Q_CATALOG_NZ_CODE = 0x06;
  public static final int Q_LC_TIME_NAMES_CODE = 0x07;
  public static final int Q_CHARSET_DATABASE_CODE = 0x08;
  public static final int Q_TABLE_MAP_FOR_UPDATE_CODE = 0x09;
  public static final int Q_MASTER_DATA_WRITTEN_CODE = 0xa;
  public static final int Q_INVOKERS = 0x0B;
  public static final int Q_UPDATED_DB_NAMES = 0x0C;
  public static final int Q_MICROSECONDS = 0x0D;
}
