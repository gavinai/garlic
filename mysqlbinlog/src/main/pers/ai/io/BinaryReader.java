package pers.ai.io;

import java.io.IOException;

public abstract class BinaryReader {
  private byte[] _buffer;

  protected BinaryReader() {
    this._buffer = new byte[8];
  }

  public abstract boolean hasMore();

  public abstract int read(byte[] buffer, int offset, int length) throws IOException;

  public abstract void close() throws Exception;

  public abstract void skip(long length) throws Exception;

  public int read(ByteBuffer buffer, int offset, int length) throws Exception {
    buffer.setLength(offset + length);
    return this.read(buffer.getPayload(), offset, length);
  }

  public byte readByte() throws IOException {
    this.read(this._buffer, 0, 1);
    return this._buffer[0];
  }

  public short readInt2L() throws IOException {
    this.read(this._buffer, 0, 2);
    return (short)((this._buffer[1] << 8) | (this._buffer[0] & 0xff));
  }

  public int readInt4L() throws IOException {
    this.read(this._buffer, 0, 4);
    return (((this._buffer[3]       ) << 24) |
            ((this._buffer[2] & 0xff) << 16) |
            ((this._buffer[1] & 0xff) <<  8) |
            ((this._buffer[0] & 0xff)      )
    );
  }

  public String readString() throws IOException {
    return null;
  }

}