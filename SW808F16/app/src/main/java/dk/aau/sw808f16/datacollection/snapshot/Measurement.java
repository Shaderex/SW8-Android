package dk.aau.sw808f16.datacollection.snapshot;

public class Measurement<T> {

  private T data;

  public Measurement(final T data) {
    if (data == null) {
      throw new IllegalArgumentException("Data cannot be null");
    }

    setData(data);
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public Class<?> getDataType() {
    return data.getClass();
  }
}
