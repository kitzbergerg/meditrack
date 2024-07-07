## Guidelines
In order to guarantee a consistent code base and repo structure, the coding conventions must be implemented. It can be helpful to use Checkstyle directly in the IDE. You can find out how this can be set up [here](/Development/Setup & Environment). 

## Conventions
### Code
#### Java
We use the Sun coding conventions. The general checks can be found [here](https://checkstyle.sourceforge.io/checks.html) and sun specific styling [here](https://checkstyle.sourceforge.io/sun_style.html). However, it is sufficient to simply perform a build, as this automatically checks the rules and displays any failures in the console.

#### TypeScript
To follow Angular and TypeScript best practices, we use the `Recommended` set of ESLint rules. This can be viewed [here](https://github.com/angular-eslint/angular-eslint/blob/main/packages/eslint-plugin/src/configs/README.md).

### Branches
We differentiate between feature and bugfix branches. The structure is as follows: `feature|fix: #<issuenumber>`. For example, when a new feature is implemented:
`feature: #13`. This can be used for the majority of issues. If bugs are detected (after an issue has been completed!) a new issue must be created and a branch defined as follows: `fix: #xx`, where `xx` is the new created issue number.

### Commits
Depending on the branch (feature or bugfix), commits must follow the following structure:
`feature|fix: #<issue-number>: <commit-message>`
For example, if I am on a feature branch: `feature: #13: my meaningful commit message`