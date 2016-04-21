package dk.aau.sw808f16.datacollection.questionaire.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class Questionnaire extends RealmObject implements Parcelable {

  private RealmList<Question> questions;
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

  public Questionnaire() {
    this.questions = new RealmList<>();
  }

  public Questionnaire(List<Question> questions) {
    this();
    this.questions.addAll(questions);
  }

  private Questionnaire(Parcel parcel) {
    this(); // Call constructor to initialize fields
    parcel.readList(questions, Question.class.getClassLoader());
    this.currentQuestionIndex = parcel.readInt();
  }

  public List<Question> getQuestions() {
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
