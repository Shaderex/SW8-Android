package dk.aau.sw808f16.datacollection.questionaire.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Questionnaire implements Parcelable {

  private final List<Question> questions;
  private int currentQuestionIndex = -1;
  public static final Parcelable.Creator<Questionnaire> CREATOR = new Creator<Questionnaire>() {
    @Override
    public Questionnaire createFromParcel(Parcel source) {
      return new Questionnaire(source);
    }

    @Override
    public Questionnaire[] newArray(int size) {
      return new Questionnaire[size];
    }
  };

  private Questionnaire() {
    this.questions = new ArrayList<>();
  }

  public Questionnaire(List<Question> questions) {
    this.questions = questions;
  }

  private Questionnaire(Parcel parcel) {
    this(); // Call constructor to initialize fields
    parcel.readList(questions, Question.class.getClassLoader());
    this.currentQuestionIndex = parcel.readInt();
  }

  private List<Question> getQuestions() {
    return questions;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || !(obj instanceof Questionnaire)) {
      return false;
    }

    Questionnaire instance = (Questionnaire) obj;

    return this.getQuestions().equals(instance.getQuestions());
  }

  // Gets the next question
  public Question getNextQuestion() {
    this.currentQuestionIndex++;
    return questions.get(currentQuestionIndex);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeList(questions);
    dest.writeInt(currentQuestionIndex);
  }
}
