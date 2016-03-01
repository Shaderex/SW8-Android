package dk.aau.sw808f16.datacollection.snapshot;

public class Measurement<T> {

  private T data;

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }
}
