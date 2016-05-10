package dk.aau.sw808f16.datacollection.snapshot;

import org.json.JSONException;

public interface JsonValueAble {
  Object toJsonValue() throws JSONException;
}