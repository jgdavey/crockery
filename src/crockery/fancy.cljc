(ns crockery.fancy
  (:require [clojure.string :as str]
            [crockery.fixed :refer [make-renderer
                                    aligned-th
                                    aligned-td]]))

(defn assemble [colspecs header-row body-rows]
  (let [spacer (fn [l c r]
                 (str l
                      (str/join c
                                (map #(apply str (repeat (:width %) "─"))
                                     colspecs))
                      r))]
    (concat [(spacer "┌─"  "─┬─"  "─┐")
             (str "│ " (str/join " │ " header-row) " │")
             (spacer "├─"  "─┼─"  "─┤")]
            (for [tr body-rows]
              (str "│ " (str/join " │ " tr) " │"))
            [(spacer "└─"  "─┴─"  "─┘")])))

(def renderer (make-renderer {:th aligned-th
                              :td aligned-td
                              :assemble assemble}))

