package dk.aau.sw808f16.datacollection.snapshot;

import org.json.JSONException;

public interface JsonValueAble {

  String toJSONValue() throws JSONException;
}