package pers.ai.mysql.binlog;

public interface IBinlogReaderEvent {
  void fireEventPreview(EventPreviewArgs args);
}