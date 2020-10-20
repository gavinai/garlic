package pers.ai.io;

public class ByteBuffer {
  private byte[] _data;
  private int _length;

  public ByteBuffer(int capacity) {
    this._length = 0;
    if (capacity > 0) {
      this._data = new byte[capacity];
    }
  }

  public void put(int off, byte val) {
    this._data[off] = val;
  }

  public ByteBuffer inflate(int capacity) {
    this._length = 0;
    if (this._data != null && this._data.length >= capacity) {
      return this;
    }

    this._data = new byte[capacity];
    return this;
  }

  public byte[] getPayload() {
    return this._data;
  }

  public int getLength() {
    return this._length;
  }

  public void setLength(int len) {
    this._length = len;
  }
}
