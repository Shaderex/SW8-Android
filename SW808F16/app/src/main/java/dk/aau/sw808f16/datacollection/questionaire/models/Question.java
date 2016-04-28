package dk.aau.sw808f16.datacollection.questionaire.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import dk.aau.sw808f16.datacollection.snapshot.JsonObjectAble;
import io.realm.RealmObject;

public class Question extends RealmObject implements Parcelable, JsonObjectAble {

  private Boolean answer;
  private String question;
  private long timestamp;
  private long identifier;

  /**
   * @deprecated Do not use this constructor. This is reserved for Realm.io
   */
  @Deprecated
  public Question() {
  }

  /**
   * @deprecated Use {@link #Question(String, long)} instead.
   */
  @Deprecated
  public Question(String question) {
    this.setQuestion(question);
  }

  Question(Parcel in) {
    byte byteAnswer = in.readByte();

    if (byteAnswer == -1) {
      answer = null;
    } else {
      answer = byteAnswer == 1;
    }

    question = in.readString();
  }

  public static final Creator<Question> CREATOR = new Creator<Question>() {
    @Override
    public Question createFromParcel(Parcel in) {
      return new Question(in);
    }

    @Override
    public Question[] newArray(int size) {
      return new Question[size];
    }
  };

  public Question(Question question) {
    this.question = question.getQuestion();
    this.answer = question.getAnswer();
    this.identifier = question.getIdentifier();
  }

  public Question(String question, long identifier) {
    this.question = question;
    this.identifier = identifier;
  }

  public String getQuestion() {
    return question;
  }

  private void setQuestion(String question) {

    if (question == null) {
      throw new NullPointerException("Question cannot be null");
    } else if (question.isEmpty()) {
      throw new IllegalArgumentException("Question cannot be empty");
    }

    this.question = question;
  }

  public void setAnswer(final Boolean answer) {
    this.answer = answer;
    this.timestamp = Calendar.getInstance().getTimeInMillis();
  }

  public Boolean getAnswer() {
    return this.answer;
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }

    if (object == null) {
      return false;
    }

    if (!(object instanceof Question)) {
      return false;
    }

    final Question that = (Question) object;

    boolean isSame = (this.getQuestion() != null ? this.getQuestion().equals(that.getQuestion()) : that.getQuestion() == null) &&
        (this.getAnswer() != null ? this.getAnswer().equals(that.getAnswer()) : that.getAnswer() == null) &&
        (this.identifier == that.identifier);
    return isSame;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    byte byteAnswer;

    if (answer == null) {
      byteAnswer = -1;
    } else if (answer) {
      byteAnswer = 1;
    } else {
      byteAnswer = 0;
    }

    dest.writeByte(byteAnswer);
    dest.writeString(question);
  }

  public long getIdentifier() {
    return identifier;
  }

  public void setIdentifier(long id) {
    this.identifier = id;
  }

  @Override
  public JSONObject toJsonObject() throws JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("id", identifier);

    if (answer == null) {
      jsonObject.put("answer", "undefined");
    } else if (answer) {
      jsonObject.put("answer", "true");
    } else {
      jsonObject.put("answer", "false");
    }


    jsonObject.put("timestamp", timestamp);
    return jsonObject;
  }
}
