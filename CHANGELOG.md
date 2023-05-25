# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.0]

### Added

- resolution of the following git properties
    - `git.commit.id`
    - `git.commit.id.abbrev`
    - `git.commit.user.name`
    - `git.commit.user.email`
    - `git.commit.time`
    - `git.commit.message.short`
    - `git.commit.message.full`
    - `git.branch`
    - `git.remote.origin.url`
    - `git.build.host`
    - `git.build.user.name`
    - `git.build.user.email`
- gradle `--configuration-cache` support
- allows continuing build with `generateGitProperties --continue-on-error` option

[unreleased]: https://github.com/hndrs/gradle-git-properties-plugin/compare/v1.0.0...HEAD

[1.0.0]: https://github.com/hndrs/gradle-git-properties-plugin/compare/7a9a8b9114cea8ce50e2985abe293260b91a5eab...v1.0.0