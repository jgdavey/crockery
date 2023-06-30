(ns crockery.gfm
  (:require
   [clojure.string :as str]
   [crockery.fixed :refer [make-renderer
                           parse-format]]))

(defn delimiter [{:keys [width align] :as _colspec}]
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

(defn add-alignment [colspecs processed]
  (let [[h _ & body] processed
        spacer (str "|" (str/join "|" (map delimiter colspecs)) "|")]
    (cons h (cons spacer body))))

(defn escape-pipe [^String s]
  (str/escape s {\| "\\|"}))

(def renderer (make-renderer {:chrome (parse-format ["| A | B |"
                                                     "|---|---|"
                                                     "| C | D |"])
                              :postprocess add-alignment
                              :escape escape-pipe}))
