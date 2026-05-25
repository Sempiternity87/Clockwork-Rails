# mc-skeleton

A production-grade multiloader Minecraft mod template for **Fabric** and **NeoForge**, targeting MC 26.1 (Java 25). Use this template to start a new mod with a full development workflow already configured.

## What's Included

- **Multiloader Gradle build** (common/fabric/neoforge subprojects via convention plugins)
- **Spotless formatting** with a pre-commit git hook
- **CI/CD workflows**: PR checks, pre-release publishing, full release publishing to Modrinth and CurseForge
- **Automated versioning** via release-please (semantic versioning, auto-changelog)
- **JUnit 5 + AssertJ** test infrastructure in the common module
- **Multi-branch strategy** with git worktree support documented in [CLAUDE.md](CLAUDE.md)

## Prerequisites

- **Java 25**
- A Minecraft account (for `runClient`)
- Git

## Getting Started

### 1. Create your repo from this template

**Option A — GitHub web UI:**
Click **"Use this template" → "Create a new repository"** at the top of this page. GitHub creates a fresh repo with a clean single commit (no skeleton history).

**Option B — GitHub CLI:**
```bash
gh repo create my-mod --template Indemnity83/mc-skeleton --clone --private
cd my-mod
```

### 2. Initialize your mod identity

Run the setup task to rename all placeholders in one shot:

```bash
./gradlew setupMod \
  -PmodId=mymod \
  -PpackageName=com.yourname.mymod \
  -PmodDisplayName="My Mod" \
  -Pdescription="A mod that does something cool." \
  -Pauthors="Your Name (@yourhandle)"
```

This renames every file, directory, and text occurrence of the `template` placeholder — Java class names, package paths, mod metadata, Gradle config, everything. Review the changes with `git diff`, then commit.

**Manual things to update after `setupMod`:**
- `LICENSE` — replace `[YEAR]` and `[AUTHOR]`
- `fabric.mod.json` — update `contact.homepage`, `contact.issues`, `contact.sources`
- `CLAUDE.md` — update worktree paths from `your-mod-mc-*` to your actual repo name

### 3. Set up developer tooling

```bash
./gradlew installGitHooks
```

This installs the pre-commit hook that auto-formats your code with Spotless before every commit.

### 4. Build and test

```bash
./gradlew :common:test       # Run unit tests
./gradlew :fabric:jar        # Build the Fabric JAR
./gradlew :neoforge:jar      # Build the NeoForge JAR (may be unstable on beta MC versions)
./gradlew build              # Build everything + tests
```

### 5. Run in-game

```bash
# From repo root
./gradlew :fabric:runClient
./gradlew :neoforge:runClient
```

## Branch Structure

This template is designed for a **multi-branch, multi-version** strategy:

| Branch | MC Version | Purpose |
|--------|-----------|---------|
| `mc/1.21.1` | 1.21.1 | Stable maintenance |
| `mc/1.21.11` | 1.21.11 | Active development |
| `mc/26.1` | 26.1 | Snapshot forward-port |

Each branch targets a different MC version. Features are developed on one branch and **cherry-picked** to others. See [CLAUDE.md](CLAUDE.md) for the full development strategy.

## Git Worktrees

Worktrees let you check out multiple branches simultaneously in sibling directories, so you can compare or copy code across versions without stashing or switching branches.

**One-time setup** (after your `mc/*` branches exist):

```bash
git worktree add ../my-mod-mc-1.21.1 mc/1.21.1
git worktree add ../my-mod-mc-1.21.11 mc/1.21.11
git worktree add ../my-mod-mc-26.1 mc/26.1
```

Each path becomes a fully independent working directory on that branch. Open each one in its own IDE session, run Gradle in any of them, and cherry-pick between them without touching your main checkout.

**Typical cross-version workflow:**

```bash
# Fix a bug on mc/1.21.11
cd ../my-mod-mc-1.21.11
git checkout -b fix/some-bug
# ... make fix, commit ...
git push && gh pr create

# After it merges, port it to mc/26.1
cd ../my-mod-mc-26.1
git fetch origin
git cherry-pick <commit-sha>
git push origin mc/26.1
```

## CI/CD Setup

After creating your GitHub repository, configure these secrets and variables:

**Secrets** (Settings → Secrets and variables → Actions):
- `PERSONAL_TOKEN` — GitHub PAT with `contents:write` and `pull-requests:write` (for release-please)
- `GRADLE_ENCRYPTION_KEY` — any random string (encrypts Gradle cache in CI)
- `MODRINTH_TOKEN` — from modrinth.com → User Settings → API Tokens
- `CURSEFORGE_TOKEN` — from curseforge.com → API Tokens

**Variables**:
- `MODRINTH_PROJECT_ID` — your Modrinth project slug or ID
- `CURSEFORGE_PROJECT_ID` — your CurseForge project ID

**Workflows included:**

| Workflow | Trigger | Description |
|----------|---------|-------------|
| `check-pr.yml` | Every PR | Validates semantic title + checks loader isolation |
| `build-pr.yml` | PRs to `mc/*` | Format check, tests, build artifacts |
| `build-prerelease.yml` | Manual dispatch | Publishes a `-pre.N` build to Modrinth/CurseForge |
| `build-release.yml` | GitHub Release published | Publishes the release to Modrinth/CurseForge |
| `prepare-release.yml` | Push to `mc/*` | release-please creates/updates release PRs |

## Versioning

Versioning is fully automated. The release cycle:

1. Merge `fix:` or `feat:` PRs → release-please opens a release PR
2. Optionally publish a pre-release via `build-prerelease.yml` for community testing
3. Merge the release PR → GitHub Release is created automatically
4. `build-release.yml` triggers and publishes to Modrinth/CurseForge

Version format: `{semver}+mc{mc-version}.{loader}` (e.g., `0.1.0+mc26.1.fabric`)

Tag format: `mc{mc-version}-v{semver}` (e.g., `mc26.1-v0.1.0`)

## License

MIT — see [LICENSE](LICENSE). Replace `[YEAR]` and `[AUTHOR]` before publishing.
