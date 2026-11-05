# Copilot PR review example

[copilot-pr-review.md](./copilot-pr-review.md) is a
[GitHub Agentic Workflow](https://github.github.io/gh-aw/) that uses the Copilot
engine to review pull requests. Its generated
[copilot-pr-review.lock.yml](./copilot-pr-review.lock.yml) is the GitHub Actions
workflow that actually runs. Keep both files in version control.
The compiler also maintains [action pins](../aw/actions-lock.json) and marks
generated lock files in [`.gitattributes`](../../.gitattributes); keep those
compiler-generated files too.

## Setup

1. Enable GitHub Actions for the repository.
2. Create a fine-grained personal access token owned by a user with an active
   Copilot subscription. Grant **Account permissions > Copilot Requests: Read**.
   See the [Copilot authentication documentation](https://github.github.io/gh-aw/reference/auth/#copilot_github_token).
3. Add that token as an Actions repository secret named **`COPILOT_TOKEN`** in
   **Settings > Secrets and variables > Actions**, or use the interactive CLI:

   ```sh
   gh secret set COPILOT_TOKEN
   ```

   The workflow maps `secrets.COPILOT_TOKEN` to the Copilot engine's
   `COPILOT_GITHUB_TOKEN` environment variable. No additional secret named
   `COPILOT_GITHUB_TOKEN` is needed. Do not add `copilot-requests: write` to this
   example: that selects organization-billed authentication instead of the
   requested personal token.
4. Commit and push both workflow files. Open or update a non-draft PR from a
   branch in this repository to trigger a review. Watch **Actions > Copilot PR
   Review** for execution details.

## Behavior and safety

- Runs on PR `opened`, `synchronize`, `reopened`, and `ready_for_review` events.
- Skips drafts and fork PRs. Fork-triggered `pull_request` runs do not receive
  repository secrets or a write-capable token; this example deliberately does
  not use `pull_request_target` to expose them.
- Retains gh-aw's default trusted-actor policy (repository roles `admin`,
  `maintainer`, and `write`). Automated actors may also be excluded by gh-aw's
  default activation checks.
- Uses read-only GitHub tools to inspect changes; it does not run builds or
  tests and is not a replacement for the existing [PR build](./pr.yml).
- Publishes up to five inline findings and one consolidated `COMMENT` review
  per run. Reviews are informational: they never approve or block merging.
- The Copilot token is for inference, not repository writes. The compiler grants
  the isolated safe-output job the GitHub Actions permissions needed to publish
  reviews; the agent itself retains read-only repository permissions.
- The compiler also enables threat detection and issue-based failure reporting
  by default. These helper jobs can use `issues: write`; only the safe-output
  job receives `pull-requests: write`.
- Each new commit can produce a new review. The prompt asks the agent to avoid
  repeating existing findings, but AI review quality and deduplication are not
  guaranteed. Human review remains necessary.
- A missing or invalid token causes the run to fail. Inspect the Actions logs
  for authentication errors and verify the token owner's Copilot access.

## Editing and compiling

Edit the Markdown source, not the generated lock file. This example was
compiled using `gh-aw` **v1.0.1**:

```sh
gh extension install github/gh-aw --pin v1.0.1
gh aw compile copilot-pr-review --strict
```

If the extension is already installed, check `gh aw version` before compiling.
Commit both the source and regenerated lock file after changes. Recompilation
is required because GitHub Actions does not execute the Markdown directly.
