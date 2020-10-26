package pers.ai.mysql.binlog.events;

import pers.ai.io.BinaryReader;
import pers.ai.io.ByteBuffer;
import pers.ai.mysql.binlog.BinlogReader;
import pers.ai.mysql.binlog.BinlogVersions;
import pers.ai.mysql.binlog.EventContext;
import pers.ai.mysql.binlog.EventTypes;
import java.sql.Timestamp;

public class QueryEvent extends EventBase {

  private long _slaveProxyId;
  private long _executionTime;
  private int _errorCode;
  private QueryStatusVar _statusVars;
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

  private QueryStatusVar parseStatusVars(BinaryReader reader, int length) throws Exception {
    // TODO: Not done yet.
    QueryStatusVar vars = new QueryStatusVar();
    int consumed = 0;
    for (; consumed < length; ) {
      byte code = reader.readByte();
      consumed += 1;
      switch (code) {
        case QueryStatusVar.Q_FLAGS2_CODE: {
          vars.Flags2Code = reader.readInt4L();
          consumed += 4;
          break;
        }
        case QueryStatusVar.Q_SQL_MODE_CODE: {
          vars.SqlModeCode = reader.readLong8L();
          consumed += 8;
          break;
        }
        case QueryStatusVar.Q_CATALOG: {
          vars.Catalog = new ByteBuffer(reader.readByte());
          reader.read(vars.Catalog, 0, vars.Catalog.getCapacity());
          consumed += (1 + vars.Catalog.getLength());
          break;
        }
        case QueryStatusVar.Q_AUTO_INCREMENT: {
          vars.AutoIncrement = reader.readInt2L();
          vars.AutoIncrementOffset = reader.readInt2L();
          consumed += (2 + 2);
          break;
        }
        case QueryStatusVar.Q_CHARSET_CODE: {
          vars.ClientCharset = reader.readInt2L();
          vars.ConnectionCollation = reader.readInt2L();
          vars.ServerCollation = reader.readInt2L();
          consumed += (2 + 2 + 2);
          break;
        }
        case QueryStatusVar.Q_TIME_ZONE_CODE: {
          vars.TimeZone = new ByteBuffer(reader.readByte());
          reader.read(vars.TimeZone, 0, vars.TimeZone.getCapacity());
          consumed += (1 + vars.TimeZone.getLength());
          break;
        }
        case QueryStatusVar.Q_CATALOG_NZ_CODE: {
          vars.CatalogNZ = new ByteBuffer(reader.readByte());
          reader.read(vars.CatalogNZ, 0, vars.CatalogNZ.getCapacity());
          consumed += (1 + vars.CatalogNZ.getLength());
          break;
        }
        case QueryStatusVar.Q_LC_TIME_NAMES_CODE: {
          vars.LCTimeNamesCode = reader.readInt2L();
          consumed += 2;
          break;
        }
        case QueryStatusVar.Q_CHARSET_DATABASE_CODE: {
          vars.CharsetDatabaseCode = reader.readInt2L();
          consumed += 2;
          break;
        }
        case QueryStatusVar.Q_TABLE_MAP_FOR_UPDATE_CODE: {
          vars.TableMapForUpdateCode = reader.readLong8L();
          consumed += 8;
          break;
        }
        case QueryStatusVar.Q_MASTER_DATA_WRITTEN_CODE: {
          reader.skip(4);
          consumed += 4;
          break;
        }
        case QueryStatusVar.Q_INVOKERS: {
          vars.Username = new ByteBuffer(reader.readByte());
          reader.read(vars.Username, 0, vars.Username.getCapacity());
          vars.Hostname = new ByteBuffer(reader.readByte());
          reader.read(vars.Hostname, 0, vars.Username.getCapacity());
          consumed += (1 + vars.Username.getLength() + 1 + vars.Hostname.getLength());
          break;
        }
        case QueryStatusVar.Q_UPDATED_DB_NAMES: {
          int len = reader.readByte();
          reader.skip(len);
          consumed += len;
          break;
        }
        case QueryStatusVar.Q_MICROSECONDS: {
          vars.Microseconds = reader.readInt3L();
          consumed += 3;
          break;
        }
        case QueryStatusVar.Q_EXPLICIT_DEFAULTS_FOR_TIMESTAMP: {
          reader.skip(1);
          consumed += 1;
          break;
        }
        case QueryStatusVar.Q_DDL_LOGGED_WITH_XID: {
          vars.DDLLoggedWithXID = reader.readLong8L();
          consumed += 8;
          break;
        }
        case QueryStatusVar.Q_DEFAULT_COLLATION_FOR_UTF8MB4: {
          vars.DefaultCollationForUTF8MB4 = reader.readInt2L();
          consumed += 2;
          break;
        }
        case QueryStatusVar.Q_SQL_REQUIRE_PRIMARY_KEY: {
          reader.skip(1);
          consumed += 1;
          break;
        }

        default:
          throw new Exception("Unknown Status Vars." + code);
      }
    }

    return vars;
  }
}