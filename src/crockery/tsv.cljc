(ns crockery.tsv
  (:require [clojure.string :as str]
            [crockery.strings :refer [escape]]
            [crockery.protocols :as p]))

(def renderer
  (reify
    p/RenderTable
    (render-table [_ _opts cols data]
      (cons (str/join "\t" (map escape (map :title cols)))
            (for [row data]
              (str/join "\t"
                        (for [col cols]
                          (-> ((:key-fn col) row)
                              (p/render-cell col)
                              escape))))))))
