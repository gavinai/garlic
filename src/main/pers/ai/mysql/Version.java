package pers.ai.mysql;

public class Version {
  public final int Major, Minor, Revision;

  public Version(int[] data) {
    this.Major = data[0];
    this.Minor = data[1];
    this.Revision = data[2];
  }

  public int compareTo(int major, int minor, int reversion) {
    if (major > this.Major) {
      return 1;
    } else if (major == this.Major) {
      if (minor > this.Minor) {
        return 1;
      } else if (minor == this.Minor) {
        if (reversion > this.Revision) {
          return 1;
        } else if (reversion == this.Revision) {
          return 0;
        }
      }
    }

    return -1;
  }
}
