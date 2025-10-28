# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.3.1] - 2025-10-28

- Fix for bad 0.3.0 release
- New tools.deps build pipeline

## [0.3.0] - 2023-07-04
### Added
- New alignment option `:decimal`
- New fixed-width formats: `:plain`, `:simple`, `:presto`, `:rst`, `:grid`
- New "fancy" fixed-with formats: `:fancy-grid`, `:rounded`,
  `:rounded-grid`, `:heavy`, `:heavy-grid`, `:double`, `:double-grid`,
  `:mixed-grid`

### Changed
- Some of the format implementation namespaces have been removed.
  `crockery.org`, which contained the org formatter, in now in the new
  `crockery.simple` format namespace.
- Some formatter vars have been renamed. These are mostly an
  implementation detail, but for completeness:

  | Old var                   | New var                |
  |---------------------------|------------------------|
  | `crockery.fancy/renderer` | `crockery.fancy/fancy` |
  | `crockery.org/renderer`   | `crockery.simple/org`  |
  | `crockery.gfm/renderer`   | `crockery.gfm/gfm`     |

## [0.2.0] - 2021-07-12
### Added
- New table option `:max-width` to resize columns that would be too
  big, defaults to terminal width if possible.
- Support more shapes of data directly, i.e. vector of vectors, or a
  single map.

### Changed
- The RenderTable/render-table protocol method signature has changed.
  It now requires a map of table options as the first non-self
  argument.

## [0.1.3] - 2021-07-08
### Added
- Support babashka by removing unsupported Java usage

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

[Unreleased]: https://github.com/jgdavey/crockery/compare/v0.3.1...HEAD
[0.3.1]: https://github.com/jgdavey/crockery/compare/v0.3.0...v0.3.1
[0.3.0]: https://github.com/jgdavey/crockery/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/jgdavey/crockery/compare/v0.1.3...v0.2.0
[0.1.3]: https://github.com/jgdavey/crockery/compare/v0.1.2...v0.1.3
[0.1.2]: https://github.com/jgdavey/crockery/compare/v0.1.1...v0.1.2
[0.1.1]: https://github.com/jgdavey/crockery/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/jgdavey/crockery/compare/...v0.1.0
