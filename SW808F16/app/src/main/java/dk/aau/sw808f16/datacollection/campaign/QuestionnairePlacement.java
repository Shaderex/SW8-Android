package dk.aau.sw808f16.datacollection.campaign;

public enum QuestionnairePlacement {
  END(0, "End of snapshot"),
  START(1, "Start of snapshot");

  private final int identifier;
  private final String name;

  QuestionnairePlacement(final int id, final String name) {
    this.identifier = id;
    this.name = name;
  }

  public int getIdentifier() {
    return identifier;
  }

  public static QuestionnairePlacement getQuestionnairePlacementById(final int id) {
    for (final QuestionnairePlacement questionnairePlacement : QuestionnairePlacement.values()) {
      if (questionnairePlacement.getIdentifier() == id) {
        return questionnairePlacement;
      }
    }

    return null;
  }

  @Override
  public String toString() {
    return name;
  }
}

