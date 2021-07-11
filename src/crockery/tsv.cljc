(ns crockery.tsv
  (:require [clojure.string :as str]
            [crockery.fixed :refer [make-renderer]]))

(defn assemble [colspecs header-row body-rows]
  (cons (str/join "\t" header-row)
        (for [tr body-rows]
          (str/join "\t" tr))))

(def renderer (make-renderer {:assemble assemble
                              :chrome-width-fn (fn [i] (dec i))}))
