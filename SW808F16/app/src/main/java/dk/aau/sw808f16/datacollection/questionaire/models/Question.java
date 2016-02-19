package dk.aau.sw808f16.datacollection.questionaire.models;

/**
 * Created by rex on 19-02-2016.
 */
public class Question {

  private boolean answer;
  private String question;

  public Question(String question) {
    this.setQuestion(question);
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

  public void answer(boolean answer) {
    this.answer = answer;
  }

  public boolean getAnswer() {
    return this.answer;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Question) || o == null) {
      return false;
    }

    Question instance = (Question) o;

    return instance.getQuestion().equals(this.getQuestion()) && instance.getAnswer() == this.getAnswer();
  }
}
