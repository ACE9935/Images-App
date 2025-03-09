package pdl.backend;

public class Image {
  private static Long count = Long.valueOf(1);
  private Long id;
  private byte[] data;

  public Image(final byte[] data) {
    id = count++;
    this.data = data;
  }

  public long getId() {
    return id;
  }

  public byte[] getData() {
    return data;
  }

  public static void resetCount() {
    count = Long.valueOf(1);
}
}
