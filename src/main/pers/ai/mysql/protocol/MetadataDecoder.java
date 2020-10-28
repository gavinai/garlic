package pers.ai.mysql.protocol;

import pers.ai.io.BinaryReader;

public class MetadataDecoder {

  public static long decodeLengthEncodedInteger(BinaryReader reader) throws Exception {
    int flag = 0xFF & reader.readByte();
    if (flag < 251) {
      return flag;
    } else if (flag == 251) {
      throw new Exception("Not support the NULL value in this method.");
    } else if (flag == 252) {
      return 0xFFFFFFFFL & reader.readInt2L();
    } else if (flag == 253) {
      return 0xFFFFFFFFL & reader.readInt3L();
    } else if (flag == 254) {
      return reader.readLong8L();
    } else {
      throw new Exception("Not support the 0xff, it expects as a first byte of an ERR_Packet.");
    }
  }

  public static String decodeLengthEncodedString(BinaryReader reader, String charset) throws Exception {
    int len = (int)decodeLengthEncodedInteger(reader);
    return reader.readString(len, charset);
  }
}
