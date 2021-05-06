(ns crockery.core
  (:require [crockery.org :as org]
            [crockery.fancy :as fancy]
            [crockery.gfm :as gfm]
            [crockery.tsv :as tsv]
            [crockery.protocols :as p]
            [crockery.util :refer [column-xform]]))

(def ^:dynamic *default-options* {:format :org})

(defn builtin-renderers []
  {:org org/renderer
   :fancy fancy/renderer
   :gfm gfm/renderer
   :tsv tsv/renderer})

(defn table
  "Render a table.

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

  When a map is provided, either `:name` or `:key-fn` is
  required.

  `opts` is a map of options. Currently accepts a single
  key :format, which can either be one of the built-in
  formatters by key, e.g. :org, or anything that implements
  crockery.protocols/RenderTable.

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
   (let [{:keys [format] :as opts} (merge *default-options* opts)
         renderer (get (builtin-renderers) format format)
         ;;_ (assert (satisfies? p/RenderTable renderer))
         cols (into [] column-xform
                    (or cols
                        (:columns opts)
                        (-> data first keys)))]
     (p/render-table renderer cols data))))

(defn print-table
  "Print a table to *out*

  Takes same arguments as `table`, but prints rather than returning
  strings. Returns nil."
  [& args]
  (doseq [line (apply table args)]
    (println line)))
