(ns crockery.core
  (:require [crockery.org :as org]
            [crockery.fancy :as fancy]
            [crockery.gfm :as gfm]
            [crockery.simple :as simple]
            [crockery.tsv :as tsv]
            [crockery.protocols :as p]
            [crockery.util :refer [to-column-map normalize-column data->cols-rows]]))

(def ^:dynamic *default-options* {:format :org
                                  :defaults {:align :left}})

(defn builtin-renderers []
  {:org org/renderer
   :fancy fancy/fancy
   :fancy-grid fancy/fancy-grid
   :gfm gfm/renderer
   :simple simple/simple
   :plain simple/plain
   :presto simple/presto
   :grid simple/grid
   :tsv tsv/renderer})

(defn table
  "Render a table as sequence of strings.

  `data` should be a collection of maps with similar keys.

  `cols` is a list of \"colspecs\", which can take the form
  of keywords or maps with any of the following keys:

      :name          Specifies both the function (as a
                     keyword) and the title for the column

      :title         The title to use for the column in
                     the header. When not provided, uses a
                     titleized version of `:name`.

      :key-fn        The function that gets called on each
                     map in the collection to get the column.
                     When not specified, calls `get` on the
                     map with the `:name`

      :align         One of #{:left :right :center}, how to
                     align the text in the rows for this
                     column. Defaults to `:left`.

      :title-align   Similar to `:align`, but for the header
                     of the column. When not specified, will
                     use the value of `:align`.

      :render-cell   Function to format body cells (not titles)
                     after acesss, but before escaping. Default
                     is `crockery.protocols/render-cell*`, but
                     a useful alternative might be `pr-str`.
                     Must return a string.

      :render-title  Like `:render-cell`, but for titles.
                     Must return a string. Defaults to
                     `crockery.protocols/render-title*`.

  When a map is provided, either `:name` or `:key-fn` is
  required.

  `opts` is a map of table options. All are optional, as is the
  argument itself.

      :format       Can either be one of the built-in formatters
                    by key (:org, :tsv, :gfm, :fancy), or
                    anything that implements crockery.protocols/RenderTable.

      :defaults     Column defaults to be used when not provided
                    in an individual column's colspec.

      :max-width    The maximum textual width of a table. Useful when
                    printing to a tty terminal. The default is your
                    terminal width, if it can be detected.

  Returns a lazy sequence of strings, each representing a
  printable line."
  ([data]
   (table nil nil data))
  ([cols-or-opts data]
   (let [[opts cols] (if (map? cols-or-opts)
                       [cols-or-opts nil]
                       [nil cols-or-opts])]
     (table opts cols data)))
  ([opts cols data]
   (let [{:keys [format defaults] :as opts} (merge *default-options* opts)
         renderer (get (builtin-renderers) format format)
         ;;_ (assert (satisfies? p/RenderTable renderer))
         [detected-cols data] (data->cols-rows data)
         cols (into [] (comp (map to-column-map)
                             (map #(merge defaults %))
                             (map normalize-column))
                    (or cols
                        (:columns opts)
                        detected-cols))]
     (p/render-table renderer opts cols data))))

(defn print-table
  "Print a table to *out*

  Takes same arguments as `table`, but prints rather than returning
  strings. Returns nil."
  [& args]
  (doseq [line (apply table args)]
    (println line)))
