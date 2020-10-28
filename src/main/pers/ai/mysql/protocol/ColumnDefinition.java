package pers.ai.mysql.protocol;

public class ColumnDefinition {
  public String Schema, Table, OrgTable, Name, OrgName;
  public short Charset;
  public int ColumnLength;
  public byte DataType;
  public short Flags;
  public byte Decimals;
}
