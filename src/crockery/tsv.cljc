(ns crockery.tsv
  (:require [clojure.string :as str]
            [crockery.strings :refer [escape]]
            [crockery.protocols :as p]))

(def renderer
  (reify
    p/RenderTable
    (render-table [_ opts cols data]
      (cond->> (for [row data]
                 (str/join "\t"
                           (for [col cols]
                             (-> ((:key-fn col) row)
                                 (p/render-cell col)
                                 escape))))
        (:titles? opts) (cons (str/join "\t" (map escape (map :title cols))))))))
