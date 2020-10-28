package pers.ai.mysql.protocol;

public class DataTypes {
  public final static byte MYSQL_TYPE_DECIMAL     = 0x00;
  public final static byte MYSQL_TYPE_TINY        = 0x01;
  public final static byte MYSQL_TYPE_SHORT       = 0x02;
  public final static byte MYSQL_TYPE_LONG        = 0x03;
  public final static byte MYSQL_TYPE_FLOAT       = 0x04;
  public final static byte MYSQL_TYPE_DOUBLE      = 0x05;
  public final static byte MYSQL_TYPE_NULL        = 0x06;
  public final static byte MYSQL_TYPE_TIMESTAMP   = 0x07;
  public final static byte MYSQL_TYPE_LONGLONG    = 0x08;
  public final static byte MYSQL_TYPE_INT24       = 0x09;
  public final static byte MYSQL_TYPE_DATE        = 0x0a;
  public final static byte MYSQL_TYPE_TIME        = 0x0b;
  public final static byte MYSQL_TYPE_DATETIME    = 0x0c;
  public final static byte MYSQL_TYPE_YEAR        = 0x0d;
  public final static byte MYSQL_TYPE_NEWDATE     = 0x0e;
  public final static byte MYSQL_TYPE_VARCHAR     = 0x0f;
  public final static byte MYSQL_TYPE_BIT         = 0x10;
  public final static byte MYSQL_TYPE_TIMESTAMP2  = 0x11;
  public final static byte MYSQL_TYPE_DATETIME2   = 0x12;
  public final static byte MYSQL_TYPE_TIME2       = 0x13;
  public final static byte MYSQL_TYPE_NEWDECIMAL  = (byte)0xf6;
  public final static byte MYSQL_TYPE_ENUM        = (byte)0xf7;
  public final static byte MYSQL_TYPE_SET         = (byte)0xf8;
  public final static byte MYSQL_TYPE_TINY_BLOB   = (byte)0xf9;
  public final static byte MYSQL_TYPE_MEDIUM_BLOB = (byte)0xfa;
  public final static byte MYSQL_TYPE_LONG_BLOB   = (byte)0xfb;
  public final static byte MYSQL_TYPE_BLOB        = (byte)0xfc;
  public final static byte MYSQL_TYPE_VAR_STRING  = (byte)0xfd;
  public final static byte MYSQL_TYPE_STRING      = (byte)0xfe;
  public final static byte MYSQL_TYPE_GEOMETRY    = (byte)0xff;
}
