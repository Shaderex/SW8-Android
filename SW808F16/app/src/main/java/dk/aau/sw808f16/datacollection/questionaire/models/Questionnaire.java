package dk.aau.sw808f16.datacollection.questionaire.models;

import java.util.ArrayList;
import java.util.List;

public class Questionnaire {

  private List<Question> questions;
  private int currentQuestionIndex = -1;

  public Questionnaire() {
    this.questions = new ArrayList<>();
  }

  public Questionnaire(List<Question> questions) {
    this.questions = questions;
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
}
