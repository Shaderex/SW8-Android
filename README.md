# SW8-Code

## Code Standard
Throughout the project, the following code standard must be used:
[https://google.github.io/styleguide/javaguide.html](https://google.github.io/styleguide/javaguide.html)

---

## SDK versions
All development must support [SDK versions 19 and 21](http://developer.android.com/guide/topics/manifest/uses-sdk-element.html)

---

## Version control

### Commits

- Do commit **early** and **often**, commits should be as **atomic** as possible.
- Commit messages should use the imperative, present tense eg: "Change some ..." and not: "Changes some...", "Changed some...".
- **Always** ensure that the committed code can compile.
- **Always** run automated tests before committing.
    - If some tests do not pass comment upon this in the commit message.
- Do copy/move a file in a different commit to any changes of the file.
- **Never commit** auto generated files, instead add them to `.gitignore`.

### Branching

- There exist three types of branches:
    - `master`: the main development branch.
    - `feature-*`: a feature in development.
    - `release`: must always contain the latest stable version of the system.
- Feature branches must be named `feature-` followed by a very short and precise description of the feature eg: `feature-questionnaire-model`.
- Branch out when developing a new feature.
- Always merge with the `--no-ff` flag.
    - In Source Tree >> Options >> Git >> Do not fast forward when merging, always create commit.
- Before merging back to `master`, first pull `master` in to your branch, and:
    - Ensure that the code compiles.
    - If some automated tests do not pass, comment upon this in the commit message.
- **Never** delete a branch.