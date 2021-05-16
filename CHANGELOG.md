# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.2] - 2021-05-25
### Fixed
- Remove errant `def` leftover from debugging
- Don't require test.check to be on classpath for specs to work

## [0.1.1] - 2021-05-25
### Added
- Provide custom "render" functions for cells and titles using the
  `:render-cell` and `:render-title` colspec options, respectively.
- Default colspec options can now be provided table-wide, using the
  `:defaults` option. All are optional, but useful keys might include
  `:align`, `:title-align`, :`:render-cell`, and `:render-title`.

## [0.1.0] - 2021-05-14
### Added
- Initial release
- Simple table printing via the `crockery.core/print-table` function
- ClojureScript support

[Unreleased]: https://github.com/jgdavey/crockery/compare/v0.1.2...HEAD
[0.1.2]: https://github.com/jgdavey/crockery/compare/v0.1.1...v0.1.2
[0.1.1]: https://github.com/jgdavey/crockery/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/jgdavey/crockery/compare/...v0.1.0
