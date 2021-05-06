# Crockery

Print clojure maps as a human-readable table.

1.  [Usage](#usage)
2.  [Colspec options](#column-options)
3.  [Formats](#formats)
4.  [License](#license)


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
  [{:first-name "Alice", :last-name "Anderson", :age 32}
   {:first-name "Bob", :last-name "Bobberson", :age 29},
   {:first-name "Carol", :last-name "Carola", :age 26},
   {:first-name "Doug", :last-name "Duggler", :age 41}])
```

Print a table, inferring column names from the first map of the collection:

```clojure
(crockery/print-table people)
```

    |------------+-----------+-----|
    | First Name | Last Name | Age |
    |------------+-----------+-----|
    | Alice      | Anderson  | 32  |
    | Bob        | Bobberson | 29  |
    | Carol      | Carola    | 26  |
    | Doug       | Duggler   | 41  |
    |------------+-----------+-----|

Or, specify the columns you want included:

```clojure
(crockery/print-table [:age :last-name] people)
```

    |-----+-----------|
    | Age | Last Name |
    |-----+-----------|
    | 32  | Anderson  |
    | 29  | Bobberson |
    | 26  | Carola    |
    | 41  | Duggler   |
    |-----+-----------|

You can mix and match colspec forms (maps and keywords):

```clojure
(crockery/print-table [{:name :last-name, :align :right} :first-name] people)
```

    |-----------+------------|
    | Last Name | First Name |
    |-----------+------------|
    |  Anderson | Alice      |
    | Bobberson | Bob        |
    |    Carola | Carol      |
    |   Duggler | Doug       |
    |-----------+------------|


<a id="column-options"></a>

## Colspec options

In map form, most keys are optional, but a colspec must have at least `:name` or `:key-fn` and `:title`.


### :name

Use a keyword for both the getter function and the title of the column (titleized):

```clojure
(crockery/print-table [{:name :age} {:name :last-name}] people)
```

    |-----+-----------|
    | Age | Last Name |
    |-----+-----------|
    | 32  | Anderson  |
    | 29  | Bobberson |
    | 26  | Carola    |
    | 41  | Duggler   |
    |-----+-----------|


### :key-fn

Specify a different accessor function. It should be a function that takes one arg, and will be called for each "row" in the collection.

```clojure
(crockery/print-table
 [{:title "Age (months)" :key-fn (comp (partial * 12) :age)}
  :first-name]
 people)
```

    |--------------+------------|
    | Age (months) | First Name |
    |--------------+------------|
    | 384          | Alice      |
    | 348          | Bob        |
    | 312          | Carol      |
    | 492          | Doug       |
    |--------------+------------|


### :width

Widths are normally calculated by finding the longest string per column, but you can also specify one:

```clojure
(crockery/print-table [{:name :age, :width 10} {:name :last-name, :width 5}] people)
```

    |------------+-------|
    | Age        | Last  |
    |------------+-------|
    | 32         | Ander |
    | 29         | Bobbe |
    | 26         | Carol |
    | 41         | Duggl |
    |------------+-------|

Values that are too long will be truncated.


### :align

One of `#{:left :center :right}`, defaults to `:left`. Affects the data rows. When no `:title-align` is specified, also affects the header.


### :title

Provide your own header title rather than titleizing the `:name` parameter.

```clojure
(crockery/print-table [:last-name {:name :first-name, :title "Given name"}] people)
```

    |-----------+------------|
    | Last Name | Given name |
    |-----------+------------|
    | Anderson  | Alice      |
    | Bobberson | Bob        |
    | Carola    | Carol      |
    | Duggler   | Doug       |
    |-----------+------------|


### :title-align

Same properties as `:align`, but only affects the header.


<a id="formats"></a>

## Formats

The default output format is an `:org`, which outputs an org-mode compatible table. There are other built-in formats that can be used. You can specify the format with the `:format` key in the options map, either before other arguments, or globally by rebinding the `crockery.core/*default-options*` var.


### :org

This is the default format. See any of the above examples.


### :fancy

This format uses unicode pipe characters.

```clojure
(crockery/print-table {:format :fancy}
                      [:last-name {:name :first-name, :title "Given name"}]
                      people)
```

    ┌───────────┬────────────┐
    │ Last Name │ Given name │
    ├───────────┼────────────┤
    │ Anderson  │ Alice      │
    │ Bobberson │ Bob        │
    │ Carola    │ Carol      │
    │ Duggler   │ Doug       │
    └───────────┴────────────┘


### :tsv

This tab-delimited format doesn't look great when printed directly, but is convenient for further processing with common unix utils.

```clojure
(crockery/print-table {:format :tsv}
                      [:last-name {:name :first-name, :title "Given name"}]
                      people)
```

    Last Name	Given name
    Anderson	Alice
    Bobberson	Bob
    Carola	Carol
    Duggler	Doug


### :gfm

Github-flavored Markdown (GFM) extends standard Markdown with a table syntax, including alignment designators.

```clojure
(crockery/print-table {:format :gfm}
                      [{:name :last-name, :align :right}
                       {:name :first-name, :title "Given name"}]
                      people)
```

    | Last Name | Given name |
    |----------:|:-----------|
    |  Anderson | Alice      |
    | Bobberson | Bob        |
    |    Carola | Carol      |
    |   Duggler | Doug       |


<a id="license"></a>

## License

Copyright © 2021 Joshua Davey

Distributed under the Eclipse Public License version 1.0.
