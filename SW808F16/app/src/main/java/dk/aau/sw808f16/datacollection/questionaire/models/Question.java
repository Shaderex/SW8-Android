package dk.aau.sw808f16.datacollection.questionaire.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Question implements Parcelable {

  private Boolean answer;
  private String question;

  public Question(String question) {
    this.setQuestion(question);
  }

  protected Question(Parcel in) {
    byte byteAnswer = in.readByte();

    if (byteAnswer == -1) {
      answer = null;
    } else if (byteAnswer == 1) {
      answer = true;
    } else {
      answer = false;
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

  public void setAnswer(Boolean answer) {
    this.answer = answer;
  }

  public Boolean getAnswer() {
    return this.answer;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof Question) || object == null) {
      return false;
    }

    Question instance = (Question) object;

    return instance.getQuestion().equals(this.getQuestion())
        && instance.getAnswer() == this.getAnswer();
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
