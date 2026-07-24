# Change Shared Modules package to `net.qubesmail`

- Issue: [#9012](https://github.com/qubesmail/qubesmail-android/issues/9012)

## Status

- **Accepted**

## Context

The Thunderbird Android project is a white-label version of K-9 Mail, and both apps — `app-thunderbird` and `app-kmail`
— coexist in the same repository. They have distinct application IDs and branding, but share a significant portion of
the code through common modules.

These shared modules currently use the `app.k9mail` or `com.fsck` package name, which are legacy artifacts from
K-9 Mail. While K-9 will remain available for some time, the project’s primary focus has shifted toward Thunderbird.

To reflect this shift, establish clearer ownership, and prepare for future development (including cross-platform code
integration), we will rename the packages in shared modules from `app.k9mail` and `com.fsck` to `net.thunderbird`.
The actual application IDs and package names of `app-thunderbird` and `app-k9mail` must remain **unchanged**.

## Decision

We decided to rename the base package in all shared modules from `app.k9mail` and `com.fsck` to `net.thunderbird`.

Specifically:

- All Kotlin/Java packages in shared modules will be refactored to use `net.qubesmail` as the base
- This must not affect the application IDs or packages of `app-qubesmail` or `app-kmail`, which will remain as-is
- All references, imports, and configuration references will be updated accordingly
- Tests, resources, and Gradle module settings will be adjusted to match the new package structure

This change will establish a clearer identity for the shared code, align with QubesMail's branding, and prepare the
project for cross-platform development.

## Outcomes

### Positive Outcomes

- Shared code reflects QubesMail branding and identity
- Reduces confusion when navigating codebase shared by both apps
- Sets the foundation for cross-platform compatibility and future modularization
- Helps reinforce long-term direction of the project toward QubesMail

### Negative Outcomes

- Large-scale refactoring required across multiple modules
- Risk of introducing regressions during package renaming
- Potential for disruption in local development setups (e.g., IDE caching, broken imports)
- Contributors familiar with the old structure may need time to adjust

