package pers.ai.mysql.binlog.events;

import pers.ai.io.ByteBuffer;

public class QueryStatusVar {
  public static final byte Q_FLAGS2_CODE                     =  0x00;
  public static final byte Q_SQL_MODE_CODE                   =  0x01;
  public static final byte Q_CATALOG                         =  0x02;
  public static final byte Q_AUTO_INCREMENT                  =  0x03;
  public static final byte Q_CHARSET_CODE                    =  0x04;
  public static final byte Q_TIME_ZONE_CODE                  =  0x05;
  public static final byte Q_CATALOG_NZ_CODE                 =  0x06;
  public static final byte Q_LC_TIME_NAMES_CODE              =  0x07;
  public static final byte Q_CHARSET_DATABASE_CODE           =  0x08;
  public static final byte Q_TABLE_MAP_FOR_UPDATE_CODE       =  0x09;
  public static final byte Q_MASTER_DATA_WRITTEN_CODE        =  0x0a;
  public static final byte Q_INVOKERS                        =  0x0b;
  public static final byte Q_UPDATED_DB_NAMES                =  0x0c;
  public static final byte Q_MICROSECONDS                    =  0x0d;
  public static final byte Q_COMMIT_TS                       =  0x0e;
  public static final byte Q_COMMIT_TS2                      =  0x0f;
  public static final byte Q_EXPLICIT_DEFAULTS_FOR_TIMESTAMP =  0x10;
  public static final byte Q_DDL_LOGGED_WITH_XID             =  0x11;
  public static final byte Q_DEFAULT_COLLATION_FOR_UTF8MB4   =  0x12;
  public static final byte Q_SQL_REQUIRE_PRIMARY_KEY         =  0x13;
  
  // Q_FLAGS2_CODE
  public static final int OPTION_AUTO_IS_NULL                 = 0x00004000;
  public static final int OPTION_NOT_AUTOCOMMIT               = 0x00080000;
  public static final int OPTION_NO_FOREIGN_KEY_CHECKS        = 0x04000000;
  public static final int OPTION_RELAXED_UNIQUE_CHECKS        = 0x08000000;

  // Q_SQL_MODE_CODE
  public static final int MODE_REAL_AS_FLOAT                  = 0x00000001;
  public static final int MODE_PIPES_AS_CONCAT                = 0x00000002;
  public static final int MODE_ANSI_QUOTES                    = 0x00000004;
  public static final int MODE_IGNORE_SPACE                   = 0x00000008;
  public static final int MODE_NOT_USED                       = 0x00000010;
  public static final int MODE_ONLY_FULL_GROUP_BY             = 0x00000020;
  public static final int MODE_NO_UNSIGNED_SUBTRACTION        = 0x00000040;
  public static final int MODE_NO_DIR_IN_CREATE               = 0x00000080;
  public static final int MODE_POSTGRESQL                     = 0x00000100;
  public static final int MODE_ORACLE                         = 0x00000200;
  public static final int MODE_MSSQL                          = 0x00000400;
  public static final int MODE_DB2                            = 0x00000800;
  public static final int MODE_MAXDB                          = 0x00001000;
  public static final int MODE_NO_KEY_OPTIONS                 = 0x00002000;
  public static final int MODE_NO_TABLE_OPTIONS               = 0x00004000;
  public static final int MODE_NO_FIELD_OPTIONS               = 0x00008000;
  public static final int MODE_MYSQL323                       = 0x00010000;
  public static final int MODE_MYSQL40                        = 0x00020000;
  public static final int MODE_ANSI                           = 0x00040000;
  public static final int MODE_NO_AUTO_VALUE_ON_ZERO          = 0x00080000;
  public static final int MODE_NO_BACKSLASH_ESCAPES           = 0x00100000;
  public static final int MODE_STRICT_TRANS_TABLES            = 0x00200000;
  public static final int MODE_STRICT_ALL_TABLES              = 0x00400000;
  public static final int MODE_NO_ZERO_IN_DATE                = 0x00800000;
  public static final int MODE_NO_ZERO_DATE                   = 0x01000000;
  public static final int MODE_INVALID_DATES                  = 0x02000000;
  public static final int MODE_ERROR_FOR_DIVISION_BY_ZERO     = 0x04000000;
  public static final int MODE_TRADITIONAL                    = 0x08000000;
  public static final int MODE_NO_AUTO_CREATE_USER            = 0x10000000;
  public static final int MODE_HIGH_NOT_PRECEDENCE            = 0x20000000;
  public static final int MODE_NO_ENGINE_SUBSTITUTION         = 0x40000000;
  public static final int MODE_PAD_CHAR_TO_FULL_LENGTH        = 0x80000000;

  public int Flags2Code = 0;
  public long SqlModeCode = 0;
  public ByteBuffer Catalog;
  public short AutoIncrement = 0;
  public short AutoIncrementOffset = 0;
  public short ClientCharset, ConnectionCollation, ServerCollation;
  public ByteBuffer TimeZone;
  public ByteBuffer CatalogNZ;
  public short LCTimeNamesCode = 0;
  public short CharsetDatabaseCode = 0;
  public long TableMapForUpdateCode = 0;
  public ByteBuffer Username, Hostname;
  public int Microseconds;
  public long DDLLoggedWithXID = 0;
  public short DefaultCollationForUTF8MB4 = 0;
}
