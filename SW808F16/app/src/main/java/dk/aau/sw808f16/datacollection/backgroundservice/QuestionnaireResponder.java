package dk.aau.sw808f16.datacollection.backgroundservice;

import dk.aau.sw808f16.datacollection.questionaire.models.Questionnaire;

public interface QuestionnaireResponder {
  void notifyQuestionnaireCompleted(final long snapshotTimeStamp, final Questionnaire questionnaire);
}
