(ns crockery.gfm
  (:require [clojure.string :as str]
            [crockery.fixed :refer [make-renderer
                                    aligned-th
                                    aligned-td]]))

(defn delimiter [{:keys [width align] :as colspec}]
  (let [cfirst (case align
                 :left ":"
                 :center ":"
                 "-")
        clast (case align
                :right ":"
                :center ":"
                "-")]
    (str cfirst
         (apply str (repeat width "-"))
         clast)))

(defn assemble [colspecs header-row body-rows]
  (let [spacer (str "|" (str/join "|" (map delimiter colspecs)) "|")]
    (concat [(str "| " (str/join " | " header-row) " |")
             spacer]
            (for [tr body-rows]
              (str "| " (str/join " | " tr) " |")))))

(defn escape-pipe [^String s]
  (str/escape s {\| "\\|"}))

(def renderer (make-renderer {:th aligned-th
                              :td aligned-td
                              :assemble assemble
                              :escape escape-pipe}))
