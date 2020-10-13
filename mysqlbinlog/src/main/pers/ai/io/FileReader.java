package pers.ai.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileReader extends BinaryReader {
  private FileInputStream _stream;

  private FileReader(FileInputStream stream) {
    this._stream = stream;
  }

  public static FileReader open(String filename) throws Exception {
    return new FileReader(new FileInputStream(filename));
  }

  @Override
  public boolean hasMore() {
    try {
      return this._stream.available() > 0;
    } catch (Exception ex) {
      return false;
    }
  }

  @Override
  public int read(byte[] buffer, int offset, int length) throws IOException {
    return this._stream.read(buffer, offset, length);
  }

  @Override
  public void close() throws Exception {
    if (this._stream != null) {
      this._stream.close();
    }
  }

  @Override
  public void skip(long length) throws Exception {
    this._stream.skip(length);
  }
}