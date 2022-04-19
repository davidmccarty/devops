# Gitea Setup

## Install and craete a user
Described in minikube setup readme

## Setup a mirror

### Create a n access token in github
From User settings --> developer settings --> access tokens.
Createa  token and use it in the following steps.

### Pulling from a remote repository

For an existing remote repository, you can set up pull mirroring as follows:

1. Select New Migration in the Create… menu on the top right.
2. Select the remote repository service.
3. Enter a repository URL.
4. If the repository needs authentication fill in your authentication information.
5. Check the box This repository will be a mirror.
6. Select Migrate repository to save the configuration.

The repository now gets mirrored periodically from the remote repository. You can force a sync by selecting Synchronize Now in the repository settings.


### Pushing to a remote repository

For an existing repository, you can set up push mirroring as follows:

1. In your repository, go to Settings > Repository, and then the Mirror Settings section.
2. Enter a repository URL.
3. If the repository needs authentication expand the Authorization section and fill in your authentication information.
4. Select Add Push Mirror to save the configuration.

The repository now gets mirrored periodically to the remote repository. You can force a sync by selecting Synchronize Now. In case of an error a message displayed to help you resolve it.

❗❗ NOTE: This will force push to the remote repository. This will overwrite any changes in the remote repository! ❗❗

### Setting up a push mirror from Gitea to GitHub

To set up a mirror from Gitea to GitHub, you need to follow these steps:

1. Create a GitHub personal access token with the public_repo box checked.
2. Fill in the Git Remote Repository URL: https://github.com/<your_github_group>/<your_github_project>.git.
3. Fill in the Authorization fields with your GitHub username and the personal access token.
4. Select Add Push Mirror to save the configuration.

The repository pushes shortly thereafter. To force a push, select the Synchronize Now button.
