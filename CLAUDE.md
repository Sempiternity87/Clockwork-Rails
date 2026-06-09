# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Branch Strategy (IMPORTANT!)

**FIRST:** Always check your current branch using `git branch --show-current` or by checking the working directory path.

This repository uses a multi-version strategy to support different Minecraft releases:
- **`mc/1.21.1`** - Stable releases for MC 1.21.1, maintenance focus
- **`mc/1.21.11`** - Stable releases for MC 1.21.11, active feature development
- **`mc/26.1`** - Snapshot development for MC 26.1 snapshots, forward-port for testing

### Critical Understanding

**Domain architecture enables cross-version cherry-picking.** After organizing version-specific code properly:
- ✅ **Can cherry-pick** commits between branches (mc/1.21.1 ↔ mc/1.21.11 ↔ mc/26.1)
- ✅ **Can share code** across versions via git operations
- ✅ **Can maintain feature parity** with minimal manual intervention
- ✅ **Cherry-pick is the primary porting mechanism**

**Porting = git cherry-pick, with occasional conflict resolution for API differences.**

### Recommended Worktree Setup

For easier cross-version development, set up git worktrees for each mc/* branch in sibling directories:
- `../clockwork-rails-1.21.1/` - mc/1.21.1 branch worktree
- `../clockwork-rails-1.21.11/` - mc/1.21.11 branch worktree
- `../clockwork-rails-26.1/` - mc/26.1 branch worktree

**Setting up worktrees:**
```bash
# From the main repo
git worktree add ../clockwork-rails-1.21.11 mc/1.21.11
git worktree add ../clockwork-rails-26.1 mc/26.1
```

**Benefits:**
- Reference code across versions without switching branches
- Compare implementations when cherry-picking
- Resolve conflicts by viewing other version's code directly

### Development Strategy

- **`mc/1.21.11`**: Primary development target (latest stable release, most players)
- **`mc/26.1`**: Port features to keep up with snapshot releases
- **`mc/1.21.1`**: Backport features/fixes (many tech mod users still on this version)

### Branch Protection Rules (CRITICAL)

**Never push or commit directly to `mc/*` branches** (`mc/26.1`, `mc/1.21.11`, `mc/1.21.1`). These are protected primary branches. All work — including in auto mode — must go through a feature branch and PR.

**Required workflow for any new work:**
1. Create a feature branch first: `git checkout -b descriptive-branch-name`
2. Make commits on the feature branch
3. Push the feature branch: `git push origin descriptive-branch-name`
4. Open a PR targeting the appropriate `mc/*` branch

**The only exception** is cherry-picking between `mc/*` branches for porting already-merged commits. Even then, confirm with the user before pushing.

### Cross-Version Workflow

**When fixing bugs:**
1. Fix on the branch where reported
2. Check if bug exists on other branches
3. **Cherry-pick** the fix to affected branches (resolve conflicts if needed)
4. Test on each target branch after cherry-pick
5. Priority order for porting: mc/1.21.11 → mc/26.1 → mc/1.21.1

**When adding features:**
- **During Pre-1.0 phase**: Develop on mc/1.21.11, cherry-pick to mc/26.1 and mc/1.21.1
- **After MC 26.1 releases**: Develop on mc/26.1, backport to mc/1.21.11 if needed

### Writing Cherry-Pick-Friendly Code

Since commits will be cherry-picked across branches, write code that minimizes conflicts:

1. **Match structure** where possible across versions
2. **Keep version-specific code isolated** in dedicated methods/classes
3. **Use similar method names and signatures** across versions
4. **Document any intentional divergences** in commit messages

## Build Commands

```bash
./gradlew build              # Build both Fabric and NeoForge JARs + run tests
./gradlew :fabric:jar        # Build Fabric JAR only
./gradlew :neoforge:jar      # Build NeoForge JAR only
./gradlew :common:test       # Run unit tests
./gradlew runClient          # Launch Minecraft client (run from fabric/ or neoforge/ subproject)
./gradlew runServer          # Launch Minecraft server
./gradlew spotlessApply      # Format all code
./gradlew spotlessCheck      # Check formatting without changes
./gradlew installGitHooks    # Install pre-commit formatting hook (run once per clone)
```

**Requirements:** See `gradle.properties` for current versions (`java_version`, `minecraft_version`, `loom_version`).

**Build output:**
- Fabric: `fabric/build/libs/template-{version}.jar`
- NeoForge: `neoforge/build/libs/template-{version}.jar`

### Version Management

All branches use **release-please** for automated versioning with **SemVer build metadata**.

**How it works:**
1. Create a feature/fix branch
2. Make commits using imperative mood (non-conventional format on individual commits)
3. Create PR with conventional commit title (`fix:`, `feat:`, etc.)
4. PR gets squash-merged with the conventional commit message
5. release-please sees the commit and creates a release PR
6. Merge the release PR to create a GitHub release
7. Release workflow builds and publishes to Modrinth/CurseForge

**Version format:** `{semver}+mc{version}.{loader}` (e.g., `0.1.0+mc26.1.fabric`)

**Tag format:** `mc{version}-v{semver}` (e.g., `mc26.1-v0.1.0`)

**Pre-1.0.0 behavior (configured in release-please-config.json):**
- `fix:` → patch (0.1.0 → 0.1.1)
- `feat:` → patch (0.1.0 → 0.1.1) ← `bump-patch-for-minor-pre-major` enabled
- `feat!:` / `BREAKING CHANGE:` → minor (0.1.0 → 0.2.0) ← `bump-minor-pre-major` enabled

**Do NOT manually edit version numbers.** Let release-please manage it.

## Architecture

This is a multiloader mod supporting **Fabric** and **NeoForge** via a shared `common` module.

```
src layout per subproject:
├── common/           # Shared vanilla MC code — compiled against vanilla only
│   └── src/
│       ├── main/     # Server-safe code
│       ├── client/   # Client-only code (Loom splitEnvironmentSourceSets)
│       └── test/     # Unit tests (JUnit 5 + AssertJ)
├── fabric/           # Fabric platform code + runnable mod jar
│   └── src/
│       ├── main/     # Fabric-specific registrations
│       └── client/   # Fabric client entry point
└── neoforge/         # NeoForge platform code + runnable mod jar
    └── src/
        └── main/     # NeoForge-specific registrations
```

### Multiloader Rules

- **`common/`** must not import from `net.fabricmc` or `net.neoforged` (enforced by CI)
- **`fabric/`** must not import from `net.neoforged` (enforced by CI)
- **`neoforge/`** must not import from `net.fabricmc` (enforced by CI)
- Shared interfaces belong in `common/`; loader implementations belong in `fabric/` or `neoforge/`

### Convention Plugins (`buildSrc/`)

- **`multiloader-common`**: Sets up the common module — exports source directories as Gradle configurations
- **`multiloader-loader`**: Consumes common's exported sources — wires them into `compileJava` and `processResources`

This lets loader subprojects compile common sources directly without creating a JAR dependency.

## Code Style

- **Formatting:** Automated via Spotless (minimal rules for consistency)
- Keep nesting depth reasonable (prefer max 3 levels)

### Code Formatting (Spotless)

**Setup (one-time per clone):**
```bash
./gradlew installGitHooks
```

This installs a pre-commit hook that automatically formats code before each commit.

**Manual formatting:**
```bash
./gradlew spotlessApply    # Format all files
./gradlew spotlessCheck    # Check formatting without changes
```

**CI Enforcement:** Pull requests must pass `spotlessCheck` before merging.

## Commit Messages

Output a SINGLE-LINE commit subject only:
- No conventional-commit prefix (no "feat:", "fix:", etc.)
- Imperative mood ("Add", "Fix", "Refactor")
- Aim for <= 72 characters
- Be specific about what changed

The PR title uses conventional format for release-please.

## Pull Requests

Use conventional commit format for PR title:
```
<type>: <description>
```

**Valid types:** `feat`, `fix`, `refactor`, `perf`, `test`, `docs`, `build`, `ci`, `chore`, `revert`

**Breaking changes:** `feat!: description` or `BREAKING CHANGE:` footer in the PR body.

## Secrets Required for CI

Set these in your GitHub repository settings:

| Secret | Purpose |
|--------|---------|
| `PERSONAL_TOKEN` | GitHub PAT for release-please to open release PRs |
| `GRADLE_ENCRYPTION_KEY` | Encrypts Gradle build cache in CI |
| `MODRINTH_TOKEN` | Publish to Modrinth |
| `CURSEFORGE_TOKEN` | Publish to CurseForge |

| Variable | Purpose |
|----------|---------|
| `MODRINTH_PROJECT_ID` | Your Modrinth project ID |
| `CURSEFORGE_PROJECT_ID` | Your CurseForge project ID |
