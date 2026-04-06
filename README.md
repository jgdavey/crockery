# Crockery

Print clojure maps as a human-readable table.

[![img](https://img.shields.io/clojars/v/com.joshuadavey/crockery.svg "Clojars project")](https://clojars.org/com.joshuadavey/crockery)

1.  [Usage](#usage)
2.  [Colspec options](#column-options)
3.  [Table options](#table-options)
4.  [Formats](#formats)
5.  [License](#license)


<a id="usage"></a>

## Usage

Add the latest version of library to your app.

For leiningen or boot:

```clojure
[com.joshuadavey/crockery "<version>"]}
```

or, for deps.edn:

```clojure
{com.joshuadavey/crockery {:mvn/version "<latest version>"}}
```

Require `crockery.core`, which contains the primary API:

```clojure
(require '[crockery.core :as crockery])
```

The examples that follow use this data:

```clojure
(def people
  [{:first-name "Alice", :last-name "Anderson", :age 32, :dues "98.00"}
   {:first-name "Bob", :last-name "Bobberson", :age 29, :dues "17"},
   {:first-name "Carol", :last-name "Carola", :age 26, :dues "105.50"},
   {:first-name "Doug", :last-name "Duggler", :age 41, :dues "0.125"}])
```

Print a table, inferring column names from the first map of the collection:

```clojure
(crockery/print-table people)
```

```
|------------+-----------+-----+--------|
| First Name | Last Name | Age | Dues   |
|------------+-----------+-----+--------|
| Alice      | Anderson  | 32  | 98.00  |
| Bob        | Bobberson | 29  | 17     |
| Carol      | Carola    | 26  | 105.50 |
| Doug       | Duggler   | 41  | 0.125  |
|------------+-----------+-----+--------|
```

Or, specify the columns you want included:

```clojure
(crockery/print-table [:age :last-name] people)
```

```
|-----+-----------|
| Age | Last Name |
|-----+-----------|
| 32  | Anderson  |
| 29  | Bobberson |
| 26  | Carola    |
| 41  | Duggler   |
|-----+-----------|
```

You can mix and match colspec forms (maps and keywords):

```clojure
(crockery/print-table [{:name :last-name, :align :right} :first-name] people)
```

```
|-----------+------------|
| Last Name | First Name |
|-----------+------------|
|  Anderson | Alice      |
| Bobberson | Bob        |
|    Carola | Carol      |
|   Duggler | Doug       |
|-----------+------------|
```


### Alternative data shapes

In addition to a collection of maps, `table` accepts a few other data shapes.

A plain map is rendered as a two-column key/value table:

```clojure
(crockery/print-table (array-map :first-name "Alice" :last-name "Anderson" :age 32))
```

```
|-------------+----------|
| Key         | Value    |
|-------------+----------|
| :first-name | Alice    |
| :last-name  | Anderson |
| :age        | 32       |
|-------------+----------|
```

A sequence of sequences is rendered using the first inner sequence as column headers:

```clojure
(crockery/print-table [["Name" "Score"]
                       ["Alice" 95]
                       ["Bob" 87]])
```

```
|-------+-------|
| Name  | Score |
|-------+-------|
| Alice | 95    |
| Bob   | 87    |
|-------+-------|
```


<a id="column-options"></a>

## Colspec options

In map form, most keys are optional, but a colspec must have at least `:name` or `:key-fn` and `:title`.


### :name

Use a keyword for both the getter function and the title of the column (titleized):

```clojure
(crockery/print-table [{:name :age} {:name :last-name}] people)
```

```
|-----+-----------|
| Age | Last Name |
|-----+-----------|
| 32  | Anderson  |
| 29  | Bobberson |
| 26  | Carola    |
| 41  | Duggler   |
|-----+-----------|
```


### :key-fn

Specify a different accessor function. It should be a function that takes one arg, and will be called for each "row" in the collection.

```clojure
(crockery/print-table
 [{:title "Age (months)" :key-fn (comp (partial * 12) :age)}
  :first-name]
 people)
```

```
|--------------+------------|
| Age (months) | First Name |
|--------------+------------|
| 384          | Alice      |
| 348          | Bob        |
| 312          | Carol      |
| 492          | Doug       |
|--------------+------------|
```


### :width

Widths are normally calculated by finding the longest string per column, but you can also specify one:

```clojure
(crockery/print-table [{:name :age, :width 10} {:name :last-name, :width 5}] people)
```

```
|------------+-------|
| Age        | Last  |
|------------+-------|
| 32         | Ander |
| 29         | Bobbe |
| 26         | Carol |
| 41         | Duggl |
|------------+-------|
```

Values that are too long will be truncated.


### :align

One of `#{:left :center :right}`, defaults to `:left`. Affects the data rows. When no `:title-align` is specified, also affects the header.

One special alignment, `:decimal`, attempts to line up the column so that the decimal point is in the same place in each row:

```clojure
(crockery/print-table [:first-name :last-name {:name :dues :align :decimal}] people)
```

```
|------------+-----------+---------|
| First Name | Last Name | Dues    |
|------------+-----------+---------|
| Alice      | Anderson  |  98.00  |
| Bob        | Bobberson |  17     |
| Carol      | Carola    | 105.50  |
| Doug       | Duggler   |   0.125 |
|------------+-----------+---------|
```

Since numbers are printed as strings, in reality, the decimal alignment just looks for the first decimal point as an alignment anchor.


### :title

Provide your own header title rather than titleizing the `:name` parameter.

```clojure
(crockery/print-table [:last-name {:name :first-name, :title "Given name"}] people)
```

```
|-----------+------------|
| Last Name | Given name |
|-----------+------------|
| Anderson  | Alice      |
| Bobberson | Bob        |
| Carola    | Carol      |
| Duggler   | Doug       |
|-----------+------------|
```


### :title-align

Same properties as `:align`, but only affects the header.


### :render-cell

A function applied to each cell value (after `:key-fn`, before escaping) to produce the display string. Defaults to `str`, with special handling for `nil` (empty string) and `java.util.Date`. Must return a string.

```clojure
(crockery/print-table
 [:first-name
  {:name :age, :render-cell #(str % " yrs")}]
 people)
```

```
|------------+--------|
| First Name | Age    |
|------------+--------|
| Alice      | 32 yrs |
| Bob        | 29 yrs |
| Carol      | 26 yrs |
| Doug       | 41 yrs |
|------------+--------|
```


### :render-title

A function applied to the column's `:name` (or `:key-fn`) to produce the header string, instead of the default titleization. Must return a string.

```clojure
(crockery/print-table
 [{:name :first-name, :render-title name} :age]
 people)
```

```
|------------+-----|
| first-name | Age |
|------------+-----|
| Alice      | 32  |
| Bob        | 29  |
| Carol      | 26  |
| Doug       | 41  |
|------------+-----|
```


### :ellipsis

When `true` and a cell value exceeds `:width`, the value is truncated and `...` is appended rather than cutting off abruptly.

```clojure
(crockery/print-table [{:name :last-name, :width 7, :ellipsis true}] people)
```

```
|---------|
| Last... |
|---------|
| Ande... |
| Bobb... |
| Carola  |
| Duggler |
|---------|
```

Note: `:ellipsis` is also set automatically on columns that are shrunk by `:max-width` rebalancing.


### :ignore-ansi?

When `true`, ANSI escape codes in cell values are excluded from width calculations and truncation. Useful when passing pre-colored strings to the table so that the visual width is correct.


<a id="table-options"></a>

## Table options

Options are passed as a map, either as the first argument to `table` or `print-table`, or by rebinding `crockery.core/*default-options*`.


### :format

The output format. Can be a built-in format keyword (e.g. `:org`, `:gfm`, `:fancy`) or any value implementing `crockery.protocols/RenderTable`. Defaults to `:org`.


### :columns

An alternative to passing columns as a separate argument — useful when passing everything in a single options map.

```clojure
(crockery/print-table {:columns [:first-name :age]} people)
```

```
|------------+-----|
| First Name | Age |
|------------+-----|
| Alice      | 32  |
| Bob        | 29  |
| Carol      | 26  |
| Doug       | 41  |
|------------+-----|
```


### :defaults

A map of colspec defaults applied to every column before per-column options. Useful for setting a global alignment or other option.

```clojure
(crockery/print-table {:defaults {:align :right}} [:first-name :age] people)
```

```
|------------+-----|
| First Name | Age |
|------------+-----|
|      Alice |  32 |
|        Bob |  29 |
|      Carol |  26 |
|       Doug |  41 |
|------------+-----|
```


### :max-width

The maximum total character width of the rendered table. When the auto-calculated width exceeds this, columns are proportionally shrunk and `:ellipsis` is enabled on the affected columns. Defaults to the detected terminal width (via the `COLUMNS` environment variable or `stty=/=tput`).


### :titles?

When `false`, suppresses the header row entirely. Defaults to `true`.


<a id="formats"></a>

## Formats

The default output format is an `:org`, which outputs an org-mode compatible table. There are other built-in formats that can be used. You can specify the format with the `:format` key in the options map, either before other arguments, or globally by rebinding the `crockery.core/*default-options*` var.

For each of these formats, the following colspec will be used:

```clojure
(def colspec [:last-name
              {:name :first-name
               :title "Given name"
               :align :right}
              :age])
```


### :org

This is the default format. See any of the above examples.


### :plain

A fixed-width, unadorned output format.

```clojure
(crockery/print-table {:format :plain} colspec people)
```

```
Last Name  Given name  Age
Anderson        Alice  32 
Bobberson         Bob  29 
Carola          Carol  26 
Duggler          Doug  41 
```


### :simple

```clojure
(crockery/print-table {:format :simple} colspec people)
```

```
Last Name  Given name  Age
---------  ----------  ---
Anderson        Alice  32 
Bobberson         Bob  29 
Carola          Carol  26 
Duggler          Doug  41 
```


### :grid

```clojure
(crockery/print-table {:format :grid} colspec people)
```

```
+-----------+------------+-----+
| Last Name | Given name | Age |
+===========+============+=====+
| Anderson  |      Alice | 32  |
+-----------+------------+-----+
| Bobberson |        Bob | 29  |
+-----------+------------+-----+
| Carola    |      Carol | 26  |
+-----------+------------+-----+
| Duggler   |       Doug | 41  |
+-----------+------------+-----+
```


### :presto

Another fixed-width format, with no surrounding border.

```clojure
(crockery/print-table {:format :presto} colspec people)
```

```
 Last Name | Given name | Age 
-----------|------------|-----
 Anderson  |      Alice | 32  
 Bobberson |        Bob | 29  
 Carola    |      Carol | 26  
 Duggler   |       Doug | 41  
```


### :rst

Based on the reStructured Text table format.

```clojure
(crockery/print-table {:format :rst} colspec people)
```

```
=========  ==========  ===
Last Name  Given name  Age
=========  ==========  ===
Anderson        Alice  32 
Bobberson         Bob  29 
Carola          Carol  26 
Duggler          Doug  41 
=========  ==========  ===
```


### :fancy

This format uses unicode pipe characters. `:fancy-grid` is also available if you'd like separators between data rows.

```clojure
(crockery/print-table {:format :fancy} colspec people)
```

```
┌───────────┬────────────┬─────┐
│ Last Name │ Given name │ Age │
├───────────┼────────────┼─────┤
│ Anderson  │      Alice │ 32  │
│ Bobberson │        Bob │ 29  │
│ Carola    │      Carol │ 26  │
│ Duggler   │       Doug │ 41  │
└───────────┴────────────┴─────┘
```


### :heavy

Like `:fancy`, but with bolder lines. `:heavy-grid` is also available if you'd like separators between data rows.

```clojure
(crockery/print-table {:format :heavy} colspec people)
```

```
┏━━━━━━━━━━━┳━━━━━━━━━━━━┳━━━━━┓
┃ Last Name ┃ Given name ┃ Age ┃
┣━━━━━━━━━━━╋━━━━━━━━━━━━╋━━━━━┫
┃ Anderson  ┃      Alice ┃ 32  ┃
┃ Bobberson ┃        Bob ┃ 29  ┃
┃ Carola    ┃      Carol ┃ 26  ┃
┃ Duggler   ┃       Doug ┃ 41  ┃
┗━━━━━━━━━━━┻━━━━━━━━━━━━┻━━━━━┛
```


### :rounded

Like `:fancy`, but with rounded corners. `:rounded-grid` is also available if you'd like separators between data rows.

```clojure
(crockery/print-table {:format :rounded} colspec people)
```

```
╭───────────┬────────────┬─────╮
│ Last Name │ Given name │ Age │
├───────────┼────────────┼─────┤
│ Anderson  │      Alice │ 32  │
│ Bobberson │        Bob │ 29  │
│ Carola    │      Carol │ 26  │
│ Duggler   │       Doug │ 41  │
╰───────────┴────────────┴─────╯
```


### :double

Like `:fancy`, but with double-line borders. `:double-grid` is also available if you'd like separators between data rows.

```clojure
(crockery/print-table {:format :double} colspec people)
```

```
╔═══════════╦════════════╦═════╗
║ Last Name ║ Given name ║ Age ║
╠═══════════╬════════════╬═════╣
║ Anderson  ║      Alice ║ 32  ║
║ Bobberson ║        Bob ║ 29  ║
║ Carola    ║      Carol ║ 26  ║
║ Duggler   ║       Doug ║ 41  ║
╚═══════════╩════════════╩═════╝
```


### :mixed-grid

Like `:fancy-grid`, but with bolder separators between the header and body rows.

```clojure
(crockery/print-table {:format :mixed-grid} colspec people)
```

```
┍━━━━━━━━━━━┯━━━━━━━━━━━━┯━━━━━┑
│ Last Name │ Given name │ Age │
┝━━━━━━━━━━━┿━━━━━━━━━━━━┿━━━━━┥
│ Anderson  │      Alice │ 32  │
├───────────┼────────────┼─────┤
│ Bobberson │        Bob │ 29  │
├───────────┼────────────┼─────┤
│ Carola    │      Carol │ 26  │
├───────────┼────────────┼─────┤
│ Duggler   │       Doug │ 41  │
┕━━━━━━━━━━━┷━━━━━━━━━━━━┷━━━━━┙
```


### :tsv

This tab-delimited format doesn't look great when printed directly, but is convenient for further processing with common unix utils. Alignment options are ignored.

```clojure
(crockery/print-table {:format :tsv} colspec people)
```

```
Last Name	Given name	Age
Anderson	Alice	32
Bobberson	Bob	29
Carola	Carol	26
Duggler	Doug	41
```


### :gfm

Github-flavored Markdown (GFM) extends standard Markdown with a table syntax, including alignment designators.

```clojure
(crockery/print-table {:format :gfm} colspec people)
```

```
| Last Name | Given name | Age |
|:----------|-----------:|:----|
| Anderson  |      Alice | 32  |
| Bobberson |        Bob | 29  |
| Carola    |      Carol | 26  |
| Duggler   |       Doug | 41  |
```


<a id="license"></a>

## License

Copyright © 2021 Joshua Davey

Distributed under the Eclipse Public License version 1.0.
