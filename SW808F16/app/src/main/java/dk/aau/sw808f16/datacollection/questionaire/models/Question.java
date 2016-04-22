package dk.aau.sw808f16.datacollection.questionaire.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

import io.realm.RealmObject;

public class Question extends RealmObject implements Parcelable {

  private Boolean answer;
  private String question;
  private long timestamp;

  public Question() {}
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
    if (!(object instanceof Question)) {
      return false;
    }

    final Question that = (Question) object;

    return that.getQuestion().equals(this.getQuestion())
        && this.getAnswer() != null ?  this.getAnswer().equals(that.getAnswer()) : that.getAnswer() == null;
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
}
