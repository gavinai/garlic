package pers.ai.mysql.binlog.events;

import pers.ai.io.BinaryReader;
import pers.ai.io.ByteBuffer;
import pers.ai.mysql.binlog.BinlogReader;
import pers.ai.mysql.binlog.BinlogVersions;
import pers.ai.mysql.binlog.EventContext;
import pers.ai.mysql.binlog.EventTypes;
import java.sql.Timestamp;

public class QueryEvent extends EventBase {
  private static final byte Q_FLAGS2_CODE                     =  0x00;
  private static final byte Q_SQL_MODE_CODE                   =  0x01;
  private static final byte Q_CATALOG                         =  0x02;
  private static final byte Q_AUTO_INCREMENT                  =  0x03;
  private static final byte Q_CHARSET_CODE                    =  0x04;
  private static final byte Q_TIME_ZONE_CODE                  =  0x05;
  private static final byte Q_CATALOG_NZ_CODE                 =  0x06;
  private static final byte Q_LC_TIME_NAMES_CODE              =  0x07;
  private static final byte Q_CHARSET_DATABASE_CODE           =  0x08;
  private static final byte Q_TABLE_MAP_FOR_UPDATE_CODE       =  0x09;
  private static final byte Q_MASTER_DATA_WRITTEN_CODE        =  0x0a;
  private static final byte Q_INVOKERS                        =  0x0b;
  private static final byte Q_UPDATED_DB_NAMES                =  0x0c;
  private static final byte Q_MICROSECONDS                    =  0x0d;
  private static final byte Q_COMMIT_TS                       = 0x0e;
  private static final byte Q_COMMIT_TS2                      = 0x0f;
  private static final byte Q_EXPLICIT_DEFAULTS_FOR_TIMESTAMP = 0x10;
  private static final byte Q_DDL_LOGGED_WITH_XID             = 0x11;
  private static final byte Q_DEFAULT_COLLATION_FOR_UTF8MB4   = 0x12;
  private static final byte Q_SQL_REQUIRE_PRIMARY_KEY         = 0x13;

  // Q_FLAGS2_CODE
  private static final int OPTION_AUTO_IS_NULL                 = 0x00004000;
  private static final int OPTION_NOT_AUTOCOMMIT               = 0x00080000;
  private static final int OPTION_NO_FOREIGN_KEY_CHECKS        = 0x04000000;
  private static final int OPTION_RELAXED_UNIQUE_CHECKS        = 0x08000000;

  // Q_SQL_MODE_CODE
  private static final int MODE_REAL_AS_FLOAT                  = 0x00000001;
  private static final int MODE_PIPES_AS_CONCAT                = 0x00000002;
  private static final int MODE_ANSI_QUOTES                    = 0x00000004;
  private static final int MODE_IGNORE_SPACE                   = 0x00000008;
  private static final int MODE_NOT_USED                       = 0x00000010;
  private static final int MODE_ONLY_FULL_GROUP_BY             = 0x00000020;
  private static final int MODE_NO_UNSIGNED_SUBTRACTION        = 0x00000040;
  private static final int MODE_NO_DIR_IN_CREATE               = 0x00000080;
  private static final int MODE_POSTGRESQL                     = 0x00000100;
  private static final int MODE_ORACLE                         = 0x00000200;
  private static final int MODE_MSSQL                          = 0x00000400;
  private static final int MODE_DB2                            = 0x00000800;
  private static final int MODE_MAXDB                          = 0x00001000;
  private static final int MODE_NO_KEY_OPTIONS                 = 0x00002000;
  private static final int MODE_NO_TABLE_OPTIONS               = 0x00004000;
  private static final int MODE_NO_FIELD_OPTIONS               = 0x00008000;
  private static final int MODE_MYSQL323                       = 0x00010000;
  private static final int MODE_MYSQL40                        = 0x00020000;
  private static final int MODE_ANSI                           = 0x00040000;
  private static final int MODE_NO_AUTO_VALUE_ON_ZERO          = 0x00080000;
  private static final int MODE_NO_BACKSLASH_ESCAPES           = 0x00100000;
  private static final int MODE_STRICT_TRANS_TABLES            = 0x00200000;
  private static final int MODE_STRICT_ALL_TABLES              = 0x00400000;
  private static final int MODE_NO_ZERO_IN_DATE                = 0x00800000;
  private static final int MODE_NO_ZERO_DATE                   = 0x01000000;
  private static final int MODE_INVALID_DATES                  = 0x02000000;
  private static final int MODE_ERROR_FOR_DIVISION_BY_ZERO     = 0x04000000;
  private static final int MODE_TRADITIONAL                    = 0x08000000;
  private static final int MODE_NO_AUTO_CREATE_USER            = 0x10000000;
  private static final int MODE_HIGH_NOT_PRECEDENCE            = 0x20000000;
  private static final int MODE_NO_ENGINE_SUBSTITUTION         = 0x40000000;
  private static final int MODE_PAD_CHAR_TO_FULL_LENGTH        = 0x80000000;

  private long _slaveProxyId;
  private long _executionTime;
  private int _errorCode;
  private StatusVars _statusVars;
  private ByteBuffer _schema;
  private ByteBuffer _query;
  private ByteBuffer _checksum;

  public QueryEvent() {
    super(EventTypes.QUERY_EVENT);
    this._schema = new ByteBuffer(0);
    this._query = new ByteBuffer(0);
    this._checksum = new ByteBuffer(4);
  }

  @Override
  public EventBase fill(EventContext context, EventHeader header, BinaryReader reader) throws Exception {
    // Post-header
    //    4              slave_proxy_id
    //    4              execution time
    //    1              schema length
    //    2              error-code
    //    if binlog-version ≥ 4:
    //    2              status-vars length
    // Payload
    //    string[$len]   status-vars
    //    string[$len]   schema
    //    1              [00]
    //    string[EOF]    query
    //    4              checksum

    // slave_proxy_id
    this._slaveProxyId = 0xFFFFFFFFL & reader.readInt4L();

    // execution time
    this._executionTime = 0xFFFFFFFFL & reader.readInt4L();

    // schema length
    int schemaLength = 0xFF & reader.readByte();

    // error-code
    this._errorCode = 0xFFFF & reader.readInt2L();

    int statusVarsLength;
    if (context.FormatDescription.getBinlogVersion() >= BinlogVersions.VERSION_4) {
      statusVarsLength = 0xFFFF & reader.readInt2L();
    } else {
      throw new Exception("Not supported.");
    }

    int remains = (int)header.EventSize - (context.FormatDescription.getEventHeaderLength() + 4 + 4 + 1 + 2 + 2);

    // Payload
    this._statusVars = this.parseStatusVars(reader, statusVarsLength);
    remains -= statusVarsLength;

    // schema
    this._schema = BinlogReader.readFixedLengthString(reader, this._schema, schemaLength);
    remains -= schemaLength;

    reader.skip(1); // - skip 0
    remains -= 1;

    // query
    int queryLength = context.FormatDescription.getCheckSumType() == FormatDescriptionEvent.CHECK_SUM_NONE ? remains : remains - 4;
    this._query = BinlogReader.readFixedLengthString(reader, this._query, queryLength);

    // checksum
    reader.read(this._checksum, 0, 4);

    return this;
  }

  @Override
  public String getDescription() {
    StringBuilder desc = new StringBuilder();
    desc.append("slave_proxy_id: ").append(this._slaveProxyId).append("\r\n");
    desc.append("execution time: ").append(new Timestamp(this._executionTime * 1000).toGMTString()).append("\r\n");
    desc.append("error-code: ").append(this._errorCode).append("\r\n");
    desc.append("status-vars: ").append("\r\n");
    try {
      desc.append("Q_FLAGS2_CODE: " + this._statusVars.Flags2Code).append("\r\n");
    } catch (Exception ex) {
      desc.append("Unable to parse the status-vars: ").append(ex.getMessage()).append("\r\n");
    }

    desc.append("query: ").append(this._query.toString()).append("\r\n");
    return desc.toString();
  }

  public long getSlaveProxyId() {
    return this._slaveProxyId;
  }

  public long getExecutionTime() {
    return this._executionTime;
  }

  public int getErrorCode() {
    return this._errorCode;
  }

  public String getQuery() {
    return this._query.toString();
  }

  private StatusVars parseStatusVars(BinaryReader reader, int length) throws Exception {
    // TODO: Not done yet.
    StatusVars vars = new StatusVars();
    int consumed = 0;
    for (; consumed < length; ) {
      byte code = reader.readByte();
      consumed += 1;
      switch (code) {
        case Q_FLAGS2_CODE: {
          vars.Flags2Code = reader.readInt4L();
          consumed += 4;
          break;
        }
        case Q_SQL_MODE_CODE: {
          vars.SqlModeCode = reader.readLong8L();
          consumed += 8;
          break;
        }
        case Q_CATALOG: {
          int len = reader.readByte();
          reader.read(vars.Catalog, 0, len);
          consumed += (len + 1);
          break;
        }
        case Q_AUTO_INCREMENT: {
          vars.AutoIncrement = reader.readInt2L();
          vars.AutoIncrementOffset = reader.readInt2L();
          consumed += (2 + 2);
          break;
        }
        case Q_CHARSET_CODE: {
          vars.ClientCharset = reader.readInt2L();
          vars.ConnectionCollation = reader.readInt2L();
          vars.ServerCollation = reader.readInt2L();
          consumed += (2 + 2 + 2);
          break;
        }
        case Q_TIME_ZONE_CODE: {
          int len = reader.readInt2L();
          reader.read(vars.TimeZone, 0, len);
          consumed += (len + 1);
          break;
        }
        case Q_CATALOG_NZ_CODE: {
          int len = reader.readByte();
          reader.read(vars.CatalogNZ, 0, len);
          consumed += (len + 1);
          break;
        }
        case Q_DDL_LOGGED_WITH_XID: {
          vars.DDLLoggedWithXID = reader.readLong8L();
          consumed += 8;
          break;
        }
        case Q_DEFAULT_COLLATION_FOR_UTF8MB4:
          vars.DefaultCollationForUTF8MB4 = reader.readInt2L();
          consumed += 2;
          break;
        default:
          throw new Exception("Unknown Status Vars." + code);
      }
    }

    return vars;
  }

  public class StatusVars {
    public int Flags2Code = 0;
    public long SqlModeCode = 0;
    public ByteBuffer Catalog = new ByteBuffer(0);
    public short AutoIncrement = 0;
    public short AutoIncrementOffset = 0;
    public short ClientCharset, ConnectionCollation, ServerCollation;
    public ByteBuffer TimeZone = new ByteBuffer(0);
    public ByteBuffer CatalogNZ = new ByteBuffer(0);

    public long DDLLoggedWithXID = 0;
    public short DefaultCollationForUTF8MB4 = 0;
  }
}