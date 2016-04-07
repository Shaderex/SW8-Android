package dk.aau.sw808f16.datacollection.snapshot;

import org.json.JSONException;
import org.json.JSONObject;

public interface JsonObjectAble {

  JSONObject toJsonObject() throws JSONException;
}


