(ns crockery.org
  (:require [clojure.string :as str]
            [crockery.fixed :refer [make-renderer
                                    aligned-th
                                    aligned-td]]))

(defn assemble [colspecs header-row body-rows]
  (let [spacer (str "|-"
                    (str/join "-+-"
                              (map #(apply str (repeat (:width %) "-"))
                                   colspecs))
                    "-|")]
    (concat [spacer
             (str "| " (str/join " | " header-row) " |")
             spacer]
            (for [tr body-rows]
              (str "| " (str/join " | " tr) " |"))
            [spacer])))

(defn org-escape [^String s]
  (str/escape s {\| "\\vert{}"}))

(def renderer (make-renderer {:th aligned-th
                              :td aligned-td
                              :assemble assemble
                              :escape org-escape}))
