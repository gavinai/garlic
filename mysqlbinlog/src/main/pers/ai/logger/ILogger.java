package pers.ai.logger;

public interface ILogger {
  void log(int level, String title, String message, Object ... args);
}