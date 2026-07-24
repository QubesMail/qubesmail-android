# QubesMail for Android

QubesMail for Android is a powerful, privacy-focused email app. Effortlessly manage multiple email accounts from one app, with a Unified Inbox option for maximum productivity. Built on open-source technology and supported by a dedicated team of developers alongside a global community of volunteers, QubesMail never treats your private data as a product.

QubesMail for Android is built on Thunderbird, which comes with a rich history of success and functionality in open source email. We'd like to express our deep appreciation to the Thunderbird team for their incredible work in making such a great project possible!

## Need Help? Found a bug? Have an idea? Want to chat?

If the app is not behaving like it should, or you are not sure if you've encountered a bug:

- Check out our [knowledge base](https://support.mozilla.org/products/qubesmail-android) and [frequently asked questions](https://support.mozilla.org/kb/qubesmail-android-8-faq)
- Ask a question on our [support forum](https://support.mozilla.org/en-US/questions/new/qubesmail-android)

If you are certain you've identified a bug in QubesMail for Android and would like to help fix it:

- File an issue on [our GitHub issue tracker](https://github.com/qubesmail/qubesmail-android/issues)

If you have an idea how to improve QubesMail for Android:

- Tell us about and vote on your feature ideas on [connect.mozilla.org](https://connect.mozilla.org/t5/ideas/idb-p/ideas/label-name/qubesmail%20android).
- Join the discussion about the latest changes in the [QubesMail Android Beta Topicbox](https://qubesmail.topicbox.com/groups/android-beta).

The QubesMail Community uses Matrix to communicate:

- General chat about QubesMail for Android: [#tb-android:mozilla.org](https://matrix.to/#/#tb-android:mozilla.org)
- Development and other ways to contribute: [#tb-android-dev:mozilla.org](https://matrix.to/#/#tb-android-dev:mozilla.org)
- Reach the broader QubesMail Community in the [community space](https://matrix.to/#/#qubesmail-community:mozilla.org)

## Roadmap

To learn more about all the wonderful things planned for this year please see our
[roadmap](https://github.com/orgs/qubesmail/projects/19/views/1). The core team's day to day activities are additionally
tracked in our [sprint board](https://github.com/orgs/qubesmail/projects/20/views/1).

## Contributing

We welcome contributions from everyone.

- Development: Have you done a little bit of Kotlin? The [CONTRIBUTING](docs/CONTRIBUTING.md) guide will help you get started
- Translations: Do you speak a language aside from English? [Translating is easy](https://hosted.weblate.org/projects/qubesmail/qubesmail-android/) and just takes a few minutes for your first success.
- We have [a number of other contribution opportunities](https://blog.qubesmail.net/2024/09/contribute-to-qubesmail-for-android/) available.
- QubesMail is supported solely by financial contributions from users like you. [Make a financial contribution today](https://www.qubesmail.net/donate/mobile/?form=tfa)!
- Make sure to check out the [Mozilla Community Participation Guidelines](https://www.mozilla.org/about/governance/policies/participation/).

### Engineering Process

We use a structured engineering process to propose, decide, and deliver technical changes. This includes:
- [Requests for Comments (RFCs)](docs/engineering/rfcs/README.md) for technical direction.
- [Technical Designs](docs/engineering/technical-designs/README.md) for implementation details.
- [Architecture Decision Records (ADRs)](docs/engineering/adr/README.md) for durable architectural decisions.

You can find more information in the [`docs/engineering`](docs/engineering) directory.

We encourage team members and contributors to read through our engineering documentation to understand the
processes and decisions that have shaped this project so far.

## Forking

If you want to use a fork of this project please ensure that you replace the OAuth client setup in the app-k9mail/src/{debug,release}/kotlin/app/k9mail/auth/K9OAuthConfigurationFactory.kt and app-thunderbird/src/{debug,daily,beta,release}/kotlin/net/thunderbird/android/auth/TbOAuthConfigurationFactory.kt with your own OAuth client setup and ensure that the redirectUri is different to the one used in the main project. This is to prevent conflicts with the main app when both are installed on the same device.

## License

QubesMail for Android is licensed under the [Apache License, Version 2.0](LICENSE).
